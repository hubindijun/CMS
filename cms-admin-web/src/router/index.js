import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/system/user',
        name: 'SysUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/system/role',
        name: 'SysRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: '/system/permission',
        name: 'SysPermission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: { title: '权限管理' }
      },
      {
        path: '/system/login-log',
        name: 'SysLoginLog',
        component: () => import('@/views/system/login-log/index.vue'),
        meta: { title: '登录日志' }
      },
      {
        path: '/system/dict',
        name: 'SysDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理' }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  if (to.meta?.public) {
    next()
    return
  }

  const userStore = useUserStore()
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
      await userStore.fetchMenuTree()
    } catch (e) {
      next(`/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})

export default router
