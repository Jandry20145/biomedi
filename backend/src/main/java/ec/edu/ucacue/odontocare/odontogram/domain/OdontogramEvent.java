package ec.edu.ucacue.odontocare.odontogram.domain;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import ec.edu.ucacue.odontocare.clinicalrecord.domain.DentalConsultation;
import jakarta.persistence.*;

@Entity
@Table(name = "odontogram_events")
public class OdontogramEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "consultation_id") private DentalConsultation consultation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "dental_piece_id") private DentalPiece piece;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "status_id") private OdontogramStatusCatalog status;
    @Column(columnDefinition = "TEXT") private String observations;
    @Column(name = "event_date", nullable = false) private LocalDateTime eventDate;
    @Column(name = "created_by", nullable = false) private Long createdBy;
    @ManyToMany
    @JoinTable(name = "odontogram_event_surfaces", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "surface_id"))
    @OrderBy("id ASC")
    private Set<OdontogramSurface> surfaces = new LinkedHashSet<>();

    protected OdontogramEvent() {}
    public OdontogramEvent(DentalConsultation consultation, DentalPiece piece, OdontogramStatusCatalog status,
            String observations, LocalDateTime eventDate, Long createdBy, Set<OdontogramSurface> surfaces) {
        this.consultation = consultation; this.piece = piece; this.status = status; this.observations = observations;
        this.eventDate = eventDate; this.createdBy = createdBy; this.surfaces.addAll(surfaces);
    }
    public Long getId() { return id; }
    public DentalConsultation getConsultation() { return consultation; }
    public DentalPiece getPiece() { return piece; }
    public OdontogramStatusCatalog getStatus() { return status; }
    public String getObservations() { return observations; }
    public LocalDateTime getEventDate() { return eventDate; }
    public Long getCreatedBy() { return createdBy; }
    public Set<OdontogramSurface> getSurfaces() { return surfaces; }
}
