export interface ConsultationInput {
  date:string; time:string; consultationType:string; reason:string; currentIllness:string; observations:string
  vitalSigns:{systolicBp?:number;diastolicBp?:number;heartRate?:number;respiratoryRate?:number;temperatureC?:number;oxygenSaturation?:number;weightKg?:number;heightCm?:number}
  extraoral:{facialSymmetry:string;lymphNodes:string;temporomandibularJoint:string;mouthOpening:string;facialPain:string;lips:string;softTissues:string;observations:string}
  intraoral:{oralMucosa:string;tongue:string;palate:string;mouthFloor:string;gums:string;periodontium:string;oralHygiene:string;occlusion:string;saliva:string;lesions:string;observations:string}
  diagnoses:{description:string;icd10Code:string;affectedTeeth:string;priority:string;isPrimary:boolean}[]
  closing:{treatment:string;evolution:string;recommendations:string;nextControlDate?:string}
}
export interface ConsultationSummary {id:number;consultationDate:string;consultationType:string;lifecycleStatus:'DRAFT'|'CLOSED';reason:string;primaryDiagnosis:string}
export interface DiagnosisView {id:number;description:string;icd10Code?:string;affectedTeeth?:string;priority:string;primary:boolean}
export interface EvolutionView {id:number;description:string;recordedAt:string;dentalPieceId?:number}
export interface Consultation extends Omit<ConsultationInput,'date'|'time'|'diagnoses'> {id:number;clinicalRecordId:number;consultationDate:string;lifecycleStatus:'DRAFT'|'CLOSED';closedAt?:string;closedBy?:number;diagnoses:DiagnosisView[];evolutions:EvolutionView[]}
