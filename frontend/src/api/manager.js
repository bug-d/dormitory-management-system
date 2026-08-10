/**
 * 宿舍管理员接口
 * 路径：frontend/src/api/manager.js
 * 作用：包含宿舍管理员相关的接口
 */

import request from '@/utils/request'

// ============ 管辖宿舍 ============

/**
 * 获取管理员管辖的宿舍列表
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.buildingNo - 楼栋号筛选
 * @param {string} params.status - 状态筛选
 * @returns {Promise}
 */
export const getManagedDorms = (params) => {
  return request({
    url: '/manager/dorms/page',
    method: 'get',
    params
  })
}

/**
 * 获取管理员管辖的所有宿舍
 * @returns {Promise}
 */
export const getAllManagedDorms = () => {
  return request({
    url: '/manager/dorms/list',
    method: 'get'
  })
}

/**
 * 根据ID查询管辖宿舍详情
 * @param {number} id - 宿舍ID
 * @returns {Promise}
 */
export const getManagedDormById = (id) => {
  return request({
    url: `/manager/dorms/${id}`,
    method: 'get'
  })
}

/**
 * 更新管辖宿舍信息
 * @param {Object} data - 宿舍数据
 * @returns {Promise}
 */
export const updateManagedDorm = (data) => {
  return request({
    url: '/manager/dorms',
    method: 'put',
    data
  })
}

// ============ 管辖宿舍统计 ============

/**
 * 获取管辖宿舍统计信息
 * @returns {Promise}
 */
export const getManagedDormStats = () => {
  return request({
    url: '/manager/dorms/stats/overall',
    method: 'get'
  })
}

/**
 * 获取管辖宿舍各楼栋统计
 * @returns {Promise}
 */
export const getManagedBuildingStats = () => {
  return request({
    url: '/manager/dorms/stats/buildings',
    method: 'get'
  })
}

// ============ 入住人员 ============

/**
 * 获取管辖宿舍的入住人员列表
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const getDormResidents = (dormId) => {
  return request({
    url: `/manager/dorms/${dormId}/residents`,
    method: 'get'
  })
}

/**
 * 获取管辖宿舍的入住历史
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const getDormHistory = (dormId) => {
  return request({
    url: `/manager/dorms/${dormId}/history`,
    method: 'get'
  })
}

// ============ 审核管理（管辖范围） ============

/**
 * 获取管辖范围内的待审核申请
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.status - 状态筛选
 * @param {string} params.type - 类型筛选
 * @returns {Promise}
 */
export const getManagerPendingList = (params) => {
  return request({
    url: '/manager/audit/pending',
    method: 'get',
    params
  })
}

/**
 * 审核申请（管辖范围内）
 * @param {Object} data
 * @param {number} data.assignmentId - 申请ID
 * @param {string} data.action - 动作：approve/reject
 * @param {string} data.remark - 审核备注
 * @returns {Promise}
 */
export const managerAudit = (data) => {
  return request({
    url: '/manager/audit/audit',
    method: 'put',
    data
  })
}

/**
 * 批量审核通过（管辖范围内）
 * @param {number[]} ids - 申请ID列表
 * @returns {Promise}
 */
export const managerBatchApprove = (ids) => {
  return request({
    url: '/manager/audit/batch-approve',
    method: 'put',
    data: ids
  })
}

/**
 * 批量审核驳回（管辖范围内）
 * @param {Object} data
 * @param {number[]} data.ids - 申请ID列表
 * @param {string} data.remark - 驳回理由
 * @returns {Promise}
 */
export const managerBatchReject = (data) => {
  return request({
    url: '/manager/audit/batch-reject',
    method: 'put',
    data
  })
}

/**
 * 获取管辖范围内待审核数量
 * @returns {Promise}
 */
export const getManagerPendingCount = () => {
  return request({
    url: '/manager/audit/stats/pending-count',
    method: 'get'
  })
}

/**
 * 获取管辖范围内各状态申请数量统计
 * @returns {Promise}
 */
export const getManagerAuditStats = () => {
  return request({
    url: '/manager/audit/stats/by-status',
    method: 'get'
  })
}

// ============ 报表导出 ============

/**
 * 导出管辖宿舍入住率报表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const exportManagedDormReport = (params) => {
  return request({
    url: '/manager/reports/occupancy',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出管辖宿舍学生列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const exportManagedStudentList = (params) => {
  return request({
    url: '/manager/reports/students',
    method: 'get',
    params,
    responseType: 'blob'
  })
}