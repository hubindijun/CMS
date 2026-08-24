import request from '@/utils/request'

export function getRolePage(params) {
  return request.get('/api/system/role/page', { params })
}

export function getRoleList() {
  return request.get('/api/system/role/list')
}

export function addRole(data) {
  return request.post('/api/system/role', data)
}

export function updateRole(data) {
  return request.put('/api/system/role', data)
}

export function deleteRole(id) {
  return request.delete(`/api/system/role/${id}`)
}

export function toggleRoleStatus(id, status) {
  return request.put(`/api/system/role/${id}/status`, null, { params: { status } })
}

export function getRolePermissions(id) {
  return request.get(`/api/system/role/${id}/permissions`)
}

export function assignRolePermissions(id, permissionIds) {
  return request.put(`/api/system/role/${id}/permissions`, permissionIds)
}
