import axios from 'axios'
import { Message } from '@arco-design/web-vue'
import router from '@/router'

const TOKEN_KEY = 'access_token'

const request = axios.create({
  baseURL: '/',
  timeout: 30000,
  withCredentials: false
})

request.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

let is401Handled = false

async function handle401() {
  if (is401Handled) return
  is401Handled = true
  const TOKEN_KEY = 'access_token'
  sessionStorage.removeItem(TOKEN_KEY)
  Message.error('登录已过期，请重新登录')
  router.push('/login')
  setTimeout(() => { is401Handled = false }, 1000)
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
