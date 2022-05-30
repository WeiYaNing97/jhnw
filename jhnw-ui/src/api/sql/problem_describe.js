import request from '@/utils/request'

// 查询问题描述列表
export function listProblem_describe(query) {
  return request({
    url: '/sql/problem_describe/list',
    method: 'get',
    params: query
  })
}

// 查询问题描述详细
export function getProblem_describe(id) {
  return request({
    url: '/sql/problem_describe/' + id,
    method: 'get'
  })
}

// 新增问题描述
export function addProblem_describe(data) {
  return request({
    url: '/sql/problem_describe',
    method: 'post',
    data: data
  })
}

// 修改问题描述
export function updateProblem_describe(data) {
  return request({
    url: '/sql/problem_describe',
    method: 'put',
    data: data
  })
}

// 删除问题描述
export function delProblem_describe(id) {
  return request({
    url: '/sql/problem_describe/' + id,
    method: 'delete'
  })
}

// 导出问题描述
export function exportProblem_describe(query) {
  return request({
    url: '/sql/problem_describe/export',
    method: 'get',
    params: query
  })
}