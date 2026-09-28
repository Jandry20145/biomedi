package ec.edu.ucacue.odontocare.appointment.presentation;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppointmentRequest(
    @NotNull(message = "El ID del paciente es obligatorio")
    Long patientId,

    @NotNull(message = "El ID del odontólogo es obligatorio")
    Long dentistId,

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    @Future(message = "La fecha de la cita debe ser futura")
    LocalDateTime appointmentDate,

    @NotBlank(message = "El motivo de la cita no puede estar vacío")
    @Size(max = 500, message = "El motivo de la cita no puede exceder 500 caracteres")
    String reason
) {}
