import request from '@/utils/request'

// 查询定时任务列表
export function listTimedTask(query) {
  return request({
    url: '/sql/TimedTask/list',
    method: 'get',
    params: query
  })
}

// 查询定时任务详细
export function getTimedTask(id) {
  return request({
    url: '/sql/TimedTask/' + id,
    method: 'get'
  })
}

// 新增定时任务
export function addTimedTask(data) {
  return request({
    url: '/sql/TimedTask',
    method: 'post',
    data: data
  })
}

// 修改定时任务
export function updateTimedTask(data) {
  return request({
    url: '/sql/TimedTask',
    method: 'put',
    data: data
  })
}

// 删除定时任务
export function delTimedTask(id) {
  return request({
    url: '/sql/TimedTask/' + id,
    method: 'delete'
  })
}

// 导出定时任务
export function exportTimedTask(query) {
  return request({
    url: '/sql/TimedTask/export',
    method: 'get',
    params: query
  })
}
