import request from '@/utils/request'

// 查询路由聚合命令列表
export function listRouteAggregationCommand(query) {
  return request({
    url: '/advanced/RouteAggregationCommand/list',
    method: 'get',
    params: query
  })
}

// 查询路由聚合命令详细
export function getRouteAggregationCommand(id) {
  return request({
    url: '/advanced/RouteAggregationCommand/' + id,
    method: 'get'
  })
}

// 新增路由聚合命令
export function addRouteAggregationCommand(data) {
  return request({
    url: '/advanced/RouteAggregationCommand',
    method: 'post',
    data: data
  })
}

// 修改路由聚合命令
export function updateRouteAggregationCommand(data) {
  return request({
    url: '/advanced/RouteAggregationCommand',
    method: 'put',
    data: data
  })
}

// 删除路由聚合命令
export function delRouteAggregationCommand(id) {
  return request({
    url: '/advanced/RouteAggregationCommand/' + id,
    method: 'delete'
  })
}

// 导出路由聚合命令
export function exportRouteAggregationCommand(query) {
  return request({
    url: '/advanced/RouteAggregationCommand/export',
    method: 'get',
    params: query
  })
}