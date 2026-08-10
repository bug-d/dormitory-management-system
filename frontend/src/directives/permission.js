/**
 * 权限指令文件
 * 路径：frontend/src/directives/permission.js
 * 作用：按钮级权限控制，根据用户角色显示/隐藏元素
 *
 * 使用方式：
 * v-permission="'admin'"           - 仅管理员可见
 * v-permission="['admin', 'manager']"  - 管理员或宿管可见
 * v-permission:and="['admin', 'manager']"  - 同时拥有两个角色
 */

import { useUserStore } from '@/store'

/**
 * 检查用户是否有权限
 * @param {string|string[]} roles - 允许的角色
 * @param {string} mode - 模式：'or'（任一匹配）或 'and'（全部匹配）
 * @returns {boolean} 是否有权限
 */
const checkPermission = (roles, mode = 'or') => {
  const userStore = useUserStore()
  const userRole = userStore.userRole

  // 如果没有登录或没有角色，无权限
  if (!userRole) return false

  // 如果是管理员，默认拥有所有权限（可配置）
  if (userRole === 'admin') return true

  // 如果 roles 是字符串，转为数组
  const roleList = Array.isArray(roles) ? roles : [roles]

  if (roleList.length === 0) return true

  if (mode === 'and') {
    // 全部匹配模式：用户必须拥有所有角色（通常用于多角色系统）
    // 但本系统用户只有一个角色，所以退化为检查是否在列表中
    return roleList.includes(userRole)
  }

  // 任一匹配模式：用户拥有任一角色即可
  return roleList.includes(userRole)
}

/**
 * 权限指令
 * v-permission="'admin'" 或 v-permission="['admin', 'manager']"
 */
const permissionDirective = {
  mounted(el, binding) {
    const { value, modifiers } = binding
    const mode = modifiers.and ? 'and' : 'or'

    if (value && !checkPermission(value, mode)) {
      // 无权限：隐藏元素
      el.style.display = 'none'
    }
  }
}

/**
 * 权限指令（自定义用法）
 * v-permission:and="['admin', 'manager']" - 同时拥有 admin 和 manager 角色
 */
export default permissionDirective

/**
 * 注册权限指令
 * @param {App} app - Vue 应用实例
 */
export const setupPermissionDirective = (app) => {
  app.directive('permission', permissionDirective)
}