package ec.edu.ucacue.odontocare.treatment.presentation;
import static org.junit.jupiter.api.Assertions.assertTrue;import java.time.*;import java.util.Set;import org.junit.jupiter.api.*;import ec.edu.ucacue.odontocare.treatment.domain.*;import jakarta.validation.*;
class TreatmentRequestValidationTests{static Validator validator;@BeforeAll static void setup(){validator=Validation.buildDefaultValidatorFactory().getValidator();}
 @Test void rejectsProcedureWithoutPieces(){var r=new ProcedureRequest(LocalDateTime.now().minusMinutes(1),ProcedureType.PROPHYLAXIS,Set.of(),null,"Prueba",null,null,null,ProcedureStatus.COMPLETED,null);assertTrue(validator.validate(r).stream().anyMatch(v->v.getPropertyPath().toString().equals("pieceCodes")));}
 @Test void rejectsFutureEvolution(){var r=new EvolutionClinicalRequest(LocalDateTime.now().plusDays(1),"Prueba","TOOTH_36",null,EvolutionResult.STABLE,null);assertTrue(validator.validate(r).stream().anyMatch(v->v.getPropertyPath().toString().equals("date")));}
}
