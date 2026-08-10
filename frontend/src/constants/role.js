/**
 * 角色常量文件
 * 路径：frontend/src/constants/role.js
 * 作用：定义系统中所有角色和权限映射
 */

// ============ 角色定义 ============
export const ROLES = {
  /** 系统管理员 */
  ADMIN: 'admin',
  /** 宿舍管理员 */
  MANAGER: 'manager',
  /** 学生 */
  STUDENT: 'student'
}

// ============ 角色名称映射 ============
export const ROLE_NAMES = {
  [ROLES.ADMIN]: '系统管理员',
  [ROLES.MANAGER]: '宿舍管理员',
  [ROLES.STUDENT]: '学生'
}

// ============ 角色标签类型映射 ============
export const ROLE_TAG_TYPES = {
  [ROLES.ADMIN]: 'danger',
  [ROLES.MANAGER]: 'warning',
  [ROLES.STUDENT]: 'success'
}

// ============ 角色权限映射 ============
export const ROLE_PERMISSIONS = {
  [ROLES.ADMIN]: {
    canManageUsers: true,
    canManageStudents: true,
    canManageDorms: true,
    canAudit: true,
    canManageManagers: true,
    canExportReports: true
  },
  [ROLES.MANAGER]: {
    canManageUsers: false,
    canManageStudents: false,
    canManageDorms: true,
    canAudit: true,
    canManageManagers: false,
    canExportReports: true
  },
  [ROLES.STUDENT]: {
    canManageUsers: false,
    canManageStudents: false,
    canManageDorms: false,
    canAudit: false,
    canManageManagers: false,
    canExportReports: false
  }
}

// ============ 路由角色映射 ============
export const ROUTE_ROLES = {
  // 管理员路由
  '/admin/users': [ROLES.ADMIN],
  '/admin/students': [ROLES.ADMIN],
  '/admin/dorms': [ROLES.ADMIN],
  '/admin/audit': [ROLES.ADMIN],
  
  // 宿舍管理员路由
  '/manager/dorms': [ROLES.MANAGER, ROLES.ADMIN],
  '/manager/audit': [ROLES.MANAGER, ROLES.ADMIN],
  
  // 学生路由
  '/student/select': [ROLES.STUDENT, ROLES.ADMIN],
  '/student/my-dorm': [ROLES.STUDENT, ROLES.ADMIN],
  '/student/transfer': [ROLES.STUDENT, ROLES.ADMIN]
}

// ============ 工具函数 ============

/**
 * 获取角色名称
 * @param {string} role - 角色代码
 * @returns {string} 角色中文名称
 */
export const getRoleName = (role) => {
  return ROLE_NAMES[role] || role
}

/**
 * 获取角色标签类型
 * @param {string} role - 角色代码
 * @returns {string} 标签类型 (danger/warning/success)
 */
export const getRoleTagType = (role) => {
  return ROLE_TAG_TYPES[role] || 'info'
}

/**
 * 检查角色是否有效
 * @param {string} role - 角色代码
 * @returns {boolean} 是否有效
 */
export const isValidRole = (role) => {
  return Object.values(ROLES).includes(role)
}

/**
 * 获取路由允许的角色列表
 * @param {string} path - 路由路径
 * @returns {string[]} 允许的角色列表
 */
export const getRouteRoles = (path) => {
  return ROUTE_ROLES[path] || []
}

/**
 * 检查用户是否有权限访问某个路由
 * @param {string} role - 用户角色
 * @param {string} path - 路由路径
 * @returns {boolean} 是否有权限
 */
export const hasRoutePermission = (role, path) => {
  const allowedRoles = getRouteRoles(path)
  if (allowedRoles.length === 0) return true
  return allowedRoles.includes(role)
}

/**
 * 检查用户是否有某个权限
 * @param {string} role - 用户角色
 * @param {string} permission - 权限名称
 * @returns {boolean} 是否有权限
 */
export const hasPermission = (role, permission) => {
  const permissions = ROLE_PERMISSIONS[role]
  return permissions && permissions[permission] === true
}