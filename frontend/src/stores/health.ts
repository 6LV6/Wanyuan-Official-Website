import { defineStore } from 'pinia'
import { ref } from 'vue'

import { fetchHealth, type HealthInfo } from '@/api/health'

type Status = 'idle' | 'loading' | 'up' | 'down'

export const useHealthStore = defineStore('health', () => {
  const status = ref<Status>('idle')
  const info = ref<HealthInfo | null>(null)
  const error = ref<string | null>(null)

  async function check() {
    status.value = 'loading'
    error.value = null
    try {
      info.value = await fetchHealth()
      status.value = 'up'
    } catch (e) {
      info.value = null
      error.value = e instanceof Error ? e.message : '未知错误'
      status.value = 'down'
    }
  }

  return { status, info, error, check }
})
