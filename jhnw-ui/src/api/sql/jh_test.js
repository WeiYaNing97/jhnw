import request from '@/utils/request'

// 查询嘉豪测试列表
export function listJh_test(query) {
  return request({
    url: '/sql/jh_test/list',
    method: 'get',
    params: query
  })
}

//连接ip
// export function con(datas){
//   return request({
//     url:'/sql/ConnectController/requestConnect',
//     method:'post',
//     params:datas
//   })
// }

//获取后台内容
export function testget1(datas) {
  return request({
    url:'/sql/ConnectController/testget',
    method:'get',
    params:datas
  })
}

// 查询嘉豪测试详细
export function getJh_test(id) {
  return request({
    url: '/sql/jh_test/' + id,
    method: 'get'
  })
}

// 新增嘉豪测试
export function addJh_test(data) {
  return request({
    url: '/sql/jh_test',
    method: 'post',
    data: data
  })
}

// 修改嘉豪测试
export function updateJh_test(data) {
  return request({
    url: '/sql/jh_test',
    method: 'put',
    data: data
  })
}

// 删除嘉豪测试
export function delJh_test(id) {
  return request({
    url: '/sql/jh_test/' + id,
    method: 'delete'
  })
}

// 导出嘉豪测试
export function exportJh_test(query) {
  return request({
    url: '/sql/jh_test/export',
    method: 'get',
    params: query
  })
}
