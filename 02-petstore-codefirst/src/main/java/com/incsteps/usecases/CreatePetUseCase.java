package com.incsteps.usecases;

import com.incsteps.dtos.CreatePetRequest;
import com.incsteps.dtos.Pet;
import com.incsteps.exceptions.CreatePetException;
import com.incsteps.repository.PetEntity;
import com.incsteps.repository.PetstoreRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class CreatePetUseCase {

    private final PetstoreRepository repository;

    public CreatePetUseCase(PetstoreRepository repository) {
        this.repository = repository;
    }

    public Pet create(CreatePetRequest request) throws CreatePetException {
        validate(request);
        var entity = save(request);
        return new Pet(
                entity.id(),
                entity.name(),
                entity.age()
        );
    }

    private void validate(CreatePetRequest request) throws CreatePetException {
        if( request.age() < 0)
            throw new CreatePetException();
    }

    @Transactional
    protected PetEntity save(CreatePetRequest request) {
        var entity = new PetEntity(null, request.name(), request.age());
        return repository.save(entity);
    }
}
