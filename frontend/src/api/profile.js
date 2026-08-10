/**
 * 个人信息接口
 * 路径：frontend/src/api/profile.js
 * 作用：包含获取个人信息、修改密码、更新资料等接口
 */

import request from '@/utils/request'

// ============ 个人信息 ============

/**
 * 获取当前用户个人信息
 * @returns {Promise}
 */
export const getProfile = () => {
  return request({
    url: '/profile/info',
    method: 'get'
  })
}

/**
 * 更新个人信息
 * @param {Object} data
 * @param {string} data.realName - 真实姓名
 * @param {string} data.email - 邮箱
 * @param {string} data.phone - 手机号
 * @returns {Promise}
 */
export const updateProfile = (data) => {
  return request({
    url: '/profile/update',
    method: 'put',
    data
  })
}

/**
 * 上传头像
 * @param {File} file - 头像文件
 * @returns {Promise}
 */
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/profile/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ============ 密码管理 ============

/**
 * 修改密码
 * @param {Object} data
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @param {string} data.confirmPassword - 确认密码
 * @returns {Promise}
 */
export const changePassword = (data) => {
  return request({
    url: '/profile/change-password',
    method: 'put',
    data
  })
}

/**
 * 验证旧密码
 * @param {string} password - 旧密码
 * @returns {Promise}
 */
export const verifyPassword = (password) => {
  return request({
    url: '/profile/verify-password',
    method: 'post',
    data: { password }
  })
}

// ============ 学生专属 ============

/**
 * 获取学生详细信息（含学号、年级、专业等）
 * @returns {Promise}
 */
export const getStudentProfile = () => {
  return request({
    url: '/profile/student-info',
    method: 'get'
  })
}

/**
 * 更新学生信息
 * @param {Object} data
 * @param {string} data.phone - 手机号
 * @param {string} data.emergencyContact - 紧急联系人
 * @param {string} data.emergencyPhone - 紧急联系电话
 * @returns {Promise}
 */
export const updateStudentInfo = (data) => {
  return request({
    url: '/profile/student-info',
    method: 'put',
    data
  })
}

// ============ 宿舍管理员专属 ============

/**
 * 获取宿舍管理员详细信息（含管辖范围）
 * @returns {Promise}
 */
export const getManagerProfile = () => {
  return request({
    url: '/profile/manager-info',
    method: 'get'
  })
}

/**
 * 更新宿舍管理员信息
 * @param {Object} data
 * @param {string} data.phone - 手机号
 * @param {string} data.email - 邮箱
 * @returns {Promise}
 */
export const updateManagerInfo = (data) => {
  return request({
    url: '/profile/manager-info',
    method: 'put',
    data
  })
}