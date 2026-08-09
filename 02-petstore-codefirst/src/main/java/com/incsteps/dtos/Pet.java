package com.incsteps.dtos;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Pet(
        Long id,
        String name,
        int age
) {
}
