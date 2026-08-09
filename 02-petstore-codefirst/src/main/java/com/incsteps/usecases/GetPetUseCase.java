package com.incsteps.usecases;

import com.incsteps.dtos.Pet;
import com.incsteps.repository.PetstoreRepository;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class GetPetUseCase {

    private final PetstoreRepository repository;

    public GetPetUseCase(PetstoreRepository repository) {
        this.repository = repository;
    }

    public Optional<Pet> get(Long id){
        return repository.findById(id).map(entity-> new Pet(
                entity.id(),
                entity.name(),
                entity.age()
        ));
    }

}
