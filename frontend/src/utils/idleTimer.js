/**
 * 空闲超时自动登出工具
 * 路径：frontend/src/utils/idleTimer.js
 * 作用：用户无操作超过设定时间后自动登出
 */

import { useUserStore } from '@/store'
import { ElMessage } from 'element-plus'

// 空闲超时时间（毫秒） 30分钟
const IDLE_TIMEOUT = 30 * 60 * 1000

let timer = null
let isLoggedOut = false
let isInitialized = false

/**
 * 重置计时器
 */
const resetTimer = () => {
  // 如果已经登出，不再重置
  if (isLoggedOut) return
  
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  startTimer()
  //console.log('空闲计时器已重置')
}

/**
 * 启动计时器
 */
const startTimer = () => {
  if (isLoggedOut) return
  
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  timer = setTimeout(() => {
    handleLogout()
  }, IDLE_TIMEOUT)
  //console.log('空闲计时器已启动，将在', IDLE_TIMEOUT / 1000, '秒后超时')
}

/**
 * 处理登出
 */
const handleLogout = () => {
  if (isLoggedOut) return
  isLoggedOut = true
  
  const userStore = useUserStore()
  if (userStore.isLoggedIn) {
    ElMessage.warning('长时间未操作，已自动登出，请重新登录')
    userStore.logout()
  }
  clearTimer()
}

/**
 * 清除计时器
 */
const clearTimer = () => {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  isInitialized = false
  //console.log('空闲计时器已清除')
}

/**
 * 事件处理函数
 */
const handleUserActivity = () => {
  if (!isLoggedOut) {
    resetTimer()
  }
}

/**
 * 初始化空闲检测
 */
export const initIdleTimer = () => {
  // 如果已经初始化或已经登出，不重复初始化
  if (isInitialized || isLoggedOut) {
    //console.log('空闲计时器已初始化或已登出')
    return
  }
  
  //console.log('初始化空闲检测...')
  isInitialized = true
  isLoggedOut = false

  // 用户交互事件列表
  const events = [
    'mousedown',
    'mousemove',
    'keydown',
    'scroll',
    'touchstart',
    'click',
    'wheel'
  ]

  // 绑定事件
  events.forEach(event => {
    document.addEventListener(event, handleUserActivity)
  })

  // 启动计时器
  startTimer()
  //console.log('空闲检测初始化完成')
}

/**
 * 清理空闲检测
 */
export const clearIdleTimer = () => {
  //console.log('清理空闲检测...')
  
  const events = [
    'mousedown',
    'mousemove',
    'keydown',
    'scroll',
    'touchstart',
    'click',
    'wheel'
  ]
  events.forEach(event => {
    document.removeEventListener(event, handleUserActivity)
  })
  clearTimer()
  isInitialized = false
  isLoggedOut = false
  //console.log('空闲检测清理完成')
}