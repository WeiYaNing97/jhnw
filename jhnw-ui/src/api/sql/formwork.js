import request from '@/utils/request'

// 查询问题模板列表
export function listFormwork(query) {
  return request({
    url: '/sql/formwork/list',
    method: 'get',
    params: query
  })
}

// 查询问题模板详细
export function getFormwork(id) {
  return request({
    url: '/sql/formwork/' + id,
    method: 'get'
  })
}

// 新增问题模板
export function addFormwork(data) {
  return request({
    url: '/sql/formwork',
    method: 'post',
    data: data
  })
}

// 修改问题模板
export function updateFormwork(data) {
  return request({
    url: '/sql/formwork',
    method: 'put',
    data: data
  })
}

// 删除问题模板
export function delFormwork(id) {
  return request({
    url: '/sql/formwork/' + id,
    method: 'delete'
  })
}

// 导出问题模板
export function exportFormwork(query) {
  return request({
    url: '/sql/formwork/export',
    method: 'get',
    params: query
  })
}