import { Link, useNavigate } from 'react-router-dom'

import { useAuth } from './useAuth'

const roleNames = { RECEPCIONISTA: 'Recepción', AUXILIAR: 'Auxiliar odontológico', ODONTOLOGO: 'Odontólogo' }

export function RoleHomePage({ title, description }: { title: string; description: string }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  if (!user) return null
  const closeSession = () => { logout(); navigate('/login', { replace: true }) }
  return (
    <main className="container py-5">
      <header className="d-flex justify-content-between align-items-start gap-3 mb-5">
        <div><p className="text-uppercase text-primary fw-semibold mb-2">OdontoUcacue</p><h1>{title}</h1></div>
        <button className="btn btn-outline-secondary" type="button" onClick={closeSession}>Cerrar sesión</button>
      </header>
      <section className="bg-white border rounded-3 p-4" aria-labelledby="welcome-title">
        <p className="text-body-secondary mb-2">{roleNames[user.role]}</p>
        <h2 id="welcome-title" className="h4">{user.nombres}</h2>
        <p className="mb-0">{description}</p>
        <Link className="btn btn-primary mt-3" to="/pacientes">Abrir pacientes</Link>
      </section>
    </main>
  )
}
