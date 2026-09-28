package ec.edu.ucacue.odontocare.appointment.presentation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ucacue.odontocare.appointment.application.AppointmentService;
import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @Operation(summary = "Obtener citas en un rango de fechas")
    public List<AppointmentResponse> getAppointments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return appointmentService.getAppointments(start, end);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Obtener citas de un paciente")
    public List<AppointmentResponse> getPatientAppointments(@PathVariable Long patientId) {
        return appointmentService.getPatientAppointments(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nueva cita")
    public AppointmentResponse createAppointment(@Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return appointmentService.createAppointment(request, user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita")
    public AppointmentResponse updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return appointmentService.updateAppointment(id, request, user);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de la cita")
    public AppointmentResponse changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal user) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("El estado es requerido");
        }
        return appointmentService.changeStatus(id, status, user);
    }
}
