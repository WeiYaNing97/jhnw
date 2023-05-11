import request from '@/utils/request'

// 查询误码率列表
export function listRate(query) {
  return request({
    url: '/advanced/rate/list',
    method: 'get',
    params: query
  })
}

// 查询误码率详细
export function getRate(id) {
  return request({
    url: '/advanced/rate/' + id,
    method: 'get'
  })
}

// 新增误码率
export function addRate(data) {
  return request({
    url: '/advanced/rate',
    method: 'post',
    data: data
  })
}

// 修改误码率
export function updateRate(data) {
  return request({
    url: '/advanced/rate',
    method: 'put',
    data: data
  })
}

// 删除误码率
export function delRate(id) {
  return request({
    url: '/advanced/rate/' + id,
    method: 'delete'
  })
}

// 导出误码率
export function exportRate(query) {
  return request({
    url: '/advanced/rate/export',
    method: 'get',
    params: query
  })
}