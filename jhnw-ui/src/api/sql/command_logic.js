import request from '@/utils/request'

// 查询命令逻辑列表
export function listCommand_logic(query) {
  return request({
    url: '/sql/command_logic/list',
    method: 'get',
    params: query
  })
}

// 查询命令逻辑详细
export function getCommand_logic(id) {
  return request({
    url: '/sql/command_logic/' + id,
    method: 'get'
  })
}

// 新增命令逻辑
export function addCommand_logic(data) {
  return request({
    url: '/sql/command_logic',
    method: 'post',
    data: data
  })
}

// 修改命令逻辑
export function updateCommand_logic(data) {
  return request({
    url: '/sql/command_logic',
    method: 'put',
    data: data
  })
}

// 删除命令逻辑
export function delCommand_logic(id) {
  return request({
    url: '/sql/command_logic/' + id,
    method: 'delete'
  })
}

// 导出命令逻辑
export function exportCommand_logic(query) {
  return request({
    url: '/sql/command_logic/export',
    method: 'get',
    params: query
  })
}