package ec.edu.ucacue.odontocare.shared.presentation;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ec.edu.ucacue.odontocare.patient.application.PatientConflictException;
import ec.edu.ucacue.odontocare.patient.application.PatientNotFoundException;
import ec.edu.ucacue.odontocare.clinicalrecord.application.ClinicalRecordNotFoundException;
import ec.edu.ucacue.odontocare.clinicalrecord.application.ConsultationNotFoundException;
import ec.edu.ucacue.odontocare.clinicalrecord.application.ClinicalLifecycleException;
import ec.edu.ucacue.odontocare.odontogram.application.OdontogramException;
import ec.edu.ucacue.odontocare.treatment.application.TreatmentException;
import ec.edu.ucacue.odontocare.reporting.PdfValidationException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(PatientConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError patientConflict(PatientConflictException exception) {
        return new ApiError(409, "PATIENT_DUPLICATE", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError patientNotFound(PatientNotFoundException exception) {
        return new ApiError(404, "PATIENT_NOT_FOUND", exception.getMessage(), Map.of());
    }

    @ExceptionHandler({ClinicalRecordNotFoundException.class, ConsultationNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError clinicalResourceNotFound(RuntimeException exception) {
        return new ApiError(404, "CLINICAL_RESOURCE_NOT_FOUND", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(ClinicalLifecycleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError clinicalLifecycle(ClinicalLifecycleException exception) {
        return new ApiError(409, exception.getCode(), exception.getMessage(), Map.of());
    }

    @ExceptionHandler(OdontogramException.class)
    org.springframework.http.ResponseEntity<ApiError> odontogram(OdontogramException exception) {
        boolean integrity = "ODONTOGRAM_CATALOG_INTEGRITY".equals(exception.getCode());
        HttpStatus status = integrity ? HttpStatus.CONFLICT
                : exception.getCode().endsWith("NOT_FOUND") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return org.springframework.http.ResponseEntity.status(status)
                .body(new ApiError(status.value(), exception.getCode(), exception.getMessage(), Map.of()));
    }

    @ExceptionHandler(TreatmentException.class)
    org.springframework.http.ResponseEntity<ApiError> treatment(TreatmentException exception) {
        HttpStatus status = exception.getCode().endsWith("NOT_FOUND") ? HttpStatus.NOT_FOUND
                : exception.getCode().contains("EXISTS") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return org.springframework.http.ResponseEntity.status(status)
                .body(new ApiError(status.value(), exception.getCode(), exception.getMessage(), Map.of()));
    }

    @ExceptionHandler(PdfValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError invalidPdfImage(PdfValidationException exception) {
        return new ApiError(400, "PDF_IMAGE_INVALID", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError dataConflict() {
        return new ApiError(409, "DATA_CONFLICT", "Los datos entran en conflicto con un registro existente.", Map.of());
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ApiError invalidCredentials() {
        return new ApiError(401, "INVALID_CREDENTIALS", "Usuario o contraseña incorrectos.", Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError validation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = exception.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null ? "Valor inválido" : error.getDefaultMessage(),
                        (first, ignored) -> first));
        return new ApiError(400, "VALIDATION_ERROR", "Revise los datos enviados.", fields);
    }

    record ApiError(int status, String code, String message, Map<String, String> fields) {}
}
