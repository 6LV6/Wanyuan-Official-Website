import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { authApi, type LoginPayload, type RegisterPayload, type UserDto } from '@/api/auth'
import { refreshAccessToken } from '@/api/http'
import { setAccessToken } from '@/api/token'

type Status = 'unknown' | 'authenticated' | 'guest'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserDto | null>(null)
  const status = ref<Status>('unknown')
  const loading = ref(false)

  const isAuthenticated = computed(() => status.value === 'authenticated')

  function applySession(result: { accessToken: { token: string }; user: UserDto }) {
    setAccessToken(result.accessToken.token)
    user.value = result.user
    status.value = 'authenticated'
  }

  function clear() {
    setAccessToken(null)
    user.value = null
    status.value = 'guest'
  }

  async function login(payload: LoginPayload) {
    loading.value = true
    try {
      applySession(await authApi.login(payload))
    } finally {
      loading.value = false
    }
  }

  async function register(payload: RegisterPayload) {
    loading.value = true
    try {
      applySession(await authApi.register(payload))
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await authApi.logout()
    } catch {
      // 即使服务端失败，本地也要清干净
    } finally {
      clear()
    }
  }

  /** 页面刷新后：Access Token 已丢，用 httpOnly Cookie 里的 Refresh Token 换一个新的 */
  async function bootstrap() {
    if (status.value !== 'unknown') return
    const token = await refreshAccessToken()
    if (!token) {
      status.value = 'guest'
      return
    }
    try {
      user.value = await authApi.me()
      status.value = 'authenticated'
    } catch {
      clear()
    }
  }

  return { user, status, loading, isAuthenticated, login, register, logout, bootstrap, clear }
})
