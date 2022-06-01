import request from '@/utils/request'

// 查询命令列表
export function listBasic_command(query) {
  return request({
    url: '/sql/basic_command/list',
    method: 'get',
    params: query
  })
}

// 查询命令详细
export function getBasic_command(id) {
  return request({
    url: '/sql/basic_command/' + id,
    method: 'get'
  })
}

// 新增命令
export function addBasic_command(data) {
  return request({
    url: '/sql/basic_command',
    method: 'post',
    data: data
  })
}

// 修改命令
export function updateBasic_command(data) {
  return request({
    url: '/sql/basic_command',
    method: 'put',
    data: data
  })
}

// 删除命令
export function delBasic_command(id) {
  return request({
    url: '/sql/basic_command/' + id,
    method: 'delete'
  })
}

// 导出命令
export function exportBasic_command(query) {
  return request({
    url: '/sql/basic_command/export',
    method: 'get',
    params: query
  })
}