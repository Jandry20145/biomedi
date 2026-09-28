import { apiClient } from '../../shared/http/apiClient'
import type { DentalSurface, OdontogramEvent, OdontogramPiece, OdontogramStatus } from './types'

export interface CreateOdontogramEventRequest {
  pieceCode: string;
  status: OdontogramStatus;
  surfaces: DentalSurface[];
  observations?: string;
}

const record=(id:number|string)=>`../clinical-records/${id}/odontogram`
export async function getCurrentOdontogram(id:number|string){return (await apiClient.get<OdontogramPiece[]>(`${record(id)}/current`)).data}
export async function getConsultationOdontogram(id:number|string,consultationId:number|string){return (await apiClient.get<OdontogramEvent[]>(`${record(id)}/consultations/${consultationId}`)).data}
export async function getPieceHistory(id:number|string,pieceCode:string){return (await apiClient.get<OdontogramEvent[]>(`${record(id)}/pieces/${pieceCode}/history`)).data}

export async function createOdontogramEvent(clinicalRecordId: number | string, consultationId: number | string, data: CreateOdontogramEventRequest) {
  return (await apiClient.post<OdontogramEvent>(`../clinical-records/${clinicalRecordId}/consultations/${consultationId}/odontogram/events`, data)).data;
}
