import request from '@/utils/request'

export function downloadExport(id) {
  return request({
    url: '/system/sysExport/' + id,
    method: 'get',
    responseType: 'blob'
  })
}
