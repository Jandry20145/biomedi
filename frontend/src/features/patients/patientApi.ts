import { apiClient } from '../../shared/http/apiClient'
import type { Patient, PatientInput, PatientPage } from './types'

export interface PatientFilters { clinicalRecordNumber?:string; identification?:string; firstNames?:string; lastNames?:string; sex?:string; phone?:string; dentistId?:string|number; page:number; size:number }
export async function searchPatients(filters:PatientFilters){ return (await apiClient.get<PatientPage>('/patients',{params:filters})).data }
export function getReportUrl(filters:Partial<PatientFilters>){ const p=new URLSearchParams(); Object.entries(filters).forEach(([k,v])=>{if(v)p.append(k,String(v))}); return `${apiClient.defaults.baseURL}/patients/report/pdf?${p.toString()}` }
export async function getPatient(id:string|number){ return (await apiClient.get<Patient>(`/patients/${id}`)).data }
export async function getClinicalRecord(id:string|number){ return (await apiClient.get<Patient>(`/patients/${id}/clinical-record`)).data }
export async function createPatient(input:PatientInput){ return (await apiClient.post<Patient>('/patients',input)).data }
export async function updatePatient(id:string|number,input:PatientInput){ return (await apiClient.put<Patient>(`/patients/${id}`,input)).data }
export async function deletePatient(id:string|number){ await apiClient.delete(`/patients/${id}`) }

