import request from '@/utils/request'

// 查询误码率列表
export function listErrorrate(query) {
  return request({
    url: '/advanced/errorrate/list',
    method: 'get',
    params: query
  })
}

// 查询误码率详细
export function getErrorrate(id) {
  return request({
    url: '/advanced/errorrate/' + id,
    method: 'get'
  })
}

// 新增误码率
export function addErrorrate(data) {
  return request({
    url: '/advanced/errorrate',
    method: 'post',
    data: data
  })
}

// 修改误码率
export function updateErrorrate(data) {
  return request({
    url: '/advanced/errorrate',
    method: 'put',
    data: data
  })
}

// 删除误码率
export function delErrorrate(id) {
  return request({
    url: '/advanced/errorrate/' + id,
    method: 'delete'
  })
}

// 导出误码率
export function exportErrorrate(query) {
  return request({
    url: '/advanced/errorrate/export',
    method: 'get',
    params: query
  })
}