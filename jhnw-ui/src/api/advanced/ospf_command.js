import request from '@/utils/request'

// 查询OSPF命令列表
export function listOspf_command(query) {
  return request({
    url: '/advanced/ospf_command/list',
    method: 'get',
    params: query
  })
}

// 查询OSPF命令详细
export function getOspf_command(id) {
  return request({
    url: '/advanced/ospf_command/' + id,
    method: 'get'
  })
}

// 新增OSPF命令
export function addOspf_command(data) {
  return request({
    url: '/advanced/ospf_command',
    method: 'post',
    data: data
  })
}

// 修改OSPF命令
export function updateOspf_command(data) {
  return request({
    url: '/advanced/ospf_command',
    method: 'put',
    data: data
  })
}

// 删除OSPF命令
export function delOspf_command(id) {
  return request({
    url: '/advanced/ospf_command/' + id,
    method: 'delete'
  })
}

// 导出OSPF命令
export function exportOspf_command(query) {
  return request({
    url: '/advanced/ospf_command/export',
    method: 'get',
    params: query
  })
}