import { apiClient } from '../../shared/http/apiClient'
import type { LoginResponse } from './types'

export async function authenticate(username: string, password: string) {
  const response = await apiClient.post<LoginResponse>('/auth/login', { username, password })
  return response.data
}

