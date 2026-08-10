<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">🏠 宿舍管理</span>
        <span v-else>🏠</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <!-- 首页 -->
        <el-menu-item index="/dashboard" @click="navigateTo('/dashboard')">
          <el-icon><DataLine /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <!-- 管理员菜单 -->
        <template v-if="userRole === 'admin'">
          <el-menu-item index="/admin/users" @click="navigateTo('/admin/users')">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/students" @click="navigateTo('/admin/students')">
            <el-icon><School /></el-icon>
            <template #title>学生管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/dorms" @click="navigateTo('/admin/dorms')">
            <el-icon><HomeFilled /></el-icon>
            <template #title>宿舍管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/audit" @click="navigateTo('/admin/audit')">
            <el-icon><Checked /></el-icon>
            <template #title>审核管理</template>
          </el-menu-item>
        </template>

        <!-- 宿舍管理员菜单 -->
        <template v-if="userRole === 'manager'">
          <el-menu-item index="/manager/dorms" @click="navigateTo('/manager/dorms')">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>管辖宿舍</template>
          </el-menu-item>
          <el-menu-item index="/manager/audit" @click="navigateTo('/manager/audit')">
            <el-icon><Checked /></el-icon>
            <template #title>审核管理</template>
          </el-menu-item>
        </template>

        <!-- 学生菜单 -->
        <template v-if="userRole === 'student'">
          <el-menu-item index="/student/select" @click="navigateTo('/student/select')">
            <el-icon><Select /></el-icon>
            <template #title>选宿舍</template>
          </el-menu-item>
          <el-menu-item index="/student/my-dorm" @click="navigateTo('/student/my-dorm')">
            <el-icon><HomeFilled /></el-icon>
            <template #title>我的宿舍</template>
          </el-menu-item>
          <el-menu-item index="/student/transfer" @click="navigateTo('/student/transfer')">
            <el-icon><Switch /></el-icon>
            <template #title>换宿舍申请</template>
          </el-menu-item>
        </template>

        <!-- 退出登录 -->
        <el-menu-item index="logout" @click="handleLogoutClick">
          <el-icon><SwitchButton /></el-icon>
          <template #title>退出登录</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容 -->
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="welcome">欢迎，{{ userName }}</span>
          <el-tag :type="roleTagType" size="small">{{ userRoleName }}</el-tag>
          <el-button type="danger" size="small" @click="handleLogoutClick">退出</el-button>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store'

const route = useRoute()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 用户信息
const userRole = computed(() => userStore.userRole)
const userName = computed(() => userStore.userName)

// 用户角色名称映射
const userRoleName = computed(() => {
  const roleMap = {
    admin: '系统管理员',
    manager: '宿舍管理员',
    student: '学生'
  }
  return roleMap[userRole.value] || '用户'
})

// 角色标签类型
const roleTagType = computed(() => {
  const typeMap = {
    admin: 'danger',
    manager: 'warning',
    student: 'success'
  }
  return typeMap[userRole.value] || 'info'
})

// 当前页面标题
const currentTitle = computed(() => {
  return route.meta?.title || ''
})

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 导航方法 - 使用 window.location 避免路由冲突
const navigateTo = (path) => {
  if (route.path !== path) {
    window.location.href = path
  }
}

// 退出登录 - 使用原生 confirm
const handleLogoutClick = () => {
  if (window.confirm('确认退出登录吗？')) {
    userStore.logout()
    window.location.href = '/login'
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

/* ========== 侧边栏 ========== */
.sidebar {
  background-color: #304156;
  transition: width 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
  flex-shrink: 0;
}

.el-menu {
  border-right: none;
  flex: 1;
}

/* ========== 头部 ========== */
.header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.collapse-icon:hover {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome {
  color: #666;
  font-size: 14px;
}

/* ========== 内容区 ========== */
.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
