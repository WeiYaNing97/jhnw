import request from '@/utils/request'

// 查询取值信息存储列表
export function listValue_information(query) {
  return request({
    url: '/sql/value_information/list',
    method: 'get',
    params: query
  })
}

// 查询取值信息存储详细
export function getValue_information(id) {
  return request({
    url: '/sql/value_information/' + id,
    method: 'get'
  })
}

// 新增取值信息存储
export function addValue_information(data) {
  return request({
    url: '/sql/value_information',
    method: 'post',
    data: data
  })
}

// 修改取值信息存储
export function updateValue_information(data) {
  return request({
    url: '/sql/value_information',
    method: 'put',
    data: data
  })
}

// 删除取值信息存储
export function delValue_information(id) {
  return request({
    url: '/sql/value_information/' + id,
    method: 'delete'
  })
}

// 导出取值信息存储
export function exportValue_information(query) {
  return request({
    url: '/sql/value_information/export',
    method: 'get',
    params: query
  })
}