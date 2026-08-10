/**
 * 权限接口
 * 路径：frontend/src/api/permission.js
 * 作用：包含权限校验、权限分配等接口
 */

import request from '@/utils/request'

// ============ 权限校验 ============

/**
 * 检查是否有某个权限
 * @param {string} permission - 权限标识
 * @returns {Promise}
 */
export const checkPermission = (permission) => {
  return request({
    url: '/permission/check',
    method: 'get',
    params: { permission }
  })
}

/**
 * 检查是否有某个角色
 * @param {string} role - 角色标识
 * @returns {Promise}
 */
export const checkRole = (role) => {
  return request({
    url: '/permission/check-role',
    method: 'get',
    params: { role }
  })
}

/**
 * 获取当前用户的所有权限
 * @returns {Promise}
 */
export const getCurrentPermissions = () => {
  return request({
    url: '/permission/current',
    method: 'get'
  })
}

// ============ 管理员权限管理 ============

/**
 * 分配管理员权限
 * @param {Object} data
 * @param {number} data.managerId - 管理员用户ID
 * @param {number} data.dormId - 宿舍ID
 * @param {string} data.permissionType - 权限类型（full/readonly）
 * @returns {Promise}
 */
export const assignPermission = (data) => {
  return request({
    url: '/admin/permissions/assign',
    method: 'post',
    data
  })
}

/**
 * 批量分配权限
 * @param {Object} data
 * @param {number} data.managerId - 管理员用户ID
 * @param {number[]} data.dormIds - 宿舍ID列表
 * @param {string} data.permissionType - 权限类型（full/readonly）
 * @returns {Promise}
 */
export const batchAssignPermissions = (data) => {
  return request({
    url: '/admin/permissions/batch-assign',
    method: 'post',
    data
  })
}

/**
 * 移除管理员的某个宿舍权限
 * @param {Object} data
 * @param {number} data.managerId - 管理员用户ID
 * @param {number} data.dormId - 宿舍ID
 * @returns {Promise}
 */
export const removePermission = (data) => {
  return request({
    url: '/admin/permissions/remove',
    method: 'delete',
    data
  })
}

/**
 * 移除管理员的所有权限
 * @param {number} managerId - 管理员用户ID
 * @returns {Promise}
 */
export const removeAllPermissions = (managerId) => {
  return request({
    url: `/admin/permissions/manager/${managerId}`,
    method: 'delete'
  })
}

/**
 * 移除某个宿舍的所有管理员权限
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const removePermissionsByDorm = (dormId) => {
  return request({
    url: `/admin/permissions/dorm/${dormId}`,
    method: 'delete'
  })
}

// ============ 权限查询 ============

/**
 * 查询管理员管辖的所有宿舍ID
 * @param {number} managerId - 管理员用户ID
 * @returns {Promise}
 */
export const getManagedDormIds = (managerId) => {
  return request({
    url: `/admin/permissions/manager/${managerId}/dorms`,
    method: 'get'
  })
}

/**
 * 查询管理员管辖的所有宿舍
 * @param {number} managerId - 管理员用户ID
 * @returns {Promise}
 */
export const getManagedDorms = (managerId) => {
  return request({
    url: `/admin/permissions/manager/${managerId}/dorms-detail`,
    method: 'get'
  })
}

/**
 * 查询某个宿舍的所有管理员
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const getDormManagers = (dormId) => {
  return request({
    url: `/admin/permissions/dorm/${dormId}/managers`,
    method: 'get'
  })
}

/**
 * 查询管理员的权限记录
 * @param {number} managerId - 管理员用户ID
 * @returns {Promise}
 */
export const getManagerPermissions = (managerId) => {
  return request({
    url: `/admin/permissions/manager/${managerId}/permissions`,
    method: 'get'
  })
}

// ============ 统计 ============

/**
 * 统计管理员管辖的宿舍数量
 * @param {number} managerId - 管理员用户ID
 * @returns {Promise}
 */
export const getManagedDormCount = (managerId) => {
  return request({
    url: `/admin/permissions/manager/${managerId}/count`,
    method: 'get'
  })
}

/**
 * 统计某个宿舍的管理员数量
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const getDormManagerCount = (dormId) => {
  return request({
    url: `/admin/permissions/dorm/${dormId}/manager-count`,
    method: 'get'
  })
}

/**
 * 获取所有有权限的管理员列表
 * @returns {Promise}
 */
export const getAllManagers = () => {
  return request({
    url: '/admin/permissions/managers',
    method: 'get'
  })
}