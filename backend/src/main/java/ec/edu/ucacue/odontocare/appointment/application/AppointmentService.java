package ec.edu.ucacue.odontocare.appointment.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ucacue.odontocare.appointment.domain.Appointment;
import ec.edu.ucacue.odontocare.appointment.domain.AppointmentStatus;
import ec.edu.ucacue.odontocare.appointment.infrastructure.AppointmentRepository;
import ec.edu.ucacue.odontocare.appointment.presentation.AppointmentRequest;
import ec.edu.ucacue.odontocare.appointment.presentation.AppointmentResponse;
import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.auth.domain.UserAccount;
import ec.edu.ucacue.odontocare.auth.infrastructure.UserAccountRepository;
import ec.edu.ucacue.odontocare.patient.domain.Patient;
import ec.edu.ucacue.odontocare.patient.infrastructure.PatientRepository;
import ec.edu.ucacue.odontocare.shared.domain.AuditLog;
import ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserAccountRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
            UserAccountRepository userRepository, AuditLogRepository auditLogRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointments(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, UserPrincipal user) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        UserAccount dentist = userRepository.findById(request.dentistId())
                .orElseThrow(() -> new IllegalArgumentException("Odontólogo no encontrado"));

        Appointment appointment = new Appointment(patient, dentist, request.appointmentDate(), request.reason(), user.id());
        appointment = appointmentRepository.save(appointment);

        auditLogRepository.save(new AuditLog(user.id(), "APPOINTMENT_CREATED", "Appointment", appointment.getId(), 
            String.format("{\"patient\":\"%s %s\", \"dentist\":\"%s\", \"date\":\"%s\"}", 
                patient.getFirstNames(), patient.getLastNames(), dentist.getNombres(), appointment.getAppointmentDate().toString())));

        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request, UserPrincipal user) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        UserAccount dentist = userRepository.findById(request.dentistId())
                .orElseThrow(() -> new IllegalArgumentException("Odontólogo no encontrado"));

        appointment.update(request.appointmentDate(), request.reason(), dentist, user.id());
        appointment = appointmentRepository.save(appointment);

        auditLogRepository.save(new AuditLog(user.id(), "APPOINTMENT_UPDATED", "Appointment", appointment.getId(), 
            String.format("{\"dentist\":\"%s\", \"date\":\"%s\"}", 
                dentist.getNombres(), appointment.getAppointmentDate().toString())));

        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse changeStatus(Long id, String status, UserPrincipal user) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        
        AppointmentStatus oldStatus = appointment.getStatus();
        AppointmentStatus newStatus = AppointmentStatus.valueOf(status.toUpperCase());
        appointment.changeStatus(newStatus, user.id());
        appointment = appointmentRepository.save(appointment);

        auditLogRepository.save(new AuditLog(user.id(), "APPOINTMENT_STATUS_CHANGED", "Appointment", appointment.getId(), 
            String.format("{\"status\":\"%s\", \"previousValue\":\"%s\", \"patient\":\"%s %s\"}", 
                newStatus.name(), oldStatus != null ? oldStatus.name() : "N/A", appointment.getPatient().getFirstNames(), appointment.getPatient().getLastNames())));

        return toResponse(appointment);
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFirstNames() + " " + appointment.getPatient().getLastNames(),
                appointment.getPatient().getIdentification(),
                appointment.getDentist().getId(),
                appointment.getDentist().getNombres(),
                appointment.getAppointmentDate(),
                appointment.getReason(),
                appointment.getStatus()
        );
    }
}
