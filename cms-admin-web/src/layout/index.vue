<template>
  <a-layout class="app-layout">
    <Sidebar :collapsed="collapsed" />
    <a-layout>
      <Header :collapsed="collapsed" @toggle-collapse="toggleCollapse" />
      <a-layout-content class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'

const collapsed = ref(false)

function toggleCollapse() {
  collapsed.value = !collapsed.value
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #f5f6f7;
}

.layout-content {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

:global(.fade-transform-enter-active),
:global(.fade-transform-leave-active) {
  transition: all 0.3s ease;
}

:global(.fade-transform-enter-from) {
  opacity: 0;
  transform: translateY(10px);
}

:global(.fade-transform-leave-to) {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
