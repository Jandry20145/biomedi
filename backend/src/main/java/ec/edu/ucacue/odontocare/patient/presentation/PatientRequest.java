package ec.edu.ucacue.odontocare.patient.presentation;

import java.time.LocalDate;
import java.util.Map;

import ec.edu.ucacue.odontocare.patient.domain.ClinicalBackgroundType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record PatientRequest(
        @Size(max = 40) String clinicalRecordNumber,
        @Size(max = 255) String identification,
        @Size(max = 120) String responsibleName,
        @NotBlank @Size(max = 120) String firstNames,
        @NotBlank @Size(max = 120) String lastNames,
        @NotNull @PastOrPresent LocalDate birthDate,
        @NotBlank @Size(max = 30) String sex,
        @Size(max = 255) String address,
        @Size(max = 30) String phone,
        @Email @Size(max = 160) String email,
        Map<ClinicalBackgroundType, @Size(max = 5000) String> backgrounds) {}

