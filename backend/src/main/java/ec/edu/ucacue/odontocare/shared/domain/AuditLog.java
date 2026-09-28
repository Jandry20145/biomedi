package ec.edu.ucacue.odontocare.shared.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(nullable = false, length = 60)
    private String action;
    
    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(columnDefinition = "json")
    private String details;
    
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt = LocalDateTime.now();
    
    protected AuditLog() {}
    
    public AuditLog(Long userId, String action, String entityType, Long entityId, String details) {
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.occurredAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
