package ec.edu.ucacue.odontocare.clinicalrecord.presentation;

import java.time.LocalDateTime;

public record ConsultationSummary(Long id, LocalDateTime consultationDate, String consultationType,
        String lifecycleStatus, String reason, String primaryDiagnosis) {}
