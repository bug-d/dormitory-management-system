/**
 * 认证接口
 * 路径：frontend/src/api/auth.js
 * 作用：包含登录、登出、刷新Token等接口
 */

import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录参数
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * @returns {Promise}
 */
export const logout = () => {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 刷新Token
 * @returns {Promise}
 */
export const refreshToken = () => {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}

/**
 * 测试Token是否有效
 * @returns {Promise}
 */
export const testToken = () => {
  return request({
    url: '/auth/test',
    method: 'get'
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export const getCurrentUser = () => {
  return request({
    url: '/auth/current-user',
    method: 'get'
  })
}

/**
 * 修改密码
 * @param {Object} data
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise}
 */
export const changePassword = (data) => {
  return request({
    url: '/auth/change-password',
    method: 'put',
    data
  })
}