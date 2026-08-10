/**
 * 本地存储工具
 * 路径：frontend/src/utils/storage.js
 * 作用：封装 localStorage 操作，统一管理 Token 和数据持久化
 */

// ============ 存储键名常量 ============
const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_ROLE: 'role',
  USER_NAME: 'userName',
  USER_INFO: 'userInfo'
}

// ============ Token 操作 ============

/**
 * 获取 Token
 * @returns {string} Token
 */
export const getToken = () => {
  return localStorage.getItem(STORAGE_KEYS.TOKEN) || ''
}

/**
 * 设置 Token
 * @param {string} token - JWT Token
 */
export const setToken = (token) => {
  localStorage.setItem(STORAGE_KEYS.TOKEN, token)
}

/**
 * 移除 Token
 */
export const removeToken = () => {
  localStorage.removeItem(STORAGE_KEYS.TOKEN)
}

// ============ 用户信息操作 ============

/**
 * 获取用户角色
 * @returns {string} 用户角色
 */
export const getUserRole = () => {
  return localStorage.getItem(STORAGE_KEYS.USER_ROLE) || ''
}

/**
 * 设置用户角色
 * @param {string} role - 用户角色
 */
export const setUserRole = (role) => {
  localStorage.setItem(STORAGE_KEYS.USER_ROLE, role)
}

/**
 * 获取用户姓名
 * @returns {string} 用户姓名
 */
export const getUserName = () => {
  return localStorage.getItem(STORAGE_KEYS.USER_NAME) || ''
}

/**
 * 设置用户姓名
 * @param {string} name - 用户姓名
 */
export const setUserName = (name) => {
  localStorage.setItem(STORAGE_KEYS.USER_NAME, name)
}

// ============ 通用操作 ============

/**
 * 设置存储项
 * @param {string} key - 键名
 * @param {any} value - 值（对象会自动转为 JSON）
 */
export const setItem = (key, value) => {
  if (typeof value === 'object') {
    localStorage.setItem(key, JSON.stringify(value))
  } else {
    localStorage.setItem(key, value)
  }
}

/**
 * 获取存储项
 * @param {string} key - 键名
 * @param {any} defaultValue - 默认值
 * @returns {any} 存储的值
 */
export const getItem = (key, defaultValue = null) => {
  const value = localStorage.getItem(key)
  if (value === null) {
    return defaultValue
  }
  try {
    return JSON.parse(value)
  } catch {
    return value
  }
}

/**
 * 移除存储项
 * @param {string} key - 键名
 */
export const removeItem = (key) => {
  localStorage.removeItem(key)
}

/**
 * 清空所有存储
 */
export const clearAll = () => {
  localStorage.clear()
}

/**
 * 清除登录相关存储（登出时调用）
 */
export const clearLoginStorage = () => {
  removeToken()
  localStorage.removeItem(STORAGE_KEYS.USER_ROLE)
  localStorage.removeItem(STORAGE_KEYS.USER_NAME)
  localStorage.removeItem(STORAGE_KEYS.USER_INFO)
}

// ============ 导出键名 ============
export { STORAGE_KEYS }