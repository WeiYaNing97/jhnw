import request from '@/utils/request'

// 查询操作日志记录列表
export function listSwitch_oper_log(query) {
  return request({
    url: '/system/switch_oper_log/list',
    method: 'get',
    params: query
  })
}

// 查询操作日志记录详细
export function getSwitch_oper_log(operId) {
  return request({
    url: '/system/switch_oper_log/' + operId,
    method: 'get'
  })
}

// 新增操作日志记录
export function addSwitch_oper_log(data) {
  return request({
    url: '/system/switch_oper_log',
    method: 'post',
    data: data
  })
}

// 修改操作日志记录
export function updateSwitch_oper_log(data) {
  return request({
    url: '/system/switch_oper_log',
    method: 'put',
    data: data
  })
}

// 删除操作日志记录
export function delSwitch_oper_log(operId) {
  return request({
    url: '/system/switch_oper_log/' + operId,
    method: 'delete'
  })
}

// 导出操作日志记录
export function exportSwitch_oper_log(query) {
  return request({
    url: '/system/switch_oper_log/export',
    method: 'get',
    params: query
  })
}