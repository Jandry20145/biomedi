package ec.edu.ucacue.odontocare.odontogram.infrastructure;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramSurface;

public interface OdontogramSurfaceRepository extends JpaRepository<OdontogramSurface, Long> {
    List<OdontogramSurface> findByCodeInAndActiveTrue(Collection<String> codes);
}
