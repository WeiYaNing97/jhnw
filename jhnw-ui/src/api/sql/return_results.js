import request from '@/utils/request'

// 查询返回信息对比逻辑列表
export function listReturn_results(query) {
  return request({
    url: '/sql/return_results/list',
    method: 'get',
    params: query
  })
}

// 查询返回信息对比逻辑详细
export function getReturn_results(id) {
  return request({
    url: '/sql/return_results/' + id,
    method: 'get'
  })
}

// 新增返回信息对比逻辑
export function addReturn_results(data) {
  return request({
    url: '/sql/return_results',
    method: 'post',
    data: data
  })
}

// 修改返回信息对比逻辑
export function updateReturn_results(data) {
  return request({
    url: '/sql/return_results',
    method: 'put',
    data: data
  })
}

// 删除返回信息对比逻辑
export function delReturn_results(id) {
  return request({
    url: '/sql/return_results/' + id,
    method: 'delete'
  })
}

// 导出返回信息对比逻辑
export function exportReturn_results(query) {
  return request({
    url: '/sql/return_results/export',
    method: 'get',
    params: query
  })
}