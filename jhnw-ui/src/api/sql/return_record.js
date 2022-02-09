import request from '@/utils/request'

// 查询返回信息列表
export function listReturn_record(query) {
  return request({
    url: '/sql/return_record/list',
    method: 'get',
    params: query
  })
}

// 查询返回信息详细
export function getReturn_record(id) {
  return request({
    url: '/sql/return_record/' + id,
    method: 'get'
  })
}

// 新增返回信息
export function addReturn_record(data) {
  return request({
    url: '/sql/return_record',
    method: 'post',
    data: data
  })
}

// 修改返回信息
export function updateReturn_record(data) {
  return request({
    url: '/sql/return_record',
    method: 'put',
    data: data
  })
}

// 删除返回信息
export function delReturn_record(id) {
  return request({
    url: '/sql/return_record/' + id,
    method: 'delete'
  })
}

// 导出返回信息
export function exportReturn_record(query) {
  return request({
    url: '/sql/return_record/export',
    method: 'get',
    params: query
  })
}