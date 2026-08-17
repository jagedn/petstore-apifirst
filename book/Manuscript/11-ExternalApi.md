# OpenAPI Client

Hasta ahora hemos actuado como proveedores de la API (generando servidor e interfaces). 

Sin embargo, con frecuencia necesitamos actuar como consumidores de APIs de terceros o de microservicios internos,
"encadenando" llamadas a otras APIs.

En este capítulo vamos a ver cómo configurar Micronaut para que, usando un contrato de una API externa,
se pueda generar un cliente para consumirla por nuestra aplicación.

En esta iteración el equipo decide que quiere facilitar el registro de mascotas en Petstore Lite. 
Así pues, si el cliente envía una petición POST /v1/pets con el nombre "pokemon", nuestro sistema no guardará esa 
cadena literal, sino que llamará a la PokeAPI externa para obtener un nombre de Pokémon real y asignárselo a la nueva mascota.

En un enfoque _ContractFirst_ crearíamos una interfaz anotada como _@Client_ y definiríamos los métodos disponibles, 
seguramente haciendo una traducción manual del contrato a anotaciones en los métodos, creando parámetros y tipos, etc.

En el enfoque _ApiFirst_ lo que vamos a hacer es delegar en Micronaut la creación de estos artefactos indicándole
simplemente la URL de la API externa.

W> Debido a la limitación del plugin que solo trabaja con ficheros y no con URLs, añadiremos una tarea Gradle 
que descargará el contrato desde la URL remota a local.

## PokeApi

Para este ejemplo vamos a usar la PokeApi pública de [https://pokeapi.co/](https://pokeapi.co/).

I> Si consultamos la documentación, https://pokeapi.co/docs/v2, podemos ver que la API permite obtener una lista de
Pokémons en "/api/v2/pokemon/" y a partir de ella podremos elegir el nombre de uno de forma aleatoria.

