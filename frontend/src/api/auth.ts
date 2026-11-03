import { request } from './http'

export interface UserDto {
  id: number
  username: string
  email: string
  role: string
  createdAt: string
}

export interface AccessToken {
  token: string
  expiresIn: number
  tokenType: string
}

export interface AuthResult {
  accessToken: AccessToken
  user: UserDto
}

export interface LoginPayload {
  account: string
  password: string
}

export interface RegisterPayload {
  username: string
  email: string
  password: string
}

export const authApi = {
  login: (payload: LoginPayload) => request.post<AuthResult>('/auth/login', payload),
  register: (payload: RegisterPayload) => request.post<AuthResult>('/auth/register', payload),
  me: () => request.get<UserDto>('/auth/me'),
  logout: () => request.post<void>('/auth/logout'),
}
