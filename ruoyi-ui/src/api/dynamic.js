import request from '@/utils/request'

export function queryCondition(key, type) {
  return request({
    url: '/query/condition?page=' + encodeURIComponent(key) + "&type=" + encodeURIComponent(type),
    method: 'get',
  })
}

export function saveCondition(data) {
  return request({
    url: '/query/condition',
    method: 'post',
    data: data
  })
}

export function deleteCondition(id) {
  return request({
    url: `/query/condition/${id}`,
    method: 'delete'
  })
}

export function copyCondition(id, remark) {
  return request({
    url: '/query/condition/copy',
    method: 'post',
    data: { id, remark }
  })
}

export function dynamicQuery(data) {
  return request({
    url: '/dynamic/query/specification',
    method: 'post',
    data
  })
}

export function dynamicExport(data) {
  return request({
    url: '/dynamic/export/specification',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

export function dynamicQuerydsl(data) {
  return request({
    url: '/dynamic/query/querydsl',
    method: 'post',
    data
  })
}

export function dynamicExportQuerydsl(data) {
  return request({
    url: '/dynamic/export/querydsl',
    method: 'post',
    data,
    responseType: 'blob'
  })
}
