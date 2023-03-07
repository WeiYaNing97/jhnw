import request from '@/utils/request'

// 查询交换机信息列表
export function listInformation(query) {
  return request({
    url: '/sql/information/list',
    method: 'get',
    params: query
  })
}

// 查询交换机信息详细
export function getInformation(id) {
  return request({
    url: '/sql/information/' + id,
    method: 'get'
  })
}

// 新增交换机信息
export function addInformation(data) {
  return request({
    url: '/sql/information',
    method: 'post',
    data: data
  })
}

// 修改交换机信息
export function updateInformation(data) {
  return request({
    url: '/sql/information',
    method: 'put',
    data: data
  })
}

// 删除交换机信息
export function delInformation(id) {
  return request({
    url: '/sql/information/' + id,
    method: 'delete'
  })
}

// 导出交换机信息
export function exportInformation(query) {
  return request({
    url: '/sql/information/export',
    method: 'get',
    params: query
  })
}