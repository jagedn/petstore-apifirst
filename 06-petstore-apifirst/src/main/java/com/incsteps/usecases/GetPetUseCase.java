package com.incsteps.usecases;

import com.incsteps.http.model.PetResponse;
import com.incsteps.repository.PetstoreRepository;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class GetPetUseCase {

    private final PetstoreRepository repository;

    public GetPetUseCase(PetstoreRepository repository) {
        this.repository = repository;
    }

    public Optional<PetResponse> get(Long id){
        return repository.findById(id).map(entity-> new PetResponse(
                entity.id(),
                entity.name(),
                entity.age(),
                entity.status()
        ));
    }

}
