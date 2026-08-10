/**
 * 审核接口
 * 路径：frontend/src/api/audit.js
 * 作用：包含审核相关的接口
 */

import request from '@/utils/request'

// ============ 审核查询 ============

/**
 * 查询所有待审核申请
 * @returns {Promise}
 */
export const getPendingList = () => {
  return request({
    url: '/admin/audit/pending',
    method: 'get'
  })
}

/**
 * 查询某个宿舍的待审核申请
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const getPendingByDormId = (dormId) => {
  return request({
    url: `/admin/audit/pending/${dormId}`,
    method: 'get'
  })
}

/**
 * 根据申请ID查询详情
 * @param {number} id - 申请ID
 * @returns {Promise}
 */
export const getAuditDetail = (id) => {
  return request({
    url: `/admin/audit/${id}`,
    method: 'get'
  })
}

// ============ 审核操作 ============

/**
 * 审核申请（通过/驳回）
 * @param {Object} data
 * @param {number} data.assignmentId - 申请ID
 * @param {string} data.action - 动作：approve/reject
 * @param {string} data.remark - 审核备注
 * @returns {Promise}
 */
export const audit = (data) => {
  return request({
    url: '/admin/audit/audit',
    method: 'put',
    data
  })
}

/**
 * 批量审核通过
 * @param {number[]} ids - 申请ID列表
 * @returns {Promise}
 */
export const batchApprove = (ids) => {
  return request({
    url: '/admin/audit/batch-approve',
    method: 'put',
    data: ids
  })
}

/**
 * 批量审核驳回
 * @param {Object} data
 * @param {number[]} data.ids - 申请ID列表
 * @param {string} data.remark - 驳回理由
 * @returns {Promise}
 */
export const batchReject = (data) => {
  return request({
    url: '/admin/audit/batch-reject',
    method: 'put',
    data
  })
}

// ============ 统计 ============

/**
 * 获取待审核数量
 * @returns {Promise}
 */
export const getPendingCount = () => {
  return request({
    url: '/admin/audit/stats/pending-count',
    method: 'get'
  })
}

/**
 * 获取各状态申请数量统计
 * @returns {Promise}
 */
export const getStatsByStatus = () => {
  return request({
    url: '/admin/audit/stats/by-status',
    method: 'get'
  })
}

/**
 * 获取各类型申请数量统计
 * @returns {Promise}
 */
export const getStatsByType = () => {
  return request({
    url: '/admin/audit/stats/by-type',
    method: 'get'
  })
}

// ============ 退宿管理 ============

/**
 * 强制退宿
 * @param {Object} data
 * @param {number} data.assignmentId - 入住记录ID
 * @param {string} data.reason - 退宿原因
 * @returns {Promise}
 */
export const forceLeave = (data) => {
  return request({
    url: `/admin/audit/force-leave/${data.assignmentId}`,
    method: 'put',
    params: { reason: data.reason }
  })
}

/**
 * 获取学生当前入住信息
 * @param {number} studentId - 学生ID
 * @returns {Promise}
 */
export const getStudentDormInfo = (studentId) => {
  return request({
    url: `/admin/audit/student-dorm/${studentId}`,
    method: 'get'
  })
}

/**
 * 获取宿舍入住人员列表
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const getDormResidents = (dormId) => {
  return request({
    url: `/admin/audit/dorm-residents/${dormId}`,
    method: 'get'
  })
}

/**
 * 获取宿舍入住历史
 * @param {number} dormId - 宿舍ID
 * @returns {Promise}
 */
export const getDormHistory = (dormId) => {
  return request({
    url: `/admin/audit/dorm-history/${dormId}`,
    method: 'get'
  })
}

// ============ 宿舍管理员端 ============

/**
 * 宿舍管理员查询待审核申请（管辖范围内）
 * @returns {Promise}
 */
export const getManagerPendingList = () => {
  return request({
    url: '/manager/audit/pending',
    method: 'get'
  })
}

/**
 * 宿舍管理员审核
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