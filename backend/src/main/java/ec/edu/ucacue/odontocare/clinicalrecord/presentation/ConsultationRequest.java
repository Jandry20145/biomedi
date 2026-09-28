package ec.edu.ucacue.odontocare.clinicalrecord.presentation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConsultationRequest(
        @NotNull LocalDate date, @NotNull LocalTime time, @NotBlank String consultationType,
        @NotBlank String reason, String currentIllness, String observations,
        @Valid VitalSignsInput vitalSigns, @Valid ExtraoralInput extraoral,
        @Valid IntraoralInput intraoral, @Valid java.util.List<DiagnosisInput> diagnoses,
        @Valid ClosingInput closing) {
    public record VitalSignsInput(Integer systolicBp, Integer diastolicBp, Integer heartRate,
            Integer respiratoryRate, BigDecimal temperatureC, BigDecimal oxygenSaturation,
            BigDecimal weightKg, BigDecimal heightCm) {}
    public record ExtraoralInput(String facialSymmetry, String lymphNodes, String temporomandibularJoint,
            String mouthOpening, String facialPain, String lips, String softTissues, String observations) {}
    public record IntraoralInput(String oralMucosa, String tongue, String palate, String mouthFloor,
            String gums, String periodontium, String oralHygiene, String occlusion, String saliva,
            String lesions, String observations) {}
    public record DiagnosisInput(@NotBlank String description, String icd10Code,
            String affectedTeeth, @NotBlank String priority, boolean isPrimary) {}
    public record ClosingInput(String treatment, String evolution, String recommendations,
            @FutureOrPresent LocalDate nextControlDate) {}
}

