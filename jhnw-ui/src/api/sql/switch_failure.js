import request from '@/utils/request'

// 查询交换机故障列表
export function listSwitch_failure(query) {
  return request({
    url: '/sql/switch_failure/list',
    method: 'get',
    params: query
  })
}

// 查询交换机故障详细
export function getSwitch_failure(failureId) {
  return request({
    url: '/sql/switch_failure/' + failureId,
    method: 'get'
  })
}

// 新增交换机故障
export function addSwitch_failure(data) {
  return request({
    url: '/sql/switch_failure',
    method: 'post',
    data: data
  })
}

// 修改交换机故障
export function updateSwitch_failure(data) {
  return request({
    url: '/sql/switch_failure',
    method: 'put',
    data: data
  })
}

// 删除交换机故障
export function delSwitch_failure(failureId) {
  return request({
    url: '/sql/switch_failure/' + failureId,
    method: 'delete'
  })
}

// 导出交换机故障
export function exportSwitch_failure(query) {
  return request({
    url: '/sql/switch_failure/export',
    method: 'get',
    params: query
  })
}