/**
 * 学生管理接口
 * 路径：frontend/src/api/student.js
 * 作用：包含学生增删改查等接口
 */

import request from '@/utils/request'

// ============ 基础 CRUD ============

/**
 * 分页查询学生
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.keyword - 搜索关键字（学号/姓名）
 * @param {string} params.grade - 年级筛选
 * @param {string} params.gender - 性别筛选
 * @param {number} params.status - 状态筛选
 * @returns {Promise}
 */
export const getStudents = (params) => {
  return request({
    url: '/admin/students/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有学生
 * @returns {Promise}
 */
export const getAllStudents = () => {
  return request({
    url: '/admin/students/list',
    method: 'get'
  })
}

/**
 * 根据ID查询学生
 * @param {number} id - 学生ID
 * @returns {Promise}
 */
export const getStudentById = (id) => {
  return request({
    url: `/admin/students/${id}`,
    method: 'get'
  })
}

/**
 * 根据学号查询学生
 * @param {string} studentNo - 学号
 * @returns {Promise}
 */
export const getStudentByNo = (studentNo) => {
  return request({
    url: `/admin/students/no/${studentNo}`,
    method: 'get'
  })
}

/**
 * 新增学生
 * @param {Object} data - 学生数据
 * @returns {Promise}
 */
export const addStudent = (data) => {
  return request({
    url: '/admin/students',
    method: 'post',
    data
  })
}

/**
 * 更新学生
 * @param {Object} data - 学生数据
 * @returns {Promise}
 */
export const updateStudent = (data) => {
  return request({
    url: '/admin/students',
    method: 'put',
    data
  })
}

/**
 * 删除学生
 * @param {number} id - 学生ID
 * @returns {Promise}
 */
export const deleteStudent = (id) => {
  return request({
    url: `/admin/students/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除学生
 * @param {number[]} ids - 学生ID列表
 * @returns {Promise}
 */
export const batchDeleteStudents = (ids) => {
  return request({
    url: '/admin/students/batch',
    method: 'delete',
    data: ids
  })
}

// ============ 查询 ============

/**
 * 查询所有新生
 * @returns {Promise}
 */
export const getNewStudents = () => {
  return request({
    url: '/admin/students/new',
    method: 'get'
  })
}

/**
 * 查询在读学生
 * @returns {Promise}
 */
export const getActiveStudents = () => {
  return request({
    url: '/admin/students/active',
    method: 'get'
  })
}

/**
 * 根据年级查询学生
 * @param {string} grade - 年级
 * @returns {Promise}
 */
export const getStudentsByGrade = (grade) => {
  return request({
    url: `/admin/students/grade/${grade}`,
    method: 'get'
  })
}

/**
 * 查询未分配宿舍的学生
 * @returns {Promise}
 */
export const getStudentsWithoutDorm = () => {
  return request({
    url: '/admin/students/without-dorm',
    method: 'get'
  })
}

/**
 * 查询已分配宿舍的学生
 * @returns {Promise}
 */
export const getStudentsWithDorm = () => {
  return request({
    url: '/admin/students/with-dorm',
    method: 'get'
  })
}

// ============ 统计 ============

/**
 * 获取学生总数
 * @returns {Promise}
 */
export const getStudentCount = () => {
  return request({
    url: '/admin/students/stats/count',
    method: 'get'
  })
}

// ============ 状态管理 ============

/**
 * 标记学生为已毕业
 * @param {number} id - 学生ID
 * @returns {Promise}
 */
export const graduateStudent = (id) => {
  return request({
    url: `/admin/students/${id}/graduate`,
    method: 'put'
  })
}

/**
 * 标记学生为新生
 * @param {number} id - 学生ID
 * @returns {Promise}
 */
export const markStudentAsNew = (id) => {
  return request({
    url: `/admin/students/${id}/mark-new`,
    method: 'put'
  })
}

// ============ 导入导出 ============

/**
 * 导入学生（Excel）
 * @param {File} file - Excel文件
 * @returns {Promise}
 */
export const importStudents = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/admin/students/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出学生（Excel）
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const exportStudents = (params) => {
  return request({
    url: '/admin/students/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}