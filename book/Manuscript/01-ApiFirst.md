# ApiFirst

El concepto de API, o application programming interfaces, tiene mucha historia ya en el mundo de la computación moderna. 

Su funcion principal es permitir que diferentes piezas de software se comuniquen, incluso siendo servicios corriendo en máquinas remotas, estando hoy en día presentes en prácticamente todos los proyectos.

Podemos decir, de forma muy simplificada, que existen dos formas de desarrollar una API: definiéndolo al "principio" (Api-first) del proyecto o al "final" (Code-first)

Code-first, plantea que el API será generado a partir del propio código, bien mediante anotación o por inferencia del mismo, permitiendo al programador centrarse en el negocio y generar la especificación del API a partir del mismo.

Por su parte, la metodología API-first prioriza el diseño del API desde el inicio del proyecto, incluso antes de escribir ninguna línea de código. Esto permite a los equipos construir aplicaciones pensando desde el principio en la interconectividad

Podemos decir que con API-first el equipo multidisciplinar (backend, frontend, product...) comienza diseñando, discutiendo y formalizando la especificación de la API —habitualmente usando la especificación OpenAPI 3.x— antes de escribir una sola línea de lógica de negocio o infraestructura.

## Pilares

Los 3 pilares en los que se basa API-first son:

- La API es el contrato de la verdad, es decir, la única fuente oficial de lo que el sistema puede hacer (y lo que no).

- Diseño colaborativo. Promueve que un equipo multidisciplinar participe en esta definición, puesto que todos son partes interesadas, a diferencia de Code-first donde usualmente es el backend el que define el contrato y los consumidores deben adaptarse

- Desacoplamiento del código, donde el framework/servidor se ajustan a la especificacion y no al reves

API-first NO implica que la API diseñada sea inmutable y no se puede cambiar. De hecho es una buena práctica que el equipo multidisciplinar tenga reuniones de revision frecuentes sobre todo al principio cuando se está en fase
de descubrimiento. 

Esto permite que todas las partes implicadas en el desarrollo (tanto productores como consumidores) puedan avanzar sin "esperar" a que la otra parte tenga completada y desplegada la suya como suele ocurrir con CodeFirst

## Herramientas

Aunque en un plano agnóstico API-first es independiente de las herramientas, en la práctica es recomendable que el framework elegido las proporcione de una forma integrada y de calidad.

Hoy en día existen multitud de herramientas que a partir de la especificación generan MockServers, por ejemplo, de tal forma que el consumidor puede ir avanzando su parte 
contra este y reemplazarlo con la versión generada por el productor cuando esté lista

De la misma forma existen herramientas que a partir de las especificaciones pueden generar diferentes clases de tests (unitarios, de integración, ...) para que el
productor pueda asegurar que su proyecto cumple con las especificaciones acordadas.

En este libro nos vamos a centrar en la parte de productor, y usaremos cualquier IDE capaz de 
interpretar proyectos Java con Gradle como herramienta de desarrollo.



