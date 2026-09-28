package ec.edu.ucacue.odontocare.clinicalrecord.domain;
import java.math.BigDecimal;
import ec.edu.ucacue.odontocare.clinicalrecord.presentation.ConsultationRequest;
import jakarta.persistence.*;
@Entity @Table(name="vital_signs") public class VitalSigns{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="consultation_id") private DentalConsultation consultation;
 @Column(name="systolic_bp") private Short systolicBp;@Column(name="diastolic_bp") private Short diastolicBp;@Column(name="heart_rate") private Short heartRate;@Column(name="respiratory_rate") private Short respiratoryRate;@Column(name="temperature_c") private BigDecimal temperatureC;@Column(name="oxygen_saturation") private BigDecimal oxygenSaturation;@Column(name="weight_kg") private BigDecimal weightKg;@Column(name="height_cm") private BigDecimal heightCm;@Column(name="created_by") private Long createdBy;@Column(name="updated_by") private Long updatedBy;
 protected VitalSigns(){} public VitalSigns(DentalConsultation c,ConsultationRequest.VitalSignsInput v,Long u){consultation=c;systolicBp=shortValue(v.systolicBp());diastolicBp=shortValue(v.diastolicBp());heartRate=shortValue(v.heartRate());respiratoryRate=shortValue(v.respiratoryRate());temperatureC=v.temperatureC();oxygenSaturation=v.oxygenSaturation();weightKg=v.weightKg();heightCm=v.heightCm();createdBy=u;}
 public void update(ConsultationRequest.VitalSignsInput v,Long userId){systolicBp=shortValue(v.systolicBp());diastolicBp=shortValue(v.diastolicBp());heartRate=shortValue(v.heartRate());respiratoryRate=shortValue(v.respiratoryRate());temperatureC=v.temperatureC();oxygenSaturation=v.oxygenSaturation();weightKg=v.weightKg();heightCm=v.heightCm();updatedBy=userId;}
 private static Short shortValue(Integer value){return value==null?null:value.shortValue();}
 private static Integer integerValue(Short value){return value==null?null:value.intValue();}
 public ConsultationRequest.VitalSignsInput view(){return new ConsultationRequest.VitalSignsInput(integerValue(systolicBp),integerValue(diastolicBp),integerValue(heartRate),integerValue(respiratoryRate),temperatureC,oxygenSaturation,weightKg,heightCm);}
}
