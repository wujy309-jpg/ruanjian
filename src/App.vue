<template>
  <div id="app" :data-theme="theme">
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const theme = ref('light')

onMounted(() => {
  // Check system preference
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    theme.value = 'dark'
  }
  
  // Listen for changes
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
    theme.value = e.matches ? 'dark' : 'light'
  })
})
</script>

<style>
/* Apple HIG Base Styles */
#app {
  min-height: 100vh;
  background: var(--color-bg-grouped);
  color: var(--color-text-primary);
  font-family: var(--font-family);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Element Plus Theme Overrides */
:root {
  --el-color-primary: var(--color-accent);
  --el-color-primary-light-3: var(--color-accent-hover);
  --el-color-primary-light-5: var(--color-accent-medium);
  --el-color-primary-light-7: var(--color-accent-light);
  --el-color-primary-light-8: var(--color-accent-light);
  --el-color-primary-light-9: var(--color-accent-light);
  --el-color-primary-dark-2: var(--color-accent-hover);
  --el-color-success: var(--color-green);
  --el-color-warning: var(--color-orange);
  --el-color-danger: var(--color-red);
  --el-color-info: var(--color-text-secondary);
  --el-bg-color: var(--color-surface);
  --el-bg-color-page: var(--color-bg-grouped);
  --el-text-color-primary: var(--color-text-primary);
  --el-text-color-regular: var(--color-text-secondary);
  --el-text-color-secondary: var(--color-text-tertiary);
  --el-border-color: var(--color-separator);
  --el-border-color-light: var(--color-separator);
  --el-fill-color-blank: var(--color-surface);
  --el-fill-color-light: var(--color-fill);
  --el-mask-color: rgba(0, 0, 0, 0.4);
}

/* Smooth transitions for theme switching */
*,
*::before,
*::after {
  transition: background-color 0.2s ease, color 0.2s ease, border-color 0.2s ease;
}

/* Disable transitions during page load */
.no-transition * {
  transition: none !important;
}
</style>
