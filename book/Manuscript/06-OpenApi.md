# OpenAPI

Hasta ahora hemos delegado en Micronaut la tarea de generar el OpenAPI a partir de nuestras anotaciones (Code-First).
Pero en Api-First somos nosotros quienes definimos la API para que, por un lado, el framework genere el código y por otro,
los clientes puedan consumirlo.

OpenAPI es el estándar abierto de facto en la industria para describir APIs RESTful de forma independiente del lenguaje de programación. 
Mantenido por la OpenAPI Initiative (bajo el paraguas de la Linux Foundation), permite definir endpoints, 
formatos de entrada/salida, mecanismos de autenticación y esquemas de datos en un documento estructurado en formato YAML o JSON.

I> En este capítulo se presenta el formato OpenAPI brevemente, pero puedes consultar toda la especificación en [OpenAPI](https://swagger.io/specification/)


## Estructura básica

A continuación se presenta un extracto de cómo sería un OpenAPi en formato YAML:

```yaml
openapi: 3.0.3
info:
  title: Petstore Lite API
  version: 1.0.0
  description: Contrato oficial para el servicio de gestión de mascotas.

servers:
  - url: http://localhost:8080/api/v1
    description: Servidor local de desarrollo

paths:
  # Definición de rutas y operaciones HTTP
  /pets/{id}:
    get:
      summary: Obtener mascota por ID
      operationId: getPetById
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Mascota encontrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Pet'
      ...

components:
  # Elementos reutilizables (Schemas, Respuestas, Parámetros)
  schemas:
    Pet:
      type: object
      ...
```

I> En el ejemplo anterior se ha utilizado el formato YAML, escrito a "mano" pero existen editores o plugins 
que facilitan la definición del OpenAPI de forma visual. Por ejemplo [Swagger Editor Online](https://editor.swagger.io/)
es el editor online de referencia.

- openapi: Especifica la versión del estándar utilizada. La serie 3.0.x y 3.1.x son las normas actuales en producción.

- info: Metadatos de la API (título, versión semántica, términos de servicio, contacto, licencia).

- servers: Array con las URLs base donde la API está desplegada o estará disponible.

- paths: La espina dorsal del documento. Define los endpoints disponibles, los métodos HTTP admitidos (get, post, delete, etc.), parámetros de entrada y respuestas esperadas.

- components: El almacén de componentes reutilizables. Aquí se definen los Schemas (los DTOs), respuestas comunes o esquemas de seguridad para evitar duplicar código YAML.

## Paths

En __paths__ asociamos rutas HTTP con parámetros de entradas y las respuestas esperadas

En cada __path__ indicamos el verbo HTTP (get, post, delete, etc.) así como los parámetros 
que pueden venir en la ruta y/o en el body

Así mismo definimos las respuestas esperadas por el endpoint (cuantas más definamos mejor explicado quedará la API)

W> El campo **operationId** es fundamental al trabajar con herramientas de generación de código como las de Micronaut. Determinará directamente el nombre del método Java que se creará en la interfaz resultante, lo cual nos ayudará en la implementación.

## Schemas

Mediante los __schemas__ definimos la estructura de los DTOS que se van a utilizar en la API. 

En Api-First definiremos los campos y formatos que se van a utilizar en la API pensando en la integración con los clientes, 
no en la implementación. Es decir, prestaremos cuidado en no definir campos que serían propios de la capa de persistencia por ejemplo

Micronaut (y otras herramientas orientadas a la generación del código) usarán los __schemas__ para generar las clases
Java que corresponden junto con los métodos de acceso a sus propiedades.

En esta fase podremos indicar si un campo es obligatorio o no, su tipo y su formato, si es un enumerado, etc. Por ejemplo:

```yaml
components:
  schemas:
    Pet:
      type: object
      required:
        - id
        - name
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
          example: 10          
        status:
          type: string
          enum: [AVAILABLE, ADOPTED]
```

Atributos como required, enum, minimum o pattern son leídos por Micronaut durante la fase de compilación para aplicar automáticamente las anotaciones de Jakarta Validation (@NotNull, @Pattern, @Min) sobre las clases Java generadas.

