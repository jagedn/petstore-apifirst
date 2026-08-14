# Implementando Api First

En el capítulo anterior creamos un proyecto que implementa el contrato OpenAPI definido en el resource __openapi.yaml__ 
generando un simple _Controller_ y un par de clases _Model_ para representar los modelos de la API.

En este capítulo vamos a ampliar la definición del contrato así como, al fín, implementar la lógica de negocio que
cumple con el contrato.

## petstore-api.yml

En la iteración del equipo se decide que se necesitan los endpoints siguientes:

- crear una nueva mascota
- actualizar una mascota existente
- eliminar una mascota existente

Además, se decide añadir un atributo __status__ a la mascota. Este atributo puede tener los siguientes valores:

- `AVAILABLE`
- `ADOPTED`

Este atributo podrá ser usado como filtro para listar las mascotas.

I> En el proyecto `04-pestore-apifirst` puedes encontrar el contrato completo de la API. 

Tomando como base el contrato del ejemplo `04-pestore-apifirst` ejecutaremos un build para analizar los artefactos generados
por Micronaut.

`$ ./gradlew build`


## Model

En primer lugar, podemos ver que Micronaut ha generado unas clases Java que representan el modelo de la API
en la carpeta `build/generated/openapi/generateServerOpenApiMoels/main/java/com/incsteps/http/model/`
incluido el enum `PetStatus`.

El modelo generado son simples clases POJO con todas las anotaciones necesarias para cumplir con el contrato, como
por ejemplo `@NotNull` para indicar que un atributo es obligatorio, o _@Min(0)_ para indicar que un atributo debe ser
mayor o igual a 0, etc. 

El plugin de Micronaut es capaz así de generar diferentes
clases POJO para cada operación, como por ejemplo _CreatePetRequest.java_ vs _UpdatePetRequest.java_, donde el 
contrato especifica que el atributo `name` es obligatorio para crear una mascota, pero no para actualizarla.


### Enum

Como hemos dicho anteriormente, negocio ha decidido añadir un atributo __status__ a la mascota que puede tener unos
valores definidos:

```yaml
  schemas:
    PetStatus:
      type: string
      enum:
        - AVAILABLE
        - ADOPTED
```

El plugin de Micronaut es capaz así de generar un enum para representar los valores posibles de este atributo.
Pero no sólo eso, si inspeccionamos el código generado podemos ver que NO solo se ha generado una clase `PetStatus.java` con los
valores definidos, sino que Micronaut ha generado un Factory que permita convertir de String a `PetStatus` y viceversa.


## Controller

Gracias a que la especificación de OpenAPI es abierta, podemos indicarle al plugin de Micronaut los nombres de las clases
y métodos que se debe generar para cada operación usando los **atributos `operationId` y `tags`**.

Por ejemplo:

```yaml
  ....
  /v1/pets:
    get:
      summary: Listar todas las mascotas
      operationId: getPets
      tags:
        - pets
  ....
```
I> Indicando `pets` como tag Micronaut generará una clase `PetsApi.java` mientras que si el tag fuera `mascotas` se genera una clase `MascotasApi.java`.

En un OpenAPI puede ser normal tener en el mismo contrato la definición de diferentes dominios de negocio.
Usando diferentes tags en las diferentes operaciones a lo largo del fichero podemos hacer que Micronaut genere
clases separadas para cada dominio ayudando al mantenimiento del aplicativo.

De la misma forma, indicando `getPets` en `operationId` le indicamos a Micronaut qué nombre dar al método correspondiente
en el interface a crear.

Por último, Micronaut usará tanto `summary` como los parámetros de entrada y salida para generar el JavaDoc correspondiente.

Utilizando el atributo `tags` podemos ver que Micronaut ha generado un Controller que implementa la interfaz `PetsApi`
Esto es útil cuando nuestra API tiene muchas operaciones y queremos organizarlas en diferentes clases. En nuestro
caso, como es un simple CRUD, vamos a usar el mismo tag para todas las operaciones, lo que hará que residan todas
ellas en la misma clase.

Si inspeccionamos el interface creado por Micronaut veremos que en este se encuentran definidas todas las operaciones
que hemos definido en el contrato. Por ejemplo:

```java
    @Get("/v1/pets/{id}")
    PetResponse getPetById(
        @PathVariable("id") @NotNull @Min(1L) Long id
    );
```

## Implementación

Ahora que hemos definido el contrato y hemos generado las clases necesarias para implementarlo, vamos a añadir 
la lógica de negocio en sí.

En nuestro proyecto, y ahora ya sí bajo el control de versiones, crearemos la clase `PetController.java` que implementa
la interfaz `PetsApi` generada por Micronaut:

```java
@Controller
public class PetsController implements PetsApi{
    private final CreatePetUseCase createPetUseCase;
    private final DeletePetUseCase deletePetUseCase;
    private final GetPetUseCase getPetUseCase;
    ...

    @Override
    public HttpResponse<@Valid PetResponse> createPet(CreatePetRequest createPetRequest) {
        return HttpResponse.created(createPetUseCase.create(createPetRequest));        
    }
}
```

Al igual que en el ejemplo de CodeFirst (`02-CodeFirst`), hemos optado en la implementación por crear unos UseCase
donde se encuentran las reglas de negocio, de tal forma que el Controller solo tiene que llamar a los UseCase.

En el código anterior podemos apreciar algunos detalles:

- La clase debe ser anotada como _@Controller_, al igual que lo está _PetsApi.java_
- La implementación NO necesita todas las anotaciones del Api, como _@Body_, @NotNull, etc. simplemente hacemos un _@Override_

## Iteración 2 

Supongamos ahora que realizamos una nueva iteración con el equipo y decidimos que necesitamos un endpoint para
listar las mascotas y que sería bueno que se usara el atributo `status` para filtrar las mascotas por lo que se añade
al contrato:

```yaml
  /v1/pets:
    get:
      summary: Listar todas las mascotas
      operationId: getPets
      tags:
        - pets
      parameters:
        - name: status
          in: query
          required: false
          description: Filtrar mascotas por su estado
          schema:
            $ref: '#/components/schemas/PetStatus'
      responses:
        '200':
          description: Lista de mascotas recuperada con éxito
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/PetResponse'
```

I> En el proyecto `05-pestore-apifirst` puedes encontrar el contrato completo de la API.

Si ejecutamos el build de nuevo, el compilador abortará la operación con un error. Ahora el interface _PetsApi.java_
se ha modificado y define un nuevo método `getPets` que tiene que ser implementado en la clase `PetsController.java`.

Y aquí es donde vemos la diferencia entre CodeFirst y ApiFirst: El contrato **obliga** a implementar todos los endpoints
definidos en el mismo.

Para poder "avanzar" con el proyecto tendremos que implementar el nuevo método `getPets` en la clase `PetsController.java`.