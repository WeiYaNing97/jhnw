import request from '@/utils/request'

// 查询错误包列表
export function listRate(query) {
  return request({
    url: '/advanced/rate/list',
    method: 'get',
    params: query
  })
}

// 查询错误包详细
export function getRate(id) {
  return request({
    url: '/advanced/rate/' + id,
    method: 'get'
  })
}

// 新增错误包
export function addRate(data) {
  return request({
    url: '/advanced/rate',
    method: 'post',
    data: data
  })
}

// 修改错误包
export function updateRate(data) {
  return request({
    url: '/advanced/rate',
    method: 'put',
    data: data
  })
}

// 删除错误包
export function delRate(id) {
  return request({
    url: '/advanced/rate/' + id,
    method: 'delete'
  })
}

// 导出错误包
export function exportRate(query) {
  return request({
    url: '/advanced/rate/export',
    method: 'get',
    params: query
  })
}
