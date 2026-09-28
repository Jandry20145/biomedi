package ec.edu.ucacue.odontocare.clinicalrecord.presentation;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.clinicalrecord.application.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
@RestController @RequestMapping("/api/clinical-records/{clinicalRecordId}/consultations") public class ConsultationController{
 private final ConsultationService service; public ConsultationController(ConsultationService s){service=s;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Crear consulta dentro de una historia clínica") public ConsultationResponse create(@PathVariable Long clinicalRecordId,@Valid @RequestBody ConsultationRequest request,@AuthenticationPrincipal UserPrincipal user){return service.create(clinicalRecordId,request,user);}
 @GetMapping @Operation(summary="Listar consultas cronológicamente") public List<ConsultationSummary> list(@PathVariable Long clinicalRecordId){return service.list(clinicalRecordId);}
 @GetMapping("/{consultationId}") @Operation(summary="Ver detalle de consulta") public ConsultationResponse detail(@PathVariable Long clinicalRecordId,@PathVariable Long consultationId){return service.detail(clinicalRecordId,consultationId);}
 @PutMapping("/{consultationId}/vital-signs") @Operation(summary="Registrar signos vitales de una consulta abierta") public ConsultationResponse vitalSigns(@PathVariable Long clinicalRecordId,@PathVariable Long consultationId,@Valid @RequestBody ConsultationRequest.VitalSignsInput request,@AuthenticationPrincipal UserPrincipal user){return service.updateVitalSigns(clinicalRecordId,consultationId,request,user);}
 @PostMapping("/{consultationId}/close") @Operation(summary="Cerrar de forma irreversible una consulta") public ConsultationResponse close(@PathVariable Long clinicalRecordId,@PathVariable Long consultationId,@AuthenticationPrincipal UserPrincipal user){return service.close(clinicalRecordId,consultationId,user);}
 @PostMapping("/{consultationId}/evolutions") @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Agregar evolución a una consulta abierta") public ConsultationResponse evolution(@PathVariable Long clinicalRecordId,@PathVariable Long consultationId,@Valid @RequestBody EvolutionRequest request,@AuthenticationPrincipal UserPrincipal user){return service.addEvolution(clinicalRecordId,consultationId,request,user);}
}
