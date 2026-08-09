package com.incsteps.dtos;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record CreatePetRequest(
        @NotNull String name,
        int age
) {
}
