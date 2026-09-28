package ec.edu.ucacue.odontocare.appointment.presentation;

import java.time.LocalDateTime;

import ec.edu.ucacue.odontocare.appointment.domain.AppointmentStatus;

public record AppointmentResponse(
    Long id,
    Long patientId,
    String patientName,
    String patientIdentification,
    Long dentistId,
    String dentistName,
    LocalDateTime appointmentDate,
    String reason,
    AppointmentStatus status
) {}
