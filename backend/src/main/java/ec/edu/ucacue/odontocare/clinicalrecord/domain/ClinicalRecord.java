package ec.edu.ucacue.odontocare.clinicalrecord.domain;

import ec.edu.ucacue.odontocare.patient.domain.Patient;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "clinical_records")
public class ClinicalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;
    private String status = "ACTIVE";
    @jakarta.persistence.Column(name = "created_by")
    private Long createdBy;

    protected ClinicalRecord() {}
    public ClinicalRecord(Patient patient, Long createdBy) { this.patient = patient; this.createdBy = createdBy; }
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
}
