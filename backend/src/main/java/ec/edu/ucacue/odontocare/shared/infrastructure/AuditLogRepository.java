package ec.edu.ucacue.odontocare.shared.infrastructure;

import ec.edu.ucacue.odontocare.shared.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    long countByAction(String action);
    java.util.List<AuditLog> findAllByOrderByOccurredAtDesc();
}
