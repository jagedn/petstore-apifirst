# Inicio

Antes de continuar vamos a comprobar que nuestro equipo cuenta con todas las herramientas y requisitos para poder completar el resto de capítulos sin problemas

## Java y Sdkman

Como ya se ha mencionado vamos a usar una versión moderna de Java, 25, más que nada por si quieres usar alguna característica de tu interés aunque los ejemplos intentaran ser los más simples.

En concreto vamos a usar la version `25.0.4-graal` y lo más sencillo para instalarla es usando Sdkman

Sdkman permite la instalación (y eliminación) de multiples herramientas y JDK facilitando el poder usar una version u otra de una forma muy comoda

Si no lo tienes instalada consulta su página web [](https://sdkman.io/install/)

Una vez instalado le indicaremos que descargue la version que vamos a usar en los ejemplos:

`$ sdk install java 25.0.4-graal`

Podemos usar esta versión como la por defecto o continuar con la que usemos en nuestros proyectos. Simplemente, cuando vayamos a trabajar con algún ejemplo del libro, tendremos que decirle a Sdkman que configure esta version como defecto en la sesión de trabajo actual:

`$ sdk env`

    Cada proyecto del libro contiene un fichero `.sdkmanrc` que le indica a Sdkman qué version usar


## Micronaut

De la misma forma, para los ejemplos de este libro, vamos a usar una version moderna de Micronaut, en concreto `5.0.4`

Vamos a indicarle a Sdkman que la instale (y si queremos la marcaremos como por defecto)

`$ sdk install micronaut 5.0.4`

## 01-petstore-codefirst

Es hora de crear el primer proyecto usando el propio Micronaut:

```
$ create-app --inplace --build=gradle --jdk=25 \
    --lang=java --test=spock \
    --features=validation,openapi,swagger-ui \
    com.incsteps.petstore
```

   Puedes usar tanto Gradle como Maven en Micronaut, así como usar JUnit o Spock para tus tests. Yo uso Gradle y Spock, pero para este libro es indiferente

Ahora comprobaremos si tenemos todo lo necesario para compilar y ejecutar la aplicación:

`$ ./gradlew build`

    Si usas Windows el comando seria "gradlew build"

Si todo ha ido bien deberemos ver algo como

```
BUILD SUCCESSFUL in 17s
30 actionable tasks: 30 executed
```

Podemos comprobar que se ha generado un Jar runnable y que este se ejecuta sin problema:

`$ java -jar build/libs/petstore-0.1-all.jar`

Si todo va bien veremos en consola algo como

`$ Startup completed in 415ms. Server Running: http://localhost:8080`

Y podremos ver nuestra primera aplicación abriendo un navegador en [](http://localhost:8080/swagger-ui)

![](01.png)

