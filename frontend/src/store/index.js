/**
 * Pinia 状态管理统一导出入口
 * 路径：frontend/src/store/index.js
 */

import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// 创建 Pinia 实例
const pinia = createPinia()

// 使用持久化插件
pinia.use(piniaPluginPersistedstate)

// 导出 Pinia 实例
export default pinia

// 导出所有 Store
export { useUserStore } from './user'