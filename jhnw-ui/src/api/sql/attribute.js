import request from '@/utils/request'

// 查询基本信息属性列表
export function listAttribute(query) {
  return request({
    url: '/sql/attribute/list',
    method: 'get',
    params: query
  })
}

// 查询基本信息属性详细
export function getAttribute(attributeKey) {
  return request({
    url: '/sql/attribute/' + attributeKey,
    method: 'get'
  })
}

// 新增基本信息属性
export function addAttribute(data) {
  return request({
    url: '/sql/attribute',
    method: 'post',
    data: data
  })
}

// 修改基本信息属性
export function updateAttribute(data) {
  return request({
    url: '/sql/attribute',
    method: 'put',
    data: data
  })
}

// 删除基本信息属性
export function delAttribute(attributeKey) {
  return request({
    url: '/sql/attribute/' + attributeKey,
    method: 'delete'
  })
}

// 导出基本信息属性
export function exportAttribute(query) {
  return request({
    url: '/sql/attribute/export',
    method: 'get',
    params: query
  })
}