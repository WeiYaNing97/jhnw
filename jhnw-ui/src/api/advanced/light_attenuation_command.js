import request from '@/utils/request'

// 查询光衰命令列表
export function listLight_attenuation_command(query) {
  return request({
    url: '/advanced/light_attenuation_command/list',
    method: 'get',
    params: query
  })
}

// 查询光衰命令详细
export function getLight_attenuation_command(id) {
  return request({
    url: '/advanced/light_attenuation_command/' + id,
    method: 'get'
  })
}

// 新增光衰命令
export function addLight_attenuation_command(data) {
  return request({
    url: '/advanced/light_attenuation_command',
    method: 'post',
    data: data
  })
}

// 修改光衰命令
export function updateLight_attenuation_command(data) {
  return request({
    url: '/advanced/light_attenuation_command',
    method: 'put',
    data: data
  })
}

// 删除光衰命令
export function delLight_attenuation_command(id) {
  return request({
    url: '/advanced/light_attenuation_command/' + id,
    method: 'delete'
  })
}

// 导出光衰命令
export function exportLight_attenuation_command(query) {
  return request({
    url: '/advanced/light_attenuation_command/export',
    method: 'get',
    params: query
  })
}