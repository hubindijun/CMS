<template>
  <div class="dashboard-page">
    <!-- 欢迎卡片 -->
    <a-card class="welcome-card">
      <div class="welcome-left">
        <div class="welcome-title">
          欢迎回来，{{ userInfo?.nickname || userInfo?.username || '管理员' }} 👋
        </div>
        <div class="welcome-desc">
          今天是 {{ currentDate }}，祝您工作愉快！
        </div>
      </div>
      <div class="welcome-right">
        <div class="welcome-stat">
          <div class="stat-value">{{ stats.users }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
    </a-card>

    <!-- 数据统计卡片 -->
    <a-row :gutter="16" class="stat-cards">
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-card-icon icon-blue">
            <icon-user-group />
          </div>
          <div class="stat-card-info">
            <div class="stat-card-value">{{ stats.users }}</div>
            <div class="stat-card-label">用户总数</div>
          </div>
          <div class="stat-card-trend trend-up">
            <icon-arrow-up />
            12%
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-card-icon icon-green">
            <icon-file />
          </div>
          <div class="stat-card-info">
            <div class="stat-card-value">{{ stats.articles }}</div>
            <div class="stat-card-label">文章数量</div>
          </div>
          <div class="stat-card-trend trend-up">
            <icon-arrow-up />
            8%
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-card-icon icon-orange">
            <icon-eye />
          </div>
          <div class="stat-card-info">
            <div class="stat-card-value">{{ stats.views }}</div>
            <div class="stat-card-label">今日访问</div>
          </div>
          <div class="stat-card-trend trend-down">
            <icon-arrow-down />
            3%
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-card-icon icon-purple">
            <icon-message />
          </div>
          <div class="stat-card-info">
            <div class="stat-card-value">{{ stats.messages }}</div>
            <div class="stat-card-label">待办消息</div>
          </div>
          <div class="stat-card-trend trend-up">
            <icon-arrow-up />
            5%
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 快捷入口 -->
    <a-row :gutter="16" class="quick-entries">
      <a-col :span="16">
        <a-card title="快捷操作">
          <div class="entry-list">
            <div class="entry-item" @click="goTo('/system/user')">
              <div class="entry-icon icon-blue">
                <icon-user-group />
              </div>
              <span>用户管理</span>
            </div>
            <div class="entry-item" @click="goTo('/system/role')">
              <div class="entry-icon icon-green">
                <icon-lock />
              </div>
              <span>角色管理</span>
            </div>
            <div class="entry-item" @click="goTo('/system/menu')">
              <div class="entry-icon icon-orange">
                <icon-menu />
              </div>
              <span>菜单管理</span>
            </div>
            <div class="entry-item" @click="goTo('/content/article')">
              <div class="entry-icon icon-purple">
                <icon-file />
              </div>
              <span>文章管理</span>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="系统公告">
          <div class="notice-list">
            <div class="notice-item">
              <a-tag color="blue">重要</a-tag>
              <span class="notice-text">系统将于本周日凌晨进行升级维护</span>
            </div>
            <div class="notice-item">
              <a-tag color="green">新功能</a-tag>
              <span class="notice-text">RBAC 权限管理模块已上线</span>
            </div>
            <div class="notice-item">
              <a-tag color="orange">提醒</a-tag>
              <span class="notice-text">请定期修改您的登录密码</span>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const stats = ref({
  users: 128,
  articles: 256,
  views: 1024,
  messages: 12
})

const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekDay = weekDays[now.getDay()]
  return `${year}年${month}月${day}日 ${weekDay}`
})

function goTo(path) {
  router.push(path)
}
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #165dff 0%, #4080ff 100%);
  border: none;
}

.welcome-card :deep(.arco-card-body) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}

.welcome-left {
  color: #fff;
}

.welcome-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.welcome-desc {
  font-size: 14px;
  opacity: 0.9;
}

.welcome-right {
  text-align: right;
}

.welcome-stat .stat-value {
  font-size: 36px;
  font-weight: 600;
  color: #fff;
}

.welcome-stat .stat-label {
  font-size: 14px;
  opacity: 0.9;
}

/* 统计卡片 */
.stat-cards {
  margin: 0;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-card :deep(.arco-card-body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.icon-blue {
  background: #e8f3ff;
  color: #165dff;
}

.icon-green {
  background: #e8ffea;
  color: #00b42a;
}

.icon-orange {
  background: #fff7e8;
  color: #ff7d00;
}

.icon-purple {
  background: #f0e8ff;
  color: #722ed1;
}

.stat-card-info {
  flex: 1;
  min-width: 0;
}

.stat-card-value {
  font-size: 28px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1.2;
}

.stat-card-label {
  font-size: 14px;
  color: #86909c;
  margin-top: 4px;
}

.stat-card-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.trend-up {
  color: #00b42a;
}

.trend-down {
  color: #f53f3f;
}

/* 快捷入口 */
.quick-entries {
  margin: 0;
}

.entry-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.entry-item:hover {
  background: #f7f8fa;
}

.entry-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.entry-item span {
  font-size: 14px;
  color: #4e5969;
}

/* 系统公告 */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.notice-text {
  color: #4e5969;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
