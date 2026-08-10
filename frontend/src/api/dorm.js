/**
 * 宿舍管理接口
 * 路径：frontend/src/api/dorm.js
 * 作用：包含宿舍增删改查等接口
 */

import request from '@/utils/request'

// ============ 基础 CRUD ============

/**
 * 分页查询宿舍
 * @param {Object} params
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.buildingNo - 楼栋号筛选
 * @param {string} params.gender - 性别筛选
 * @param {string} params.status - 状态筛选
 * @returns {Promise}
 */
export const getDorms = (params) => {
  return request({
    url: '/admin/dorms/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有宿舍
 * @returns {Promise}
 */
export const getAllDorms = () => {
  return request({
    url: '/admin/dorms/list',
    method: 'get'
  })
}

/**
 * 根据ID查询宿舍
 * @param {number} id - 宿舍ID
 * @returns {Promise}
 */
export const getDormById = (id) => {
  return request({
    url: `/admin/dorms/${id}`,
    method: 'get'
  })
}

/**
 * 根据楼栋和房间号查询宿舍
 * @param {string} buildingNo - 楼栋号
 * @param {string} roomNo - 房间号
 * @returns {Promise}
 */
export const getDormByBuildingAndRoom = (buildingNo, roomNo) => {
  return request({
    url: '/admin/dorms/find',
    method: 'get',
    params: { buildingNo, roomNo }
  })
}

/**
 * 新增宿舍
 * @param {Object} data - 宿舍数据
 * @returns {Promise}
 */
export const addDorm = (data) => {
  return request({
    url: '/admin/dorms',
    method: 'post',
    data
  })
}

/**
 * 更新宿舍
 * @param {Object} data - 宿舍数据
 * @returns {Promise}
 */
export const updateDorm = (data) => {
  return request({
    url: '/admin/dorms',
    method: 'put',
    data
  })
}

/**
 * 删除宿舍
 * @param {number} id - 宿舍ID
 * @returns {Promise}
 */
export const deleteDorm = (id) => {
  return request({
    url: `/admin/dorms/${id}`,
    method: 'delete'
  })
}

// ============ 查询 ============

/**
 * 查询所有可用宿舍
 * @returns {Promise}
 */
export const getAvailableDorms = () => {
  return request({
    url: '/admin/dorms/available',
    method: 'get'
  })
}

/**
 * 根据性别查询可用宿舍
 * @param {string} gender - 性别（M/F）
 * @returns {Promise}
 */
export const getAvailableDormsByGender = (gender) => {
  return request({
    url: `/admin/dorms/available/${gender}`,
    method: 'get'
  })
}

/**
 * 根据楼栋查询宿舍
 * @param {string} buildingNo - 楼栋号
 * @returns {Promise}
 */
export const getDormsByBuilding = (buildingNo) => {
  return request({
    url: `/admin/dorms/building/${buildingNo}`,
    method: 'get'
  })
}

/**
 * 查询所有楼栋号
 * @returns {Promise}
 */
export const getAllBuildings = () => {
  return request({
    url: '/admin/dorms/buildings',
    method: 'get'
  })
}

/**
 * 查询已满的宿舍
 * @returns {Promise}
 */
export const getFullDorms = () => {
  return request({
    url: '/admin/dorms/full',
    method: 'get'
  })
}

/**
 * 查询空宿舍
 * @returns {Promise}
 */
export const getEmptyDorms = () => {
  return request({
    url: '/admin/dorms/empty',
    method: 'get'
  })
}

// ============ 统计 ============

/**
 * 获取总体统计数据
 * @returns {Promise}
 */
export const getOverallStats = () => {
  return request({
    url: '/admin/dorms/stats/overall',
    method: 'get'
  })
}

/**
 * 获取各楼栋统计
 * @returns {Promise}
 */
export const getBuildingStats = () => {
  return request({
    url: '/admin/dorms/stats/buildings',
    method: 'get'
  })
}

/**
 * 获取某楼栋的入住率
 * @param {string} buildingNo - 楼栋号
 * @returns {Promise}
 */
export const getBuildingOccupancyRate = (buildingNo) => {
  return request({
    url: `/admin/dorms/stats/building/${buildingNo}`,
    method: 'get'
  })
}

/**
 * 获取某宿舍的入住率
 * @param {number} id - 宿舍ID
 * @returns {Promise}
 */
export const getDormOccupancyRate = (id) => {
  return request({
    url: `/admin/dorms/stats/rate/${id}`,
    method: 'get'
  })
}