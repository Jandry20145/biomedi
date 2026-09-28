import type { BackgroundType } from './types'
export const backgroundFields: {key:BackgroundType;label:string;group:string}[] = [
  {key:'PERSONAL',label:'Antecedentes personales',group:'Generales'},{key:'FAMILY',label:'Antecedentes familiares',group:'Generales'},
  {key:'PATHOLOGICAL',label:'Antecedentes patológicos',group:'Médicos'},{key:'SURGICAL',label:'Antecedentes quirúrgicos',group:'Médicos'},
  {key:'ALLERGY',label:'Alergias',group:'Médicos'},{key:'CURRENT_MEDICATION',label:'Medicación actual',group:'Médicos'},
  {key:'CHRONIC_DISEASES',label:'Enfermedades crónicas',group:'Médicos'},{key:'LAST_DENTAL_VISIT',label:'Última visita odontológica',group:'Odontológicos'},
  {key:'PREVIOUS_DENTAL_TREATMENTS',label:'Tratamientos odontológicos previos',group:'Odontológicos'},
  {key:'PROSTHESIS_USE',label:'Uso de prótesis',group:'Odontológicos'},{key:'ORTHODONTICS_USE',label:'Uso de ortodoncia',group:'Odontológicos'},
  {key:'BRUXISM',label:'Bruxismo',group:'Odontológicos'},{key:'ORAL_HABITS',label:'Hábitos orales',group:'Hábitos e higiene'},
  {key:'ORAL_HYGIENE',label:'Higiene oral',group:'Hábitos e higiene'},{key:'BRUSHING_FREQUENCY',label:'Frecuencia de cepillado',group:'Hábitos e higiene'},
  {key:'DENTAL_FLOSS_USE',label:'Uso de hilo dental',group:'Hábitos e higiene'},{key:'MOUTHWASH_USE',label:'Uso de enjuague bucal',group:'Hábitos e higiene'},
  {key:'DENTAL_OBSERVATIONS',label:'Observaciones odontológicas',group:'Odontológicos'},
]

