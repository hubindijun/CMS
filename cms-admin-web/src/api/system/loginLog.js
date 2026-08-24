import request from '@/utils/request'

export function getLoginLogPage(params) {
  return request.get('/api/system/login-log/page', { params })
}

export function deleteLoginLog(id) {
  return request.delete(`/api/system/login-log/${id}`)
}

export function clearLoginLog() {
  return request.delete('/api/system/login-log/clear')
}
