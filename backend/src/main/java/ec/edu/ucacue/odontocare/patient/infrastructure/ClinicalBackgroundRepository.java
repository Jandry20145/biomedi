package ec.edu.ucacue.odontocare.patient.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ucacue.odontocare.patient.domain.ClinicalBackground;

public interface ClinicalBackgroundRepository extends JpaRepository<ClinicalBackground, Long> {
    List<ClinicalBackground> findByPatientIdAndActiveTrue(Long patientId);
}

