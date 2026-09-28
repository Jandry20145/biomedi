package ec.edu.ucacue.odontocare.appointment.infrastructure;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.edu.ucacue.odontocare.appointment.domain.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDentistIdAndAppointmentDateBetweenOrderByAppointmentDateAsc(Long dentistId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
}
