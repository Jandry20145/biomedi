import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { closeConsultation, updateVitalSigns } from './consultationApi'
import type { Consultation, ConsultationInput } from './types'
import './open-consultation.css'

export function OpenConsultationActions({data,role,onUpdated}:{data:Consultation;role:string;onUpdated:()=>Promise<void>}){
 const {register,handleSubmit,formState:{isSubmitting}}=useForm<ConsultationInput['vitalSigns']>({defaultValues:data.vitalSigns})
 const [message,setMessage]=useState('')
 const save=handleSubmit(async values=>{await updateVitalSigns(data.clinicalRecordId,data.id,values);setMessage('Signos vitales guardados en la consulta abierta.');await onUpdated()})
 const close=async()=>{if(!window.confirm('¿Cerrar esta consulta? Después no podrá modificarse.'))return;await closeConsultation(data.clinicalRecordId,data.id);await onUpdated()}
 const fields:[keyof ConsultationInput['vitalSigns'],string,string][]=[['systolicBp','Sistólica','mmHg'],['diastolicBp','Diastólica','mmHg'],['heartRate','Frecuencia cardíaca','lpm'],['respiratoryRate','Frecuencia respiratoria','rpm'],['temperatureC','Temperatura','°C'],['oxygenSaturation','Saturación','%'],['weightKg','Peso','kg'],['heightCm','Talla','cm']]
 return <section className="open-consultation-card"><div className="open-consultation-intro"><p className="kicker">Captura preliminar</p><h3>Signos vitales</h3><p>Disponible para Auxiliar y Odontólogo mientras la consulta permanezca abierta.</p></div><form onSubmit={save}>{fields.map(([name,label,unit])=><label key={name}><span>{label}<small>{unit}</small></span><input type="number" step="0.01" {...register(name,{valueAsNumber:true})}/></label>)}<div className="open-actions"><button className="btn btn-outline-primary" disabled={isSubmitting}>Guardar signos vitales</button>{role==='ODONTOLOGO'&&<button type="button" className="btn btn-primary" onClick={close}>Cerrar consulta</button>}</div>{message&&<p role="status">{message}</p>}</form></section>
}
