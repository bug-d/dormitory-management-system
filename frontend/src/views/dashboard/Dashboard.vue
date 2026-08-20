<template>
  <div class="dashboard">
    <!-- ===== 统计卡片 ===== -->
    <el-row :gutter="20" class="stats-row" v-loading="loading">
      <el-col :xs="12" :sm="12" :md="6" v-for="item in statsCards" :key="item.label">
        <el-card class="stats-card" :body-style="{ padding: '20px' }">
          <div class="stats-content">
            <div class="stats-info">
              <div class="stats-label">{{ item.label }}</div>
              <div class="stats-value">{{ item.value }}</div>
            </div>
            <div class="stats-icon" :style="{ background: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 图表区域 ===== -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card" v-loading="chartLoading">
          <template #header>
            <span>各楼栋入住率</span>
            <span style="float:right;font-size:13px;color:#909399;">
              数据更新时间：{{ updateTime }}
            </span>
          </template>
          <div ref="chartRef1" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card" v-loading="chartLoading">
          <template #header>
            <span>男女比例</span>
          </template>
          <div ref="chartRef2" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 最近动态 ===== -->
    <el-card class="activity-card" v-loading="loading">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>
            <el-icon><Bell /></el-icon>
            最近动态
            <el-tag size="small" type="danger" style="margin-left: 8px;">{{ activities.length }}</el-tag>
          </span>
          <el-button size="small" type="primary" link @click="loadActivities">刷新</el-button>
        </div>
      </template>

      <div class="activity-list">
        <div
          v-for="(activity, index) in activities"
          :key="index"
          class="activity-item"
          @click="handleActivityClick(activity)"
        >
          <!-- 图标 -->
          <div class="activity-icon" :style="{ background: getActivityColor(activity.type) }">
            <el-icon :size="16"><component :is="getActivityIcon(activity.type)" /></el-icon>
          </div>

          <!-- 内容 -->
          <div class="activity-content">
            <div class="activity-text">
              <span class="activity-user">{{ activity.username || '系统' }}</span>
              <span class="activity-action">{{ activity.operation_detail || activity.action || '' }}</span>
            </div>
            <div class="activity-time">{{ activity.time }}</div>
          </div>

          <!-- 状态标签 -->
          <div class="activity-status">
            <el-tag :type="getStatusType(activity.status)" size="small">
              {{ activity.status || '已完成' }}
            </el-tag>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="activities.length === 0" description="暂无动态" :image-size="60" />

        <!-- 查看更多 -->
        <div class="view-more" v-if="activities.length > 0" @click="viewAllActivities">
          查看更多动态 →
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getDashboardStats, getBuildingOccupancyData, getGenderRatioData, getRecentActivities } from '@/api/dashboard'
import { Bell } from '@element-plus/icons-vue'

const router = useRouter()

// ============ 加载状态 ============
const loading = ref(false)
const chartLoading = ref(false)

// ============ 更新时间 ============
const updateTime = ref('')

// ============ 统计卡片数据 ============
const statsCards = ref([
  { label: '总床位', value: 0, icon: 'OfficeBuilding', color: '#409EFF' },
  { label: '已入住', value: 0, icon: 'User', color: '#67C23A' },
  { label: '空床位', value: 0, icon: 'HomeFilled', color: '#E6A23C' },
  { label: '入住率', value: '0%', icon: 'DataLine', color: '#F56C6C' }
])

// ============ 最近动态 ============
const activities = ref([])

// ============ 图表引用 ============
const chartRef1 = ref(null)
const chartRef2 = ref(null)
let chart1 = null
let chart2 = null

// ============ 动态图标映射 ============
const getActivityIcon = (type) => {
  const map = {
    login: 'User',
    apply: 'Plus',
    audit: 'Checked',
    checkin: 'HomeFilled',
    checkout: 'SwitchButton',
    transfer: 'Refresh'
  }
  return map[type] || 'Bell'
}

// ============ 动态颜色映射 ============
const getActivityColor = (type) => {
  const map = {
    login: '#409EFF',
    apply: '#E6A23C',
    audit: '#67C23A',
    checkin: '#409EFF',
    checkout: '#F56C6C',
    transfer: '#909399'
  }
  return map[type] || '#909399'
}

// ============ 状态标签类型 ============
const getStatusType = (status) => {
  const map = {
    '已完成': 'success',
    '待处理': 'warning',
    '已驳回': 'danger',
    '进行中': 'primary'
  }
  return map[status] || 'info'
}

// ============ 加载统计数据 ============
const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.code === 200 && res.data) {
      const data = res.data
      statsCards.value = [
        { label: '总床位', value: data.totalBeds || 0, icon: 'OfficeBuilding', color: '#409EFF' },
        { label: '已入住', value: data.occupiedBeds || 0, icon: 'User', color: '#67C23A' },
        { label: '空床位', value: data.emptyBeds || 0, icon: 'HomeFilled', color: '#E6A23C' },
        { label: '入住率', value: (data.occupancyRate || 0) + '%', icon: 'DataLine', color: '#F56C6C' }
      ]
      updateTime.value = new Date().toLocaleString()
    } else {
      useMockStats()
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    useMockStats()
  }
}

const useMockStats = () => {
  statsCards.value = [
    { label: '总床位', value: 1200, icon: 'OfficeBuilding', color: '#409EFF' },
    { label: '已入住', value: 986, icon: 'User', color: '#67C23A' },
    { label: '空床位', value: 214, icon: 'HomeFilled', color: '#E6A23C' },
    { label: '入住率', value: '82.2%', icon: 'DataLine', color: '#F56C6C' }
  ]
}

// ============ 加载楼栋入住率数据 ============
const loadBuildingData = async () => {
  chartLoading.value = true
  try {
    const res = await getBuildingOccupancyData()
    if (res.code === 200 && res.data) {
      renderChart1(res.data)
    } else {
      renderChart1([
        { name: '1号楼', value: 85 },
        { name: '2号楼', value: 62 },
        { name: '3号楼', value: 93 },
        { name: '4号楼', value: 78 },
        { name: '5号楼', value: 45 },
        { name: '6号楼', value: 70 }
      ])
    }
  } catch (error) {
    renderChart1([
      { name: '1号楼', value: 85 },
      { name: '2号楼', value: 62 },
      { name: '3号楼', value: 93 },
      { name: '4号楼', value: 78 },
      { name: '5号楼', value: 45 },
      { name: '6号楼', value: 70 }
    ])
  } finally {
    chartLoading.value = false
  }
}

// ============ 加载男女比例数据 ============
const loadGenderData = async () => {
  try {
    const res = await getGenderRatioData()
    if (res.code === 200 && res.data) {
      renderChart2(res.data)
    } else {
      renderChart2([
        { name: '男生', value: 520, color: '#409EFF' },
        { name: '女生', value: 466, color: '#F56C6C' }
      ])
    }
  } catch (error) {
    renderChart2([
      { name: '男生', value: 520, color: '#409EFF' },
      { name: '女生', value: 466, color: '#F56C6C' }
    ])
  }
}

// ============ 加载最近动态 ============
const loadActivities = async () => {
  try {
    const res = await getRecentActivities({ limit: 10 })
    if (res.code === 200 && res.data) {
      activities.value = res.data
    } else {
      useMockActivities()
    }
  } catch (error) {
    console.error('加载动态失败:', error)
    useMockActivities()
  }
}

const useMockActivities = () => {
  activities.value = [
    { username: '系统', operation_detail: '登录系统', type: 'login', time: new Date().toLocaleString(), status: '已完成' },
    { username: '张三', operation_detail: '申请入住 1栋-101-A', type: 'apply', time: new Date().toLocaleString(), status: '待处理' },
    { username: '管理员', operation_detail: '审核通过 李四 的入住申请', type: 'audit', time: new Date().toLocaleString(), status: '已完成' }
  ]
}

// ============ 点击动态 ============
const handleActivityClick = (activity) => {
  if (activity.type === 'apply' || activity.type === 'audit') {
    router.push('/admin/audit')
  } else if (activity.type === 'checkin' || activity.type === 'checkout') {
    router.push('/student/my-dorm')
  } else {
    ElMessage.info(`动态：${activity.operation_detail || activity.action}`)
  }
}

// ============ 查看更多 ============
const viewAllActivities = () => {
  ElMessage.info('跳转到完整动态列表')
}

// ============ 渲染楼栋入住率图 ============
const renderChart1 = (data) => {
  if (!chartRef1.value) return
  if (chart1) {
    chart1.dispose()
    chart1 = null
  }
  chart1 = echarts.init(chartRef1.value)

  const names = data.map(item => item.name)
  const values = data.map(item => item.value)

  chart1.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}<br/>入住率: {c}%' },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: names
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: { formatter: '{value}%' }
    },
    series: [
      {
        name: '入住率',
        type: 'bar',
        data: values,
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: function (params) {
            const val = params.value
            if (val > 80) return '#67C23A'
            if (val > 60) return '#E6A23C'
            return '#F56C6C'
          }
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%'
        }
      }
    ]
  })
}

// ============ 渲染男女比例图 ============
const renderChart2 = (data) => {
  if (!chartRef2.value) return
  if (chart2) {
    chart2.dispose()
    chart2 = null
  }
  chart2 = echarts.init(chartRef2.value)

  const chartData = data.map(item => ({
    value: item.value,
    name: item.name,
    itemStyle: { color: item.color }
  }))

  chart2.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      orient: 'horizontal',
      right: '3%',
      top: '3%',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { fontSize: 12 }
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '62%'],
        center: ['50%', '55%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}\n{d}%',
          fontSize: 13,
          lineHeight: 18,
          color: '#333'
        },
        labelLine: {
          show: true,
          length: 12,
          length2: 8
        },
        emphasis: {
          scale: true,
          label: { show: true }
        },
        data: chartData
      }
    ]
  })
}

// ============ 加载所有数据 ============
const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadStats(),
      loadBuildingData(),
      loadGenderData(),
      loadActivities()
    ])
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// ============ 窗口变化重新渲染 ============
const handleResize = () => {
  if (chart1) chart1.resize()
  if (chart2) chart2.resize()
}

// ============ 初始化 ============
onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  if (chart1) {
    chart1.dispose()
    chart1 = null
  }
  if (chart2) {
    chart2.dispose()
    chart2 = null
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard {
  padding: 4px 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stats-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stats-label {
  color: #909399;
  font-size: 14px;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-top: 4px;
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

/* ===== 动态列表 ===== */
.activity-card {
  border-radius: 8px;
}

.activity-list {
  max-height: 420px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.activity-item:hover {
  background: #f5f7fa;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
  margin-right: 12px;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 14px;
  color: #303133;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
}

.activity-user {
  font-weight: 600;
  color: #409EFF;
}

.activity-action {
  color: #606266;
}

.activity-target {
  color: #409EFF;
  cursor: pointer;
}

.activity-target:hover {
  text-decoration: underline;
}

.activity-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.activity-status {
  flex-shrink: 0;
  margin-left: 12px;
}

.view-more {
  text-align: center;
  padding: 12px 0;
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
  border-top: 1px solid #f0f0f0;
}

.view-more:hover {
  color: #66b1ff;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #909399;
}

:deep(.el-timeline-item__content) {
  font-size: 14px;
  color: #606266;
}

@media (max-width: 768px) {
  .stats-value {
    font-size: 20px;
  }
  .stats-icon {
    width: 44px;
    height: 44px;
  }
  .stats-icon .el-icon {
    font-size: 20px !important;
  }
  .activity-item {
    padding: 10px 12px;
    flex-wrap: wrap;
  }
  .activity-status {
    margin-left: 44px;
    margin-top: 4px;
  }
}
</style>