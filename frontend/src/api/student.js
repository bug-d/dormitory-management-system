import request from '@/utils/request'

// ============ 基础 CRUD ============

export const getStudents = (params) => {
  return request({
    url: '/admin/students/page',
    method: 'get',
    params
  })
}

export const getAllStudents = () => {
  return request({
    url: '/admin/students/list',
    method: 'get'
  })
}

export const getStudentById = (id) => {
  return request({
    url: `/admin/students/${id}`,
    method: 'get'
  })
}

export const getStudentByNo = (studentNo) => {
  return request({
    url: `/admin/students/no/${studentNo}`,
    method: 'get'
  })
}

export const addStudent = (data) => {
  return request({
    url: '/admin/students',
    method: 'post',
    data
  })
}

export const updateStudent = (data) => {
  return request({
    url: '/admin/students',
    method: 'put',
    data
  })
}

export const deleteStudent = (id) => {
  return request({
    url: `/admin/students/${id}`,
    method: 'delete'
  })
}

export const batchDeleteStudents = (ids) => {
  return request({
    url: '/admin/students/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 按条件批量删除学生（跨页删除）
 */
export const deleteStudentsByCondition = (params) => {
  return request({
    url: '/admin/students/delete-by-condition',
    method: 'delete',
    params
  })
}

// ============ 查询 ============

export const getNewStudents = () => {
  return request({
    url: '/admin/students/new',
    method: 'get'
  })
}

export const getActiveStudents = () => {
  return request({
    url: '/admin/students/active',
    method: 'get'
  })
}

export const getStudentsByGrade = (grade) => {
  return request({
    url: `/admin/students/grade/${grade}`,
    method: 'get'
  })
}

export const getStudentsWithoutDorm = () => {
  return request({
    url: '/admin/students/without-dorm',
    method: 'get'
  })
}

export const getStudentsWithDorm = () => {
  return request({
    url: '/admin/students/with-dorm',
    method: 'get'
  })
}

// ============ 统计 ============

export const getStudentCount = () => {
  return request({
    url: '/admin/students/stats/count',
    method: 'get'
  })
}

// ============ 状态管理 ============

export const graduateStudent = (id) => {
  return request({
    url: `/admin/students/${id}/graduate`,
    method: 'put'
  })
}

export const markStudentAsNew = (id) => {
  return request({
    url: `/admin/students/${id}/mark-new`,
    method: 'put'
  })
}

// ============ 导入导出 ============

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

export const exportStudents = (params) => {
  return request({
    url: '/admin/students/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}