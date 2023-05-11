import request from '@/utils/request'

// 查询返回信息列表
export function listReturnrecord(query) {
  return request({
    url: '/share/returnrecord/list',
    method: 'get',
    params: query
  })
}

// 查询返回信息详细
export function getReturnrecord(id) {
  return request({
    url: '/share/returnrecord/' + id,
    method: 'get'
  })
}

// 新增返回信息
export function addReturnrecord(data) {
  return request({
    url: '/share/returnrecord',
    method: 'post',
    data: data
  })
}

// 修改返回信息
export function updateReturnrecord(data) {
  return request({
    url: '/share/returnrecord',
    method: 'put',
    data: data
  })
}

// 删除返回信息
export function delReturnrecord(id) {
  return request({
    url: '/share/returnrecord/' + id,
    method: 'delete'
  })
}

// 导出返回信息
export function exportReturnrecord(query) {
  return request({
    url: '/share/returnrecord/export',
    method: 'get',
    params: query
  })
}