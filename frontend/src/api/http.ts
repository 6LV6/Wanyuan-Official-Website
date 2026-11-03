import axios, { AxiosError, type AxiosRequestConfig } from 'axios'

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

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10_000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  // M2 会在这里注入 Authorization: Bearer <token>
  const token = localStorage.getItem('access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

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
  (error: AxiosError<ApiResponse<unknown>>) => {
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
