<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app-shell">
    <header class="site-header">
      <RouterLink class="brand" to="/">
        <span class="brand-mark">清</span>
        <span class="brand-text">清河</span>
      </RouterLink>

      <nav class="site-nav">
        <RouterLink to="/">首页</RouterLink>
        <a href="https://github.com/6LV6" target="_blank" rel="noopener">GitHub</a>
      </nav>
    </header>

    <main class="site-main">
      <RouterView />
    </main>

    <footer class="site-footer">
      <span>Vue 3 · Vite · TypeScript · Spring Boot</span>
      <span>从零开始的全栈练习</span>
    </footer>
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.site-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 18px clamp(16px, 5vw, 48px);
  border-bottom: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg) 80%, transparent);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  color: var(--text-strong);
  text-decoration: none;
  letter-spacing: 0.04em;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: var(--brand);
  color: #fff;
  font-size: 16px;
}

.site-nav {
  display: flex;
  align-items: center;
  gap: 22px;
  font-size: 14px;
}

.site-nav a {
  color: var(--text-muted);
  text-decoration: none;
  transition: color 0.2s ease;
}

.site-nav a:hover,
.site-nav a.router-link-active {
  color: var(--brand);
}

.site-main {
  flex: 1;
  width: min(1080px, 100%);
  margin: 0 auto;
  padding: clamp(24px, 5vw, 56px) clamp(16px, 5vw, 32px);
}

.site-footer {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 12px;
  padding: 20px clamp(16px, 5vw, 48px);
  border-top: 1px solid var(--border);
  color: var(--text-muted);
  font-size: 13px;
}
</style>
