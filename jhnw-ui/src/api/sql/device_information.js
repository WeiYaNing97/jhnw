import request from '@/utils/request'

// 查询设备基本信息列表
export function listDevice_information(query) {
  return request({
    url: '/sql/device_information/list',
    method: 'get',
    params: query
  })
}

// 查询设备基本信息详细
export function getDevice_information(id) {
  return request({
    url: '/sql/device_information/' + id,
    method: 'get'
  })
}

// 新增设备基本信息
export function addDevice_information(data) {
  return request({
    url: '/sql/device_information',
    method: 'post',
    data: data
  })
}

// 修改设备基本信息
export function updateDevice_information(data) {
  return request({
    url: '/sql/device_information',
    method: 'put',
    data: data
  })
}

// 删除设备基本信息
export function delDevice_information(id) {
  return request({
    url: '/sql/device_information/' + id,
    method: 'delete'
  })
}

// 导出设备基本信息
export function exportDevice_information(query) {
  return request({
    url: '/sql/device_information/export',
    method: 'get',
    params: query
  })
}