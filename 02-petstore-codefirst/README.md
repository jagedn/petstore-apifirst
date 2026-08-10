# Code first

## Java 25

sdk use java 25.0.4-graal

## Create

mn create-app --inplace --build=gradle --jdk=25 --lang=java --test=spock --features=openapi,swagger-ui,validation,data-jdbc,postgres com.incsteps.petstore

## Initial build

./gradlew build

## Validation

./gradlew run

http://localhost:8080/swagger-ui

curl -X 'GET' 'http://localhost:8080/petstore' -H 'accept: text/plain'

