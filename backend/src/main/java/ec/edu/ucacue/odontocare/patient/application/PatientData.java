package ec.edu.ucacue.odontocare.patient.application;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record PatientData(
        @NotBlank @Size(max = 40) String clinicalRecordNumber,
        @NotBlank @Size(max = 255) String identification,
        @NotBlank @Size(max = 120) String responsibleName,
        @NotBlank @Size(max = 120) String firstNames,
        @NotBlank @Size(max = 120) String lastNames,
        @NotNull @PastOrPresent LocalDate birthDate,
        @NotBlank @Size(max = 30) String sex,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Email @Size(max = 160) String email) {

    public int ageOn(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "La fecha de referencia es obligatoria");
        if (referenceDate.isBefore(birthDate)) {
            throw new IllegalArgumentException("La fecha de referencia no puede ser anterior al nacimiento");
        }
        return Period.between(birthDate, referenceDate).getYears();
    }
}

