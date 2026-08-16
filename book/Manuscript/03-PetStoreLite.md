# Petstore Lite

El objetivo de este libro es poder explicar los conceptos de una forma práctica por lo que se provee de enlaces a proyectos completos asi como código comentado. 

A lo largo del libro vamos a implementar **Petstore Lite**, una versión superreducida del famoso **PetStore** de Spring

Petstore Lite es un servicio REST de ejemplo en el cual vamos a implementar solo unos pocos endpoints (crear, recuperar y borrar mascotas) 
usando mecanismos de persistencia temporal, es decir, cada vez que arranquemos la aplicación empezaremos desde cero, 
ya que el objetivo del libro es trabajar sobre los conceptos API-First, no tanto el tener una aplicación funcional completa.

Así pues Petstore Lite permitirá:

- crear un Pet proporcionando un nombre y una edad para el mismo
- recuperar el detalle de un Pet dado su ID
- eliminar el Pet dado su ID


## Persistencia

De cara a poder ver en acción todo el potencial de Micronaut, el proyecto contará con persistencia de datos 
(Postgres en este caso) con la particularidad de que dichos datos serán borrados en cada arranque de la aplicación.

A> Si se desea desplegar el proyecto manteniendo la persistencia, simplemente hay que proporcionar la configuración 
a una base de datos mediante variables de entorno lo cual queda fuera del objetivo de este libro.


## Entity

Para este ejemplo vamos a usar una Entity muy simple, __PetEntity.java__, la cual simplemente guardará el nombre y 
la edad de la mascota asignándola un ID único

```java
package com.incsteps.repository;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity("pet")
public record PetEntity(
        @Id @GeneratedValue Long id,
        String name,
        int age
) {
}
```

- @MappedEntity le indica a Micronaut que la clase corresponde a una entidad persistente
- @Id le indica a Micronaut que el atributo será la clave principal de la entidad
- @GeneratedValue es una anotación que le indica a Micronaut que el valor puede ser nulo y será asignado por el motor de la base de datos

{blurb, class: information}
Como puedes observar vamos a usar `Record` en lugar de un `Pojo` con get/set. Micronaut nos permite usar los 
records asegurando así la inmutabilidad de las instancias que creemos
{/blurb}

## Repository

Siguiendo el patrón __Repository__ vamos a definir __PetstoreRepository.java__ que nos permitirá realizar 
las operaciones típicas contra la base de datos:

```java
package com.incsteps.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PetstoreRepository extends CrudRepository<PetEntity, Long> {

}
```

En Micronaut, simplemente definiendo un interface que extienda de __CrudRepository__ y 
anotándolo con __JdbcRepository__ dispondremos de una clase que nos permitirá guardar, por ejemplo,
__PetEntity__ con una simple llamada:


```java
   petstoreRepositovy.save( new PetEntity(null, "tiramisu", 12));
```

{blurb, class: information}
Micronaut Data proporciona diferentes formas de trabajar con bases de datos como Jdbc, JPA, Hibernate, etc. Para los ejemplos de este libro vamos a usar Jdbc por su simplicidad y eficiencia.
{/blurb}

A lo largo de los ejemplos usaremos esta Entity y Repository para crear el servicio REST de Petstore Lite, primero siguiendo
CodeFirst y después API-First.
