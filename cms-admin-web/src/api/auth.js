import request from '@/utils/request'

export function login(data) {
  const form = new URLSearchParams()
  form.append('username', data.username)
  form.append('password', data.password)
  form.append('captcha', data.captcha)
  form.append('captchaKey', data.captchaKey)
  return request.post('/api/auth/login', form, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function logout() {
  return request.post('/api/auth/logout')
}

export function getUserInfo() {
  return request.get('/api/auth/user-info')
}

export function getCaptcha() {
  return request.get('/api/auth/captcha')
}

export function getMenuTree() {
  return request.get('/api/menu/tree')
}
