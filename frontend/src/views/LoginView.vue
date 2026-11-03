<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'

import { ApiError } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const account = ref('')
const password = ref('')
const error = ref('')
const submitting = ref(false)

const redirect = computed(() => {
  const target = route.query.redirect
  return typeof target === 'string' && target.startsWith('/') ? target : '/console'
})

async function onSubmit() {
  error.value = ''
  if (!account.value.trim() || !password.value) {
    error.value = '请填写账号和密码'
    return
  }
  submitting.value = true
  try {
    await auth.login({ account: account.value.trim(), password: password.value })
    await router.replace(redirect.value)
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '登录失败，请稍后再试'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-card">
      <h1>登录</h1>
      <p class="sub">用用户名或邮箱登录控制台</p>

      <form novalidate @submit.prevent="onSubmit">
        <label class="field">
          <span>账号</span>
          <input v-model="account" type="text" autocomplete="username" placeholder="用户名或邮箱" />
        </label>

        <label class="field">
          <span>密码</span>
          <input v-model="password" type="password" autocomplete="current-password" placeholder="至少 8 位" />
        </label>

        <p v-if="error" class="error-banner">{{ error }}</p>

        <button class="btn-primary" type="submit" :disabled="submitting">
          {{ submitting ? '登录中…' : '登录' }}
        </button>
      </form>

      <p class="switch">
        还没有账号？
        <RouterLink :to="{ name: 'register', query: route.query }">去注册</RouterLink>
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
