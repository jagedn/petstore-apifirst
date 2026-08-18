# OpenAPI Client

Hasta ahora hemos actuado como proveedores de la API (generando servidor e interfaces). 

Sin embargo, con frecuencia necesitamos actuar como consumidores de APIs de terceros o de microservicios internos,
"encadenando" llamadas a otras APIs.

En este capítulo vamos a ver cómo configurar Micronaut para que, usando un contrato de una API externa,
se pueda generar un cliente para consumirla por nuestra aplicación.

En esta iteración el equipo decide que quiere facilitar el registro de mascotas en Petstore Lite. 
Una de las quejas más comunes por parte de los consumidores es que no se pueden registrar mascotas sin un nombre.

Así pues, se decide que si el cliente envía una petición POST /v1/pets con el nombre vacío, nuestro sistema 
llamará a una API externa para obtener un nombre y asignárselo a la nueva mascota.

En un enfoque _ContractFirst_ crearíamos una interfaz anotada como _@Client_ y definiríamos los métodos disponibles, 
seguramente haciendo una traducción manual del contrato a anotaciones en los métodos, creando parámetros y tipos, etc.

En el enfoque _ApiFirst_, puesto que se dispone del contrato "remoto", lo que vamos a hacer es delegar 
en Micronaut la creación de estos artefactos indicándole simplemente la URL de la API externa.

W> Debido a la limitación del plugin que solo trabaja con ficheros y no con URLs, añadiremos una tarea Gradle 
que descargará el contrato desde la URL remota a local.

## PokeApi

Para este ejemplo vamos a usar la PokeApi pública de [https://pokeapi.co/](https://pokeapi.co/).

I> Si consultamos la documentación, https://pokeapi.co/docs/v2, podemos ver que la API permite obtener una lista de
Pokémons en "/api/v2/pokemon/" y a partir de ella podremos elegir el nombre de uno de forma aleatoria.

Una posible implementación sería:

```java
if( name == null || "".equalsIgnoreCase(name)) {
    var response = pokemonApi.pokemonList(100, 0, null);
    Random r = new Random();
    var e = response.getResults().get(r.nextInt(response.getResults().size()));
    return e.getName();
}
```
Es decir, pedimos a la API el listado de pokemons, seleccionamos uno aleatorio y devolvemos su nombre.

## Micronaut Client

De la misma forma que usábamos el plugin de Micronaut para generar un servidor, podemos usarlo para generar un cliente.

build.gradle
```groovy
openapi {
        server(file("src/main/resources/openapi/petstore-api.yml")) {
            ...
        }
        client(file('build/pokeapi-specs/pokeapi.yml')){
            apiPackageName = "io.pokeapi.api"
            modelPackageName = "io.pokeapi.model"
            useReactive = false
            useAuth = false
            clientId = 'pokeapi'
        }
    }
```

En esta ocasión usaremos la closure `client` para indicarle que queremos generar un cliente para la API externa.
De la misma forma que en el servidor, podemos indicarle el nombre del paquete donde queremos que se generen las interfaces.
En este caso, vamos a utilizar un paquete llamado `io.pokeapi` diferente al que usamos para el servidor simplemente
para remarcar que es un cliente externo.

Mediante la property `clientId` podemos indicarle el nombre del cliente que se va a generar y de esta forma poder
configurar el interface `HttpClient` generado. Si no lo indicamos Micronaut le generará uno por defecto.

I> Podemos crear varios clientes para distintas APIs externas indicándoles diferentes `clientId`s.

### Download PokeApi Spec

Como ya se ha mencionado antes, el plugin de Micronaut no puede descargar ficheros remotos.

Para ello, vamos a añadir una tarea Gradle que descargue el fichero de la API externa (puedes verla en el `build.gradle`
del proyecto `07-petstore-apifirst`), y vamos a hacer que las tareas de generar el cliente dependa de ella
para que se ejecuten una vez que se haya descargado.

build.gradle
```groovy
def pokeapi = resources.text.fromUri(
        "https://raw.githubusercontent.com/PokeAPI/pokeapi/refs/heads/master/openapi.yml"
)
tasks.register "pokeapi", Sync, {
    from(pokeapi){
        rename { 'pokeapi.yml' }
    }
    into layout.buildDirectory.dir("pokeapi-specs")
}
tasks.matching { it.name.startsWith("generateClientOpenApi") }.configureEach {
    dependsOn(tasks.named("pokeapi"))
}
```

Obviamente, también se puede optar por descargar el fichero en el directorio `src/main/resources` y hacer que el plugin lo lea,
teniendo así versionado el fichero en el repositorio.

### Build

Si ejecutamos el comando `gradlew build` veremos que se ha generado el cliente en `build/pokeapi-specs/pokeapi.yml`, así
como los artefactos de la interfaz en `build/generated/openapi/generateClientOpenApiApis/` y 
`build/generated/openapi/generateClientOpenApiModels/`

Al igual que con el server, estos artefactos se generan en el directorio `build` y **NO** tienen que ser versionados,
pues se regeneran cada vez que se ejecuta el comando `gradlew build` (si no existen) a partir del contrato.

Si inspeccionamos, por ejemplo, el interface `PokemonApi.java` generado podemos ver cómo ha sido anotado con 
el `@Client` que indicamos en el build.gradle.

```java
@Client("pokeapi")
public interface PokemonApi {
    ...
}
```

### Client config

Al igual que con el resto de _@Client_s, debemos configurar el cliente generado:

application.properties
```properties
micronaut.http.services.pokeapi.url=https://pokeapi.co
```

T> `pokeapi` es el nombre del cliente que hemos especificado en la closure `client` del build.gradle y que
Micronaut ha usado para generar el cliente.

## Consumiendo el cliente

Para consumir el cliente, simplemente lo inyectaremos en el constructor de la clase que lo necesite, CreateUseCase 
en nuestro caso.

```java
@Inject
PokemonApi pokemonApi;
```

O especificándolo en el constructor de la clase que lo necesite:

```java
    private final PetstoreRepository repository;
    private final PokemonApi pokemonApi;

    public CreatePetUseCase(PetstoreRepository repository, PokemonApi pokemonApi) {
        this.repository = repository;
        this.pokemonApi = pokemonApi;
    }
```

Y ahora podemos usar el cliente para obtener un nombre de mascota:

```java
var response = pokemonApi.pokemonList(100, 0, null);
Random r = new Random();
var e = response.getResults().get(r.nextInt(response.getResults().size()));
return new PetEntity(null, e.getName(), request.getAge(), PetStatus.AVAILABLE);
```

I> Como la llamada al caso de uso Create se realiza desde un Controller debemos tener cuidado de **NO** bloquear
los hilos de ejecución. Simplemente le indicaremos a Micronaut que el método del Controller debe ser 
ejecutado en un hilo diferente, por ejemplo anotándolo con `@ExecuteOn(TaskExecutors.VIRTUAL)`.

## HttpClient

Hasta ahora nuestro proyecto ha usado la librería `micronaut-http-client` de Micronaut para resolver las anotaciones
de la parte _Server_ por lo que está configurada como `compileOnly`

Sin embargo, ahora queremos usar esta librería también en la parte cliente así que simplemente cambiaremos 
el _scope_ de la dependencia a `implementation`.

build.gradle
```groovy
dependencies {
    // Cambiamos de compileOnly a implementation para habilitar el motor cliente HTTP en runtime
    implementation("io.micronaut:micronaut-http-client")
}
```

## Test

En el proyecto `07-pestore-apifirst` se ha creado un nuevo test para verificar que la implementación acepta nombres
de mascotas vacíos y que le asigna un nombre aleatorio.

Este test **NO** mockea el cliente y se usa la API externa. Se deja al lector el ejercicio de crear un mock para
el cliente.