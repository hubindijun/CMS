import { defineStore } from 'pinia'
import { login, logout, getUserInfo, getMenuTree } from '@/api/auth'
import { resetRouter } from '@/router'

const TOKEN_KEY = 'access_token'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: sessionStorage.getItem(TOKEN_KEY) || '',
    userInfo: null,
    menuTree: []
  }),
  actions: {
    async login(formData) {
      const token = await login(formData)
      this.token = token
      sessionStorage.setItem(TOKEN_KEY, token)
      await this.fetchUserInfo()
      await this.fetchMenuTree()
    },
    async fetchUserInfo() {
      const info = await getUserInfo()
      this.userInfo = info
    },
    async fetchMenuTree() {
      const tree = await getMenuTree()
      this.menuTree = tree
    },
    async logout() {
      try {
        await logout()
      } catch (e) {}
      this.token = ''
      this.userInfo = null
      this.menuTree = []
      sessionStorage.removeItem(TOKEN_KEY)
      resetRouter()
    }
  }
})
