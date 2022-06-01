import request from '@/utils/request'

// 查询多个扫描列表
export function listJh_test1(query) {
  return request({
    url: '/sql/jh_test1/list',
    method: 'get',
    params: query
  })
}

// 查询多个扫描详细
export function getJh_test1(id) {
  return request({
    url: '/sql/jh_test1/' + id,
    method: 'get'
  })
}

// 新增多个扫描
export function addJh_test1(data) {
  return request({
    url: '/sql/jh_test1',
    method: 'post',
    data: data
  })
}

// 修改多个扫描
export function updateJh_test1(data) {
  return request({
    url: '/sql/jh_test1',
    method: 'put',
    data: data
  })
}

// 删除多个扫描
export function delJh_test1(id) {
  return request({
    url: '/sql/jh_test1/' + id,
    method: 'delete'
  })
}

// 导出多个扫描
export function exportJh_test1(query) {
  return request({
    url: '/sql/jh_test1/export',
    method: 'get',
    params: query
  })
}