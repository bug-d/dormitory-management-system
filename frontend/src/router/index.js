import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/layout/Layout.vue'

// ============ 路由配置 ============
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页', icon: 'DataLine', requiresAuth: true }
      },
      // ========== 管理员路由 ==========
      {
        path: 'admin/users',
        name: 'UserManage',
        component: () => import('@/views/admin/user/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User', role: 'admin', requiresAuth: true }
      },
      {
        path: 'admin/students',
        name: 'StudentManage',
        component: () => import('@/views/admin/student/StudentManage.vue'),
        meta: { title: '学生管理', icon: 'School', role: 'admin', requiresAuth: true }
      },
      {
        path: 'admin/dorms',
        name: 'DormManage',
        component: () => import('@/views/admin/dorm/DormManage.vue'),
        meta: { title: '宿舍管理', icon: 'HomeFilled', role: 'admin', requiresAuth: true }
      },
      {
        path: 'admin/audit',
        name: 'AuditManage',
        component: () => import('@/views/admin/audit/AuditManage.vue'),
        meta: { title: '审核管理', icon: 'Checked', role: 'admin', requiresAuth: true }
      },
      // ========== 宿舍管理员路由 ==========
      {
        path: 'manager/dorms',
        name: 'MyDorms',
        component: () => import('@/views/manager/dorm/MyDorms.vue'),
        meta: { title: '管辖宿舍', icon: 'OfficeBuilding', role: 'manager', requiresAuth: true }
      },
      {
        path: 'manager/audit',
        name: 'MyAudit',
        component: () => import('@/views/manager/audit/MyAudit.vue'),
        meta: { title: '审核管理', icon: 'Checked', role: 'manager', requiresAuth: true }
      },
      // ========== 学生路由 ==========
      {
        path: 'student/select',
        name: 'SelectDorm',
        component: () => import('@/views/student/select/SelectDorm.vue'),
        meta: { title: '选宿舍', icon: 'Select', role: 'student', requiresAuth: true }
      },
      {
        path: 'student/my-dorm',
        name: 'MyDorm',
        component: () => import('@/views/student/mydorm/MyDorm.vue'),
        meta: { title: '我的宿舍', icon: 'HomeFilled', role: 'student', requiresAuth: true }
      },
      {
        path: 'student/transfer',
        name: 'TransferApply',
        component: () => import('@/views/student/transfer/TransferApply.vue'),
        meta: { title: '换宿舍申请', icon: 'Switch', role: 'student', requiresAuth: true }
      }
    ]
  }
]

// ============ 创建路由实例 ============
const router = createRouter({
  history: createWebHistory(),
  routes
})

// ============================================
// 路由守卫
// ============================================
router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token') || localStorage.getItem('token')
  
  //console.log('守卫 - 目标:', to.path, 'token:', token ? '有' : '无')
  
  // 登录页
  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  
  // 其他页面
  if (!token) {
    next('/login')
    return
  }
  
  next()
})

export default router