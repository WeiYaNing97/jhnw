import request from '@/utils/request'

// 查询问题扫描逻辑列表
export function listProblem_scan_logic(query) {
  return request({
    url: '/sql/problem_scan_logic/list',
    method: 'get',
    params: query
  })
}

// 查询问题扫描逻辑详细
export function getProblem_scan_logic(id) {
  return request({
    url: '/sql/problem_scan_logic/' + id,
    method: 'get'
  })
}

// 新增问题扫描逻辑
export function addProblem_scan_logic(data) {
  return request({
    url: '/sql/problem_scan_logic',
    method: 'post',
    data: data
  })
}

// 修改问题扫描逻辑
export function updateProblem_scan_logic(data) {
  return request({
    url: '/sql/problem_scan_logic',
    method: 'put',
    data: data
  })
}

// 删除问题扫描逻辑
export function delProblem_scan_logic(id) {
  return request({
    url: '/sql/problem_scan_logic/' + id,
    method: 'delete'
  })
}

// 导出问题扫描逻辑
export function exportProblem_scan_logic(query) {
  return request({
    url: '/sql/problem_scan_logic/export',
    method: 'get',
    params: query
  })
}