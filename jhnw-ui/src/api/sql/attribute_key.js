import request from '@/utils/request'

// 查询基本信息属性值列表
export function listAttribute_key(query) {
  return request({
    url: '/sql/attribute_key/list',
    method: 'get',
    params: query
  })
}

// 查询基本信息属性值详细
export function getAttribute_key(attributeKey) {
  return request({
    url: '/sql/attribute_key/' + attributeKey,
    method: 'get'
  })
}

// 新增基本信息属性值
export function addAttribute_key(data) {
  return request({
    url: '/sql/attribute_key',
    method: 'post',
    data: data
  })
}

// 修改基本信息属性值
export function updateAttribute_key(data) {
  return request({
    url: '/sql/attribute_key',
    method: 'put',
    data: data
  })
}

// 删除基本信息属性值
export function delAttribute_key(attributeKey) {
  return request({
    url: '/sql/attribute_key/' + attributeKey,
    method: 'delete'
  })
}

// 导出基本信息属性值
export function exportAttribute_key(query) {
  return request({
    url: '/sql/attribute_key/export',
    method: 'get',
    params: query
  })
}