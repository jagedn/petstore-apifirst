{sample: true}
# Code First

Una vez validado que contamos con el entorno de trabajo adecuado, y que la aplicación arranca correctamente, pasaremos
a crear nuestra primera versión de Petstore Lite usando CodeFirst

T> El código de este capítulo se encuentra en la carpeta **02-petstore-codefirst** del repositorio de github

Partiendo del proyecto creado anteriormente, y como queremos proporcionar al equipo de FrontEnd una API REST válida
cuanto antes, vamos a trabajar lo primero en la capa http definiendo los Controllers y los DTOs de entrada y salida.

W> Como el Controller creado automáticamente por Micronaut no nos interesa podemos/debemos eliminarlo.

## DTOs

Creamos un DTO en el package __dtos__ para representar el Pet que queremos crear.

```java
package com.incsteps.dtos;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record CreatePetRequest(
        @NotNull String name,
        int age
) {
}
```

La anotación `@Serdeable` nos permite serializar y deserializar el DTO utilizando el formato JSON facilitando su
uso en las llamadas HTTP.

Así mismo, crearemos un DTO para representar el Pet creado.

```java
package com.incsteps.dtos;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Pet(
        Long id,
        String name,
        int age
) {
}
```

Como podemos ver, el DTO de salida es muy similar al DTO de entrada, incluyendo el ID del Pet creado.

Con estos DTOs ya podemos crear el Controller para crear un nuevo Pet.

```java
package com.incsteps.http;

import com.incsteps.dtos.CreatePetRequest;
import com.incsteps.dtos.Pet;
// resto de imports eliminados por brevedad

@Controller("/pets")
@Tag(name = "Pets", description = "Operaciones sobre el catálogo de mascotas")
public class PetstoreController {

}
```

### Get Pet by ID

Por ahora vamos a definir solo el interface http para obtener un Pet por su ID pero devolviendo siempre un 404.

```java
    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Obtener mascota por ID",
            description = "Devuelve los detalles de una mascota concreta dada su clave primaria."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Mascota encontrada",
            content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "ID suministrado no válido",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Mascota no encontrada",
            content = @Content
    )
    public HttpResponse<Pet> get(
            @Parameter(description = "Identificador único de la mascota", required = true)
            @PathVariable("id")
            @NotNull
            @Positive Long id
    ) {
        return HttpResponse.notFound();
    }
```

## Post Pet

```java
    @Post
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Registrar una nueva mascota",
            description = "Crea una nueva mascota en el catálogo con los datos proporcionados."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Mascota creada con éxito",
            content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Petición no válida (datos faltantes o erróneos)",
            content = @Content
    )
    public HttpResponse<Pet> create(
            @Parameter(description = "Datos de la mascota a crear", required = true)
            @Body @NotNull @Valid CreatePetRequest request
    ) {
        return HttpResponse.badRequest();
    }
```

Al igual que en el caso de obtener un Pet por su ID, por ahora vamos a devolver un bad request.

### Delete Pet

De la misma forma que en el caso de obtener un Pet por su ID o crear un nuevo Pet, 
definiremos un método DELETE para eliminar un Pet.

T> Puedes ver el método completo en el archivo **PetstoreController.java** en la carpeta **02-petstore-codefirst** del repositorio de github.

## Desplegando

Una vez que tenemos definido los endpoints (sin lógica de negocio implementada) procederíamos a desplegarlo. Por 
ahora nos bastará con ejecutarlo en local y comprobar que Micronaut genere correctamente el contrato OpenAPI
a partir de los DTOs y los endpoints definidos.

`$ ./gradlew run`

En un navegador abrimos la URL http://localhost:8080/swagger/petstore-0.0.yml y veremos el contrato OpenAPI generado.

```yaml
openapi: 3.0.1
info:
  title: petstore
  version: "0.0"
tags:
- name: Pets
  description: Operaciones sobre el catálogo de mascotas
paths:
  /pets:
    post:
      tags:
      - Pets
      summary: Registrar una nueva mascota
      description: Crea una nueva mascota en el catálogo con los datos proporcionados.
      operationId: create
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CreatePetRequest"
 (continua)
```

## Iterando Code First

Como puedes ver, **Micronaut ha generado a partir del código un recurso OpenAPI en formato YAML que representa el contrato
de la API**. 

Esto nos permite documentar la API y generar clientes de cliente para consumirla. Es decir, una vez desplegada 
la aplicación podremos crear un MockServer para simular la API en nuestro entorno de desarrollo mientras desarrollamos
la funcionalidad de negocio por separado.

## UseCases

En el repositorio de ejemplo encontrarás una de las tantas posibles implementaciones. Unas implementarían 
arquitectura hexagonal, otras una arquitectura simple Controller-Service-Repository. 

Cúal elegir (y cómo testearlas) queda fuera del alcance de este libro. Por no complicar los ejemplos y tener un proyecto completo,
he optado por crear unos casos de uso como SingletonS, que puedes encontrar en la carpeta **02-petstore-codefirst/src/main/java/com/incsteps/usecases** los cuales usa el Controller.




