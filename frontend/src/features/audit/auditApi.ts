import { apiClient } from '../../shared/http/apiClient'

export interface AuditLog {
  id: number
  userName: string | null
  userRole: string | null
  action: string
  entityType: string
  entityId: number | null
  details: string
  patientName: string | null
  occurredAt: string
}

export async function getAuditLogs(): Promise<AuditLog[]> {
  const { data } = await apiClient.get<AuditLog[]>('/audit-logs')
  return data
}
