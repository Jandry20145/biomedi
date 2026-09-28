package ec.edu.ucacue.odontocare.patient.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ec.edu.ucacue.odontocare.patient.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    boolean existsByIdentificationIgnoreCase(String identification);
    boolean existsByClinicalRecordNumberIgnoreCase(String clinicalRecordNumber);
    boolean existsByIdentificationIgnoreCaseAndIdNot(String identification, Long id);
    boolean existsByClinicalRecordNumberIgnoreCaseAndIdNot(String clinicalRecordNumber, Long id);
    long countByActiveTrue();
    java.util.List<Patient> findTop5ByActiveTrueOrderByIdDesc();

    @org.springframework.data.jpa.repository.Query("SELECT MAX(CAST(SUBSTRING(p.clinicalRecordNumber, 5) AS int)) FROM Patient p WHERE p.clinicalRecordNumber LIKE 'HCE_%'")
    Integer findMaxHceNumber();
}

