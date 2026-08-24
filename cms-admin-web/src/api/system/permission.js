import request from '@/utils/request'

export function getPermissionPage(params) {
  return request.get('/api/system/permission/page', { params })
}

export function getPermissionList() {
  return request.get('/api/system/permission/list')
}

export function getPermissionTree() {
  return request.get('/api/system/permission/tree')
}

export function getPermission(id) {
  return request.get(`/api/system/permission/${id}`)
}

export function addPermission(data) {
  return request.post('/api/system/permission', data)
}

export function updatePermission(data) {
  return request.put('/api/system/permission', data)
}

export function deletePermission(id) {
  return request.delete(`/api/system/permission/${id}`)
}
