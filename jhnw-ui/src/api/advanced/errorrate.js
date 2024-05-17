import request from '@/utils/request'

// 查询错误包列表
export function listErrorrate(query) {
  return request({
    url: '/advanced/errorrate/list',
    method: 'get',
    params: query
  })
}

// 查询错误包详细
export function getErrorrate(id) {
  return request({
    url: '/advanced/errorrate/' + id,
    method: 'get'
  })
}

// 新增错误包
export function addErrorrate(data) {
  return request({
    url: '/advanced/errorrate',
    method: 'post',
    data: data
  })
}

// 修改错误包
export function updateErrorrate(data) {
  return request({
    url: '/advanced/errorrate',
    method: 'put',
    data: data
  })
}

// 删除错误包
export function delErrorrate(id) {
  return request({
    url: '/advanced/errorrate/' + id,
    method: 'delete'
  })
}

// 导出错误包
export function exportErrorrate(query) {
  return request({
    url: '/advanced/errorrate/export',
    method: 'get',
    params: query
  })
}
