package com.incsteps.usecases;

import com.incsteps.exceptions.CreatePetException;
import com.incsteps.http.model.CreatePetRequest;
import com.incsteps.http.model.PetResponse;
import com.incsteps.http.model.PetStatus;
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

    public PetResponse create(CreatePetRequest request) throws CreatePetException {
        validate(request);
        var entity = save(request);
        return new PetResponse(entity.id(),entity.name(),entity.age(), entity.status());
    }

    private void validate(CreatePetRequest request) throws CreatePetException {
        if( request.getAge() < 0)
            throw new CreatePetException();
    }

    @Transactional
    protected PetEntity save(CreatePetRequest request) {
        var entity = new PetEntity(null, request.getName(), request.getAge(), PetStatus.AVAILABLE);
        return repository.save(entity);
    }
}
