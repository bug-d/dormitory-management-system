/**
 * 首页统计接口
 * 路径：frontend/src/api/dashboard.js
 * 作用：包含仪表盘数据、图表数据等接口
 */

import request from '@/utils/request'

// ============ 统计卡片 ============

/**
 * 获取首页统计数据（统计卡片）
 * @returns {Promise}
 */
export const getDashboardStats = () => {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

/**
 * 获取首页统计数据（管理员专用）
 * @returns {Promise}
 */
export const getAdminDashboardStats = () => {
  return request({
    url: '/dashboard/admin/stats',
    method: 'get'
  })
}

/**
 * 获取首页统计数据（宿舍管理员专用）
 * @returns {Promise}
 */
export const getManagerDashboardStats = () => {
  return request({
    url: '/dashboard/manager/stats',
    method: 'get'
  })
}

/**
 * 获取首页统计数据（学生专用）
 * @returns {Promise}
 */
export const getStudentDashboardStats = () => {
  return request({
    url: '/dashboard/student/stats',
    method: 'get'
  })
}

// ============ 图表数据 ============

/**
 * 获取各楼栋入住率数据（柱状图）
 * @param {Object} params
 * @param {string} params.gender - 性别（可选）
 * @returns {Promise}
 */
export const getBuildingOccupancyData = (params) => {
  return request({
    url: '/dashboard/chart/building-occupancy',
    method: 'get',
    params
  })
}

/**
 * 获取男女比例数据（饼图）
 * @returns {Promise}
 */
export const getGenderRatioData = () => {
  return request({
    url: '/dashboard/chart/gender-ratio',
    method: 'get'
  })
}

/**
 * 获取各年级入住率数据（柱状图）
 * @returns {Promise}
 */
export const getGradeOccupancyData = () => {
  return request({
    url: '/dashboard/chart/grade-occupancy',
    method: 'get'
  })
}

/**
 * 获取申请趋势数据（折线图）
 * @param {Object} params
 * @param {number} params.days - 天数（默认30天）
 * @returns {Promise}
 */
export const getApplicationTrend = (params) => {
  return request({
    url: '/dashboard/chart/application-trend',
    method: 'get',
    params
  })
}

/**
 * 获取各类型申请占比数据（饼图）
 * @returns {Promise}
 */
export const getApplicationTypeData = () => {
  return request({
    url: '/dashboard/chart/application-type',
    method: 'get'
  })
}

/**
 * 获取各状态申请数量数据（柱状图）
 * @returns {Promise}
 */
export const getApplicationStatusData = () => {
  return request({
    url: '/dashboard/chart/application-status',
    method: 'get'
  })
}

/**
 * 获取各专业入住率数据（柱状图）
 * @param {Object} params
 * @param {string} params.grade - 年级（可选）
 * @returns {Promise}
 */
export const getMajorOccupancyData = (params) => {
  return request({
    url: '/dashboard/chart/major-occupancy',
    method: 'get',
    params
  })
}

// ============ 最近动态 ============

/**
 * 获取最近动态列表
 * @param {Object} params
 * @param {number} params.limit - 条数（默认10条）
 * @returns {Promise}
 */
export const getRecentActivities = (params) => {
  return request({
    url: '/dashboard/activities',
    method: 'get',
    params
  })
}

/**
 * 获取待办事项列表
 * @returns {Promise}
 */
export const getTodoList = () => {
  return request({
    url: '/dashboard/todos',
    method: 'get'
  })
}

// ============ 通知公告 ============

/**
 * 获取通知公告列表
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @returns {Promise}
 */
export const getNotifications = (params) => {
  return request({
    url: '/dashboard/notifications',
    method: 'get',
    params
  })
}

/**
 * 获取未读通知数量
 * @returns {Promise}
 */
export const getUnreadNotificationCount = () => {
  return request({
    url: '/dashboard/notifications/unread-count',
    method: 'get'
  })
}

/**
 * 标记通知为已读
 * @param {number} id - 通知ID
 * @returns {Promise}
 */
export const markNotificationRead = (id) => {
  return request({
    url: `/dashboard/notifications/${id}/read`,
    method: 'put'
  })
}

/**
 * 标记所有通知为已读
 * @returns {Promise}
 */
export const markAllNotificationsRead = () => {
  return request({
    url: '/dashboard/notifications/read-all',
    method: 'put'
  })
}