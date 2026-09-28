import { useEffect, useState } from 'react'
import { ClinicHeader } from '../patients/ClinicHeader'
import { getAuditLogs, type AuditLog } from './auditApi'
import '../home/dashboard.css'

export function AuditPage() {
  const [logs, setLogs] = useState<AuditLog[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getAuditLogs()
      .then((data) => {
        setLogs(data)
        setLoading(false)
      })
      .catch((err) => {
        console.error(err)
        setError('Error al cargar el registro de auditoría.')
        setLoading(false)
      })
  }, [])

  return (
    <>
      <ClinicHeader />
      <main id="main-content" className="dashboard-shell">
        <div className="page-heading">
          <div>
            <p className="kicker">Seguridad y Control</p>
            <h1>Auditoría del Sistema</h1>
            <p>Registro completo de acciones críticas y reportes generados.</p>
          </div>
        </div>

        {loading ? (
          <p>Cargando auditoría...</p>
        ) : error ? (
          <div className="alert alert-danger">{error}</div>
        ) : (
          <section className="dashboard-lists">
            <article className="list-card agenda-card" style={{ gridColumn: '1 / -1' }}>
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light">
                    <tr>
                      <th>Fecha</th>
                      <th>Paciente</th>
                      <th>Acción</th>
                      <th>Módulo</th>
                      <th>Usuario</th>
                    </tr>
                  </thead>
                  <tbody>
                    {logs.length === 0 ? (
                      <tr>
                        <td colSpan={5} className="text-center py-4 text-muted">No hay registros de auditoría.</td>
                      </tr>
                    ) : (
                      logs.map((log) => {
                        let details: any = {}
                        try { details = JSON.parse(log.details) } catch {}
                        
                        let paciente = log.patientName || details.patient || details.name || (log.entityType === 'Patient' ? 'Paciente #' + log.entityId : '-')
                        if (log.action === 'USER_LOGIN') paciente = '-'
                        
                        const actionMap: Record<string, string> = {
                          'PATIENT_CREATED': 'Creación de paciente',
                          'PATIENT_UPDATED': 'Actualización de datos',
                          'PATIENT_DEACTIVATED': 'Desactivación de paciente',
                          'APPOINTMENT_CREATED': 'Creación de cita',
                          'APPOINTMENT_UPDATED': 'Actualización de cita',
                          'APPOINTMENT_STATUS_CHANGED': 'Cambio de estado de cita',
                          'REPORT_GENERATED': 'Generación de reporte',
                          'CONSULTATION_CREATED': 'Apertura de consulta',
                          'CONSULTATION_UPDATED_VITALS': 'Actualización de signos vitales',
                          'CONSULTATION_CLOSED': 'Cierre de consulta',
                          'CONSULTATION_EVOLUTION_ADDED': 'Adición de evolución',
                          'USER_LOGIN': 'Inicio de sesión'
                        }

                        const entityMap: Record<string, string> = {
                          'Patient': 'Pacientes',
                          'DentalConsultation': 'Consulta',
                          'ClinicalRecord': 'Historia Clínica',
                          'Appointment': 'Citas',
                          'User': 'Sistema'
                        }

                        return (
                          <tr key={log.id}>
                            <td>{new Date(log.occurredAt).toLocaleString('es-EC')}</td>
                            <td>{paciente}</td>
                            <td><span className="badge bg-secondary">{actionMap[log.action] || log.action}</span></td>
                            <td>{entityMap[log.entityType] || log.entityType}</td>
                            <td>
                              {log.userName || 'Sistema'} 
                              {log.userRole && <small className="text-muted d-block">{log.userRole}</small>}
                            </td>
                          </tr>
                        )
                      })
                    )}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        )}
      </main>
    </>
  )
}
