<template>
  <a-layout-header class="layout-header">
    <div class="header-left">
      <a-button
        type="text"
        class="collapse-btn"
        @click="$emit('toggle-collapse')"
      >
        <icon-menu-fold v-if="!collapsed" />
        <icon-menu-unfold v-else />
      </a-button>
      <a-breadcrumb class="header-breadcrumb">
        <a-breadcrumb-item>{{ pageTitle }}</a-breadcrumb-item>
      </a-breadcrumb>
    </div>
    <div class="header-right">
      <a-dropdown>
        <div class="user-info">
          <a-avatar :size="32" :style="{ backgroundColor: '#165dff' }">
            <template #icon>
              <icon-user />
            </template>
          </a-avatar>
          <span class="username">{{ userInfo?.username || '用户' }}</span>
          <icon-down />
        </div>
        <template #content>
          <a-doption @click="handleProfile">
            <icon-user class="mr-2" />
            个人中心
          </a-doption>
          <a-doption @click="handleChangePassword">
            <icon-lock class="mr-2" />
            修改密码
          </a-doption>
          <a-doption @click="handleLogout">
            <icon-logout class="mr-2" />
            退出登录
          </a-doption>
        </template>
      </a-dropdown>
    </div>

    <a-modal
      v-model:visible="logoutVisible"
      title="退出登录"
      @ok="confirmLogout"
      @cancel="logoutVisible = false"
      ok-text="确定"
      cancel-text="取消"
    >
      <p>确定要退出登录吗？</p>
    </a-modal>
  </a-layout-header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { useUserStore } from '@/stores/user'

defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle-collapse'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const logoutVisible = ref(false)

const userInfo = computed(() => userStore.userInfo)

const pageTitle = computed(() => {
  return route.meta?.title || '首页'
})

function handleProfile() {
  Message.info('个人中心功能开发中')
}

function handleChangePassword() {
  Message.info('修改密码功能开发中')
}

function handleLogout() {
  logoutVisible.value = true
}

async function confirmLogout() {
  try {
    await userStore.logout()
    Message.success('已退出登录')
    router.push('/login')
  } catch (e) {
    // error handled by interceptor
  } finally {
    logoutVisible.value = false
  }
}
</script>

<style scoped>
.layout-header {
  height: 60px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e5e6eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 5;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  padding: 0;
}

.header-breadcrumb {
  font-size: 16px;
  font-weight: 500;
  color: #1d2129;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.user-info:hover {
  background: #f2f3f5;
}

.username {
  font-size: 14px;
  color: #4e5969;
}

.mr-2 {
  margin-right: 8px;
}
</style>
