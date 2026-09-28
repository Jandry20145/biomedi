package ec.edu.ucacue.odontocare.appointment.domain;

import java.time.LocalDateTime;

import ec.edu.ucacue.odontocare.auth.domain.UserAccount;
import ec.edu.ucacue.odontocare.patient.domain.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentist_id", nullable = false)
    private UserAccount dentist;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    protected Appointment() {}

    public Appointment(Patient patient, UserAccount dentist, LocalDateTime appointmentDate, String reason, Long createdBy) {
        this.patient = patient;
        this.dentist = dentist;
        this.appointmentDate = appointmentDate;
        this.reason = reason;
        this.createdBy = createdBy;
        this.status = AppointmentStatus.PENDING;
    }

    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public UserAccount getDentist() { return dentist; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getReason() { return reason; }
    public AppointmentStatus getStatus() { return status; }
    public Long getCreatedBy() { return createdBy; }

    public void update(LocalDateTime appointmentDate, String reason, UserAccount dentist, Long updatedBy) {
        this.appointmentDate = appointmentDate;
        this.reason = reason;
        this.dentist = dentist;
        this.updatedBy = updatedBy;
    }

    public void changeStatus(AppointmentStatus newStatus, Long updatedBy) {
        this.status = newStatus;
        this.updatedBy = updatedBy;
    }
}
