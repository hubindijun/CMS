import { useUserStore } from '@/stores/user'

export function hasPermission(perm) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []
  if (permissions.includes('*:*:*')) return true
  return permissions.includes(perm)
}
