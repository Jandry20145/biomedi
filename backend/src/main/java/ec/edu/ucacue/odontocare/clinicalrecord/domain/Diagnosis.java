package ec.edu.ucacue.odontocare.clinicalrecord.domain;
import jakarta.persistence.*;
@Entity @Table(name="diagnoses") public class Diagnosis{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;@ManyToOne(fetch=FetchType.LAZY)@JoinColumn(name="consultation_id")private DentalConsultation consultation;private String description;@Column(name="icd10_code")private String icd10Code;@Column(name="affected_teeth")private String affectedTeeth;private String priority;@Column(name="is_primary")private boolean primary;@Column(name="created_by")private Long createdBy;
 protected Diagnosis(){}public Diagnosis(DentalConsultation c,String d,String code,String teeth,String priority,boolean primary,Long user){consultation=c;description=d;icd10Code=code;affectedTeeth=teeth;this.priority=priority;this.primary=primary;createdBy=user;}
 public Long getId(){return id;}public String getDescription(){return description;}public String getIcd10Code(){return icd10Code;}public String getAffectedTeeth(){return affectedTeeth;}public String getPriority(){return priority;}public boolean isPrimary(){return primary;}
}

