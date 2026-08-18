package com.incsteps.http.api

import com.incsteps.http.model.CreatePetRequest
import com.incsteps.http.model.PetResponse
import com.incsteps.http.model.PetStatus
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class PetsControllerSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient client

    void "debe validar el apikey para crear una mascota"() {
        given: "un payload válido de creación"
        def requestBody = new CreatePetRequest()
                .name("Firulais")
                .age(3)
                .status(PetStatus.AVAILABLE)

        def request = HttpRequest.POST("/v1/pets", requestBody)

        when: "se realiza la petición POST al servidor"
        def response = client.toBlocking().exchange(request, PetResponse)

        then: "responde 201 Created y retorna el recurso generado"
        def execp = thrown(HttpClientResponseException)
        execp.status == HttpStatus.UNAUTHORIZED
    }

    void "debe crear una mascota correctamente cuando los datos cumplen el contrato"() {
        given: "un payload válido de creación"
        def requestBody = new CreatePetRequest()
                .name("Firulais")
                .age(3)
                .status(PetStatus.AVAILABLE)

        def request = HttpRequest.POST("/v1/pets", requestBody)
                .header("X-API-KEY","petstore-secret-key")


        when: "se realiza la petición POST al servidor"
        def response = client.toBlocking().exchange(request, PetResponse)

        then: "responde 201 Created y retorna el recurso generado"
        response.status == HttpStatus.CREATED
        response.body() != null
        response.body().id != null
        response.body().name == "Firulais"
        response.body().status == PetStatus.AVAILABLE
    }

    void "debe fallar con 400 Bad Request cuando el payload viola las restricciones del YAML"() {
        given: "un JSON explícito con edad negativa para saltarnos validaciones de cliente"
        String invalidJson = '''
            {
              "name": "Firulais",
              "age": -1,
              "status": "AVAILABLE"
            }
        '''
        def request = HttpRequest.POST("/v1/pets", invalidJson)
                .header("X-API-KEY","petstore-secret-key")

        when: "se envía el JSON inválido"
        client.toBlocking().exchange(request, Map)

        then: "el servidor rechaza la petición HTTP con un 400 Bad Request"
        def ex = thrown(HttpClientResponseException)
        ex.status == HttpStatus.BAD_REQUEST
    }

    void "debe recuperar una mascota por su ID"() {
        given: "una mascota creada previa en la base de datos"
        def createRequest = HttpRequest.POST("/v1/pets", new CreatePetRequest()
                .name("Rex")
                .age(5)
                .status(PetStatus.AVAILABLE))
                .header("X-API-KEY","petstore-secret-key")

        def createdPet = client.toBlocking().retrieve(createRequest, PetResponse)


        when: "se solicita la mascota por su identificador"
        def getRequest = HttpRequest.GET("/v1/pets/${createdPet.id}")
        def response = client.toBlocking().exchange(getRequest, PetResponse)

        then: "devuelve la mascota con estado 200 OK"
        response.status == HttpStatus.OK
        response.body().id == createdPet.id
        response.body().name == "Rex"
    }

    void "debe crear una mascota correctamente cuando el nombre está vacio el contrato"() {
        given: "un payload válido de creación"
        def requestBody = new CreatePetRequest()
                .status(PetStatus.AVAILABLE)

        def request = HttpRequest.POST("/v1/pets", requestBody)
                .header("X-API-KEY","petstore-secret-key")


        when: "se realiza la petición POST al servidor"
        def response = client.toBlocking().exchange(request, PetResponse)

        then: "responde 201 Created y retorna el recurso generado"
        response.status == HttpStatus.CREATED
        response.body() != null
        response.body().id != null
        response.body().name.length()
        response.body().status == PetStatus.AVAILABLE
    }
}