package ec.edu.ucacue.odontocare.clinicalrecord.infrastructure;
import java.util.List;import java.util.Optional;import org.springframework.data.jpa.repository.JpaRepository;import ec.edu.ucacue.odontocare.clinicalrecord.domain.DentalConsultation;
public interface DentalConsultationRepository extends JpaRepository<DentalConsultation,Long>{
    List<DentalConsultation> findByClinicalRecordIdOrderByConsultationDateDescIdDesc(Long id);
    Optional<DentalConsultation> findByIdAndClinicalRecordId(Long id, Long recordId);
    long countByConsultationDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<DentalConsultation> findByConsultationDateBetweenOrderByConsultationDateAsc(java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<DentalConsultation> findByConsultationDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
