/**
 * 报表接口
 * 路径：frontend/src/api/report.js
 * 作用：包含各类报表导出和统计数据接口
 */

import request from '@/utils/request'

// ============ 宿舍报表 ============

/**
 * 导出入住率报表
 * @param {Object} params
 * @param {string} params.buildingNo - 楼栋号（可选）
 * @param {string} params.gender - 性别（可选）
 * @returns {Promise}
 */
export const exportOccupancyReport = (params) => {
  return request({
    url: '/admin/reports/occupancy',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出空床位报表
 * @param {Object} params
 * @param {string} params.buildingNo - 楼栋号（可选）
 * @param {string} params.gender - 性别（可选）
 * @returns {Promise}
 */
export const exportVacantBedsReport = (params) => {
  return request({
    url: '/admin/reports/vacant-beds',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出宿舍详细信息报表
 * @param {Object} params
 * @param {string} params.buildingNo - 楼栋号（可选）
 * @returns {Promise}
 */
export const exportDormDetailReport = (params) => {
  return request({
    url: '/admin/reports/dorm-detail',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// ============ 学生报表 ============

/**
 * 导出新生分配报表
 * @param {Object} params
 * @param {string} params.grade - 年级（可选）
 * @param {string} params.gender - 性别（可选）
 * @returns {Promise}
 */
export const exportNewStudentReport = (params) => {
  return request({
    url: '/admin/reports/new-students',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出学生住宿信息报表
 * @param {Object} params
 * @param {string} params.grade - 年级（可选）
 * @param {string} params.major - 专业（可选）
 * @returns {Promise}
 */
export const exportStudentDormReport = (params) => {
  return request({
    url: '/admin/reports/student-dorm',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出学生名单
 * @param {Object} params
 * @param {string} params.grade - 年级（可选）
 * @param {string} params.major - 专业（可选）
 * @param {string} params.gender - 性别（可选）
 * @returns {Promise}
 */
export const exportStudentList = (params) => {
  return request({
    url: '/admin/reports/students',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// ============ 审核报表 ============

/**
 * 导出审核记录报表
 * @param {Object} params
 * @param {string} params.startDate - 开始日期
 * @param {string} params.endDate - 结束日期
 * @param {string} params.status - 状态（可选）
 * @returns {Promise}
 */
export const exportAuditReport = (params) => {
  return request({
    url: '/admin/reports/audit',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// ============ 综合报表 ============

/**
 * 导出一站式综合报表
 * @param {Object} params
 * @param {string} params.semester - 学期（可选）
 * @returns {Promise}
 */
export const exportComprehensiveReport = (params) => {
  return request({
    url: '/admin/reports/comprehensive',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// ============ 统计数据 ============

/**
 * 获取首页统计数据
 * @returns {Promise}
 */
export const getDashboardStats = () => {
  return request({
    url: '/admin/reports/dashboard',
    method: 'get'
  })
}

/**
 * 获取各楼栋入住率数据（图表）
 * @param {Object} params
 * @param {string} params.gender - 性别（可选）
 * @returns {Promise}
 */
export const getBuildingOccupancyData = (params) => {
  return request({
    url: '/admin/reports/building-occupancy',
    method: 'get',
    params
  })
}

/**
 * 获取各年级入住率数据（图表）
 * @returns {Promise}
 */
export const getGradeOccupancyData = () => {
  return request({
    url: '/admin/reports/grade-occupancy',
    method: 'get'
  })
}

/**
 * 获取男女比例数据（图表）
 * @returns {Promise}
 */
export const getGenderRatioData = () => {
  return request({
    url: '/admin/reports/gender-ratio',
    method: 'get'
  })
}

/**
 * 获取申请趋势数据（图表）
 * @param {Object} params
 * @param {number} params.days - 天数（默认30天）
 * @returns {Promise}
 */
export const getApplicationTrend = (params) => {
  return request({
    url: '/admin/reports/application-trend',
    method: 'get',
    params
  })
}