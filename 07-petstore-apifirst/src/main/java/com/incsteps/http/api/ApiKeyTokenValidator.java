package com.incsteps.http.api;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.validator.TokenValidator;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

@Singleton
class ApiKeyTokenValidator implements TokenValidator<HttpRequest<?>>  {


    @Override
    public Publisher<Authentication> validateToken(String token, HttpRequest<?> request) {
        if (request == null ) {
            return Publishers.empty();
        }
        if( request.getMethod() == HttpMethod.GET) {
            return Publishers.empty();
        }
        if( !"petstore-secret-key".equals(token)){
            return Publishers.empty();
        }
        return Publishers.just(Authentication.build("demo"));
    }
}