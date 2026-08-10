# Micronaut

Durante más de dos década, los frameworks en el ecosistema Java han confiado fuertemente en la reflexión en tiempo de ejecución (runtime reflection), la carga dinámica de clases y la generación de proxies en memoria para implementar Inyección de Dependencias (DI) y Programación Orientada a Aspectos (AOP) siendo Spring el framework de referencia en el desarrollo de aplicaciones.

Esta arquitectura funcionó bien durante la era de los servidores de aplicaciones monolíticos que funcionaban de forma ininterrumpida. Sin embargo, en el paradigma actual de contenedores (Docker/Kubernetes), microservicios y Serverless, este modelo tradicional presenta dos problemas graves: tiempos de arranque elevados y un alto consumo de memoria RAM.

En 2018 Graeme Rocher, el principal desarrollador de Grails (un framework basado en Spring y Groovy), presentó en el Greach de Madrid un proyecto en el que había empezado a trabajar [Youtube](https://youtu.be/HTUumoh7lWE)

En esta presentación Graeme mostró las bases de lo que sería Micronaut

## ¿Qué es Micronaut?

Micronaut es un framework JVM moderno y modular diseñado específicamente para la construcción de microservicios y aplicaciones cloud-native. Fue creado por el equipo detrás de Grails (Object Computing, Inc.) con una premisa fundamental: hacer en **tiempo de compilación** todo lo que tradicionalmente se hacía en tiempo de ejecución.

### Características

- **Compilación Ahead-Of-Time** (AOT): Micronaut utiliza procesadores de anotaciones (JSR-269 en Java, KSP en Kotlin o AST Transformations en Groovy) para calcular el grafo de dependencias, validar anotaciones y generar los metadatos necesarios durante la fase de compilación del proyecto
- **Cero reflexión en runtime**: Al no depender de la reflexión para la inyección de dependencias, el tiempo de arranque de la aplicación es prácticamente independiente de la cantidad de beans o controladores que tenga el proyecto
- **Consumo mínimo de memoria**: La eliminación de caches de reflexión internas permite que la aplicación arranque consumiendo una fracción de la memoria RAM requerida por otros frameworks tradicionales.

### Versiones

Al tiempo de escribir este libro Micronaut se encuentra en su versión 5.0.x y es la que usaremos, aunque versiones anteriores son también perfectamente válidas.

Si bien Micronaut demuestra ser un framework muy vivo, con releases frecuentes, la transición entre ellas es muy cuidada y fácil. 

   En muchos casos basta con cambiar el numero de version y todo el proyecto sigue siendo valido. En mi experiencia personal la mayor dificultad se presento al pasar de las versiones 2.x a 3.x, pero no he encontrado mayores problemas al pasar de 3.x a 4.x o recientemente de 4.x a 5.x, aunque siempre dependera de las dependencias y caracteristicas de tu propio proyecto

## Singleton e Inject

A lo largo del libro iremos usando diferentes funcionalidades de Micronaut, casi todas implementadas por el framework mediante anotaciones, pero antes de irlas descubriendo nos pararemos un segundo a explicar las dos que más se usan,
o al menos que yo uso.


Si has trabajado con Spring las anotaciones __@Bean__ , __@Service__, __@Prototype__, etc te sonarán familiares. 

Con estas anotaciones indicamos al framework que estas clases son especiales y que están destinadas a ser inyectadas en otras clases bien mediante métodos _setXXX_ o, preferiblemente, en el constructor de las clases dependientes de ellas.

De forma similar Micronaut no sólo proporciona las suyas propias sino que utiliza las definidas en los JSR como por ejemplo __@Singleton__

Con esta anotación indicamos al framework que nuestra aplicación requiere de una sóla instancia de esta clase, la cual deberá ser proporcionada en aquellos puntos en las que se indique.

Un ejemplo sencillo:

```java
@Singleton
public class TestService{

    void sayHello(){ ... }

}

@Singleton
public class UseCase1{

   @Inject
   TestService testService;

   void doIt(){
    testService.sayHello();
   }
}

@Singleton
public class UseCase2{

   private final TestService testService;
   public UseCase2(TestService testService){
    this.testService = testService;
   }

   void doIt(){
    testService.sayHello();
   }
}

```

En este ejemplo vemos cómo le indicamos a Micronaut que nuestra aplicación consta de tres clases y que queremos una sóla de cada una (al anotarlas como @Singleton)

Además le decimos a Micronaut que en tiempo de compilacion, antes de crear _UseCase1_ y _UseCase2_ tiene que crear _TestService_ . 

Así mismo una vez que se haya creado _UseCase1_ tiene que asignarle a la variable _testService_ la instancia creada anteriormente (al anotarla con __@Inject__).

De la misma forma, cuando se vaya a llamar al constructor de _UseCase2_ se tiene que proporcionar la misma instancia de _TestService_ que se ha usado en _UseCase1_ 

A lo largo del libro veremos alguna otra anotación, como __@Controller__, __@Transactional__ pero son más fáciles de entender y casi autoexplicativas

El enfoque de Micronaut, **a diferencia del de Spring** , es que todo esto puede hacerlo en **tiempo de compilación**, es decir, Micronaut genera e inyecta código que resuelven
todos estos detalles y los incorpora a nuestro "jar" como si los hubieramos escrito nosotros mismos. Por el contrario Spring utiliza las anotaciones para, en **tiempo de ejecución**
descubrir estos detalles y resolver las dependencias entre los Singletons


## Quarkus

Cuando uno habla de Micronaut es muy normal que alguien pregunte por Quarkus.

Quarkus es un framework que comparte muchas características e incluso historia con Micronaut, ya que fue creado casi al mismo tiempo y con una visión parecida.

La diferencia principal radica en la filosofía: Quarkus adopta el estándar MicroProfile/Jakarta EE y está muy optimizado para el ecosistema Kubernetes/Red Hat, mientras que Micronaut mantiene una sintaxis extremadamente cercana a Spring, facilitando la transición a desarrolladores acostumbrados al paradigma de Spring.

Aunque no lo he usado tanto como me gustaría Quarkus es otro framework muy maduro y con funcionalidades muy interesantes. Particularmente no lo he usado tanto porque no se puede usar Groovy, lenguaje que me aposiona, pero por lo demás
es un framework tan bueno, en mi experiencia, como Micronaut.


## ApiFirst con Micronaut

En una estrategia API-First, la especificación OpenAPI actúa como la fuente de verdad. Para llevar esto a producción de forma eficiente, necesitamos que la traducción del contrato OpenAPI a interfaces y controladores de Java no suponga una penalización de rendimiento en runtime.

El procesador de anotaciones de Micronaut no solo genera el servidor HTTP, sino que analiza la especificación OpenAPI durante la compilación para:

- Validar la consistencia de los tipos de datos antes de ejecutar la aplicación.
- Generar clases DTO y clientes HTTP sin añadir metadatos en runtime.
- Proporcionar documentación Swagger/OpenAPI estática.

En los próximos capítulos veremos la diferencia en la práctica, comparando la implementación directa basada en código con la generación automática mediante contratos.

