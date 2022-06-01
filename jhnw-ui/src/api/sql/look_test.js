import request from '@/utils/request'

// 查询查看问题列表
export function listLook_test(query) {
  return request({
    url: '/sql/look_test/list',
    method: 'get',
    params: query
  })
}

// 查询查看问题详细
export function getLook_test(id) {
  return request({
    url: '/sql/look_test/' + id,
    method: 'get'
  })
}

// 新增查看问题
export function addLook_test(data) {
  return request({
    url: '/sql/look_test',
    method: 'post',
    data: data
  })
}

// 修改查看问题
export function updateLook_test(data) {
  return request({
    url: '/sql/look_test',
    method: 'put',
    data: data
  })
}

// 删除查看问题
export function delLook_test(id) {
  return request({
    url: '/sql/look_test/' + id,
    method: 'delete'
  })
}

// 导出查看问题
export function exportLook_test(query) {
  return request({
    url: '/sql/look_test/export',
    method: 'get',
    params: query
  })
}