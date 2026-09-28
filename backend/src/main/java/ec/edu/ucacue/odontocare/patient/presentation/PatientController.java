package ec.edu.ucacue.odontocare.patient.presentation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.patient.application.PatientService;
import ec.edu.ucacue.odontocare.shared.domain.AuditLog;
import ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import ec.edu.ucacue.odontocare.reporting.ClinicalPdfService;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService service;
    private final ClinicalPdfService pdfService;
    private final AuditLogRepository auditLogRepository;
    public PatientController(PatientService service, ClinicalPdfService pdfService, AuditLogRepository auditLogRepository) { 
        this.service = service; 
        this.pdfService = pdfService; 
        this.auditLogRepository = auditLogRepository; 
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear paciente y su historia clínica")
    public PatientResponse create(@Valid @RequestBody PatientRequest request,
            @AuthenticationPrincipal UserPrincipal user) { return service.create(request, user); }

    @GetMapping
    @Operation(summary = "Buscar pacientes con filtros y paginación")
    public PageResponse<PatientResponse> search(
            @RequestParam(required = false) String clinicalRecordNumber,
            @RequestParam(required = false) String identification,
            @RequestParam(required = false) String firstNames,
            @RequestParam(required = false) String lastNames,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long dentistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(page, 0); int safeSize = Math.min(Math.max(size, 1), 100);
        return PageResponse.from(service.search(clinicalRecordNumber, identification, firstNames, lastNames,
                sex, phone, dentistId, PageRequest.of(safePage, safeSize, Sort.by("lastNames", "firstNames").ascending())));
    }

    @GetMapping("/{id}") @Operation(summary = "Obtener paciente")
    public PatientResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping("/{id}/clinical-record") @Operation(summary = "Obtener la historia clínica inicial del paciente")
    public PatientResponse clinicalRecord(@PathVariable Long id) { return service.get(id); }

    @PutMapping("/{id}") @Operation(summary = "Editar paciente y antecedentes")
    public PatientResponse update(@PathVariable Long id, @Valid @RequestBody PatientRequest request,
            @AuthenticationPrincipal UserPrincipal user) { return service.update(id, request, user); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar paciente")
    public void deactivate(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) { service.deactivate(id, user); }

    @GetMapping(value = "/report/pdf", produces = "application/pdf")
    @Operation(summary = "Generar reporte en PDF del listado de pacientes")
    public byte[] generatePdfReport(
            @RequestParam(required = false) String clinicalRecordNumber,
            @RequestParam(required = false) String identification,
            @RequestParam(required = false) String firstNames,
            @RequestParam(required = false) String lastNames,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long dentistId) {
        var patientsPage = service.search(clinicalRecordNumber, identification, firstNames, lastNames, sex, phone, dentistId, PageRequest.of(0, 1000, Sort.by("lastNames", "firstNames").ascending()));
        auditLogRepository.save(new AuditLog(null, "REPORT_GENERATED", "Patient", null, "{}"));
        return pdfService.generatePatientListReport(patientsPage.getContent());
    }
}

