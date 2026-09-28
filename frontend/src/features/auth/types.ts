export type Role = 'RECEPCIONISTA' | 'AUXILIAR' | 'ODONTOLOGO'

export interface CurrentUser {
  id: number
  username: string
  nombres: string
  role: Role
}

export interface LoginResponse {
  accessToken: string
  tokenType: 'Bearer'
  expiresAt: string
  user: CurrentUser
}

