package ec.edu.ucacue.odontocare.odontogram.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramEvent;

public interface OdontogramEventRepository extends JpaRepository<OdontogramEvent, Long> {
    @EntityGraph(attributePaths = {"piece", "status", "surfaces"})
    List<OdontogramEvent> findByConsultationClinicalRecordIdOrderByEventDateAscIdAsc(Long clinicalRecordId);

    @EntityGraph(attributePaths = {"piece", "status", "surfaces"})
    List<OdontogramEvent> findByConsultationIdOrderByEventDateAscIdAsc(Long consultationId);

    @EntityGraph(attributePaths = {"piece", "status", "surfaces"})
    List<OdontogramEvent> findByConsultationClinicalRecordIdAndPieceInternalCodeOrderByEventDateAscIdAsc(
            Long clinicalRecordId, String internalCode);

    @org.springframework.data.jpa.repository.Query("SELECT s.name, COUNT(e) FROM OdontogramEvent e JOIN e.status s GROUP BY s.name")
    List<Object[]> countEventsByStatus();
}
