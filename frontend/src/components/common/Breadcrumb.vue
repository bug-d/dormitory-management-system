<template>
  <el-breadcrumb class="breadcrumb" separator="/">
    <el-breadcrumb-item :to="{ path: '/' }">
      <el-icon><HomeFilled /></el-icon>
    </el-breadcrumb-item>
    <el-breadcrumb-item
      v-for="(item, index) in breadcrumbList"
      :key="index"
      :to="item.path ? { path: item.path } : undefined"
    >
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'

const route = useRoute()

// 面包屑数据
const breadcrumbList = computed(() => {
  const matched = route.matched
  const list = []

  // 从路由匹配中提取面包屑
  for (const item of matched) {
    // 跳过根路由和没有 title 的路由
    if (item.path === '/' || !item.meta?.title) continue
    list.push({
      title: item.meta.title,
      path: item.path
    })
  }

  return list
})
</script>

<style scoped>
.breadcrumb {
  font-size: 14px;
}

:deep(.el-breadcrumb__inner) {
  color: #666;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #333;
  font-weight: 600;
}

:deep(.el-breadcrumb__item .el-breadcrumb__inner a) {
  color: #409EFF;
  text-decoration: none;
}

:deep(.el-breadcrumb__item .el-breadcrumb__inner a:hover) {
  color: #66b1ff;
}
</style>