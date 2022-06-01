import request from '@/utils/request'

// 查询解决问题列表
export function listSolve_question(query) {
  return request({
    url: '/sql/solve_question/list',
    method: 'get',
    params: query
  })
}

// 查询解决问题详细
export function getSolve_question(commond) {
  return request({
    url: '/sql/solve_question/' + commond,
    method: 'get'
  })
}

// 新增解决问题
export function addSolve_question(data) {
  return request({
    url: '/sql/solve_question',
    method: 'post',
    data: data
  })
}

// 修改解决问题
export function updateSolve_question(data) {
  return request({
    url: '/sql/solve_question',
    method: 'put',
    data: data
  })
}

// 删除解决问题
export function delSolve_question(commond) {
  return request({
    url: '/sql/solve_question/' + commond,
    method: 'delete'
  })
}

// 导出解决问题
export function exportSolve_question(query) {
  return request({
    url: '/sql/solve_question/export',
    method: 'get',
    params: query
  })
}