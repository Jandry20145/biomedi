package ec.edu.ucacue.odontocare.clinicalrecord.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ec.edu.ucacue.odontocare.clinicalrecord.presentation.ConsultationRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity @Table(name="dental_consultations")
public class DentalConsultation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="clinical_record_id") private ClinicalRecord clinicalRecord;
 @Column(name="consultation_date",nullable=false) private LocalDateTime consultationDate;
 @Column(name="consultation_type",nullable=false) private String consultationType;
 @Enumerated(EnumType.STRING) @Column(name="lifecycle_status",nullable=false) private ConsultationLifecycleStatus lifecycleStatus;
 @Column(nullable=false,columnDefinition="TEXT") private String reason;
 @Column(name="current_illness") private String currentIllness; private String observations;
 @Column(name="facial_symmetry") private String facialSymmetry; @Column(name="lymph_nodes") private String lymphNodes;
 @Column(name="temporomandibular_joint") private String temporomandibularJoint; @Column(name="mouth_opening") private String mouthOpening;
 @Column(name="facial_pain") private String facialPain; private String lips; @Column(name="soft_tissues") private String softTissues;
 @Column(name="extraoral_observations") private String extraoralObservations; @Column(name="oral_mucosa") private String oralMucosa;
 private String tongue; private String palate; @Column(name="mouth_floor") private String mouthFloor; private String gums;
 private String periodontium; @Column(name="oral_hygiene") private String oralHygiene; private String occlusion; private String saliva; private String lesions;
 @Column(name="intraoral_observations") private String intraoralObservations; private String treatment; private String medication;
 private String evolution; private String recommendations; @Column(name="next_control_date") private LocalDate nextControlDate;
 @Column(name="created_by",nullable=false) private Long createdBy;
 @Column(name="closed_at") private LocalDateTime closedAt; @Column(name="closed_by") private Long closedBy;
 protected DentalConsultation(){}
 public DentalConsultation(ClinicalRecord record, ConsultationRequest r, Long userId){clinicalRecord=record;consultationDate=LocalDateTime.of(r.date(),r.time());consultationType=r.consultationType();lifecycleStatus=ConsultationLifecycleStatus.DRAFT;reason=r.reason();currentIllness=r.currentIllness();observations=r.observations();var e=r.extraoral();if(e!=null){facialSymmetry=e.facialSymmetry();lymphNodes=e.lymphNodes();temporomandibularJoint=e.temporomandibularJoint();mouthOpening=e.mouthOpening();facialPain=e.facialPain();lips=e.lips();softTissues=e.softTissues();extraoralObservations=e.observations();}var i=r.intraoral();if(i!=null){oralMucosa=i.oralMucosa();tongue=i.tongue();palate=i.palate();mouthFloor=i.mouthFloor();gums=i.gums();periodontium=i.periodontium();oralHygiene=i.oralHygiene();occlusion=i.occlusion();saliva=i.saliva();lesions=i.lesions();intraoralObservations=i.observations();}var c=r.closing();if(c!=null){treatment=c.treatment();evolution=c.evolution();recommendations=c.recommendations();nextControlDate=c.nextControlDate();}createdBy=userId;}
 public Long getId(){return id;} public ClinicalRecord getClinicalRecord(){return clinicalRecord;} public LocalDateTime getConsultationDate(){return consultationDate;} public String getConsultationType(){return consultationType;} public String getReason(){return reason;} public String getCurrentIllness(){return currentIllness;} public String getObservations(){return observations;} public String getFacialSymmetry(){return facialSymmetry;} public String getLymphNodes(){return lymphNodes;} public String getTemporomandibularJoint(){return temporomandibularJoint;} public String getMouthOpening(){return mouthOpening;} public String getFacialPain(){return facialPain;} public String getLips(){return lips;} public String getSoftTissues(){return softTissues;} public String getExtraoralObservations(){return extraoralObservations;} public String getOralMucosa(){return oralMucosa;} public String getTongue(){return tongue;} public String getPalate(){return palate;} public String getMouthFloor(){return mouthFloor;} public String getGums(){return gums;} public String getPeriodontium(){return periodontium;} public String getOralHygiene(){return oralHygiene;} public String getOcclusion(){return occlusion;} public String getSaliva(){return saliva;} public String getLesions(){return lesions;} public String getIntraoralObservations(){return intraoralObservations;} public String getTreatment(){return treatment;} public String getEvolution(){return evolution;} public String getRecommendations(){return recommendations;} public LocalDate getNextControlDate(){return nextControlDate;}
 public Long getCreatedBy(){return createdBy;}
 public ConsultationLifecycleStatus getLifecycleStatus(){return lifecycleStatus;} public LocalDateTime getClosedAt(){return closedAt;} public Long getClosedBy(){return closedBy;}
 public boolean isOpen(){return lifecycleStatus==ConsultationLifecycleStatus.DRAFT;}
 public void close(Long userId){if(!isOpen())throw new IllegalStateException("La consulta ya está cerrada");lifecycleStatus=ConsultationLifecycleStatus.CLOSED;closedAt=LocalDateTime.now();closedBy=userId;}
}
