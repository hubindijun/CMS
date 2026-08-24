import request from '@/utils/request'

export function getPermissionTree() {
  return request.get('/api/system/permission/tree')
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
