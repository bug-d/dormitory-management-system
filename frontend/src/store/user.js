import { defineStore } from 'pinia'
import { login as loginApi, getCurrentUser } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 从 sessionStorage 读取 token（关闭浏览器即清除）
    token: sessionStorage.getItem('token') || localStorage.getItem('token') || '',
    userInfo: {
      userId: null,
      username: '',
      realName: '',
      role: '',
      roleName: '',
      status: 1
    }
  }),

  // ========== 计算属性 ==========
  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!state.token,

    // 获取用户角色
    userRole: (state) => state.userInfo.role || '',

    // 获取用户姓名
    userName: (state) => state.userInfo.realName || state.userInfo.username || '用户',

    // 是否为管理员
    isAdmin: (state) => state.userInfo.role === 'admin',

    // 是否为宿舍管理员
    isManager: (state) => state.userInfo.role === 'manager',

    // 是否为普通学生
    isStudent: (state) => state.userInfo.role === 'student'
  },

  // ========== 同步操作 ==========
  actions: {
    /**
     * 用户登录
     * @param {Object} loginData - 登录表单数据 { username, password }
     * @returns {Promise}
     */
    async login(loginData) {
      try {
        const res = await loginApi(loginData)
        //console.log('login API 响应:', res)

        if (res.code === 200) {
          const data = res.data

          // 保存 Token（使用 sessionStorage，关闭浏览器即清除）
          this.token = data.token
          sessionStorage.setItem('token', data.token)

          //console.log('Token 已保存到 sessionStorage:', sessionStorage.getItem('token'))

          // 保存用户信息（角色和姓名用 localStorage 方便获取）
          this.userInfo = {
            userId: data.userId,
            username: data.username,
            realName: data.realName,
            role: data.role,
            roleName: data.roleName,
            status: data.status
          }
          localStorage.setItem('role', data.role)
          localStorage.setItem('userName', data.realName)

          return Promise.resolve(res)
        } else {
          return Promise.reject(res.message || '登录失败')
        }
      } catch (error) {
        return Promise.reject(error.message || '登录失败')
      }
    },

    /**
     * 获取当前用户信息
     */
    async fetchUserInfo() {
      try {
        const res = await getCurrentUser()
        if (res.code === 200) {
          const data = res.data
          this.userInfo = {
            userId: data.id,
            username: data.username,
            realName: data.realName,
            role: data.role,
            roleName: data.roleName || this.userInfo.roleName,
            status: data.status
          }
          localStorage.setItem('role', data.role)
          localStorage.setItem('userName', data.realName)
          return Promise.resolve(res)
        } else {
          return Promise.reject(res.message || '获取用户信息失败')
        }
      } catch (error) {
        return Promise.reject(error.message || '获取用户信息失败')
      }
    },

    /**
     * 用户登出
     */
    logout() {
      // 清除状态
      this.token = ''
      this.userInfo = {
        userId: null,
        username: '',
        realName: '',
        role: '',
        roleName: '',
        status: 1
      }

      // 清除存储（sessionStorage + localStorage）
      sessionStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('userName')

      // 跳转到登录页
      router.push('/login')
    },

    /**
     * 重置 Token（用于刷新 Token）
     */
    resetToken(token) {
      this.token = token
      sessionStorage.setItem('token', token)
    }
  },

  // ========== 数据持久化 ==========
  persist: {
    key: 'user-store',
    storage: sessionStorage,  // 改用 sessionStorage
    paths: ['token', 'userInfo']
  }
})