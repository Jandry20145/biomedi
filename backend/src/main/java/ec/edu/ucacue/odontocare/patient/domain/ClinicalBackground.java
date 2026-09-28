package ec.edu.ucacue.odontocare.patient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "clinical_backgrounds")
public class ClinicalBackground {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "patient_id")
    private Patient patient;
    @Enumerated(EnumType.STRING) @Column(name = "background_type", nullable = false)
    private ClinicalBackgroundType type;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    private boolean active = true;
    @Column(name = "created_by") private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;

    protected ClinicalBackground() {}
    public ClinicalBackground(Patient patient, ClinicalBackgroundType type, String description, Long userId) {
        this.patient = patient; this.type = type; this.description = description.trim(); this.createdBy = userId; this.updatedBy = userId;
    }
    public void updateDescription(String description, Long userId) { this.description = description.trim(); this.active = true; this.updatedBy = userId; }
    public ClinicalBackgroundType getType() { return type; } public String getDescription() { return description; }
}

