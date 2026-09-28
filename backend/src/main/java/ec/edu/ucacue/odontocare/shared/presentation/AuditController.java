package ec.edu.ucacue.odontocare.shared.presentation;

import ec.edu.ucacue.odontocare.shared.domain.AuditLog;
import ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import ec.edu.ucacue.odontocare.auth.infrastructure.UserAccountRepository;
import ec.edu.ucacue.odontocare.auth.domain.UserAccount;
import ec.edu.ucacue.odontocare.patient.infrastructure.PatientRepository;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.ClinicalRecordRepository;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.DentalConsultationRepository;
import ec.edu.ucacue.odontocare.appointment.infrastructure.AppointmentRepository;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Auditoría")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditLogRepository auditLogRepository;
    private final UserAccountRepository userRepository;
    private final PatientRepository patientRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final DentalConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;

    public AuditController(AuditLogRepository auditLogRepository, UserAccountRepository userRepository,
                           PatientRepository patientRepository, ClinicalRecordRepository clinicalRecordRepository,
                           DentalConsultationRepository consultationRepository, AppointmentRepository appointmentRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.consultationRepository = consultationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public record AuditLogResponse(Long id, String userName, String userRole, String action, String entityType, Long entityId, String details, String patientName, LocalDateTime occurredAt) {}

    @GetMapping
    @Operation(summary = "Obtener el registro completo de auditoría")
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogs() {
        return auditLogRepository.findAllByOrderByOccurredAtDesc().stream().map(log -> {
            String userName = "Sistema";
            String userRole = null;
            if (log.getUserId() != null) {
                UserAccount user = userRepository.findById(log.getUserId()).orElse(null);
                if (user != null) {
                    userName = user.getNombres();
                    userRole = user.getRole().getNombre();
                } else {
                    userName = "Usuario #" + log.getUserId();
                }
            }
            String patientName = null;
            if (log.getEntityType() != null && log.getEntityId() != null) {
                try {
                    switch (log.getEntityType()) {
                        case "Patient":
                            patientName = patientRepository.findById(log.getEntityId()).map(p -> p.getFirstNames() + " " + p.getLastNames()).orElse(null);
                            break;
                        case "ClinicalRecord":
                            patientName = clinicalRecordRepository.findById(log.getEntityId()).map(r -> r.getPatient().getFirstNames() + " " + r.getPatient().getLastNames()).orElse(null);
                            break;
                        case "Consultation":
                            patientName = consultationRepository.findById(log.getEntityId()).map(c -> c.getClinicalRecord().getPatient().getFirstNames() + " " + c.getClinicalRecord().getPatient().getLastNames()).orElse(null);
                            break;
                        case "Appointment":
                            patientName = appointmentRepository.findById(log.getEntityId()).map(a -> a.getPatient().getFirstNames() + " " + a.getPatient().getLastNames()).orElse(null);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return new AuditLogResponse(log.getId(), userName, userRole, log.getAction(), log.getEntityType(), log.getEntityId(), log.getDetails(), patientName, log.getOccurredAt());
        }).collect(Collectors.toList());
    }
}
