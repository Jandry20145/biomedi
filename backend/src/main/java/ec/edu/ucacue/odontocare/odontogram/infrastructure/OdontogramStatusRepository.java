package ec.edu.ucacue.odontocare.odontogram.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramStatusCatalog;

public interface OdontogramStatusRepository extends JpaRepository<OdontogramStatusCatalog, Long> {
    Optional<OdontogramStatusCatalog> findByCodeAndActiveTrue(String code);
}
