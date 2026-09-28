import { apiClient } from '../../shared/http/apiClient'
import type { Patient } from '../patients/types'
import type { ConsultationSummary } from '../consultations/types'

export interface TreatmentDistributionData {
  name: string
  value: number
}

export interface MonthlyConsultationData {
  name: string
  consultas: number
}

export interface DashboardMetrics {
  totalPatients: number
  todayConsultations: number
  currentMonthConsultations: number
  generatedReports: number
  todayAgenda: ConsultationSummary[]
  recentPatients: Patient[]
  treatmentDistribution: TreatmentDistributionData[]
  monthlyConsultations: MonthlyConsultationData[]
}

export async function getDashboardMetrics() {
  return (await apiClient.get<DashboardMetrics>('/dashboard')).data
}
