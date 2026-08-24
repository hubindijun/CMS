import request from '@/utils/request'

export function getUserPage(params) {
  return request.get('/api/system/user/page', { params })
}

export function getUser(id) {
  return request.get(`/api/system/user/${id}`)
}

export function addUser(data) {
  return request.post('/api/system/user', data)
}

export function updateUser(data) {
  return request.put('/api/system/user', data)
}

export function deleteUser(id) {
  return request.delete(`/api/system/user/${id}`)
}

export function toggleUserStatus(id, status) {
  return request.put(`/api/system/user/${id}/status`, null, { params: { status } })
}

export function resetPassword(id, password) {
  return request.put(`/api/system/user/${id}/reset-password`, null, { params: { password } })
}
