# Conclusion

A lo largo del libro hemos ido viendo cómo implementar un pequeño servicio, _Petstore Lite_, primero siguiendo
la metodología _CodeFirst_ para después tomar una aproximación diferente con _ApiFirst_ e ir profundizando en 
cómo afrontar las diferentes iteraciones del equipo.

Ambas aproximaciones son perfectamente válidas y el éxito del proyecto dependerá más de la madurez del equipo
que de la metodología a adoptar. Sin embargo, **ApiFirst** ofrece algunas ventajas sobre **CodeFirst** como:

- El contrato es la **fuente de la verdad** última
- La documentación está presente desde el principio
- Los equipos pueden trabajar en paralelo
- Los consumidores __toman el mando__ (o al menos participan) en la definición de la API.

## Micronaut

Aunque de forma resumida, a lo largo del libro hemos visto que Micronaut cuenta con todos los componentes necesarios
para implementar un servicio RESTful usando _ApiFirst_:

- Generador de código a partir del contrato mediante plugin _Micronaut OpenAPI_
- Servidor HTTP
- Client HTTP
- Mecanismos de Seguridad
- Testing

## Buenas prácticas

- El **contrato manda**: Nunca edites manualmente las interfaces generadas (*Api.java). Cualquier cambio debe nacer en el YAML.

- Aprovecha la **compilación**: Configura useAuth = true y las validaciones Jakarta (@Valid, @NotNull) en la especificación para minimizar el código boilerplate.

- **Tests alineados** con el contrato: Comprueba la serialización real con JSON crudo y clientes de bajo nivel. **NO** uses __Client__S

- **Seguridad** desde el diseño: Aplica la seguridad globalmente en el YAML y libera de forma explícita los recursos públicos con security: [].

- Aislamiento de clientes: Separa los paquetes (apiPackageName, modelPackageName) de tus servidores y clientes generados para evitar colisiones de tipos.

- Usa **records** en tus modelos/dtos para asegurar su inmutabilidad.

## El ecosistema Contract-First

En este libro nos hemos centrado en cómo Micronaut ayuda en el uso de _ApiFirst_. Sin embargo, no es la única herramienta 
que nos ayudaría en el desarrollo del servicio usando esta metodología. Por ejemplo:

1. ApiCurio, una tool completa orientada al diseño de APIs.
2. Swagger Editor, una herramienta para editar y visualizar el contrato.
3. Swagger UI, una herramienta para visualizar y ejecutar el contrato.
4. MockServer, un servidor mock para integrar en nuestros tests.
5. Prism (Stoplight), servidor de Mocks automático que lee tu archivo openapi.yml y levanta una API falsa en segundos para que los equipos de Frontend puedan trabajar sin esperar al Backend.
6. Redoc / Scalar: Alternativas modernas y visualmente más limpias que Swagger UI para publicar la documentación interactiva de tus contratos.

## Otras APIs

Aunque a lo largo del libro nos hemos centrado en OpenAPI, existen otras tecnologías interesantes que pueden servir
como alternativa:

- AsyncAPI: El estándar para arquitectura de eventos. Equivalente directo a OpenAPI, pero diseñado para brokers de mensajería (Kafka, RabbitMQ, MQTT, SQS).
- gRPC (Protobuf): Framework de RPC de alto rendimiento desarrollado por Google orientado a Contract-First.
- GraphQL (Schema-First): Lenguaje de consulta para APIs donde el cliente pide exactamente los datos que necesita.

## Referencias

- [Micronaut](https://micronaut.io/)
- [Swagger](https://swagger.io/)
- [OpenAPI](https://swagger.io/specification/)
- [Gradle](https://gradle.org/)
