import { apiClient } from '../../shared/http/apiClient'

export interface User {
  id: number
  nombres: string
}

export async function getUsers() {
  return (await apiClient.get<User[]>('/users')).data
}
