import axios from 'axios'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Navigate, useNavigate } from 'react-router-dom'

import { useAuth } from './useAuth'
import { roleHome } from './roleRoutes'
import './login.css'

interface LoginFields { username: string; password: string }

export function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const [serverError, setServerError] = useState<string | null>(null)
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginFields>()

  if (user) return <Navigate to={roleHome[user.role]} replace />

  const onSubmit = handleSubmit(async (fields) => {
    setServerError(null)
    try {
      const authenticatedUser = await login(fields.username.trim(), fields.password)
      navigate(roleHome[authenticatedUser.role], { replace: true })
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 401) {
        setServerError('Usuario o contraseña incorrectos.')
      } else {
        setServerError('No fue posible conectar con el sistema. Intente nuevamente.')
      }
    }
  })

  return (
    <main className="login-shell">
      <section className="login-intro" aria-labelledby="brand-title">
        <div className="brand-mark" aria-hidden="true">OC</div>
        <div>
          <p className="eyebrow">Historia clínica odontológica</p>
          <h1 id="brand-title">OdontoUcacue</h1>
          <p className="intro-copy">Información clínica organizada, trazable y disponible para cada atención.</p>
        </div>
        <p className="privacy-note">Acceso exclusivo para personal autorizado.</p>
      </section>

      <section className="login-panel" aria-labelledby="login-title">
        <div className="login-card">
          <p className="step-label">Portal clínico</p>
          <h2 id="login-title">Iniciar sesión</h2>
          <p className="login-help">Ingrese sus credenciales institucionales.</p>

          {serverError && <div className="alert alert-danger" role="alert">{serverError}</div>}

          <form onSubmit={onSubmit} noValidate>
            <div className="mb-4">
              <label className="form-label" htmlFor="username">Usuario</label>
              <input id="username" className={`form-control ${errors.username ? 'is-invalid' : ''}`}
                autoComplete="username" autoFocus
                {...register('username', { required: 'Ingrese su usuario.', maxLength: { value: 80, message: 'El usuario es demasiado largo.' } })} />
              {errors.username && <div className="invalid-feedback">{errors.username.message}</div>}
            </div>
            <div className="mb-4">
              <label className="form-label" htmlFor="password">Contraseña</label>
              <input id="password" type="password" className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                autoComplete="current-password"
                {...register('password', { required: 'Ingrese su contraseña.' })} />
              {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
            </div>
            <button className="btn login-button w-100" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Verificando…' : 'Ingresar al sistema'}
            </button>
          </form>
          <p className="session-note">La sesión se cierra al recargar o cerrar esta pestaña.</p>
        </div>
      </section>
    </main>
  )
}
