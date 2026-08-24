import request from '@/utils/request'

// 字典类型
export function getDictTypePage(params) {
  return request.get('/api/system/dict/type/page', { params })
}

export function getDictTypeList() {
  return request.get('/api/system/dict/type/list')
}

export function getDictType(id) {
  return request.get(`/api/system/dict/type/${id}`)
}

export function addDictType(data) {
  return request.post('/api/system/dict/type', data)
}

export function updateDictType(data) {
  return request.put('/api/system/dict/type', data)
}

export function deleteDictType(id) {
  return request.delete(`/api/system/dict/type/${id}`)
}

// 字典数据
export function getDictDataPage(params) {
  return request.get('/api/system/dict/data/page', { params })
}

export function getDictData(id) {
  return request.get(`/api/system/dict/data/${id}`)
}

export function getDictDataByType(dictType) {
  return request.get('/api/system/dict/data/byType', { params: { dictType } })
}

export function addDictData(data) {
  return request.post('/api/system/dict/data', data)
}

export function updateDictData(data) {
  return request.put('/api/system/dict/data', data)
}

export function deleteDictData(id) {
  return request.delete(`/api/system/dict/data/${id}`)
}
