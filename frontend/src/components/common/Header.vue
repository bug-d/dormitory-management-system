<template>
  <div class="header-container">
    <!-- 左侧：面包屑 + 折叠按钮 -->
    <div class="header-left">
      <el-icon class="collapse-btn" @click="toggleCollapse">
        <Fold v-if="!isCollapsed" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧：用户信息 -->
    <div class="header-right">
      <!-- 刷新按钮 -->
      <el-tooltip content="刷新页面" placement="bottom">
        <el-button :icon="Refresh" circle size="small" @click="handleRefresh" />
      </el-tooltip>

      <!-- 全屏按钮 -->
      <el-tooltip content="全屏" placement="bottom">
        <el-button :icon="FullScreen" circle size="small" @click="toggleFullscreen" />
      </el-tooltip>

      <!-- 用户下拉菜单 -->
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="32" :icon="UserFilled" />
          <span class="user-name">{{ userName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon> 个人信息
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon> 修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store'
import {
  Fold,
  Expand,
  Refresh,
  FullScreen,
  UserFilled,
  User,
  Lock,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// ============ 计算属性 ============
const currentTitle = computed(() => route.meta?.title || '')

const userName = computed(() => userStore.userName || '用户')

// ============ Props ============
defineProps({
  // 侧边栏是否折叠
  isCollapsed: {
    type: Boolean,
    default: false
  }
})

// ============ Emits ============
const emit = defineEmits(['toggle-collapse'])

// ============ 方法 ============

// 切换侧边栏折叠
const toggleCollapse = () => {
  emit('toggle-collapse')
}

// 刷新页面
const handleRefresh = () => {
  router.go(0)
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 下拉菜单命令处理
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/change-password')
      break
    case 'logout':
      handleLogout()
      break
    default:
      break
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
  }).catch(() => {})
}
</script>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

/* ========== 左侧 ========== */
.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: #409EFF;
}

/* ========== 右侧 ========== */
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px 4px 8px;
  border-radius: 20px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f0f2f5;
}

.user-name {
  font-size: 14px;
  color: #333;
}
</style>
