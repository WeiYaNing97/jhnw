import request from '@/utils/request'

// 查询问题及命令列表
export function listTotal_question_table(query) {
  return request({
    url: '/sql/total_question_table/list',
    method: 'get',
    params: query
  })
}

// 查询问题及命令详细
export function getTotal_question_table(id) {
  return request({
    url: '/sql/total_question_table/' + id,
    method: 'get'
  })
}

// 新增问题及命令
export function addTotal_question_table(data) {
  return request({
    url: '/sql/total_question_table',
    method: 'post',
    data: data
  })
}

// 修改问题及命令
export function updateTotal_question_table(data) {
  return request({
    url: '/sql/total_question_table',
    method: 'put',
    data: data
  })
}

// 删除问题及命令
export function delTotal_question_table(id) {
  return request({
    url: '/sql/total_question_table/' + id,
    method: 'delete'
  })
}

// 导出问题及命令
export function exportTotal_question_table(query) {
  return request({
    url: '/sql/total_question_table/export',
    method: 'get',
    params: query
  })
}