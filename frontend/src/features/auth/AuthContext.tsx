import { useCallback, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'

import { setAccessToken, setUnauthorizedHandler } from '../../shared/http/apiClient'
import { authenticate } from './authApi'
import type { CurrentUser } from './types'
import { AuthContext } from './authContextValue'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<CurrentUser | null>(null)

  const logout = useCallback(() => {
    setAccessToken(null)
    setUser(null)
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(logout)
    return () => setUnauthorizedHandler(null)
  }, [logout])

  const login = useCallback(async (username: string, password: string) => {
    const session = await authenticate(username, password)
    setAccessToken(session.accessToken)
    setUser(session.user)
    return session.user
  }, [])

  const value = useMemo(() => ({ user, login, logout }), [user, login, logout])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
