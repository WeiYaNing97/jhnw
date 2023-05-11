import request from '@/utils/request'

// 查询交换机扫描结果列表
export function listSwitchscanresult(query) {
  return request({
    url: '/share/switchscanresult/list',
    method: 'get',
    params: query
  })
}

// 查询交换机扫描结果详细
export function getSwitchscanresult(id) {
  return request({
    url: '/share/switchscanresult/' + id,
    method: 'get'
  })
}

// 新增交换机扫描结果
export function addSwitchscanresult(data) {
  return request({
    url: '/share/switchscanresult',
    method: 'post',
    data: data
  })
}

// 修改交换机扫描结果
export function updateSwitchscanresult(data) {
  return request({
    url: '/share/switchscanresult',
    method: 'put',
    data: data
  })
}

// 删除交换机扫描结果
export function delSwitchscanresult(id) {
  return request({
    url: '/share/switchscanresult/' + id,
    method: 'delete'
  })
}

// 导出交换机扫描结果
export function exportSwitchscanresult(query) {
  return request({
    url: '/share/switchscanresult/export',
    method: 'get',
    params: query
  })
}