import { createContext } from 'react'

import type { CurrentUser } from './types'

export interface AuthContextValue {
  user: CurrentUser | null
  login: (username: string, password: string) => Promise<CurrentUser>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)

