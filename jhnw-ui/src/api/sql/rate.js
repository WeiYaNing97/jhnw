import request from '@/utils/request'

// 查询错误包列表
export function listRate(query) {
  return request({
    url: '/sql/rate/list',
    method: 'get',
    params: query
  })
}

// 查询错误包详细
export function getRate(id) {
  return request({
    url: '/sql/rate/' + id,
    method: 'get'
  })
}

// 新增错误包
export function addRate(data) {
  return request({
    url: '/sql/rate',
    method: 'post',
    data: data
  })
}

// 修改错误包
export function updateRate(data) {
  return request({
    url: '/sql/rate',
    method: 'put',
    data: data
  })
}

// 删除错误包
export function delRate(id) {
  return request({
    url: '/sql/rate/' + id,
    method: 'delete'
  })
}

// 导出错误包
export function exportRate(query) {
  return request({
    url: '/sql/rate/export',
    method: 'get',
    params: query
  })
}
