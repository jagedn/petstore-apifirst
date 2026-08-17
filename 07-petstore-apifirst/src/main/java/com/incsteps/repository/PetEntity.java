package com.incsteps.repository;

import com.incsteps.http.model.PetStatus;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity("pet")
public record PetEntity(
        @Id @GeneratedValue Long id,
        String name,
        int age,
        PetStatus status
) {
}
