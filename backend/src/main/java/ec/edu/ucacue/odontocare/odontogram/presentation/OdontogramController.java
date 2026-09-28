package ec.edu.ucacue.odontocare.odontogram.presentation;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.odontogram.application.OdontogramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clinical-records/{clinicalRecordId}")
@Tag(name = "Odontograma", description = "Historial clínico append-only de las 36 piezas dentales")
@SecurityRequirement(name = "bearerAuth")
public class OdontogramController {
    private final OdontogramService service;
    public OdontogramController(OdontogramService service) { this.service = service; }

    @GetMapping("/odontogram/current")
    @Operation(summary = "Obtener el odontograma actual", description = "Devuelve exactamente 36 piezas; currentEvent es el último evento o null.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Proyección actual obtenida"),
            @ApiResponse(responseCode = "404", description = "Historia clínica inexistente"),
            @ApiResponse(responseCode = "409", description = "Catálogo de 36 piezas inconsistente")})
    public List<CurrentOdontogramPieceResponse> current(@PathVariable Long clinicalRecordId) {
        return service.current(clinicalRecordId);
    }

    @GetMapping("/odontogram/consultations/{consultationId}")
    @Operation(summary = "Obtener el odontograma registrado en una consulta")
    public List<OdontogramEventResponse> byConsultation(@PathVariable Long clinicalRecordId,
            @PathVariable Long consultationId) {
        return service.byConsultation(clinicalRecordId, consultationId);
    }

    @GetMapping("/odontogram/pieces/{pieceCode}/history")
    @Operation(summary = "Consultar la secuencia histórica de una pieza")
    public List<OdontogramEventResponse> history(@PathVariable Long clinicalRecordId,
            @PathVariable String pieceCode) {
        return service.pieceHistory(clinicalRecordId, pieceCode);
    }

    @PostMapping("/consultations/{consultationId}/odontogram/events")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un evento del odontograma", description = "Inserta un evento inmutable asociado a una consulta de la misma HCE.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Evento creado"),
            @ApiResponse(responseCode = "400", description = "Estado, superficie o fecha inválidos"),
            @ApiResponse(responseCode = "403", description = "Rol sin permiso"),
            @ApiResponse(responseCode = "404", description = "HCE, consulta o pieza inexistente")})
    public OdontogramEventResponse create(@PathVariable Long clinicalRecordId,
            @PathVariable Long consultationId, @Valid @RequestBody OdontogramEventRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return service.create(clinicalRecordId, consultationId, request, user);
    }
}
