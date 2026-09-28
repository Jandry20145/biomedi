import axios from 'axios'
import { useEffect, useState } from 'react'
import { useForm, useFieldArray, Controller, type FieldPath } from 'react-hook-form'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ClinicHeader } from '../patients/ClinicHeader'
import { backgroundFields } from '../patients/backgroundFields'
import { getClinicalRecord } from '../patients/patientApi'
import type { Patient } from '../patients/types'
import { createConsultation } from './consultationApi'
import type { ConsultationInput } from './types'
import { useAuth } from '../auth/useAuth'
import { Cie10Autocomplete } from './Cie10Autocomplete'
import './consultations.css'

const today=()=>new Date().toISOString().slice(0,10)
const now=()=>new Date().toTimeString().slice(0,5)
const textFields={
  extraoral:[['facialSymmetry','Simetría facial'],['lymphNodes','Ganglios linfáticos'],['temporomandibularJoint','Articulación temporomandibular'],['mouthOpening','Apertura bucal'],['facialPain','Dolor facial'],['lips','Labios'],['softTissues','Tejidos blandos'],['observations','Observaciones']],
  intraoral:[['oralMucosa','Mucosa oral'],['tongue','Lengua'],['palate','Paladar'],['mouthFloor','Piso de boca'],['gums','Encías'],['periodontium','Periodonto'],['oralHygiene','Higiene oral'],['occlusion','Oclusión'],['saliva','Saliva'],['lesions','Lesiones'],['observations','Observaciones']],
} as const

export function ConsultationFormPage(){
 const {id}=useParams();const navigate=useNavigate();const [patient,setPatient]=useState<Patient|null>(null);const [loadError,setLoadError]=useState('');const [serverError,setServerError]=useState('')
 const {user} = useAuth()
  const {register,control,handleSubmit,setValue,formState:{errors,isSubmitting}}=useForm<ConsultationInput>({defaultValues:{date:today(),time:now(),consultationType:'CONSULTA_GENERAL',vitalSigns:{},extraoral:{facialSymmetry:'',lymphNodes:'',temporomandibularJoint:'',mouthOpening:'',facialPain:'',lips:'',softTissues:'',observations:''},intraoral:{oralMucosa:'',tongue:'',palate:'',mouthFloor:'',gums:'',periodontium:'',oralHygiene:'',occlusion:'',saliva:'',lesions:'',observations:''},diagnoses:[{description:'',icd10Code:'',affectedTeeth:'',priority:'MEDIUM',isPrimary:true}],closing:{treatment:'',evolution:'',recommendations:''}}})
  const { fields, append, remove } = useFieldArray({ control, name: 'diagnoses' });
 useEffect(()=>{if(id)getClinicalRecord(id).then(setPatient).catch(()=>setLoadError('No fue posible cargar la historia clínica.'))},[id])
 const submit=handleSubmit(async values=>{if(!patient)return;setServerError('');try{
    if (String(user?.role) === 'AUXILIAR') {
      values.reason = "Registro preliminar de signos vitales";
      values.diagnoses = [];
      values.extraoral = undefined as any;
      values.intraoral = undefined as any;
      values.closing = undefined as any;
    }
    const saved=await createConsultation(patient.clinicalRecordId,values);
    navigate(`/pacientes/${patient.id}/historia-clinica/odontograma?consultation=${saved.id}&saved=1`,{replace:true})
 }catch(error){setServerError(axios.isAxiosError(error)&&error.response?.data?.message?error.response.data.message:'No fue posible guardar la consulta. Sus datos permanecen en el formulario.') }})
 if(loadError)return <><ClinicHeader/><main className="patients-shell"><div className="alert alert-danger">{loadError}</div></main></>
 if(!patient)return <><ClinicHeader/><main className="patients-shell">Cargando historia clínica…</main></>
 return <><ClinicHeader/><main className="patients-shell consultation-form-shell"><header className="consultation-heading"><div><p className="kicker">HCE {patient.clinicalRecordNumber}</p><h1>Nueva consulta odontológica</h1><p>{patient.firstNames} {patient.lastNames} · {patient.identification} · {patient.age} años</p></div><Link className="btn btn-outline-secondary" to={`/pacientes/${patient.id}/historia-clinica`}>Cancelar</Link></header>
 <details className="persistent-context"><summary>Datos y antecedentes persistentes del paciente</summary><div className="background-list">{backgroundFields.map(f=><div key={f.key}><h3>{f.label}</h3><p>{patient.backgrounds[f.key]||'Sin información registrada.'}</p></div>)}</div></details>
 {serverError&&<div className="alert alert-danger" role="alert">{serverError}</div>}
 <form onSubmit={submit} noValidate>
 <section className="form-section"><h2>1. Información general</h2><div className="form-grid">
  <label>Fecha<input className="form-control" type="date" {...register('date',{required:'La fecha es obligatoria'})}/>{errors.date&&<span className="field-error">{errors.date.message}</span>}</label>
  <label>Hora<input className="form-control" type="time" {...register('time',{required:'La hora es obligatoria'})}/></label>
  <label>Tipo de atención<select className="form-select" {...register('consultationType',{required:true})}><option value="CONSULTA_GENERAL">Consulta general</option><option value="URGENCIA">Urgencia</option><option value="CONTROL">Control</option><option value="PREVENTIVA">Preventiva</option></select></label>
  {String(user?.role) !== 'AUXILIAR' && (
      <>
      <label className="wide">Motivo de consulta<textarea className="form-control" {...register('reason',{required: String(user?.role) !== 'AUXILIAR' ? 'El motivo es obligatorio' : false})}/>{errors.reason&&<span className="field-error">{errors.reason.message}</span>}</label>
      <label className="wide">Enfermedad actual<textarea className="form-control" {...register('currentIllness')}/></label>
      <label className="wide">Observaciones<textarea className="form-control" {...register('observations')}/></label>
      </>
  )}
  {String(user?.role) === 'AUXILIAR' && (
      <input type="hidden" value="Registro preliminar de signos vitales" />
  )}
  </div></section>
  <section className="form-section"><h2>2. Signos vitales</h2><div className="form-grid">{([['systolicBp','Presión sistólica','mmHg'],['diastolicBp','Presión diastólica','mmHg'],['heartRate','Frecuencia cardíaca','lpm'],['respiratoryRate','Frecuencia respiratoria','rpm'],['temperatureC','Temperatura','°C'],['oxygenSaturation','Saturación de oxígeno','%'],['weightKg','Peso','kg'],['heightCm','Talla','cm']] as const).map(([key,label,unit])=><label key={key}>{label}<div className="input-group"><input className="form-control" type="text" onInput={(e)=>{e.currentTarget.value=e.currentTarget.value.replace(/[^0-9.,]/g,'')}} {...register(`vitalSigns.${key}`,{setValueAs: v => v === '' || v == null ? null : Number(String(v).replace(',', '.'))})}/><span className="input-group-text">{unit}</span></div></label>)}</div></section>
 {String(user?.role) !== 'AUXILIAR' && (['extraoral','intraoral'] as const).map((section,index)=><section className="form-section" key={section}><h2>{index+3}. Examen {section}</h2><div className="form-grid">{textFields[section].map(([key,label])=><label className={key==='observations'?'wide':''} key={key}>{label}<textarea className="form-control" rows={2} {...register(`${section}.${key}` as FieldPath<ConsultationInput>)}/></label>)}</div></section>)}
 {String(user?.role) !== 'AUXILIAR' && <section className="form-section"><h2>5. Diagnóstico</h2><div className="form-grid">
  {fields.map((field, index) => (
    <div key={field.id} style={{ display: 'contents' }}>
      <div className="wide" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
        <strong>{index === 0 ? 'Diagnóstico Principal' : `Diagnóstico Secundario ${index}`}</strong>
        {index > 0 && <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => remove(index)}>Quitar</button>}
      </div>
      <label className="wide">Descripción del Diagnóstico
        <textarea className="form-control" {...register(`diagnoses.${index}.description` as const)}/>
      </label>
      <label>Código CIE-10
        <Controller
          control={control}
          name={`diagnoses.${index}.icd10Code` as const}
          render={({ field: { value, onChange } }) => (
            <Cie10Autocomplete
              value={value || ''}
              onChange={(code, desc) => {
                onChange(code);
                const currentDesc = control._formValues.diagnoses?.[index]?.description;
                if (!currentDesc && desc) {
                  setValue(`diagnoses.${index}.description` as const, desc, { shouldValidate: true });
                }
              }}
            />
          )}
        />
      </label>
      <label>Piezas afectadas
        <select className="form-select" {...register(`diagnoses.${index}.affectedTeeth` as const)}>
          <option value="">-- Seleccione una pieza (opcional) --</option>
          <option value="TOOTH_02">TOOTH_02 (18 - Tercer Molar Superior Derecho)</option>
          <option value="TOOTH_03">TOOTH_03 (17 - Segundo Molar Superior Derecho)</option>
          <option value="TOOTH_04">TOOTH_04 (16 - Primer Molar Superior Derecho)</option>
          <option value="TOOTH_05">TOOTH_05 (15 - Segundo Premolar Superior Derecho)</option>
          <option value="TOOTH_06">TOOTH_06 (14 - Primer Premolar Superior Derecho)</option>
          <option value="TOOTH_07">TOOTH_07 (13 - Canino Superior Derecho)</option>
          <option value="TOOTH_08">TOOTH_08 (12 - Incisivo Lateral Superior Derecho)</option>
          <option value="TOOTH_09">TOOTH_09 (11 - Incisivo Central Superior Derecho)</option>
          
          <option value="TOOTH_10">TOOTH_10 (21 - Incisivo Central Superior Izquierdo)</option>
          <option value="TOOTH_11">TOOTH_11 (22 - Incisivo Lateral Superior Izquierdo)</option>
          <option value="TOOTH_12">TOOTH_12 (23 - Canino Superior Izquierdo)</option>
          <option value="TOOTH_13">TOOTH_13 (24 - Primer Premolar Superior Izquierdo)</option>
          <option value="TOOTH_14">TOOTH_14 (25 - Segundo Premolar Superior Izquierdo)</option>
          <option value="TOOTH_15">TOOTH_15 (26 - Primer Molar Superior Izquierdo)</option>
          <option value="TOOTH_16">TOOTH_16 (27 - Segundo Molar Superior Izquierdo)</option>
          <option value="TOOTH_17">TOOTH_17 (28 - Tercer Molar Superior Izquierdo)</option>
          
          <option value="TOOTH_20">TOOTH_20 (48 - Tercer Molar Inferior Derecho)</option>
          <option value="TOOTH_21">TOOTH_21 (47 - Segundo Molar Inferior Derecho)</option>
          <option value="TOOTH_22">TOOTH_22 (46 - Primer Molar Inferior Derecho)</option>
          <option value="TOOTH_23">TOOTH_23 (45 - Segundo Premolar Inferior Derecho)</option>
          <option value="TOOTH_24">TOOTH_24 (44 - Primer Premolar Inferior Derecho)</option>
          <option value="TOOTH_25">TOOTH_25 (43 - Canino Inferior Derecho)</option>
          <option value="TOOTH_26">TOOTH_26 (42 - Incisivo Lateral Inferior Derecho)</option>
          <option value="TOOTH_27">TOOTH_27 (41 - Incisivo Central Inferior Derecho)</option>
          
          <option value="TOOTH_28">TOOTH_28 (31 - Incisivo Central Inferior Izquierdo)</option>
          <option value="TOOTH_29">TOOTH_29 (32 - Incisivo Lateral Inferior Izquierdo)</option>
          <option value="TOOTH_30">TOOTH_30 (33 - Canino Inferior Izquierdo)</option>
          <option value="TOOTH_31">TOOTH_31 (34 - Primer Premolar Inferior Izquierdo)</option>
          <option value="TOOTH_32">TOOTH_32 (35 - Segundo Premolar Inferior Izquierdo)</option>
          <option value="TOOTH_33">TOOTH_33 (36 - Primer Molar Inferior Izquierdo)</option>
          <option value="TOOTH_34">TOOTH_34 (37 - Segundo Molar Inferior Izquierdo)</option>
          <option value="TOOTH_35">TOOTH_35 (38 - Tercer Molar Inferior Izquierdo)</option>
        </select>
      </label>
      <label>Prioridad
        <select className="form-select" {...register(`diagnoses.${index}.priority` as const)}>
          <option value="LOW">Baja</option>
          <option value="MEDIUM">Media</option>
          <option value="HIGH">Alta</option>
          <option value="URGENT">Urgente</option>
        </select>
      </label>
    </div>
  ))}
  <div className="wide">
    <button type="button" className="btn btn-outline-primary" onClick={() => append({description: '', icd10Code: '', affectedTeeth: '', priority: 'MEDIUM', isPrimary: false})}>+ Agregar otro diagnóstico</button>
  </div>
 </div></section>}
 {String(user?.role) !== 'AUXILIAR' && <section className="form-section"><h2>6. Cierre clínico</h2><div className="form-grid"><label className="wide">Tratamiento<textarea className="form-control" {...register('closing.treatment')}/></label><label className="wide">Evolución<textarea className="form-control" {...register('closing.evolution')}/></label><label className="wide">Recomendaciones<textarea className="form-control" {...register('closing.recommendations')}/></label><label>Próximo control<input className="form-control" type="date" min={today()} {...register('closing.nextControlDate')}/></label></div></section>}
 <div className="form-submit"><Link className="btn btn-outline-secondary" to={`/pacientes/${patient.id}/historia-clinica`}>Cancelar</Link><button className="btn btn-primary" disabled={isSubmitting}>{isSubmitting?'Guardando consulta…':'Guardar consulta'}</button></div></form></main></>
}
