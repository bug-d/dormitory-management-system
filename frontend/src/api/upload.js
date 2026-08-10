/**
 * 文件上传接口
 * 路径：frontend/src/api/upload.js
 * 作用：包含图片、Excel等文件上传接口
 */

import request from '@/utils/request'

// ============ 通用上传 ============

/**
 * 上传单个文件
 * @param {File} file - 文件
 * @param {string} type - 文件类型（image/excel/other）
 * @returns {Promise}
 */
export const uploadFile = (file, type = 'other') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', type)
  return request({
    url: '/upload/file',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量上传文件
 * @param {File[]} files - 文件列表
 * @param {string} type - 文件类型
 * @returns {Promise}
 */
export const uploadFiles = (files, type = 'other') => {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  formData.append('type', type)
  return request({
    url: '/upload/files',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

// ============ 图片上传 ============

/**
 * 上传图片
 * @param {File} file - 图片文件
 * @returns {Promise}
 */
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传头像
 * @param {File} file - 头像图片
 * @returns {Promise}
 */
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量上传图片
 * @param {File[]} files - 图片列表
 * @returns {Promise}
 */
export const uploadImages = (files) => {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  return request({
    url: '/upload/images',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

// ============ Excel 导入 ============

/**
 * 导入学生数据（Excel）
 * @param {File} file - Excel文件
 * @returns {Promise}
 */
export const importStudents = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/import/students',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

/**
 * 导入宿舍数据（Excel）
 * @param {File} file - Excel文件
 * @returns {Promise}
 */
export const importDorms = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/import/dorms',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

/**
 * 导入用户数据（Excel）
 * @param {File} file - Excel文件
 * @returns {Promise}
 */
export const importUsers = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/import/users',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

// ============ 文件管理 ============

/**
 * 获取文件列表
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.type - 文件类型
 * @returns {Promise}
 */
export const getFileList = (params) => {
  return request({
    url: '/upload/files',
    method: 'get',
    params
  })
}

/**
 * 删除文件
 * @param {string} fileId - 文件ID
 * @returns {Promise}
 */
export const deleteFile = (fileId) => {
  return request({
    url: `/upload/files/${fileId}`,
    method: 'delete'
  })
}

/**
 * 批量删除文件
 * @param {string[]} fileIds - 文件ID列表
 * @returns {Promise}
 */
export const batchDeleteFiles = (fileIds) => {
  return request({
    url: '/upload/files/batch',
    method: 'delete',
    data: fileIds
  })
}

/**
 * 获取文件下载链接
 * @param {string} fileId - 文件ID
 * @returns {Promise}
 */
export const getFileDownloadUrl = (fileId) => {
  return request({
    url: `/upload/files/${fileId}/download`,
    method: 'get'
  })
}

/**
 * 下载文件
 * @param {string} fileId - 文件ID
 * @param {string} filename - 文件名
 * @returns {Promise}
 */
export const downloadFile = (fileId, filename) => {
  return request({
    url: `/upload/files/${fileId}/download`,
    method: 'get',
    params: { filename },
    responseType: 'blob'
  })
}