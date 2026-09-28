import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import './app-shell.css'

const globalItems=[{label:'Dashboard',short:'DB',to:'/dashboard'},{label:'Pacientes',short:'PA',to:'/pacientes'}]

export function ClinicHeader(){
 const {user,logout}=useAuth();const navigate=useNavigate();

  return <>    <header className="app-topbar d-flex justify-content-between align-items-center py-3 pe-4 bg-dark text-white">
      <Link to="/" className="clinic-brand text-decoration-none">
        <div className="brand-mark">OU</div>
        <span className="fs-5 fw-bold">Odonto<b>Ucacue</b></span>
      </Link>
      <div className="header-user d-flex align-items-center gap-3">
        <span className="text-end lh-sm">
          <strong className="d-block text-white fw-medium">{user?.nombres}</strong>
          <small className="text-info text-capitalize">{user?.role.toLowerCase().replace('_', ' ')}</small>
        </span>
        <button className="btn btn-sm btn-outline-light" type="button" onClick={() => { logout(); navigate('/login', { replace: true }) }}>Salir</button>
      </div></header><aside className="clinic-side-nav" aria-label="Navegación principal"><nav>{[...globalItems, ...(['RECEPCIONISTA', 'ODONTOLOGO'].includes(user?.role || '') ? [{label:'Agenda',short:'AG',to:'/citas'}] : []), ...(user?.role === 'ODONTOLOGO' ? [{label:'Auditoría',short:'AU',to:'/auditoria'}] : [])].map(item=><Link key={item.label} to={item.to} title={item.label}><span aria-hidden="true">{item.short}</span><small>{item.label}</small></Link>)}</nav></aside></>
}
