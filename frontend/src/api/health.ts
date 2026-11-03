import { request } from './http'

export interface HealthInfo {
  status: string
  service: string
  java: string
  time: string
}

export function fetchHealth() {
  return request.get<HealthInfo>('/health')
}
