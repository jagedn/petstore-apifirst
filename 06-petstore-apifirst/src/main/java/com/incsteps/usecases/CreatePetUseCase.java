package com.incsteps.usecases;

import com.incsteps.exceptions.CreatePetException;
import com.incsteps.http.model.CreatePetRequest;
import com.incsteps.http.model.PetResponse;
import com.incsteps.http.model.PetStatus;
import com.incsteps.repository.PetEntity;
import com.incsteps.repository.PetstoreRepository;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class CreatePetUseCase {

    private final PetstoreRepository repository;
    private final SecurityService securityService;

    public CreatePetUseCase(PetstoreRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public PetResponse create(CreatePetRequest request) throws CreatePetException {
        validate(request);
        //demo purpose
        request.setName(request.getName()+"("+securityService.getAuthentication().orElseThrow().getName()+")");
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
