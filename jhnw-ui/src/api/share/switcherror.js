import request from '@/utils/request'

// 查询交换机错误列表
export function listSwitcherror(query) {
  return request({
    url: '/share/switcherror/list',
    method: 'get',
    params: query
  })
}

// 查询交换机错误详细
export function getSwitcherror(errorId) {
  return request({
    url: '/share/switcherror/' + errorId,
    method: 'get'
  })
}

// 新增交换机错误
export function addSwitcherror(data) {
  return request({
    url: '/share/switcherror',
    method: 'post',
    data: data
  })
}

// 修改交换机错误
export function updateSwitcherror(data) {
  return request({
    url: '/share/switcherror',
    method: 'put',
    data: data
  })
}

// 删除交换机错误
export function delSwitcherror(errorId) {
  return request({
    url: '/share/switcherror/' + errorId,
    method: 'delete'
  })
}

// 导出交换机错误
export function exportSwitcherror(query) {
  return request({
    url: '/share/switcherror/export',
    method: 'get',
    params: query
  })
}