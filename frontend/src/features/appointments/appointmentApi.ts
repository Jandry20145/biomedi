import { apiClient } from '../../shared/http/apiClient'

export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED'

export interface Appointment {
  id: number
  patientId: number
  patientName: string
  patientIdentification: string
  dentistId: number
  dentistName: string
  appointmentDate: string
  reason: string
  status: AppointmentStatus
}

export interface AppointmentRequest {
  patientId: number
  dentistId: number
  appointmentDate: string
  reason: string
}

export async function getAppointments(start: string, end: string): Promise<Appointment[]> {
  const { data } = await apiClient.get<Appointment[]>('/appointments', { params: { start, end } })
  return data
}

export async function createAppointment(request: AppointmentRequest): Promise<Appointment> {
  const { data } = await apiClient.post<Appointment>('/appointments', request)
  return data
}

export async function changeAppointmentStatus(id: number, status: AppointmentStatus): Promise<Appointment> {
  const { data } = await apiClient.put<Appointment>(`/appointments/${id}/status`, { status })
  return data
}
