<script setup lang="ts">
import { RouterView } from 'vue-router'
import Header from '@/components/Header.vue'
</script>

<template>
  <Header />
  <RouterView />
</template>

<style scoped>
/* App.vue의 스타일은 전역에 영향을 줄 수 있으므로, 필요한 경우에만 신중하게 추가합니다. */
</style>
