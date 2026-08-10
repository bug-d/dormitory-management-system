<template>
  <LoginLayout>
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="rules"
      class="login-form"
      @keyup.enter="handleLogin"
    >
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="请输入用户名"
          :prefix-icon="User"
          size="large"
          clearable
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          size="large"
          show-password
          clearable
        />
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          size="large"
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form-item>
    </el-form>
  </LoginLayout>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store'
import LoginLayout from '@/components/layout/LoginLayout.vue'

const userStore = useUserStore()

// ============ 表单引用 ============
const loginFormRef = ref(null)

// ============ 登录表单 ============
const loginForm = reactive({
  username: '',
  password: ''
})

// ============ 加载状态 ============
const loading = ref(false)

// ============ 表单校验规则 ============
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度在 3 到 20 个字符', trigger: 'blur' }
  ]
}

// ============ 登录方法 ============
const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    const res = await userStore.login(loginForm)
    if (res.code === 200) {
      ElMessage.success('登录成功')
      // 强制刷新页面，重新初始化路由守卫
      window.location.href = '/dashboard'
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  letter-spacing: 4px;
}
</style>