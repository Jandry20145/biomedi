import { useEffect, useState } from 'react'
import { ClinicHeader } from '../patients/ClinicHeader'
import { getAppointments, type Appointment, changeAppointmentStatus } from './appointmentApi'
import { AppointmentModal } from './AppointmentModal'
import '../home/dashboard.css'

export function AppointmentsPage() {
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [openDropdownId, setOpenDropdownId] = useState<number | null>(null)

  const loadAppointments = () => {
    setLoading(true)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const end = new Date(today)
    end.setDate(today.getDate() + 30) // fetch next 30 days
    getAppointments(today.toISOString(), end.toISOString())
      .then((data) => {
        setAppointments(data)
        setLoading(false)
      })
      .catch((err) => {
        console.error(err)
        setError('Error al cargar la agenda.')
        setLoading(false)
      })
  }

  useEffect(() => {
    loadAppointments()
  }, [])

  const handleStatusChange = async (id: number, newStatus: any) => {
    try {
      await changeAppointmentStatus(id, newStatus)
      loadAppointments()
    } catch (error) {
      console.error('Failed to change status', error)
      alert('Error al cambiar el estado de la cita')
    }
  }

  const statusBadge = (status: string) => {
    switch (status) {
      case 'PENDING': return <span className="badge bg-warning text-dark">Pendiente</span>
      case 'CONFIRMED': return <span className="badge bg-success">Confirmada</span>
      case 'COMPLETED': return <span className="badge bg-primary">Completada</span>
      case 'CANCELLED': return <span className="badge bg-danger">Cancelada</span>
      default: return <span className="badge bg-secondary">{status}</span>
    }
  }

  return (
    <>
      <ClinicHeader />
      <main id="main-content" className="dashboard-shell">
        <div className="page-heading">
          <div>
            <p className="kicker">Agenda</p>
            <h1>Citas Médicas</h1>
            <p>Control y programación de citas odontológicas.</p>
          </div>
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            + Agendar Cita
          </button>
        </div>

        {loading ? (
          <p>Cargando agenda...</p>
        ) : error ? (
          <div className="alert alert-danger">{error}</div>
        ) : (
          <section className="dashboard-lists">
            <article className="list-card agenda-card" style={{ gridColumn: '1 / -1' }}>
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light">
                    <tr>
                      <th>Fecha y Hora</th>
                      <th>Paciente</th>
                      <th>Odontólogo</th>
                      <th>Motivo</th>
                      <th>Estado</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {appointments.length === 0 ? (
                      <tr>
                        <td colSpan={6} className="text-center py-4 text-muted">No hay citas programadas.</td>
                      </tr>
                    ) : (
                      appointments.map((appt) => (
                        <tr key={appt.id}>
                          <td>{new Date(appt.appointmentDate).toLocaleString('es-EC')}</td>
                          <td>
                            <strong>{appt.patientName}</strong><br/>
                            <small className="text-muted">CI: {appt.patientIdentification}</small>
                          </td>
                          <td>{appt.dentistName}</td>
                          <td>{appt.reason}</td>
                          <td>{statusBadge(appt.status)}</td>
                          <td>
                            <div className="dropdown">
                              <button className="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" onClick={() => setOpenDropdownId(openDropdownId === appt.id ? null : appt.id)}>
                                Opciones
                              </button>
                              <ul className={`dropdown-menu ${openDropdownId === appt.id ? 'show' : ''}`} style={openDropdownId === appt.id ? { position: 'absolute', zIndex: 1000, margin: 0, right: 0 } : {}}>
                                <li><button className="dropdown-item" onClick={() => { handleStatusChange(appt.id, 'CONFIRMED'); setOpenDropdownId(null); }}>Confirmar</button></li>
                                <li><button className="dropdown-item" onClick={() => { handleStatusChange(appt.id, 'COMPLETED'); setOpenDropdownId(null); }}>Marcar Completada</button></li>
                                <li><hr className="dropdown-divider"/></li>
                                <li><button className="dropdown-item text-danger" onClick={() => { handleStatusChange(appt.id, 'CANCELLED'); setOpenDropdownId(null); }}>Cancelar Cita</button></li>
                              </ul>
                            </div>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        )}

        {isModalOpen && (
          <AppointmentModal 
            onClose={() => setIsModalOpen(false)} 
            onSuccess={() => {
              setIsModalOpen(false)
              loadAppointments()
            }}
          />
        )}
      </main>
    </>
  )
}
