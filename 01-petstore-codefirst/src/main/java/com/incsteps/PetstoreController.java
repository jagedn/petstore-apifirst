package com.incsteps;


import io.micronaut.http.annotation.*;

@Controller("/petstore")
public class PetstoreController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}