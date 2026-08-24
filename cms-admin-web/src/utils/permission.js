import { useUserStore } from '@/stores/user'

function simpleMatch(pattern, str) {
  const parts = pattern.split('*')
  if (parts.length === 1) return pattern === str
  let lastIndex = 0
  for (let i = 0; i < parts.length; i++) {
    if (parts[i] === '') continue
    const idx = str.indexOf(parts[i], lastIndex)
    if (idx === -1) return false
    lastIndex = idx + parts[i].length
  }
  if (pattern.endsWith('*')) return true
  return lastIndex === str.length
}

export function hasPermission(perm) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []
  return permissions.some(p => simpleMatch(p, perm))
}

export function hasAnyPermission(...perms) {
  return perms.some(p => hasPermission(p))
}
