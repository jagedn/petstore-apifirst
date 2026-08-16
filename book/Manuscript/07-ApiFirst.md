# Api First con Micronaut

En este capítulo abandonamos el enfoque Code-First para construir el proyecto 03-petstore-apifirst. 

Veremos cómo configurar el entorno de compilación en Gradle, escribir nuestro primer contrato OpenAPI 
y permitir que Micronaut genere todo el código de infraestructura por nosotros.

## Crear proyecto

En una carpeta nueva crearemos un proyecto Micronaut llamado `petstore-apifirst` ejecutando el siguiente comando:

```shell
$ sdk install micronaut 5.0.4
$ mn create-app --inplace --build=gradle --jdk=25 \
    --lang=java --test=spock \
    --features=validation,openapi,swagger-ui \
    com.incsteps.petstore
```

I> Tienes el código completo en la carpeta `03-petstore-apifirst` del repositorio.

Como puedes ver, es **exactamente** lo mismo que el proyecto 02-petstore-codefirst. 

**Borramos** el Java `src/main/java/com/incsteps/petstore/PetController.java` porque no vamos a seguir CodeFirst.
Así el proyecto se quedará completamente vacío salvo el `Application.java`

## Plugin

En primer lugar, vamos a añadir el plugin de Micronaut OpenAPI de Gradle (no me preguntes por qué no lo incluye automáticamente)

```groovy
plugins {
    id 'io.micronaut.openapi' version '5.0.2'
}
```

Y vamos a incluir la closure que le indique dónde reside el archivo OpenAPI, así como dónde generar los artefactos.

```groovy
micronaut {
    runtime("netty")
    ...
    aot {
        ...
    }
    openapi {
        server(file("src/main/resources/petstore-api.yml")) {
            apiPackageName = "com.incsteps.http.api"
            modelPackageName = "com.incsteps.http.model"
            useReactive = false
            useAuth = false
        }
    }
}
```

Antes de pasar a crear el contrato OpenAPI, vamos a parar un momento para describir las funcionalidades que nos proporciona
el plugin:

- `server` le indicamos a Micronaut que queremos generar el código para la parte _producer_ del contrato
- `apiPackageName` y `modelPackageName` nos permiten configurar el package donde generar las clases. Puede ser el mismo si así lo deseas
- `useReactive`, como nuestro proyecto no es un proyecto _reactive_ (RxJava por ejemplo), indicamos `false` lo que generará los típicos __Controller__
- `useAuth`, si tu proyecto require autentificación el plugin puede inyectar el `SecurityService` para que puedas usarlo en tus endpoints.

I> En este capítulo vamos a centrarnos en la parte _producer_ del contrato, pero en el capítulo próximo veremos 
cómo podemos indicarle a Micronaut que queremos generar el _consumer_ del contrato. 

Si intentamos ejecutar el proyecto ahora el plugin nos dará un error y abortará el build porque no encuentra el archivo `petstore-api.yml`.

## Construir el contrato

Vamos a crear el archivo `petstore-api.yml` en la carpeta `src/main/resources` (Dispones de un ejemplo completo en el repositorio)

En esta primera iteración vamos a definir un único endpoint:

```yaml
openapi: 3.0.3
info:
  title: Petstore Lite API
  version: 1.0.0
  description: Contrato API-First para la gestión de mascotas.

servers:
  - url: http://localhost:8080
    description: Servidor local de desarrollo

tags:
  - name: pets
    description: Operaciones sobre el catálogo de mascotas

paths:
  /v1/pets:
    post:
      summary: Crear una nueva mascota
      operationId: createPet
      tags:
        - pets
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreatePetRequest'
      responses:
        '201':
          description: Mascota creada correctamente
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PetResponse'
        '400':
          description: Datos de entrada no válidos

components:
  schemas:

    CreatePetRequest:
      type: object
      required:
        - name
        - age
      properties:
        name:
          type: string
          minLength: 2
          example: "Tiramisu"
        age:
          type: integer
          format: int32
          minimum: 0
          example: 3

    PetResponse:
      type: object
      required:
        - id
        - name
        - age

      properties:
        id:
          type: integer
          format: int64
          example: 1
        name:
          type: string
          example: "Tiramisu"
        age:
          type: integer
          format: int32
          example: 3
```

## Build

Ahora sí podremos ejecutar el _build_ del proyecto.

`$ ./gradlew build`

Si revisamos la carpeta `src/main/java` veremos que **no** se han generado ninguna clase nueva.

Sin embargo, si revisamos la carpeta `build/generated/openapi` veremos que se han creado dos carpetas nuevas
(_generateServerOpenApiApis_ y _generateServerOpenApiModels_)
y dentro de ellas podemos ver el código generado por Micronaut OpenAPI.

Por ejemplo se ha creado un interface _PetsApi.java_ anotada como _Controller_ junto con la 
definición del endpoint y sus parámetros:

```java
package com.incsteps.http.api;
    ....
@Controller
public interface PetsApi {
    @Post("/v1/pets")
    HttpResponse<@Valid PetResponse> createPet(
            @Body @NotNull @Valid CreatePetRequest createPetRequest
    );
}
```

y una clase _CreatePetRequest.java_ y _PetResponse.java_ que representan el modelo de la entrada/salida anotados como _Serdeable_:

```java
@Serdeable
@Generated("io.micronaut.openapi.generator.JavaMicronautServerCodegen")
public class PetResponse {
    ....
}
```

W> Estas clases *NO* hay que versionarlas, pues son generadas por Micronaut a partir del archivo OpenAPI en cada build.

## Run

Sin crear ningún artefacto más vamos a ejecutar el servidor:

`$ ./gradlew run`

y abrimos un navegador en `http://localhost:8080/swagger-ui/index.html`

Podemos comprobar que Micronaut OpenAPI ha generado el contrato correctamente y que Swagger UI se ha cargado correctamente.

![PetStore Get](02.png)

![PetResponse](03.png)

Es decir, **sin haber escrito ninguna línea de código**, tenemos un servidor REST que implementa el contrato OpenAPI.
(obviamente, devuelve un error 404 a cada petición)