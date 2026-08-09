package com.incsteps.http;

import com.incsteps.dtos.CreatePetRequest;
import com.incsteps.dtos.Pet;
import com.incsteps.exceptions.CreatePetException;
import com.incsteps.usecases.CreatePetUseCase;
import com.incsteps.usecases.DeletePetUseCase;
import com.incsteps.usecases.GetPetUseCase;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Controller("/pets")
@Tag(name = "Pets", description = "Operaciones sobre el catálogo de mascotas")
public class PetstoreController {

    private final GetPetUseCase getPetUseCase;
    private final CreatePetUseCase createPetUseCase;
    private final DeletePetUseCase deletePetUseCase;

    public PetstoreController(GetPetUseCase getPetUseCase,
                              CreatePetUseCase createPetUseCase,
                              DeletePetUseCase deletePetUseCase) {
        this.getPetUseCase = getPetUseCase;
        this.createPetUseCase = createPetUseCase;
        this.deletePetUseCase = deletePetUseCase;
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Obtener mascota por ID",
            description = "Devuelve los detalles de una mascota concreta dada su clave primaria."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Mascota encontrada",
            content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "ID suministrado no válido",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Mascota no encontrada",
            content = @Content
    )
    public HttpResponse<Pet> get(
            @Parameter(description = "Identificador único de la mascota", required = true)
            @PathVariable("id")
            @NotNull
            @Positive Long id
    ) {
        return getPetUseCase.get(id).map(HttpResponse::ok).orElse(HttpResponse.notFound());
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Registrar una nueva mascota",
            description = "Crea una nueva mascota en el catálogo con los datos proporcionados."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Mascota creada con éxito",
            content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Petición no válida (datos faltantes o erróneos)",
            content = @Content
    )
    public HttpResponse<Pet> create(
            @Parameter(description = "Datos de la mascota a crear", required = true)
            @Body @NotNull @Valid CreatePetRequest request
    ) {
        try {
            Pet created = createPetUseCase.create(request);
            return HttpResponse.created(created);
        } catch (CreatePetException e) {
            return HttpResponse.badRequest();
        }

    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar una mascota",
            description = "Elimina permanentemente una mascota del catálogo por su ID."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Mascota eliminada correctamente"
    )
    @ApiResponse(
            responseCode = "400",
            description = "ID suministrado no válido",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Mascota no encontrada",
            content = @Content
    )
    public HttpResponse<Void> delete(
            @Parameter(description = "Identificador único de la mascota a eliminar", required = true)
            @PathVariable("id")
            @NotNull
            @Positive Long id
    ) {
        deletePetUseCase.delete(id);
        return HttpResponse.noContent();
    }
}