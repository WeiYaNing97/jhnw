import request from '@/utils/request'

// 查询查看解决列表
export function listLook_solve(query) {
  return request({
    url: '/sql/look_solve/list',
    method: 'get',
    params: query
  })
}

// 查询查看解决详细
export function getLook_solve(command) {
  return request({
    url: '/sql/look_solve/' + command,
    method: 'get'
  })
}

// 新增查看解决
export function addLook_solve(data) {
  return request({
    url: '/sql/look_solve',
    method: 'post',
    data: data
  })
}

// 修改查看解决
export function updateLook_solve(data) {
  return request({
    url: '/sql/look_solve',
    method: 'put',
    data: data
  })
}

// 删除查看解决
export function delLook_solve(command) {
  return request({
    url: '/sql/look_solve/' + command,
    method: 'delete'
  })
}

// 导出查看解决
export function exportLook_solve(query) {
  return request({
    url: '/sql/look_solve/export',
    method: 'get',
    params: query
  })
}