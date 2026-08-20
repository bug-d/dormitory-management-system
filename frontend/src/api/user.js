/**
 * 用户管理接口
 * 路径：frontend/src/api/user.js
 * 作用：包含用户增删改查等接口
 */

import request from '@/utils/request'

// ============ 基础 CRUD ============

/**
 * 分页查询用户（支持排序）
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.keyword - 搜索关键字
 * @param {string} params.role - 角色筛选
 * @param {number} params.status - 状态筛选
 * @param {string} params.orderBy - 排序字段（id/username/realName/role）
 * @param {string} params.orderDir - 排序方向（asc/desc）
 * @returns {Promise}
 */
export const getUsers = (params) => {
  return request({
    url: '/admin/users/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有用户
 * @returns {Promise}
 */
export const getAllUsers = () => {
  return request({
    url: '/admin/users/list',
    method: 'get'
  })
}

/**
 * 根据ID查询用户
 * @param {number} id - 用户ID
 * @returns {Promise}
 */
export const getUserById = (id) => {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

/**
 * 新增用户
 * @param {Object} data - 用户数据
 * @returns {Promise}
 */
export const addUser = (data) => {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 * @param {Object} data - 用户数据
 * @returns {Promise}
 */
export const updateUser = (data) => {
  return request({
    url: '/admin/users',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 * @returns {Promise}
 */
export const deleteUser = (id) => {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

// ============ 状态管理 ============

/**
 * 启用用户
 * @param {number} id - 用户ID
 * @returns {Promise}
 */
export const enableUser = (id) => {
  return request({
    url: `/admin/users/${id}/enable`,
    method: 'put'
  })
}

/**
 * 禁用用户
 * @param {number} id - 用户ID
 * @returns {Promise}
 */
export const disableUser = (id) => {
  return request({
    url: `/admin/users/${id}/disable`,
    method: 'put'
  })
}

/**
 * 重置用户密码
 * @param {number} id - 用户ID
 * @returns {Promise}
 */
export const resetPassword = (id) => {
  return request({
    url: `/admin/users/${id}/reset-password`,
    method: 'put'
  })
}

// ============ 角色查询 ============

/**
 * 查询所有系统管理员
 * @returns {Promise}
 */
export const getAdmins = () => {
  return request({
    url: '/admin/users/admins',
    method: 'get'
  })
}

/**
 * 查询所有宿舍管理员
 * @returns {Promise}
 */
export const getManagers = () => {
  return request({
    url: '/admin/users/managers',
    method: 'get'
  })
}

// ============ 统计 ============

/**
 * 获取用户总数
 * @returns {Promise}
 */
export const getUserCount = () => {
  return request({
    url: '/admin/users/stats/count',
    method: 'get'
  })
}