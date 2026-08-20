import request from '@/utils/request'

// 获取首页统计数据
export const getDashboardStats = () => {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

// 获取各楼栋入住率数据
export const getBuildingOccupancyData = () => {
  return request({
    url: '/dashboard/chart/building-occupancy',
    method: 'get'
  })
}

// 获取男女比例数据
export const getGenderRatioData = () => {
  return request({
    url: '/dashboard/chart/gender-ratio',
    method: 'get'
  })
}

// 获取最近动态（从数据库 operation_logs 表读取）
export const getRecentActivities = (params) => {
  return request({
    url: '/dashboard/activities',
    method: 'get',
    params
  })
}