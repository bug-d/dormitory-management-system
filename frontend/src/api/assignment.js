/**
 * 入住申请接口
 * 路径：frontend/src/api/assignment.js
 * 作用：包含选宿舍、换宿舍、退宿等接口
 */

import request from '@/utils/request'

// ============ 学生端 ============

export const getAvailableStudentDorms = () => {
  return request({
    url: '/student/dorms/available',
    method: 'get'
  })
}

/**
 * 申请入住（选宿舍）
 * @param {Object} data
 * @param {number} data.dormId - 宿舍ID
 * @param {string} data.bedNo - 床号
 * @param {string} data.applyReason - 申请理由
 * @returns {Promise}
 */
export const applyCheckin = (data) => {
  return request({
    url: '/student/apply-checkin',
    method: 'post',
    data
  })
}

/**
 * 申请换宿舍
 * @param {Object} data
 * @param {number} data.dormId - 目标宿舍ID
 * @param {string} data.bedNo - 床号
 * @param {string} data.applyReason - 申请理由
 * @returns {Promise}
 */
export const applyTransfer = (data) => {
  return request({
    url: '/student/apply-transfer',
    method: 'post',
    data
  })
}

/**
 * 撤销申请
 * @param {number} assignmentId - 申请ID
 * @returns {Promise}
 */
export const cancelApplication = (assignmentId) => {
  return request({
    url: `/student/applications/${assignmentId}`,
    method: 'delete'
  })
}

/**
 * 获取我的宿舍信息
 * @returns {Promise}
 */
export const getMyDorm = () => {
  return request({
    url: '/student/my-dorm',
    method: 'get'
  })
}

/**
 * 退宿
 * @returns {Promise}
 */
export const leaveDorm = () => {
  return request({
    url: '/student/leave-dorm',
    method: 'put'
  })
}

/**
 * 获取我的申请记录
 * @returns {Promise}
 */
export const getMyApplications = () => {
  return request({
    url: '/student/applications',
    method: 'get'
  })
}

/**
 * 获取待审核申请数量
 * @returns {Promise}
 */
export const getMyPendingCount = () => {
  return request({
    url: '/student/applications/pending-count',
    method: 'get'
  })
}

// ============ 管理员端 ============

/**
 * 分页查询申请记录
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {number} params.studentId - 学生ID（可选）
 * @param {number} params.dormId - 宿舍ID（可选）
 * @param {string} params.status - 状态筛选（可选）
 * @param {string} params.type - 类型筛选（可选）
 * @returns {Promise}
 */
export const getAssignments = (params) => {
  return request({
    url: '/admin/assignments/page',
    method: 'get',
    params
  })
}

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
 * 根据ID查询申请详情
 * @param {number} id - 申请ID
 * @returns {Promise}
 */
export const getAssignmentById = (id) => {
  return request({
    url: `/admin/audit/${id}`,
    method: 'get'
  })
}

/**
 * 审核申请（通过/驳回）
 * @param {Object} data
 * @param {number} data.assignmentId - 申请ID
 * @param {string} data.action - 动作：approve/reject
 * @param {string} data.remark - 审核备注
 * @returns {Promise}
 */
export const auditAssignment = (data) => {
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
export const getAssignmentStatsByStatus = () => {
  return request({
    url: '/admin/audit/stats/by-status',
    method: 'get'
  })
}

/**
 * 获取各类型申请数量统计
 * @returns {Promise}
 */
export const getAssignmentStatsByType = () => {
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
export const forceLeaveDorm = (data) => {
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
