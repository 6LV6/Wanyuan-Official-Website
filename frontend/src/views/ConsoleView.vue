<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const { user } = storeToRefs(auth)

async function onLogout() {
  await auth.logout()
  await router.replace({ name: 'home' })
}
</script>

<template>
  <section class="console">
    <header class="head">
      <div>
        <p class="eyebrow">CONSOLE</p>
        <h1>欢迎回来，{{ user?.username }}</h1>
        <p class="sub">这里是登录后才能看到的页面，路由守卫会拦截未登录访问。</p>
      </div>
      <button class="btn" @click="onLogout">退出登录</button>
    </header>

    <div class="grid">
      <article class="card">
        <h2>账号信息</h2>
        <dl class="kv">
          <div><dt>用户名</dt><dd>{{ user?.username }}</dd></div>
          <div><dt>邮箱</dt><dd>{{ user?.email }}</dd></div>
          <div><dt>角色</dt><dd>{{ user?.role }}</dd></div>
          <div><dt>注册时间</dt><dd>{{ user ? new Date(user.createdAt).toLocaleString() : '-' }}</dd></div>
        </dl>
      </article>

      <article class="card">
        <h2>下一步</h2>
        <ul class="todo">
          <li>M3：新闻公告的后台增删改查（含分页）</li>
          <li>M3：前台列表页与详情页</li>
          <li>M4：接口限流与自动化测试</li>
        </ul>
        <p class="hint">
          Access Token 有效期 15 分钟，过期后前端会自动拿 Cookie 里的 Refresh Token 静默续期，你不需要手动重新登录。
        </p>
      </article>
    </div>
  </section>
</template>

<style scoped>
.head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 11.5px;
  letter-spacing: 0.22em;
  color: var(--brand);
}

h1 {
  margin: 0 0 8px;
  font-size: clamp(24px, 3.4vw, 32px);
  color: var(--text-strong);
}

.sub {
  margin: 0;
  color: var(--text-muted);
  font-size: 13.5px;
}

.btn {
  padding: 8px 16px;
  border-radius: 9px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text-strong);
  font-size: 13px;
  cursor: pointer;
}

.btn:hover {
  border-color: var(--danger);
  color: var(--danger);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.card {
  padding: 22px;
  border: 1px solid var(--border);
  border-radius: 16px;
  background: var(--surface);
  box-shadow: var(--shadow-sm);
}

.card h2 {
  margin: 0 0 16px;
  font-size: 15px;
  color: var(--text-strong);
}

.kv {
  margin: 0;
  display: grid;
  gap: 10px;
  font-size: 13.5px;
}

.kv div {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 8px;
  border-bottom: 1px dashed var(--border);
}

.kv dt {
  color: var(--text-muted);
}

.kv dd {
  margin: 0;
  color: var(--text-strong);
}

.todo {
  margin: 0;
  padding-left: 18px;
  font-size: 13.5px;
  line-height: 2;
  color: var(--text-muted);
}

.hint {
  margin: 14px 0 0;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--text-muted);
}
</style>
