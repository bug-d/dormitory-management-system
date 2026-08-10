<template>
  <div class="sidebar-container">
    <!-- Logo -->
    <div class="logo">
      <span v-if="!isCollapsed">🏠 宿舍管理</span>
      <span v-else>🏠</span>
    </div>

    <!-- 菜单 -->
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapsed"
      :collapse-transition="false"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
    >
      <!-- 首页 -->
      <el-menu-item index="/dashboard">
        <el-icon><DataLine /></el-icon>
        <template #title>首页</template>
      </el-menu-item>

      <!-- ===== 系统管理员菜单 ===== -->
      <template v-if="userRole === 'admin'">
        <el-sub-menu index="admin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/students">
            <el-icon><School /></el-icon>
            <template #title>学生管理</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="dorm">
          <template #title>
            <el-icon><HomeFilled /></el-icon>
            <span>宿舍管理</span>
          </template>
          <el-menu-item index="/admin/dorms">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>宿舍信息</template>
          </el-menu-item>
          <el-menu-item index="/admin/audit">
            <el-icon><Checked /></el-icon>
            <template #title>审核管理</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- ===== 宿舍管理员菜单 ===== -->
      <template v-if="userRole === 'manager'">
        <el-menu-item index="/manager/dorms">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>管辖宿舍</template>
        </el-menu-item>
        <el-menu-item index="/manager/audit">
          <el-icon><Checked /></el-icon>
          <template #title>审核管理</template>
        </el-menu-item>
      </template>

      <!-- ===== 学生菜单 ===== -->
      <template v-if="userRole === 'student'">
        <el-menu-item index="/student/select">
          <el-icon><Select /></el-icon>
          <template #title>选宿舍</template>
        </el-menu-item>
        <el-menu-item index="/student/my-dorm">
          <el-icon><HomeFilled /></el-icon>
          <template #title>我的宿舍</template>
        </el-menu-item>
        <el-menu-item index="/student/transfer">
          <el-icon><Switch /></el-icon>
          <template #title>换宿舍申请</template>
        </el-menu-item>
      </template>

      <!-- ===== 底部：退出登录 ===== -->
      <el-menu-item class="logout-menu" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <template #title>退出登录</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store'
import {
  DataLine,
  Setting,
  User,
  School,
  HomeFilled,
  OfficeBuilding,
  Checked,
  Select,
  Switch,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

// ============ Props ============
defineProps({
  isCollapsed: {
    type: Boolean,
    default: false
  }
})

// ============ 计算属性 ============
// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 用户角色
const userRole = computed(() => userStore.userRole)

// ============ 方法 ============
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
  }).catch(() => {})
}
</script>

<style scoped>
.sidebar-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #304156;
  overflow: hidden;
}

/* ========== Logo ========== */
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
  transition: all 0.3s ease;
  white-space: nowrap;
}

/* ========== 菜单 ========== */
.el-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.el-menu::-webkit-scrollbar {
  width: 4px;
}

.el-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.el-menu::-webkit-scrollbar-track {
  background: transparent;
}

/* ========== 子菜单 ========== */
.el-sub-menu .el-menu-item {
  padding-left: 50px !important;
}

/* ========== 退出按钮 ========== */
.logout-menu {
  margin-top: auto;
  border-top: 1px solid #1f2d3d;
}
</style>
