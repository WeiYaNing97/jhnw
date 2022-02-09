import request from '@/utils/request'

// 查询交换机问题列表
export function listSwitch_problem(query) {
  return request({
    url: '/sql/switch_problem/list',
    method: 'get',
    params: query
  })
}

// 查询交换机问题详细
export function getSwitch_problem(id) {
  return request({
    url: '/sql/switch_problem/' + id,
    method: 'get'
  })
}

// 新增交换机问题
export function addSwitch_problem(data) {
  return request({
    url: '/sql/switch_problem',
    method: 'post',
    data: data
  })
}

// 修改交换机问题
export function updateSwitch_problem(data) {
  return request({
    url: '/sql/switch_problem',
    method: 'put',
    data: data
  })
}

// 删除交换机问题
export function delSwitch_problem(id) {
  return request({
    url: '/sql/switch_problem/' + id,
    method: 'delete'
  })
}

// 导出交换机问题
export function exportSwitch_problem(query) {
  return request({
    url: '/sql/switch_problem/export',
    method: 'get',
    params: query
  })
}