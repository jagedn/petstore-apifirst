# Protección y seguridad

La seguridad debería ser una preocupación fundamental en cualquier API. El adoptar un
enfoque API-first es una buena manera de hacerlo desde el principio.

OpenAPI contempla cómo indicar los diferentes mecanismos de autentificación que ofrecerá
la API, así como definir qué endpoints estarán protegidos y bajo qué medida mediante
`Security Scheme Object`.

Mediante este esquema, podemos definir de qué forma se debe autenticar el acceso a los endpoints.
Por ejemplo, podemos definir que todos los endpoints deben autenticarse mediante un token Bearer,
un apikey o ambos, oauth, etc.

Por ejemplo, podemos definir que un securityScheme ApiKey:

```yaml
components:
  securitySchemes:
    defaultApiKey:
      description: API key provided in console
      type: apiKey
      name: api-key
      in: header
```

y luego aplicarlo a un endpoint:

```yaml
security:
  - defaultApiKey: []
paths:
  /board:
    get:
      security:
        - defaultApiKey: []
```

Los tipos de seguridad admitidos por OpenAPI son:

- ApiKey, `type: apiKey`, envía un token en una cabecera personalizada, como un parámetro en la query string o en una cookie
- Http, `type: http`, utiliza la cabecera estándar Authorization
- OAuth2, `type: oauth2` permite definir flujos completos
- OpenIdConnect, `type: openIdConnect`, permite especificar una URL de descubrimiento para obtener la configuración del proveedor de identidad de forma dinámica.

Además, OpenAPI permite definir varios tipos de seguridad en un mismo endpoint.

## Micronaut Security

Micronaut cuenta con diferentes implementaciones de seguridad, como por ejemplo JWT y OAuth2. 

Mediante las anotaciones `@Secured` que proporciona el framework, podemos indicar que un endpoint require autentificación
antes de ser invocado.

Por ejemplo, podemos indicar que un endpoint requiere el rol de administrador para ser invocado:

```java
@Controller("/board")
public class BoardController {
    @Get
    @Secured("ROLE_ADMIN")
    public List<Board> list() {
        return boardRepository.findAll();
    }
}
```

Cuando creamos un proyecto podemos especificar las "features" correspondientes, 
`micronaut-security-jwt` para JWT y `micronaut-security-oauth2` para OAuth2 por ejemplo. O bien podemos
añadirlas manualmente en un proyecto ya creado:

```groovy
aotPlugins("io.micronaut.security:micronaut-security-aot")
annotationProcessor("io.micronaut.security:micronaut-security-annotations")
implementation("io.micronaut.security:micronaut-security-jwt")
```

W> En este caso, especificamos que queremos usar JWT, pero puedes usar cualquier otro tipo de seguridad que ofrezca Micronaut.

Cada mecanismo de seguridad requiere de una configuración específica. Por ejemplo, para el caso de querer usar JWT debemos
añadir una configuración parecida a la siguiente:

```yaml
micronaut:
  security:
    enabled: true
    authentication: bearer
    token:
      jwt:
        enabled: true
        signatures:
          secret:
            generator:
              secret: "${JWT_GENERATED_SECRET:por-favor-cambia-este-secreto-en-produccion-123456789}"```
```

## Security Schema y Micronaut

En la tercera iteración con el equipo se decide que la creación y eliminación de mascotas solo debe ser accesible por usuarios con
un API-KEY, mientras que los endpoints de consulta pueden ser accesibles por cualquier usuario.

I> Cuando usamos Api-First, **NO** anotamos la implementación de los controllers con `@Secured` sino que dejamos
al plugin gradle de Micronaut que lo haga por nosotros:

```groovy
openapi {
    server(file("src/main/resources/petstore-api.yml")) {
        ...
        useAuth = true
    }
}
```
Siguiendo lo explicado al principio del capítulo, modificaremos el recurso `petstore-api.yaml` para añadir el 
schema de seguridad:

```yaml
components:
  securitySchemes:
    petstoreApiKey:
      type: apiKey
      name: X-API-KEY
      in: header
      description: Clave de acceso para operaciones de administración
```

y en los endpoints correspondiente:

```yaml
    post:
      summary: Crear una nueva mascota
      operationId: createPet
      security:
        - petstoreApiKey: [ ]
      tags:
        - pets
```

Si generamos el proyecto de nuevo, veremos que ahora los endpoints `createPet` y `deletePet` requieren el API-KEY para ser invocado,
mientras que el resto de endpoints son accesibles por cualquier usuario.

```java
    @Post("/v1/pets")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    HttpResponse<@Valid PetResponse> createPet(
        @Body @NotNull @Valid CreatePetRequest createPetRequest
    );

    @Get("/v1/pets/{id}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    PetResponse getPetById(
            @PathVariable("id") @NotNull @Min(1L) Long id
    );
```

### Seguridad global

Si queremos que todos los endpoints de nuestra API utilicen el mismo mecanismo de seguridad no hace falta ir uno
por uno indicándolo en cada path. Podemos hacerlo en el primer nivel de la API:

```yaml
security:
  - petstoreApiKey: []
```

De esta forma **todos** los endpoints se crearán con la anotación `@Secured` correspondiente.

En este caso, si quisiéramos que un endpoint determinado fuera público deberíamos indicarlo mediante un security vacío: 

```yaml
paths:
  /v1/pets/{id}:
    get:
      summary: Obtener mascota por ID
      operationId: getPetById
      security: []
```

### Validaciones de seguridad

Cada mecanismo de seguridad implica tanto una configuración específica como crear algunas clases a nuestro proyecto.

Para nuestro ejemplo vamos a añadir una clase que validará el API-KEY:

```java
@Singleton
class ApiKeyTokenValidator implements TokenValidator<HttpRequest<?>>  {


    @Override
    public Publisher<Authentication> validateToken(String token, HttpRequest<?> request) {
        if (request == null ) {
            return Publishers.empty();
        }
        if( !"petstore-secret-key".equals(token)){
            return Publishers.empty();
        }
        return Publishers.just(Authentication.build("demo"));
    }
}
```

I> El código completo de la clase se encuentra en el proyecto `06-petstore-apifirst`

Nuestro ejemplo simplemente comprueba si el API-KEY es igual a `petstore-secret-key`, lo cual no es la mejor
implementación de seguridad, pero es suficiente para explicar el concepto.

## Tests

Una vez que hemos definido la seguridad de nuestra API, los tests fallarán al intentar generar el proyecto, puesto
que Micronaut de forma automática ha añadido las anotaciones de seguridad correspondientes.

Así pues, tendremos que:

- crear un nuevo test para validar que se está validando la presencia del apiKey
- modificar los existentes para proporcionar un apiKey en aquellas operaciones que lo requieran

Puedes ver el código completo de los tests en el proyecto `06-petstore-apifirst`.

Y esto es una de las ventajas de usar API-First, que los tests **siempre** estarán alineados con el contrato,
no con la implementación.

