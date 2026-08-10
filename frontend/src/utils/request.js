import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store'

// ============ 创建 axios 实例 ============
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// ============ 请求拦截器 ============
request.interceptors.request.use(
  (config) => {
    // 从 store 获取 token（优先 sessionStorage）
    const userStore = useUserStore()
    const token = userStore.token || sessionStorage.getItem('token') || localStorage.getItem('token')

    // 如果有 token，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// ============ 响应拦截器 ============
request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 1. 成功响应（code === 200）
    if (res.code === 200) {
      return res
    }

    // 2. 未登录（code === 401）
    if (res.code === 401) {
      ElMessage.error(res.message || '登录已过期，请重新登录')
      const userStore = useUserStore()
      // 延迟执行，确保消息显示
      setTimeout(() => {
        userStore.logout()
      }, 500)
      return Promise.reject(res)
    }

    // 3. 无权限（code === 403）
    if (res.code === 403) {
      ElMessage.error(res.message || '没有权限执行此操作')
      return Promise.reject(res)
    }

    // 4. 资源不存在（code === 404）
    if (res.code === 404) {
      ElMessage.error(res.message || '请求的资源不存在')
      return Promise.reject(res)
    }

    // 5. 其他业务错误
    ElMessage.error(res.message || '操作失败')
    return Promise.reject(res)
  },
  (error) => {
    // HTTP 状态码错误
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401: {
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          setTimeout(() => {
            userStore.logout()
          }, 500)
          break
        }
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误，请稍后重试')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (error.message?.includes('Network Error')) {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error(error.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

export default request
