import request from '@/utils/request'

export function getProfile() {
  return request.get('/api/system/profile')
}

export function updateProfile(data) {
  return request.put('/api/system/profile', data)
}

export function changePassword(data) {
  return request.put('/api/system/profile/password', data)
}
