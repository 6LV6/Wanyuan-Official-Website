<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'

import { useHealthStore } from '@/stores/health'

const health = useHealthStore()
const { status, info, error } = storeToRefs(health)

const milestones = [
  { id: 'M0', title: '环境与工程骨架', detail: 'JDK 21 · Node 24 · 前后端分仓同库', done: true },
  { id: 'M1', title: '前后端跑通', detail: '代理 · 统一响应 · 全局异常 · 健康检查', done: true },
  { id: 'M2', title: '数据库与认证', detail: 'Flyway · JPA · BCrypt · JWT', done: false },
  { id: 'M3', title: '业务 CRUD', detail: 'Todo 列表 · 分页 · 前端页面', done: false },
  { id: 'M4', title: '安全与质量', detail: '限流 · 校验 · 测试 · CI', done: false },
  { id: 'M5', title: '部署与迁移', detail: 'Docker · Nginx · HTTPS · PostgreSQL', done: false },
]

onMounted(() => {
  void health.check()
})
</script>

<template>
  <section class="hero">
    <p class="eyebrow">LEARNING IN PUBLIC</p>
    <h1>
      从零搭一套
      <span class="accent">前后端分离</span>
      的网站
    </h1>
    <p class="lede">
      前端 Vue 3 + Vite + TypeScript，后端 Spring Boot + Java 21。每一个里程碑都有可运行的代码、可验证的接口和需要弄懂的知识点。
    </p>
    <div class="chips">
      <span>Vue 3.5</span>
      <span>Vite 8</span>
      <span>TypeScript 6</span>
      <span>Spring Boot 4</span>
      <span>Java 21</span>
      <span>SQLite → PostgreSQL</span>
    </div>
  </section>

  <section class="grid">
    <article class="card status-card">
      <header class="card-head">
        <h2>后端连通性</h2>
        <button class="btn" :disabled="status === 'loading'" @click="health.check()">
          {{ status === 'loading' ? '检测中…' : '重新检测' }}
        </button>
      </header>

      <div v-if="status === 'up'" class="status up">
        <span class="dot" />
        服务正常
      </div>
      <div v-else-if="status === 'down'" class="status down">
        <span class="dot" />
        {{ error }}
      </div>
      <div v-else class="status idle">
        <span class="dot" />
        {{ status === 'loading' ? '正在请求 /api/health …' : '等待检测' }}
      </div>

      <dl v-if="info" class="kv">
        <div><dt>服务</dt><dd>{{ info.service }}</dd></div>
        <div><dt>Java</dt><dd>{{ info.java }}</dd></div>
        <div><dt>响应时间</dt><dd>{{ new Date(info.time).toLocaleString() }}</dd></div>
      </dl>
      <p v-else class="hint">确认后端已在 8080 端口启动，再点「重新检测」。</p>
    </article>

    <article class="card">
      <header class="card-head">
        <h2>学习路线</h2>
        <span class="muted">2 / 6</span>
      </header>
      <ol class="timeline">
        <li v-for="m in milestones" :key="m.id" :class="{ done: m.done }">
          <span class="badge">{{ m.id }}</span>
          <div>
            <strong>{{ m.title }}</strong>
            <p>{{ m.detail }}</p>
          </div>
        </li>
      </ol>
    </article>
  </section>
</template>

<style scoped>
.hero {
  margin-bottom: 40px;
}

.eyebrow {
  margin: 0 0 12px;
  font-size: 12px;
  letter-spacing: 0.22em;
  color: var(--brand);
}

h1 {
  margin: 0 0 18px;
  font-size: clamp(32px, 5vw, 48px);
  line-height: 1.2;
  color: var(--text-strong);
  letter-spacing: -0.01em;
}

.accent {
  background: linear-gradient(120deg, var(--brand), #12b3a6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.lede {
  max-width: 62ch;
  margin: 0 0 22px;
  color: var(--text-muted);
  line-height: 1.8;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chips span {
  padding: 5px 12px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: var(--surface);
  font-size: 12.5px;
  color: var(--text-muted);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.card {
  padding: 22px;
  border: 1px solid var(--border);
  border-radius: 16px;
  background: var(--surface);
  box-shadow: var(--shadow-sm);
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.card-head h2 {
  margin: 0;
  font-size: 16px;
  color: var(--text-strong);
}

.muted {
  color: var(--text-muted);
  font-size: 13px;
}

.btn {
  padding: 7px 14px;
  border-radius: 9px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text-strong);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.18s ease;
}

.btn:hover:not(:disabled) {
  border-color: var(--brand);
  color: var(--brand);
}

.btn:disabled {
  opacity: 0.6;
  cursor: progress;
}

.status {
  display: flex;
  align-items: center;
  gap: 9px;
  font-weight: 600;
  font-size: 14.5px;
}

.dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: currentColor;
  box-shadow: 0 0 0 4px color-mix(in srgb, currentColor 18%, transparent);
}

.status.up {
  color: var(--ok);
}

.status.down {
  color: var(--danger);
}

.status.idle {
  color: var(--text-muted);
}

.kv {
  margin: 18px 0 0;
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
  font-variant-numeric: tabular-nums;
}

.hint {
  margin: 16px 0 0;
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.7;
}

.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 14px;
}

.timeline li {
  display: grid;
  grid-template-columns: 44px 1fr;
  gap: 12px;
  align-items: start;
  opacity: 0.55;
}

.timeline li.done {
  opacity: 1;
}

.badge {
  display: grid;
  place-items: center;
  height: 26px;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: var(--bg);
  font-size: 11.5px;
  font-weight: 600;
  color: var(--text-muted);
}

.timeline li.done .badge {
  border-color: transparent;
  background: color-mix(in srgb, var(--brand) 14%, transparent);
  color: var(--brand);
}

.timeline strong {
  display: block;
  font-size: 14px;
  color: var(--text-strong);
  margin-bottom: 3px;
}

.timeline p {
  margin: 0;
  font-size: 12.5px;
  color: var(--text-muted);
  line-height: 1.5;
}
</style>
