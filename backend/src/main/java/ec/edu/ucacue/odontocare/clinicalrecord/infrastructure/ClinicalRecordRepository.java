package ec.edu.ucacue.odontocare.clinicalrecord.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ucacue.odontocare.clinicalrecord.domain.ClinicalRecord;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {
    Optional<ClinicalRecord> findByPatientId(Long patientId);
}

