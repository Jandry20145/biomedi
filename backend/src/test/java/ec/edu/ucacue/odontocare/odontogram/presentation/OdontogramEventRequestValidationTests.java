package ec.edu.ucacue.odontocare.odontogram.presentation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalSurface;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class OdontogramEventRequestValidationTests {
    private static Validator validator;
    @BeforeAll static void setup() { validator = Validation.buildDefaultValidatorFactory().getValidator(); }

    @Test void rejectsFutureEventDate() {
        var request = new OdontogramEventRequest("TOOTH_36", OdontogramStatus.CARIES,
                Set.of(DentalSurface.OCLUSAL), "DATO DEMOSTRATIVO", LocalDateTime.now().plusDays(1));
        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("eventDate")));
    }

    @Test void requiresAtLeastOneSurface() {
        var request = new OdontogramEventRequest("TOOTH_36", OdontogramStatus.SANA,
                Set.of(), null, LocalDateTime.now().minusMinutes(1));
        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("surfaces")));
    }
}
