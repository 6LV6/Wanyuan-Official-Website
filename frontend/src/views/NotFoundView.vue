<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <section class="not-found">
    <p class="code">404</p>
    <h1>这个页面还不存在</h1>
    <p>可能是地址写错了，也可能它还在学习路线里等着被实现。</p>
    <RouterLink class="back" to="/">回到首页</RouterLink>
  </section>
</template>

<style scoped>
.not-found {
  padding: 60px 0;
  text-align: center;
}

.code {
  margin: 0;
  font-size: 64px;
  font-weight: 800;
  color: var(--brand);
  letter-spacing: 0.1em;
}

h1 {
  margin: 8px 0 10px;
  font-size: 24px;
  color: var(--text-strong);
}

p {
  color: var(--text-muted);
  margin: 0 0 24px;
}

.back {
  display: inline-block;
  padding: 10px 18px;
  border-radius: 10px;
  background: var(--brand);
  color: #fff;
  text-decoration: none;
  font-size: 14px;
}
</style>
