import request from '@/utils/request'

// 查询链路捆绑命令列表
export function listLinkBindingCommand(query) {
  return request({
    url: '/advanced/LinkBindingCommand/list',
    method: 'get',
    params: query
  })
}

// 查询链路捆绑命令详细
export function getLinkBindingCommand(id) {
  return request({
    url: '/advanced/LinkBindingCommand/' + id,
    method: 'get'
  })
}

// 新增链路捆绑命令
export function addLinkBindingCommand(data) {
  return request({
    url: '/advanced/LinkBindingCommand',
    method: 'post',
    data: data
  })
}

// 修改链路捆绑命令
export function updateLinkBindingCommand(data) {
  return request({
    url: '/advanced/LinkBindingCommand',
    method: 'put',
    data: data
  })
}

// 删除链路捆绑命令
export function delLinkBindingCommand(id) {
  return request({
    url: '/advanced/LinkBindingCommand/' + id,
    method: 'delete'
  })
}

// 导出链路捆绑命令
export function exportLinkBindingCommand(query) {
  return request({
    url: '/advanced/LinkBindingCommand/export',
    method: 'get',
    params: query
  })
}