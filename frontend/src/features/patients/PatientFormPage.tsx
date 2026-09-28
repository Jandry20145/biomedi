import axios from 'axios'
import { useEffect, useMemo, useState } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { backgroundFields } from './backgroundFields'
import { ClinicHeader } from './ClinicHeader'
import { useAuth } from '../auth/useAuth'
import { createPatient, getPatient, updatePatient } from './patientApi'
import type { PatientInput } from './types'
import './patients.css'

const defaults:PatientInput={identification:'',responsibleName:'',firstNames:'',lastNames:'',birthDate:'',sex:'',address:'',phone:'',email:'',backgrounds:{}}
const validateCedula=(cedula:string|undefined)=>{
  if(!cedula) return true;
  if(!/^\d{10}$/.test(cedula)) return 'La cédula debe tener 10 dígitos numéricos.';
  const prov=parseInt(cedula.substring(0,2),10);if(prov<1||prov>24) return 'Provincia inválida.';
  const coeficientes=[2,1,2,1,2,1,2,1,2];let total=0;
  for(let i=0;i<9;i++){let valor=parseInt(cedula.charAt(i),10)*coeficientes[i];if(valor>9)valor-=9;total+=valor;}
  const verificador=parseInt(cedula.charAt(9),10);const decena=Math.ceil(total/10)*10;
  let calculado=decena-total;if(calculado===10)calculado=0;
  return calculado===verificador||'Cédula inválida (Módulo 10).';
};
export function PatientFormPage(){const {id}=useParams();const editing=Boolean(id);const navigate=useNavigate();const [serverError,setServerError]=useState('');const {register,handleSubmit,reset,control,formState:{errors,isSubmitting}}=useForm<PatientInput>({defaultValues:defaults});
 const {user}=useAuth();const isReception=user?.role==='RECEPCIONISTA';
 useEffect(()=>{if(id)getPatient(id).then(p=>reset({...p,backgrounds:p.backgrounds})).catch(()=>setServerError('No fue posible cargar el paciente.'))},[id,reset]);
 const birthDate=useWatch({control,name:'birthDate'});const age=useMemo(()=>{if(!birthDate)return '';const birth=new Date(`${birthDate}T00:00:00`);const now=new Date();let value=now.getFullYear()-birth.getFullYear();if(now.getMonth()<birth.getMonth()||(now.getMonth()===birth.getMonth()&&now.getDate()<birth.getDate()))value--;return value>=0?String(value):''},[birthDate]);
 const submit=handleSubmit(async input=>{
  setServerError('');
  try {
    const cleanBackgrounds = Object.fromEntries(
      Object.entries(input.backgrounds || {}).filter(([_, v]) => v !== undefined && v !== null && v.trim() !== '')
    );
    const payload = {
      ...input,
      email: input.email?.trim() || null,
      phone: input.phone?.trim() || null,
      identification: input.identification?.trim() || null,
      backgrounds: cleanBackgrounds
    } as unknown as PatientInput;
    const saved = id ? await updatePatient(id, payload) : await createPatient(payload);
    navigate(`/pacientes/${saved.id}/historia-clinica`, {replace:true})
  } catch(error) {
    if(axios.isAxiosError(error) && error.response?.status === 409) setServerError(error.response.data?.message ?? 'Ya existe un paciente con esos datos.');
    else if(axios.isAxiosError(error) && error.response?.data?.errors) setServerError('Error de validación: ' + Object.values(error.response.data.errors).join(', '));
    else setServerError('No fue posible guardar el paciente. Revise la información.');
  }
 })
 const groups=[...new Set(backgroundFields.map(f=>f.group))]
 const basicFields = ['identification','firstNames','lastNames','birthDate','sex','phone'];
 const allFields = ['identification','responsibleName','firstNames','lastNames','birthDate','sex','address','phone','email'];
 const fieldsToShow = isReception ? basicFields : allFields;
 return <><ClinicHeader/><main className="patients-shell form-shell"><div className="page-heading"><div><p className="kicker">{editing?'Actualización':'Nuevo expediente'}</p><h1>{editing?'Editar paciente':'Registrar paciente'}</h1></div><Link to="/pacientes" className="btn btn-outline-secondary">Volver</Link></div>{isReception&&editing&&<div className="alert alert-danger" role="alert">No tiene permisos para modificar la historia clínica.</div>}{serverError&&<div role="alert" className="alert alert-danger">{serverError}</div>}<form onSubmit={submit} noValidate><section className="form-section"><h2>Datos del paciente</h2><div className="form-grid">{[
  ['identification','Identificación (Cédula)'],['responsibleName','Responsable'],['firstNames','Nombres'],['lastNames','Apellidos'],['birthDate','Fecha de nacimiento'],['sex','Sexo'],['address','Dirección'],['phone','Teléfono'],['email','Correo electrónico']
  ].filter(([name]) => fieldsToShow.includes(name)).map(([name,label])=><label key={name} className={name==='address'?'wide':''}>{label}{name === 'sex' ? <select className={`form-select ${errors.sex ? 'is-invalid border-danger' : ''}`} disabled={isReception&&editing} {...register('sex', { required: 'Sexo es obligatorio.' })}><option value="">Seleccione</option><option value="FEMENINO">Femenino</option><option value="MASCULINO">Masculino</option><option value="OTRO">Otro</option></select> : <input type={name==='birthDate'?'date':name==='email'?'email':'text'} max={name==='birthDate'?new Date().toISOString().slice(0,10):undefined} className={`form-control ${errors[name as keyof PatientInput]?'is-invalid border-danger':''}`} disabled={isReception&&editing} maxLength={name==='identification'?10:undefined} onInput={(e) => { if (name === 'identification') e.currentTarget.value = e.currentTarget.value.replace(/\D/g, '').slice(0, 10); else if (name === 'firstNames' || name === 'lastNames') e.currentTarget.value = e.currentTarget.value.replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñ\s]/g, ''); }} {...register(name as keyof Omit<PatientInput,'backgrounds'>,{required:['identification','phone','email','responsibleName','address'].includes(name)?false:`${label} es obligatorio.`, validate: name==='identification'?validateCedula:undefined, pattern: (name==='firstNames'||name==='lastNames')?{value:/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/,message:'No se permiten caracteres especiales o números.'}:undefined})}/>}{errors[name as keyof PatientInput]&&<span className="field-error text-danger" role="alert">{errors[name as keyof PatientInput]?.message as string}</span>}</label>)}<label>Edad calculada<input className="form-control" value={age} readOnly aria-describedby="age-help"/><small id="age-help">Se calcula desde la fecha de nacimiento.</small></label></div></section>
 {!isReception && groups.map(group=><section className="form-section" key={group}><h2>{group}</h2><div className="background-grid">{backgroundFields.filter(f=>f.group===group).map(field=><label key={field.key}>{field.label}{field.key === 'LAST_DENTAL_VISIT' ? <input type="date" className="form-control" disabled={isReception&&editing} {...register(`backgrounds.${field.key}` as const)} /> : <textarea className="form-control" rows={3} disabled={isReception&&editing} {...register(`backgrounds.${field.key}` as const,{maxLength:5000})}/>}</label>)}</div></section>)}<div className="form-submit"><Link className="btn btn-outline-secondary" to="/pacientes">Cancelar</Link>{!(isReception&&editing)&&<button className="btn btn-primary" disabled={isSubmitting} type="submit">{isSubmitting?'Guardando…':'Guardar paciente'}</button>}</div></form></main></>}
