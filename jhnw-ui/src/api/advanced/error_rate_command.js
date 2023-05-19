import request from '@/utils/request'

// 查询误码率命令列表
export function listError_rate_command(query) {
  return request({
    url: '/advanced/error_rate_command/list',
    method: 'get',
    params: query
  })
}

// 查询误码率命令详细
export function getError_rate_command(id) {
  return request({
    url: '/advanced/error_rate_command/' + id,
    method: 'get'
  })
}

// 新增误码率命令
export function addError_rate_command(data) {
  return request({
    url: '/advanced/error_rate_command',
    method: 'post',
    data: data
  })
}

// 修改误码率命令
export function updateError_rate_command(data) {
  return request({
    url: '/advanced/error_rate_command',
    method: 'put',
    data: data
  })
}

// 删除误码率命令
export function delError_rate_command(id) {
  return request({
    url: '/advanced/error_rate_command/' + id,
    method: 'delete'
  })
}

// 导出误码率命令
export function exportError_rate_command(query) {
  return request({
    url: '/advanced/error_rate_command/export',
    method: 'get',
    params: query
  })
}