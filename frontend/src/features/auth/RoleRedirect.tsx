import { Navigate } from 'react-router-dom'

import { roleHome } from './roleRoutes'
import { useAuth } from './useAuth'

export function RoleRedirect() {
  const { user } = useAuth()
  return <Navigate to={user ? roleHome[user.role] : '/login'} replace />
}

