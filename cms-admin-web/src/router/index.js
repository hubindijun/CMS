import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const modules = import.meta.glob('@/views/**/*.vue')

function loadView(component) {
  const path = `/src/views/${component}.vue`
  return modules[path] || (() => import('@/views/dashboard/index.vue'))
}

function generateRoutes(menuTree) {
  const routes = []
  function traverse(tree) {
    for (const item of tree) {
      if (item.type === 2 && item.component) {
        routes.push({
          path: item.path,
          name: item.name,
          component: loadView(item.component),
          meta: { title: item.name }
        })
      }
      if (item.children && item.children.length > 0) {
        traverse(item.children)
      }
    }
  }
  traverse(menuTree)
  return routes
}

const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
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
  routes: constantRoutes
})

let dynamicRoutesAdded = false

function addDynamicRoutes(menuTree) {
  if (dynamicRoutesAdded) return
  const dynamicRoutes = generateRoutes(menuTree)
  for (const route of dynamicRoutes) {
    router.addRoute('Layout', route)
  }
  dynamicRoutesAdded = true
}

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
      addDynamicRoutes(userStore.menuTree)
      next({ ...to, replace: true })
      return
    } catch (e) {
      next(`/login?redirect=${to.fullPath}`)
      return
    }
  }

  if (!dynamicRoutesAdded && userStore.menuTree?.length) {
    addDynamicRoutes(userStore.menuTree)
    next({ ...to, replace: true })
    return
  }

  next()
})

export function resetRouter() {
  dynamicRoutesAdded = false
}

export default router
