package ec.edu.ucacue.odontocare.patient.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class PatientDataTests {

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void rejectsFutureBirthDate() {
        PatientData patient = validPatient(LocalDate.now().plusDays(1));

        assertTrue(validator.validate(patient).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("birthDate")));
    }

    @Test
    void calculatesAgeAtReferenceDate() {
        PatientData patient = validPatient(LocalDate.of(2000, 6, 20));

        assertEquals(25, patient.ageOn(LocalDate.of(2026, 6, 18)));
    }

    private PatientData validPatient(LocalDate birthDate) {
        return new PatientData(
                "TEST-HC-001",
                "TEST-ID-001",
                "RESPONSABLE DE PRUEBA",
                "PACIENTE",
                "DE PRUEBA",
                birthDate,
                "TEST",
                "DIRECCIÓN DE PRUEBA",
                "0000000000",
                "patient@example.test");
    }
}

