package ec.edu.ucacue.odontocare.clinicalrecord.presentation;

import java.time.LocalDateTime;
import java.util.List;

public record ConsultationResponse(Long id, Long clinicalRecordId, LocalDateTime consultationDate,
        String consultationType, String lifecycleStatus, LocalDateTime closedAt, Long closedBy,
        String reason, String currentIllness, String observations,
        ConsultationRequest.VitalSignsInput vitalSigns, ConsultationRequest.ExtraoralInput extraoral,
        ConsultationRequest.IntraoralInput intraoral, List<DiagnosisView> diagnoses,
        ConsultationRequest.ClosingInput closing, List<EvolutionView> evolutions) {
    public record DiagnosisView(Long id, String description, String icd10Code, String affectedTeeth,
            String priority, boolean primary) {}
    public record EvolutionView(Long id, String description, LocalDateTime recordedAt, Long dentalPieceId) {}
}
