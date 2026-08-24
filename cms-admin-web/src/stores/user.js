import { defineStore } from 'pinia'
import { login, logout, getUserInfo, getMenuTree } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    menuTree: [],
    permissions: []
  }),
  actions: {
    async login(formData) {
      await login(formData)
      await this.fetchUserInfo()
      await this.fetchMenuTree()
    },
    async fetchUserInfo() {
      const info = await getUserInfo()
      this.userInfo = info
      this.permissions = info.permissions || []
    },
    async fetchMenuTree() {
      const tree = await getMenuTree()
      this.menuTree = tree
    },
    async logout() {
      try {
        await logout()
      } catch (e) {}
      this.userInfo = null
      this.menuTree = []
      this.permissions = []
    }
  }
})
