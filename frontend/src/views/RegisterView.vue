<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

import { ApiError } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const submitting = ref(false)

function validate(): string {
  if (!/^[a-zA-Z0-9_-]{3,32}$/.test(username.value)) {
    return '用户名需 3-32 位，只能包含字母、数字、下划线和短横线'
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    return '邮箱格式不正确'
  }
  if (password.value.length < 8 || password.value.length > 64) {
    return '密码长度需在 8-64 之间'
  }
  return ''
}

async function onSubmit() {
  error.value = validate()
  if (error.value) return

  submitting.value = true
  try {
    await auth.register({
      username: username.value.trim(),
      email: email.value.trim(),
      password: password.value,
    })
    await router.replace({ name: 'console' })
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '注册失败，请稍后再试'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-card">
      <h1>注册</h1>
      <p class="sub">注册成功后会自动登录</p>

      <form novalidate @submit.prevent="onSubmit">
        <label class="field">
          <span>用户名</span>
          <input v-model="username" type="text" autocomplete="username" placeholder="3-32 位字母数字" />
        </label>

        <label class="field">
          <span>邮箱</span>
          <input v-model="email" type="email" autocomplete="email" placeholder="you@example.com" />
        </label>

        <label class="field">
          <span>密码</span>
          <input v-model="password" type="password" autocomplete="new-password" placeholder="8-64 位" />
        </label>

        <p v-if="error" class="error-banner">{{ error }}</p>

        <button class="btn-primary" type="submit" :disabled="submitting">
          {{ submitting ? '注册中…' : '注册' }}
        </button>
      </form>

      <p class="switch">
        已经有账号？
        <RouterLink :to="{ name: 'login' }">去登录</RouterLink>
      </p>
    </div>
  </section>
</template>

<style scoped>
.auth-page {
  display: grid;
  place-items: center;
  padding: 24px 0 48px;
}

.auth-card {
  width: min(420px, 100%);
  padding: 30px;
  border: 1px solid var(--border);
  border-radius: 18px;
  background: var(--surface);
  box-shadow: var(--shadow-sm);
}

h1 {
  margin: 0 0 6px;
  font-size: 24px;
  color: var(--text-strong);
}

.sub {
  margin: 0 0 22px;
  font-size: 13.5px;
  color: var(--text-muted);
}

.switch {
  margin: 18px 0 0;
  font-size: 13.5px;
  color: var(--text-muted);
  text-align: center;
}

.switch a {
  color: var(--brand);
  text-decoration: none;
}
</style>
