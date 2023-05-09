import request from '@/utils/request'

// 查询交换机四项基本信息列表
export function listSwitchInformation(query) {
  return request({
    url: '/sql/switchInformation/list',
    method: 'get',
    params: query
  })
}

// 查询交换机四项基本信息详细
export function getSwitchInformation(id) {
  return request({
    url: '/sql/switchInformation/' + id,
    method: 'get'
  })
}

// 新增交换机四项基本信息
export function addSwitchInformation(data) {
  return request({
    url: '/sql/switchInformation',
    method: 'post',
    data: data
  })
}

// 修改交换机四项基本信息
export function updateSwitchInformation(data) {
  return request({
    url: '/sql/switchInformation',
    method: 'put',
    data: data
  })
}

// 删除交换机四项基本信息
export function delSwitchInformation(id) {
  return request({
    url: '/sql/switchInformation/' + id,
    method: 'delete'
  })
}

// 导出交换机四项基本信息
export function exportSwitchInformation(query) {
  return request({
    url: '/sql/switchInformation/export',
    method: 'get',
    params: query
  })
}