import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ClinicHeader } from '../patients/ClinicHeader'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts'
import { getDashboardMetrics, type DashboardMetrics } from './dashboardApi'
import './dashboard.css'

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#ff6b6b', '#4ecdc4']

export function DashboardPage() {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getDashboardMetrics()
      .then(setMetrics)
      .catch(() => setError('No se pudieron cargar las métricas del dashboard.'))
  }, [])

  if (error) {
    return (
      <>
        <ClinicHeader />
        <main className="dashboard-shell">
          <div className="alert alert-danger">{error}</div>
        </main>
      </>
    )
  }

  if (!metrics) {
    return (
      <>
        <ClinicHeader />
        <main className="dashboard-shell">
          <p>Cargando dashboard...</p>
        </main>
      </>
    )
  }

  return (
    <>
      <ClinicHeader />
      <main id="main-content" className="dashboard-shell">
        <div className="page-heading">
          <div>
            <p className="kicker">Panel de Control</p>
            <h1>Dashboard</h1>
            <p>Visualice las métricas y reportes más importantes de la clínica.</p>
          </div>
        </div>

        <section className="metrics-grid" aria-label="Métricas Principales">
          <article className="metric-card">
            <h2 className="metric-title">Total de Pacientes</h2>
            <p className="metric-value">{metrics.totalPatients}</p>
          </article>
          <article className="metric-card">
            <h2 className="metric-title">Citas Hoy</h2>
            <p className="metric-value">{metrics.todayConsultations}</p>
          </article>
          <article className="metric-card">
            <h2 className="metric-title">Consultas (Mes)</h2>
            <p className="metric-value">{metrics.currentMonthConsultations}</p>
          </article>
          <article className="metric-card">
            <h2 className="metric-title">Reportes Generados</h2>
            <p className="metric-value">{metrics.generatedReports}</p>
          </article>
        </section>

        <section className="dashboard-lists">
          <article className="list-card agenda-card">
            <div className="list-header">
              <h2>Agenda de Hoy</h2>
              <span className="text-muted">{new Date().toLocaleDateString('es-EC', { dateStyle: 'long' })}</span>
            </div>
            {metrics.todayAgenda.length === 0 ? (
              <p className="text-muted" style={{ padding: '20px' }}>No hay citas programadas para hoy.</p>
            ) : (
              <table className="agenda-table">
                <thead>
                  <tr>
                    <th>Hora</th>
                    <th>Tipo</th>
                    <th>Motivo</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {metrics.todayAgenda.map((c) => (
                    <tr key={c.id}>
                      <td>{new Date(c.consultationDate).toLocaleTimeString('es-EC', { hour: '2-digit', minute: '2-digit' })}</td>
                      <td>{c.consultationType.replace(/_/g, ' ')}</td>
                      <td>{c.reason.length > 30 ? c.reason.substring(0, 30) + '...' : c.reason}</td>
                      <td>
                        <span className={`status-badge ${c.lifecycleStatus.toLowerCase()}`}>
                          {c.lifecycleStatus === 'DRAFT' ? 'Pendiente' : 'Completada'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </article>

          <article className="list-card recent-patients-card">
            <div className="list-header">
              <h2>Pacientes Recientes</h2>
              <Link to="/pacientes">Ver todos &gt;</Link>
            </div>
            <div className="recent-patients-list">
              {metrics.recentPatients.map(p => (
                <div key={p.id} className="recent-patient-item">
                  <div className="avatar">{p.firstNames.charAt(0)}{p.lastNames.charAt(0)}</div>
                  <div className="info">
                    <strong>{p.firstNames} {p.lastNames}</strong>
                    <span>{p.clinicalRecordNumber} · {p.age} años</span>
                  </div>
                  <Link to={`/pacientes/${p.id}/historia-clinica`} className="btn btn-sm btn-outline-primary">Ver</Link>
                </div>
              ))}
            </div>
          </article>
        </section>

        <section className="charts-grid" aria-label="Gráficos de la clínica">
          <article className="chart-card">
            <h2 className="chart-title">Consultas por mes</h2>
            <div className="chart-container">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={metrics.monthlyConsultations}
                  margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e0e0e0" />
                  <XAxis dataKey="name" axisLine={false} tickLine={false} />
                  <YAxis axisLine={false} tickLine={false} />
                  <Tooltip
                    contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)' }}
                    cursor={{ fill: '#f8f9fa' }}
                  />
                  <Bar dataKey="consultas" fill="#0d6efd" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </article>

          <article className="chart-card">
            <h2 className="chart-title">Distribución de tratamientos</h2>
            <div className="chart-container">
              {metrics.treatmentDistribution.length === 0 ? (
                <p className="text-muted" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>Aún no hay odontogramas registrados.</p>
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={metrics.treatmentDistribution}
                      cx="50%"
                      cy="50%"
                      innerRadius={80}
                      outerRadius={110}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {metrics.treatmentDistribution.map((_entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)' }} />
                    <Legend verticalAlign="bottom" height={36} iconType="circle" />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </div>
          </article>
        </section>
      </main>
    </>
  )
}
