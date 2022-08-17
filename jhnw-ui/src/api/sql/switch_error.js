import request from '@/utils/request'

// 查询交换机错误列表
export function listSwitch_error(query) {
  return request({
    url: '/sql/switch_error/list',
    method: 'get',
    params: query
  })
}

// 查询交换机错误详细
export function getSwitch_error(errorId) {
  return request({
    url: '/sql/switch_error/' + errorId,
    method: 'get'
  })
}

// 新增交换机错误
export function addSwitch_error(data) {
  return request({
    url: '/sql/switch_error',
    method: 'post',
    data: data
  })
}

// 修改交换机错误
export function updateSwitch_error(data) {
  return request({
    url: '/sql/switch_error',
    method: 'put',
    data: data
  })
}

// 删除交换机错误
export function delSwitch_error(errorId) {
  return request({
    url: '/sql/switch_error/' + errorId,
    method: 'delete'
  })
}

// 导出交换机错误
export function exportSwitch_error(query) {
  return request({
    url: '/sql/switch_error/export',
    method: 'get',
    params: query
  })
}