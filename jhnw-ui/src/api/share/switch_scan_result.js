import request from '@/utils/request'

// 查询交换机扫描结果列表
export function listSwitch_scan_result(query) {
  return request({
    url: '/share/switch_scan_result/list',
    method: 'get',
    params: query
  })
}

// 查询交换机扫描结果详细
export function getSwitch_scan_result(id) {
  return request({
    url: '/share/switch_scan_result/' + id,
    method: 'get'
  })
}

// 新增交换机扫描结果
export function addSwitch_scan_result(data) {
  return request({
    url: '/share/switch_scan_result',
    method: 'post',
    data: data
  })
}

// 修改交换机扫描结果
export function updateSwitch_scan_result(data) {
  return request({
    url: '/share/switch_scan_result',
    method: 'put',
    data: data
  })
}

// 删除交换机扫描结果
export function delSwitch_scan_result(id) {
  return request({
    url: '/share/switch_scan_result/' + id,
    method: 'delete'
  })
}

// 导出交换机扫描结果
export function exportSwitch_scan_result(query) {
  return request({
    url: '/share/switch_scan_result/export',
    method: 'get',
    params: query
  })
}