import { createPinia } from 'pinia'
import { createApp } from 'vue'

import App from './App.vue'
import { onAuthExpired } from './api/http'
import router from './router'
import { useAuthStore } from './stores/auth'
import './style.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Access Token 过期且 Refresh Token 也失效时，统一退回登录页
onAuthExpired(() => {
  useAuthStore().clear()
  void router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
})

app.mount('#app')
