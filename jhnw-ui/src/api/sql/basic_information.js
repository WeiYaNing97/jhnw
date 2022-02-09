import request from '@/utils/request'

// 查询获取基本信息命令列表
export function listBasic_information(query) {
  return request({
    url: '/sql/basic_information/list',
    method: 'get',
    params: query
  })
}

// 查询获取基本信息命令详细
export function getBasic_information(id) {
  return request({
    url: '/sql/basic_information/' + id,
    method: 'get'
  })
}

// 新增获取基本信息命令
export function addBasic_information(data) {
  return request({
    url: '/sql/basic_information',
    method: 'post',
    data: data
  })
}

// 修改获取基本信息命令
export function updateBasic_information(data) {
  return request({
    url: '/sql/basic_information',
    method: 'put',
    data: data
  })
}

// 删除获取基本信息命令
export function delBasic_information(id) {
  return request({
    url: '/sql/basic_information/' + id,
    method: 'delete'
  })
}

// 导出获取基本信息命令
export function exportBasic_information(query) {
  return request({
    url: '/sql/basic_information/export',
    method: 'get',
    params: query
  })
}