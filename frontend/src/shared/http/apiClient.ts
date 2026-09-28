import axios from 'axios'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '/api/v1'

export const apiClient = axios.create({
  baseURL: apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10_000,
})

let accessToken: string | null = null
let unauthorizedHandler: (() => void) | null = null

export function setAccessToken(token: string | null) {
  accessToken = token
}

export function setUnauthorizedHandler(handler: (() => void) | null) {
  unauthorizedHandler = handler
}

apiClient.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401 && accessToken) {
      unauthorizedHandler?.()
    }
    return Promise.reject(error)
  },
)
