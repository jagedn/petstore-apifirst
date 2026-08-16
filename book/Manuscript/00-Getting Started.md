# Bienvenido

Bienvenido a esta guía práctica de **Api-First** con **Micronaut**. El objetivo de este libro es ir más allá de la teoría del diseño de APIs y construir, paso a paso, una aplicación simple pero funcional utilizando el ecosistema de Micronaut.

A> Para aprovechar al máximo este libro no necesitas ser un experto en Micronaut ni en OpenAPI, pero sí se asume cierta familiaridad con el ecosistema Java/JVM, la construcción de APIs REST y el uso de herramientas de línea de comandos así como algún IDE.

## Requisitos previos

El principal requisito para poder seguir las explicaciones y ejecutar los ejemplos que se proporcionan
es tener un JDK instalado. En este libro usaré Java 25 por ser una versión moderna, pero versiones anteriores son también perfectamente válidas.

La idea es que el código a utilizar sea sencillo y no utilice características propias de una versión específica de Java, aunque un mínimo de conocimiento de Java es necesario.

Así mismo usaré Linux como sistema operativo de referencia para aquellos comandos que haya que ejecutar en la terminal, como crear directorios o ejecutar un programa, pero tanto las explicaciones como ejemplos son igualmente válidos en Windows como MacOS. Simplemente, tendrás que usar el correspondiente en tu sistema.


## Ejemplos

Todo el código fuente del libro es 100% funcional y está alojado en GitHub. Para evitar problemas con el historial de Git o la complejidad de gestionar ramas interactivas, el repositorio utiliza una estructura basada en directorios independientes.

Cada capítulo relevante o fase del proyecto corresponde a una carpeta raíz en el repositorio:

```
petstore-apifirst
│   README.md
│
└───01-petstore-codefirst
│   │   README.md
│   │   build.gradle
│   │
│   └───src
│       │   ...
│       │   Application.java
│   
└───02-petstore-codefirst
│   │   README.md
│   │   build.gradle
│   │
│   └───src
│       │   ...
│       │   Application.java
```

Si dispones de **git** puedes clonar todos los ejemplos directamente ejecutando en un directorio de tu máquina:

```
git clone git@github.com:jagedn/petstore-apifirst.git
```

O bien bajarte con un navegador el zip y descomprimirlo en una carpeta de tu elección.


## Convenciones del libro

A lo largo de los capítulos encontrarás diferentes tipos de contenido:

- Bloques de código: Mostrarán fragmentos de Java, especificaciones OpenAPI en YAML o configuraciones Gradle por ejemplo.

- Comandos de terminal: Indicados con el prefijo $. Puedes copiarlos y ejecutarlos directamente en tu consola (quitando el prefijo $)

- Llamadas a endpoints: Ejemplos con curl preparados para copiar y pegar contra la aplicación en ejecución local.


