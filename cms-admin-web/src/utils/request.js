import axios from 'axios'
import { Message } from '@arco-design/web-vue'
import router from '@/router'

const request = axios.create({
  baseURL: '/',
  timeout: 30000,
  withCredentials: true
})

request.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => Promise.reject(error)
)

async function handle401() {
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()
  userStore.logout()
  Message.error('登录已过期，请重新登录')
  router.push('/login')
}

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else if (res.code === 401) {
      handle401()
      return Promise.reject(res)
    } else {
      Message.error(res.message || '请求失败')
      return Promise.reject(res)
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      handle401()
    } else if (error.response?.status === 403) {
      Message.error('权限不足')
    } else {
      Message.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
