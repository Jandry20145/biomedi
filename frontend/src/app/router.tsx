import { createBrowserRouter } from 'react-router-dom'

import { LoginPage } from '../features/auth/LoginPage'
import { ProtectedRoute } from '../features/auth/ProtectedRoute'
import { RoleHomePage } from '../features/auth/RoleHomePage'
import { RoleRedirect } from '../features/auth/RoleRedirect'
import { PatientsPage } from '../features/patients/PatientsPage'
import { PatientFormPage } from '../features/patients/PatientFormPage'
import { ClinicalRecordPage } from '../features/patients/ClinicalRecordPage'
import { ConsultationFormPage } from '../features/consultations/ConsultationFormPage'
import { OdontogramRoute } from '../features/odontogram/OdontogramRoute'
import { DashboardPage } from '../features/home/DashboardPage'
import { AuditPage } from '../features/audit/AuditPage'
import { AppointmentsPage } from '../features/appointments/AppointmentsPage'

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/', element: <RoleRedirect /> },
  { path: '/citas', element: <ProtectedRoute roles={['RECEPCIONISTA', 'ODONTOLOGO']}><AppointmentsPage /></ProtectedRoute> },
  { path: '/dashboard', element: <ProtectedRoute roles={['RECEPCIONISTA','AUXILIAR','ODONTOLOGO']}><DashboardPage /></ProtectedRoute> },
  { path: '/pacientes', element: <ProtectedRoute roles={['RECEPCIONISTA','AUXILIAR','ODONTOLOGO']}><PatientsPage /></ProtectedRoute> },
  { path: '/pacientes/nuevo', element: <ProtectedRoute roles={['RECEPCIONISTA', 'ODONTOLOGO']}><PatientFormPage /></ProtectedRoute> },
  { path: '/pacientes/:id/editar', element: <ProtectedRoute roles={['AUXILIAR', 'ODONTOLOGO']}><PatientFormPage /></ProtectedRoute> },
  { path: '/pacientes/:id/historia-clinica', element: <ProtectedRoute roles={['RECEPCIONISTA','AUXILIAR','ODONTOLOGO']}><ClinicalRecordPage /></ProtectedRoute> },
  { path: '/pacientes/:id/historia-clinica/consultas/nueva', element: <ProtectedRoute roles={['AUXILIAR', 'ODONTOLOGO']}><ConsultationFormPage /></ProtectedRoute> },
  { path: '/pacientes/:id/historia-clinica/odontograma', element: <ProtectedRoute roles={['ODONTOLOGO']}><OdontogramRoute /></ProtectedRoute> },
  { path: '/atencion-preliminar', element: <ProtectedRoute roles={['AUXILIAR']}><RoleHomePage title="Atención preliminar" description="Acceso habilitado para consultar pacientes e historial clínico. El registro preliminar requiere un ciclo de consulta abierta aún no implementado." /></ProtectedRoute> },
  { path: '/historia-clinica', element: <ProtectedRoute roles={['ODONTOLOGO']}><RoleHomePage title="Historia clínica odontológica" description="Acceso clínico completo según la matriz de permisos del sistema." /></ProtectedRoute> },
  { path: '/auditoria', element: <ProtectedRoute roles={['ODONTOLOGO']}><AuditPage /></ProtectedRoute> },
  { path: '*', element: <RoleRedirect /> },
])
