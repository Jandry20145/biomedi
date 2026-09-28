import { useState, useEffect } from 'react'
import { createAppointment, type AppointmentRequest } from './appointmentApi'
import { searchPatients } from '../patients/patientApi'
import { getUsers, type User } from '../auth/userApi'
import type { Patient } from '../patients/types'

interface Props {
  onClose: () => void
  onSuccess: () => void
}

export function AppointmentModal({ onClose, onSuccess }: Props) {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const [form, setForm] = useState<AppointmentRequest>({
    patientId: 0,
    dentistId: 0,
    appointmentDate: '',
    reason: ''
  })

  const [patients, setPatients] = useState<Patient[]>([])
  const [dentists, setDentists] = useState<User[]>([])

  useEffect(() => {
    searchPatients({ page: 0, size: 500 })
      .then((res) => setPatients(res.content))
      .catch(console.error)
    getUsers()
      .then((res) => setDentists(res))
      .catch(console.error)
  }, [])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setForm((prev) => ({
      ...prev,
      [name]: name === 'patientId' || name === 'dentistId' ? Number(value) : value
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      await createAppointment(form)
      onSuccess()
    } catch (err: any) {
      console.error(err)
      setError(err.response?.data?.message || 'Ocurrió un error al agendar la cita.')
      setLoading(false)
    }
  }

  return (
    <div className="modal show d-block" tabIndex={-1} style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content border-0 shadow-lg">
          <div className="modal-header border-bottom-0 pb-0">
            <h5 className="modal-title fw-bold">Agendar Nueva Cita</h5>
            <button type="button" className="btn-close" onClick={onClose} aria-label="Close"></button>
          </div>
          <div className="modal-body">
            {error && <div className="alert alert-danger py-2">{error}</div>}
            <form onSubmit={handleSubmit} id="appointmentForm">
              <div className="mb-3">
                <label className="form-label text-secondary small fw-semibold">Paciente</label>
                <select className="form-select" name="patientId" value={form.patientId || ''} onChange={handleChange} required>
                  <option value="" disabled>Seleccione un paciente...</option>
                  {patients.map(p => (
                    <option key={p.id} value={p.id}>{p.firstNames} {p.lastNames} - {p.identification}</option>
                  ))}
                </select>
              </div>
              <div className="mb-3">
                <label className="form-label text-secondary small fw-semibold">Odontólogo</label>
                <select className="form-select" name="dentistId" value={form.dentistId || ''} onChange={handleChange} required>
                  <option value="" disabled>Seleccione un odontólogo...</option>
                  {dentists.map(d => (
                    <option key={d.id} value={d.id}>{d.nombres}</option>
                  ))}
                </select>
              </div>
              <div className="mb-3">
                <label className="form-label text-secondary small fw-semibold">Fecha y Hora</label>
                <input type="datetime-local" className="form-control" name="appointmentDate" value={form.appointmentDate} onChange={handleChange} required min={new Date(Date.now() - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 16)} />
              </div>
              <div className="mb-3">
                <label className="form-label text-secondary small fw-semibold">Motivo de la consulta</label>
                <textarea className="form-control" name="reason" rows={3} value={form.reason} onChange={handleChange} required maxLength={500}></textarea>
              </div>
            </form>
          </div>
          <div className="modal-footer border-top-0 pt-0">
            <button type="button" className="btn btn-outline-secondary px-4" onClick={onClose} disabled={loading}>
              Cancelar
            </button>
            <button type="submit" form="appointmentForm" className="btn btn-primary px-4" disabled={loading}>
              {loading ? 'Guardando...' : 'Agendar Cita'}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
