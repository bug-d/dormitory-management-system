import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import App from './App.vue'
import router from './router'

// ============ Element Plus ============
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// ============ 全局样式 ============
import './assets/styles/global.css'

// ============ 空闲超时检测 ============
import { initIdleTimer } from './utils/idleTimer'

// 创建应用
const app = createApp(App)

// 创建 Pinia 并启用持久化插件
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 使用插件
app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 挂载应用
app.mount('#app')

// ============================================
// 路由守卫 - 最简版本
// ============================================

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token')
  
  //console.log('守卫 - 目标:', to.path, 'token:', token ? '有' : '无')
  
  // 登录页：有 token 就去首页，否则留在登录页
  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  
  // 其他页面：无 token 就去登录页
  if (!token) {
    next('/login')
    return
  }
  
  // 有 token，放行
  initIdleTimer()
  next()
})
