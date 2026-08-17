package com.incsteps.usecases;

import com.incsteps.exceptions.CreatePetException;
import com.incsteps.http.model.CreatePetRequest;
import com.incsteps.http.model.PetResponse;
import com.incsteps.http.model.PetStatus;
import com.incsteps.repository.PetEntity;
import com.incsteps.repository.PetstoreRepository;
import io.pokeapi.api.PokemonApi;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Random;

@Singleton
public class CreatePetUseCase {

    private final PetstoreRepository repository;
    private final PokemonApi pokemonApi;

    public CreatePetUseCase(PetstoreRepository repository, PokemonApi pokemonApi) {
        this.repository = repository;
        this.pokemonApi = pokemonApi;
    }

    public PetResponse create(CreatePetRequest request) throws CreatePetException {
        validate(request);
        var rename = rename(request);
        var entity = save(rename);
        return new PetResponse(entity.id(),entity.name(),entity.age(), entity.status());
    }

    private void validate(CreatePetRequest request) throws CreatePetException {
        if( request.getAge() < 0)
            throw new CreatePetException();
    }

    private PetEntity rename(CreatePetRequest request) {
        if( "pokemon".equalsIgnoreCase(request.getName())) {
            var response = pokemonApi.pokemonList(100, 0, null);
            Random r = new Random();
            var e = response.getResults().get(r.nextInt(response.getResults().size()));
            return new PetEntity(null, e.getName(), request.getAge(), PetStatus.AVAILABLE);
        }
        return new PetEntity(null, request.getName(), request.getAge(), PetStatus.AVAILABLE);
    }

    @Transactional
    protected PetEntity save(PetEntity entity) {
        return repository.save(entity);
    }
}
