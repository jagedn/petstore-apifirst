package com.incsteps.http.api;

import com.incsteps.exceptions.CreatePetException;
import com.incsteps.http.model.CreatePetRequest;
import com.incsteps.http.model.PetResponse;
import com.incsteps.http.model.PetStatus;
import com.incsteps.http.model.UpdatePetRequest;
import com.incsteps.usecases.CreatePetUseCase;
import com.incsteps.usecases.DeletePetUseCase;
import com.incsteps.usecases.GetPetUseCase;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.utils.SecurityService;
import jakarta.validation.Valid;

import java.util.List;

@Controller
public class PetsController implements PetsApi{

    private final CreatePetUseCase createPetUseCase;
    private final DeletePetUseCase deletePetUseCase;
    private final GetPetUseCase getPetUseCase;

    public PetsController(CreatePetUseCase createPetUseCase,
                          DeletePetUseCase deletePetUseCase,
                          GetPetUseCase getPetUseCase, SecurityService securityService) {
        this.createPetUseCase = createPetUseCase;
        this.deletePetUseCase = deletePetUseCase;
        this.getPetUseCase = getPetUseCase;
    }


    @Override
    public HttpResponse<@Valid PetResponse> createPet(CreatePetRequest createPetRequest) {
        try {
            return HttpResponse.created(createPetUseCase.create(createPetRequest));
        } catch (CreatePetException e) {
            return HttpResponse.serverError();
        }
    }

    @Override
    public HttpResponse<Void> deletePet(Long id) {
        deletePetUseCase.delete(id);
        return HttpResponse.noContent();
    }

    @Override
    public PetResponse getPetById(Long id) {
        return getPetUseCase.get(id).orElse(null);
    }

    @Override
    public List<@Valid PetResponse> getPets(PetStatus status) {
        return List.of();
    }

    @Override
    public PetResponse updatePet(Long id, UpdatePetRequest updatePetRequest) {
        return null;
    }
}
