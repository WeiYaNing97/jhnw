import request from '@/utils/request'

// 查询光衰平均值比较列表
export function listComparison(query) {
  return request({
    url: '/advanced/comparison/list',
    method: 'get',
    params: query
  })
}

// 查询光衰平均值比较详细
export function getComparison(id) {
  return request({
    url: '/advanced/comparison/' + id,
    method: 'get'
  })
}

// 新增光衰平均值比较
export function addComparison(data) {
  return request({
    url: '/advanced/comparison',
    method: 'post',
    data: data
  })
}

// 修改光衰平均值比较
export function updateComparison(data) {
  return request({
    url: '/advanced/comparison',
    method: 'put',
    data: data
  })
}

// 删除光衰平均值比较
export function delComparison(id) {
  return request({
    url: '/advanced/comparison/' + id,
    method: 'delete'
  })
}

// 导出光衰平均值比较
export function exportComparison(query) {
  return request({
    url: '/advanced/comparison/export',
    method: 'get',
    params: query
  })
}
