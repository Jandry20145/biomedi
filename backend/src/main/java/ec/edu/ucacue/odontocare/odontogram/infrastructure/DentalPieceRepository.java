package ec.edu.ucacue.odontocare.odontogram.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalPiece;

public interface DentalPieceRepository extends JpaRepository<DentalPiece, Long> {
    List<DentalPiece> findByActiveTrueOrderByVisualOrderAsc();
    Optional<DentalPiece> findByInternalCodeAndActiveTrue(String internalCode);
    long countByActiveTrue();
}
