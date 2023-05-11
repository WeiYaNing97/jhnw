import request from '@/utils/request'

// 查询交换机故障列表
export function listSwitchfailure(query) {
  return request({
    url: '/share/switchfailure/list',
    method: 'get',
    params: query
  })
}

// 查询交换机故障详细
export function getSwitchfailure(failureId) {
  return request({
    url: '/share/switchfailure/' + failureId,
    method: 'get'
  })
}

// 新增交换机故障
export function addSwitchfailure(data) {
  return request({
    url: '/share/switchfailure',
    method: 'post',
    data: data
  })
}

// 修改交换机故障
export function updateSwitchfailure(data) {
  return request({
    url: '/share/switchfailure',
    method: 'put',
    data: data
  })
}

// 删除交换机故障
export function delSwitchfailure(failureId) {
  return request({
    url: '/share/switchfailure/' + failureId,
    method: 'delete'
  })
}

// 导出交换机故障
export function exportSwitchfailure(query) {
  return request({
    url: '/share/switchfailure/export',
    method: 'get',
    params: query
  })
}