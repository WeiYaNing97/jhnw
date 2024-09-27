import request from '@/utils/request'

// 查询错误代码列表
export function listErrorCodeTable(query) {
  return request({
    url: '/share/ErrorCodeTable/list',
    method: 'get',
    params: query
  })
}

// 查询错误代码详细
export function getErrorCodeTable(errorCodeNumber) {
  return request({
    url: '/share/ErrorCodeTable/' + errorCodeNumber,
    method: 'get'
  })
}

// 新增错误代码
export function addErrorCodeTable(data) {
  return request({
    url: '/share/ErrorCodeTable',
    method: 'post',
    data: data
  })
}

// 修改错误代码
export function updateErrorCodeTable(data) {
  return request({
    url: '/share/ErrorCodeTable',
    method: 'put',
    data: data
  })
}

// 删除错误代码
export function delErrorCodeTable(errorCodeNumber) {
  return request({
    url: '/share/ErrorCodeTable/' + errorCodeNumber,
    method: 'delete'
  })
}

// 导出错误代码
export function exportErrorCodeTable(query) {
  return request({
    url: '/share/ErrorCodeTable/export',
    method: 'get',
    params: query
  })
}