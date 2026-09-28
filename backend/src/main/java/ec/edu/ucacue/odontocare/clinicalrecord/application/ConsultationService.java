package ec.edu.ucacue.odontocare.clinicalrecord.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.clinicalrecord.domain.*;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.*;
import ec.edu.ucacue.odontocare.clinicalrecord.presentation.*;

@Service public class ConsultationService {
 private final ClinicalRecordRepository records; private final DentalConsultationRepository consultations;
 private final VitalSignsRepository vitals; private final DiagnosisRepository diagnoses; private final DentalEvolutionRepository evolutions;
 private final ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository auditLogRepository;
 public ConsultationService(ClinicalRecordRepository r,DentalConsultationRepository c,VitalSignsRepository v,DiagnosisRepository d,DentalEvolutionRepository e,ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository a){records=r;consultations=c;vitals=v;diagnoses=d;evolutions=e;auditLogRepository=a;}

 @Transactional public ConsultationResponse create(Long recordId,ConsultationRequest request,UserPrincipal user){
  ClinicalRecord record=records.findById(recordId).orElseThrow(()->new ClinicalRecordNotFoundException(recordId));
  DentalConsultation c=consultations.save(new DentalConsultation(record,request,user.id()));
  if(request.vitalSigns()!=null)vitals.save(new VitalSigns(c,request.vitalSigns(),user.id()));
  if(request.diagnoses()!=null) {
      request.diagnoses().forEach(d -> {
          if(d.description()!=null && !d.description().isBlank()) {
              diagnoses.save(new Diagnosis(c, d.description(), d.icd10Code(), d.affectedTeeth(), d.priority(), d.isPrimary(), user.id()));
          }
      });
  }
  if(request.closing()!=null && request.closing().evolution()!=null && !request.closing().evolution().isBlank()) {
      evolutions.save(new DentalEvolution(c,request.closing().evolution(),null,user.id()));
  }
  auditLogRepository.save(new ec.edu.ucacue.odontocare.shared.domain.AuditLog(user.id(), "CONSULTATION_CREATED", "Consultation", c.getId(), "{\"recordId\":" + recordId + "}"));
  return detail(recordId,c.getId());
 }
 @Transactional(readOnly=true) public List<ConsultationSummary> list(Long recordId){requireRecord(recordId);return consultations.findByClinicalRecordIdOrderByConsultationDateDescIdDesc(recordId).stream().map(c->{String primary=diagnoses.findByConsultationIdOrderByPrimaryDescIdAsc(c.getId()).stream().filter(Diagnosis::isPrimary).map(Diagnosis::getDescription).findFirst().orElse("");return new ConsultationSummary(c.getId(),c.getConsultationDate(),c.getConsultationType(),c.getLifecycleStatus().name(),c.getReason(),primary);}).toList();}
 @Transactional(readOnly=true) public ConsultationResponse detail(Long recordId,Long consultationId){
  DentalConsultation c=find(recordId,consultationId);var v=vitals.findByConsultationId(c.getId()).map(VitalSigns::view).orElse(null);
  var ds=diagnoses.findByConsultationIdOrderByPrimaryDescIdAsc(c.getId()).stream().map(d->new ConsultationResponse.DiagnosisView(d.getId(),d.getDescription(),d.getIcd10Code(),d.getAffectedTeeth(),d.getPriority(),d.isPrimary())).toList();
  var es=evolutions.findByConsultationIdOrderByRecordedAtAscIdAsc(c.getId()).stream().map(e->new ConsultationResponse.EvolutionView(e.getId(),e.getDescription(),e.getRecordedAt(),e.getDentalPieceId())).toList();
  return new ConsultationResponse(c.getId(),recordId,c.getConsultationDate(),c.getConsultationType(),c.getLifecycleStatus().name(),c.getClosedAt(),c.getClosedBy(),c.getReason(),c.getCurrentIllness(),c.getObservations(),v,new ConsultationRequest.ExtraoralInput(c.getFacialSymmetry(),c.getLymphNodes(),c.getTemporomandibularJoint(),c.getMouthOpening(),c.getFacialPain(),c.getLips(),c.getSoftTissues(),c.getExtraoralObservations()),new ConsultationRequest.IntraoralInput(c.getOralMucosa(),c.getTongue(),c.getPalate(),c.getMouthFloor(),c.getGums(),c.getPeriodontium(),c.getOralHygiene(),c.getOcclusion(),c.getSaliva(),c.getLesions(),c.getIntraoralObservations()),ds,new ConsultationRequest.ClosingInput(c.getTreatment(),c.getEvolution(),c.getRecommendations(),c.getNextControlDate()),es);
 }
 @Transactional public ConsultationResponse updateVitalSigns(Long recordId,Long consultationId,ConsultationRequest.VitalSignsInput input,UserPrincipal user){DentalConsultation c=find(recordId,consultationId);requireOpen(c);VitalSigns signs=vitals.findByConsultationId(c.getId()).map(existing->{existing.update(input,user.id());return existing;}).orElseGet(()->new VitalSigns(c,input,user.id()));vitals.save(signs);auditLogRepository.save(new ec.edu.ucacue.odontocare.shared.domain.AuditLog(user.id(), "CONSULTATION_UPDATED_VITALS", "Consultation", c.getId(), "{}"));return detail(recordId,consultationId);}
 @Transactional public ConsultationResponse close(Long recordId,Long consultationId,UserPrincipal user){DentalConsultation c=find(recordId,consultationId);requireOpen(c);c.close(user.id());consultations.save(c);auditLogRepository.save(new ec.edu.ucacue.odontocare.shared.domain.AuditLog(user.id(), "CONSULTATION_CLOSED", "Consultation", c.getId(), "{}"));return detail(recordId,consultationId);}
 @Transactional public ConsultationResponse addEvolution(Long recordId,Long consultationId,EvolutionRequest request,UserPrincipal user){DentalConsultation c=find(recordId,consultationId);requireOpen(c);evolutions.save(new DentalEvolution(c,request.description(),request.dentalPieceId(),user.id()));auditLogRepository.save(new ec.edu.ucacue.odontocare.shared.domain.AuditLog(user.id(), "CONSULTATION_EVOLUTION_ADDED", "Consultation", c.getId(), "{}"));return detail(recordId,consultationId);}
 private void requireRecord(Long id){if(!records.existsById(id))throw new ClinicalRecordNotFoundException(id);}
 private DentalConsultation find(Long recordId,Long consultationId){return consultations.findByIdAndClinicalRecordId(consultationId,recordId).orElseThrow(()->new ConsultationNotFoundException(consultationId));}
 private void requireOpen(DentalConsultation c){if(!c.isOpen())throw new ClinicalLifecycleException("CONSULTATION_CLOSED","La consulta está cerrada y no admite cambios.");}
}
