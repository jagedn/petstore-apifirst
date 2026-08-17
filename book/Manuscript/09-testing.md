# Testing con API-First

Para asegurar la calidad y fiabilidad de nuestro software debemos crear tantos tests como
nos sea posible.


En un enfoque code-first, la API es una consecuencia de la lógica interna. El testing tradicional se centra en:

- Llamadas a métodos internos.
- Estados de objetos.
- Interacciones entre clases.


En un enfoque API-first, la API no es un subproducto de la implementación: es el contrato que define 
cómo interactúan los componentes del sistema. Esto cambia radicalmente la forma en que pensamos el testing.

Cuando estamos testeando un contrato API nos centramos en validar que la interfaz es correcta más que en la implementación en sí.
No nos importa cómo el servidor resuelve una petición; nos importa que la API responde correctamente, con el formato, 
los códigos de estado y los cabeceros que el contrato establece.

| Aspecto | Code-first | API-first |
| ------- | ---------- | --------- |
| Unidad de testeo | Método / Clase| Endpoint (recurso + verbo HTTP) |
| Contrato| Implícito en la firma del método| Explícito en la especificación OpenAPI |
| Consumidor del test| Desarrollador del servicio| Cualquier cliente (front, móvil, otro microservicio)|
| Regresión| Se detecta al compilar| Se detecta al ejecutar contra la spec |

## Estrategias de testing

### Tests de contrato

Validan que la implementación cumple la especificación OpenAPI. 

No ejecutan lógica de negocio, sino que comprueban:
- Código de estado correcto (200, 201, 400, 404, 500).
- Estructura del body (campos, tipos, optional/required).
- Cabeceras (Content-Type, ETag, Location).
- Mensajes de error con el formato acordado.

### Tests de integración

Levantan la aplicación completa (o un perfil mínimo) y realizan peticiones HTTP reales contra los endpoints. Los
tests no solo validan el contrato sino el comportamiento esperado en secuencias completas. 

Como veremos a continuación, Micronaut nos permite crear tests de integración de forma sencilla. Los tests levantan una aplicación completa 
y nos permiten probar peticiones HTTP reales a la API, lo cual es perfecto para validar que el contrato es correcto.

## Testing con Micronaut

Como ya se mencionó al principio del libro, Micronaut permite crear tests usando JUnit o Spock ofreciendo 
anotaciones y utilidades para facilitar la integración de los tests con la aplicación en cualquiera de sus capas.

En el caso de Api-First, Micronaut no solo levanta la aplicación completa en cada test, sino que permite inyectar
_HttpClient_ en los tests para realizar peticiones HTTP reales.

```java
@MicronautTest
class PetApiTest {

   @Inject
   @Client("/")
   HttpClient httpClient;

   @Test
   void createPet_withInvalidBody_returns400() {
      String badJson = """
              {"name": "", "age": null}
              """;

      assertThrows(HttpClientResponseException.class, () ->
              httpClient.post("/pets", badJson).retrieve()
      );
   }
}
```

Debido al coste de levantar para cada test una aplicación completa, 
otros frameworks como Spring optan por simular la API mediante un cliente tipado,
o simplemente con tests unitarios. En el caso de Micronaut, el coste es significativamente menor
por lo que ejecutar los tests no penaliza el tiempo de build.

T> En `05-petstore-apifirst` hay un ejemplo de un test de integración usando Spock

