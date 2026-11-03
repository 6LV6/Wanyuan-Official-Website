import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'

import { getAccessToken, setAccessToken } from './token'

/** 与后端 com.sixlv6.backend.common.ApiResponse 一一对应 */
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

/** 业务/网络错误统一成这个类型，组件里只需 catch ApiError */
export class ApiError extends Error {
  readonly code: number
  readonly status?: number

  constructor(code: number, message: string, status?: number) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.status = status
  }
}

interface RetryConfig extends InternalAxiosRequestConfig {
  _retry?: boolean
}

const baseURL = import.meta.env.VITE_API_BASE_URL

const http = axios.create({
  baseURL,
  timeout: 10_000,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

/** 不带拦截器的裸实例，专用于刷新令牌，避免 401 → 刷新 → 401 的死循环 */
const raw = axios.create({ baseURL, timeout: 10_000, withCredentials: true })

http.interceptors.request.use((config) => {
  const token = getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refreshing: Promise<string | null> | null = null

/**
 * 单飞（single-flight）：并发多个 401 时只发一次刷新请求。
 * 多个请求共享同一个 Promise，刷新完成后各自重放原请求。
 */
export function refreshAccessToken(): Promise<string | null> {
  if (!refreshing) {
    refreshing = raw
      .post<ApiResponse<{ accessToken: { token: string } }>>('/auth/refresh')
      .then((response) => {
        const token = response.data.data.accessToken.token
        setAccessToken(token)
        return token
      })
      .catch(() => {
        setAccessToken(null)
        return null
      })
      .finally(() => {
        refreshing = null
      })
  }
  return refreshing
}

type AuthExpiredHandler = () => void
const authExpiredHandlers = new Set<AuthExpiredHandler>()

/** 刷新失败 = 登录态彻底失效，交给上层跳转登录页 */
export function onAuthExpired(handler: AuthExpiredHandler): () => void {
  authExpiredHandlers.add(handler)
  return () => authExpiredHandlers.delete(handler)
}

function isAuthEndpoint(url?: string): boolean {
  return !!url && ['/auth/login', '/auth/register', '/auth/refresh', '/auth/logout'].some((p) => url.includes(p))
}

http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown>
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code !== 0) {
        throw new ApiError(body.code, body.message, response.status)
      }
      return body.data
    }
    return response.data
  },
  async (error: AxiosError<ApiResponse<unknown>>) => {
    const config = error.config as RetryConfig | undefined

    // Access Token 过期：静默刷新后重放一次
    if (error.response?.status === 401 && config && !config._retry && !isAuthEndpoint(config.url)) {
      config._retry = true
      const token = await refreshAccessToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
        return http.request(config)
      }
      authExpiredHandlers.forEach((handler) => handler())
    }

    const body = error.response?.data
    if (body && typeof body === 'object' && 'message' in body) {
      return Promise.reject(new ApiError(body.code, body.message, error.response?.status))
    }
    if (error.code === 'ECONNABORTED') {
      return Promise.reject(new ApiError(-1, '请求超时，请检查网络'))
    }
    return Promise.reject(new ApiError(-2, error.message || '网络异常', error.response?.status))
  },
)

/** 因为响应拦截器已经把 ApiResponse.data 解包，这里用 <T, T> 修正返回类型 */
export const request = {
  get: <T>(url: string, config?: AxiosRequestConfig) => http.get<T, T>(url, config),
  post: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    http.post<T, T>(url, data, config),
  put: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    http.put<T, T>(url, data, config),
  patch: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    http.patch<T, T>(url, data, config),
  delete: <T>(url: string, config?: AxiosRequestConfig) => http.delete<T, T>(url, config),
}

export default http
