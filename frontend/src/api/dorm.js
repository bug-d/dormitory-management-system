import request from '@/utils/request'

// ============ 基础 CRUD ============

/**
 * 分页查询宿舍（支持排序）
 */
export const getDorms = (params) => {
  return request({
    url: '/admin/dorms/page',
    method: 'get',
    params
  })
}

export const getAllDorms = () => {
  return request({
    url: '/admin/dorms/list',
    method: 'get'
  })
}

export const getDormById = (id) => {
  return request({
    url: `/admin/dorms/${id}`,
    method: 'get'
  })
}

export const getDormByBuildingAndRoom = (buildingNo, roomNo) => {
  return request({
    url: '/admin/dorms/find',
    method: 'get',
    params: { buildingNo, roomNo }
  })
}

export const addDorm = (data) => {
  return request({
    url: '/admin/dorms',
    method: 'post',
    data
  })
}

export const updateDorm = (data) => {
  return request({
    url: '/admin/dorms',
    method: 'put',
    data
  })
}

export const deleteDorm = (id) => {
  return request({
    url: `/admin/dorms/${id}`,
    method: 'delete'
  })
}

// ============ 查询 ============

export const getAvailableDorms = () => {
  return request({
    url: '/admin/dorms/available',
    method: 'get'
  })
}

export const getAvailableDormsByGender = (gender) => {
  return request({
    url: `/admin/dorms/available/${gender}`,
    method: 'get'
  })
}

export const getDormsByBuilding = (buildingNo) => {
  return request({
    url: `/admin/dorms/building/${buildingNo}`,
    method: 'get'
  })
}

export const getAllBuildings = () => {
  return request({
    url: '/admin/dorms/buildings',
    method: 'get'
  })
}

export const getFullDorms = () => {
  return request({
    url: '/admin/dorms/full',
    method: 'get'
  })
}

export const getEmptyDorms = () => {
  return request({
    url: '/admin/dorms/empty',
    method: 'get'
  })
}

// ============ 统计 ============

export const getOverallStats = () => {
  return request({
    url: '/admin/dorms/stats/overall',
    method: 'get'
  })
}