<template>
  <a-layout-sider
    :collapsed="collapsed"
    :trigger="null"
    :width="220"
    :collapsed-width="60"
    class="layout-sider"
  >
    <div class="sider-logo" @click="goHome">
      <icon-apps class="logo-icon" />
      <span v-show="!collapsed" class="logo-text">CMS 后台</span>
    </div>
    <a-menu
      :selected-keys="selectedKeys"
      :open-keys="openKeys"
      @menu-item-click="handleMenuItemClick"
      @sub-menu-title-click="handleSubMenuClick"
      :auto-open-selected="true"
      class="layout-menu"
      :style="{ width: collapsed ? '60px' : '220px' }"
    >
      <template v-for="item in menuTree" :key="item.id">
        <a-sub-menu v-if="item.children && item.children.length > 0" :key="item.id">
          <template #title>
            <component :is="getIconComponent(item.icon)" />
            <span>{{ item.name }}</span>
          </template>
          <a-menu-item
            v-for="child in item.children"
            :key="child.id"
          >
            <component :is="getIconComponent(child.icon)" />
            <span>{{ child.name }}</span>
          </a-menu-item>
        </a-sub-menu>
        <a-menu-item v-else :key="item.id">
          <component :is="getIconComponent(item.icon)" />
          <span>{{ item.name }}</span>
        </a-menu-item>
      </template>
    </a-menu>
  </a-layout-sider>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menuTree = computed(() => userStore.menuTree || [])
const selectedKeys = ref([])
const openKeys = ref([])

const iconMap = {
  'home': 'icon-home',
  'user': 'icon-user',
  'users': 'icon-user-group',
  'setting': 'icon-settings',
  'menu': 'icon-menu',
  'role': 'icon-lock',
  'dashboard': 'icon-dashboard',
  'system': 'icon-apps',
  'file': 'icon-file',
  'chart': 'icon-bar-chart',
  'message': 'icon-message'
}

function getIconComponent(iconName) {
  if (!iconName) return 'icon-menu'
  return iconMap[iconName] || 'icon-menu'
}

function findMenuPath(tree, targetPath, parents = []) {
  for (const item of tree) {
    if (item.path === targetPath) {
      return [...parents, item.id]
    }
    if (item.children && item.children.length > 0) {
      const found = findMenuPath(item.children, targetPath, [...parents, item.id])
      if (found) return found
    }
  }
  return null
}

function findMenuByPath(tree, targetPath) {
  for (const item of tree) {
    if (item.path === targetPath) return item
    if (item.children && item.children.length > 0) {
      const found = findMenuByPath(item.children, targetPath)
      if (found) return found
    }
  }
  return null
}

function updateMenuFromRoute() {
  const path = route.path
  const menuPath = findMenuPath(menuTree.value, path)
  if (menuPath && menuPath.length > 0) {
    selectedKeys.value = [String(menuPath[menuPath.length - 1])]
    if (menuPath.length > 1) {
      openKeys.value = menuPath.slice(0, -1).map(id => String(id))
    }
  }
}

function handleMenuItemClick(key) {
  const menuItem = findMenuItemById(menuTree.value, key)
  if (menuItem && menuItem.path) {
    router.push(menuItem.path)
  }
}

function handleSubMenuClick(key) {
  if (openKeys.value.includes(key)) {
    openKeys.value = openKeys.value.filter(k => k !== key)
  } else {
    openKeys.value = [...openKeys.value, key]
  }
}

function findMenuItemById(tree, id) {
  for (const item of tree) {
    if (String(item.id) === String(id)) return item
    if (item.children && item.children.length > 0) {
      const found = findMenuItemById(item.children, id)
      if (found) return found
    }
  }
  return null
}

function goHome() {
  router.push('/dashboard')
}

watch(() => route.path, () => {
  updateMenuFromRoute()
})

watch(menuTree, () => {
  updateMenuFromRoute()
}, { deep: true })

onMounted(() => {
  updateMenuFromRoute()
})
</script>

<style scoped>
.layout-sider {
  background: #001529;
  overflow: hidden;
  height: 100vh;
  flex-shrink: 0;
}

.sider-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.05);
  cursor: pointer;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  font-size: 24px;
  color: #165dff;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.layout-menu {
  border-right: none;
  background: transparent;
}

:deep(.arco-menu) {
  background: #001529;
}

:deep(.arco-menu-item) {
  color: rgba(255, 255, 255, 0.65);
}

:deep(.arco-menu-item:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

:deep(.arco-menu-item.arco-menu-selected) {
  color: #fff;
  background: #165dff;
}

:deep(.arco-menu-inline-header) {
  color: rgba(255, 255, 255, 0.65);
}

:deep(.arco-menu-inline-header:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

:deep(.arco-menu-pop-header) {
  color: rgba(255, 255, 255, 0.65);
}

:deep(.arco-menu-pop-header:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}
</style>
