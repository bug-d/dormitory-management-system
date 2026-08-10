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
        <span>最近动态</span>
        <el-button size="small" style="float:right;" @click="loadData">刷新</el-button>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in activities"
          :key="index"
          :timestamp="activity.time"
          :type="activity.type"
          :hollow="true"
        >
          {{ activity.content }}
        </el-timeline-item>
        <el-empty v-if="activities.length === 0" description="暂无动态" :image-size="60" />
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getDashboardStats, getBuildingOccupancyData, getGenderRatioData, getRecentActivities } from '@/api/dashboard'

echarts.use([BarChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

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
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// ============ 加载楼栋入住率数据 ============
const loadBuildingData = async () => {
  chartLoading.value = true
  try {
    const res = await getBuildingOccupancyData()
    if (res.code === 200 && res.data) {
      renderChart1(res.data)
    }
  } catch (error) {
    renderChart1([])
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
    }
  } catch (error) {
    renderChart2([])
  }
}

// ============ 加载最近动态 ============
const loadActivities = async () => {
  try {
    const res = await getRecentActivities({ limit: 10 })
    if (res.code === 200 && res.data) {
      activities.value = res.data
    }
  } catch (error) {
    activities.value = []
  }
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
  chart1.setOption(chart1.getOption())
}

// ============ 渲染男女比例图（饼图）- 修复版 ============
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
    tooltip: { 
      trigger: 'item', 
      formatter: '{b}: {c} ({d}%)' 
    },
    legend: {
      orient: 'horizontal',
      right: '3%',
      top: '3%',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        fontSize: 12
      }
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

.activity-card {
  border-radius: 8px;
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
}
</style>
