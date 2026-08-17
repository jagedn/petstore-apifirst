package com.incsteps.usecases;

import com.incsteps.repository.PetstoreRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class DeletePetUseCase {

    private final PetstoreRepository repository;

    public DeletePetUseCase(PetstoreRepository repository) {
        this.repository = repository;
    }

    public void delete(Long id){
        deleteById(id);
    }

    @Transactional
    protected void deleteById(Long id){
        repository.deleteById(id);
    }
}
