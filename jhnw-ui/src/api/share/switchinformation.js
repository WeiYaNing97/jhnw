import request from '@/utils/request'

// 查询交换机四项基本信息列表
export function listSwitchinformation(query) {
  return request({
    url: '/share/switchinformation/list',
    method: 'get',
    params: query
  })
}

// 查询交换机四项基本信息详细
export function getSwitchinformation(id) {
  return request({
    url: '/share/switchinformation/' + id,
    method: 'get'
  })
}

// 新增交换机四项基本信息
export function addSwitchinformation(data) {
  return request({
    url: '/share/switchinformation',
    method: 'post',
    data: data
  })
}

// 修改交换机四项基本信息
export function updateSwitchinformation(data) {
  return request({
    url: '/share/switchinformation',
    method: 'put',
    data: data
  })
}

// 删除交换机四项基本信息
export function delSwitchinformation(id) {
  return request({
    url: '/share/switchinformation/' + id,
    method: 'delete'
  })
}

// 导出交换机四项基本信息
export function exportSwitchinformation(query) {
  return request({
    url: '/share/switchinformation/export',
    method: 'get',
    params: query
  })
}