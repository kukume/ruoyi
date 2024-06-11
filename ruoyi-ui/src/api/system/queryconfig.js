import request from '@/utils/request'

export function getQueryConfig() {
  return request({
    url: '/system/sysQueryConfig',
    method: 'get'
  })
}

export function getQueryConfigByRemarkAndType(remark, type) {
  return request({
    url: '/system/sysQueryConfig/byRemarkAndType?remark=' + encodeURIComponent(remark) + "&type=" + encodeURIComponent(type),
    method: 'get'
  })
}

export function saveQueryConfig(data) {
  return request({
    url: '/system/sysQueryConfig',
    method: 'post',
    data
  })
}

export function deleteQueryConfig(id) {
  return request({
    url: `/system/sysQueryConfig/${id}`,
    method: 'delete'
  })
}
