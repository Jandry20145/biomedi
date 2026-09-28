package ec.edu.ucacue.odontocare.clinicalrecord.infrastructure;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ec.edu.ucacue.odontocare.clinicalrecord.domain.Icd10Code;

public interface Icd10CodeRepository extends JpaRepository<Icd10Code, String> {
    
    @Query("SELECT i FROM Icd10Code i WHERE LOWER(i.code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY i.code ASC")
    List<Icd10Code> search(@Param("query") String query, Pageable pageable);
}
