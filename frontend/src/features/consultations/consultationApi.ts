import { apiClient } from '../../shared/http/apiClient'
import type { Consultation, ConsultationInput, ConsultationSummary } from './types'

const resource=(recordId:number|string)=>`../clinical-records/${recordId}/consultations`
export async function createConsultation(recordId:number|string,input:ConsultationInput){return (await apiClient.post<Consultation>(resource(recordId),input)).data}
export async function listConsultations(recordId:number|string){return (await apiClient.get<ConsultationSummary[]>(resource(recordId))).data}
export async function getConsultation(recordId:number|string,id:number|string){return (await apiClient.get<Consultation>(`${resource(recordId)}/${id}`)).data}
export async function addEvolution(recordId:number|string,id:number|string,description:string,dentalPieceId?:number){return (await apiClient.post<Consultation>(`${resource(recordId)}/${id}/evolutions`,{description,dentalPieceId})).data}
export async function updateVitalSigns(recordId:number|string,id:number|string,input:ConsultationInput['vitalSigns']){return (await apiClient.put<Consultation>(`${resource(recordId)}/${id}/vital-signs`,input)).data}
export async function closeConsultation(recordId:number|string,id:number|string){return (await apiClient.post<Consultation>(`${resource(recordId)}/${id}/close`)).data}
