<template>
  <div class="login-layout">
    <div class="login-container">
      <!-- 登录卡片 -->
      <div class="login-card">
        <!-- 系统 Logo 和标题 -->
        <div class="login-header">
          <div class="logo-icon">🏠</div>
          <h1 class="title">大学宿舍管理系统</h1>
          <p class="subtitle">University Dormitory Management System</p>
        </div>

        <!-- 登录表单插槽 -->
        <div class="login-form">
          <slot />
        </div>

        <!-- 底部版权信息 -->
        <div class="login-footer">
          <span>© 2026 大学宿舍管理系统 v1.0</span>
        </div>
      </div>

      <!-- 背景装饰 -->
      <div class="bg-decoration">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 登录页布局组件，提供登录页面的背景和容器
</script>

<style scoped>
.login-layout {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* ========== 背景装饰圆 ========== */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.circle-1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -150px;
  background: rgba(255, 255, 255, 0.08);
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -100px;
  left: -100px;
  background: rgba(255, 255, 255, 0.06);
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.04);
}

/* ========== 登录卡片 ========== */
.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  padding: 20px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 48px 40px 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* ========== 登录头部 ========== */
.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px 0;
  letter-spacing: 2px;
}

.subtitle {
  font-size: 13px;
  color: #999;
  margin: 0;
  letter-spacing: 1px;
}

/* ========== 登录表单 ========== */
.login-form {
  margin-bottom: 20px;
}

/* ========== 底部版权 ========== */
.login-footer {
  text-align: center;
  color: #bbb;
  font-size: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

/* ========== 响应式适配 ========== */
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px 24px;
  }

  .title {
    font-size: 20px;
  }

  .logo-icon {
    font-size: 40px;
  }
}
</style>