<template>
  <div class="my-dorm">
    <!-- ===== 页面标题 ===== -->
    <div class="page-header">
      <h2>🏠 我的宿舍</h2>
      <p class="subtitle">查看您的宿舍信息和室友</p>
    </div>

    <!-- ===== 宿舍信息 ===== -->
    <el-card v-if="hasDorm" class="dorm-card">
      <el-row :gutter="30">
        <!-- 左侧：宿舍信息 -->
        <el-col :xs="24" :md="14">
          <div class="dorm-info">
            <div class="dorm-title">
              <span class="building">{{ dormInfo.buildingNo }}</span>
              <span class="room">{{ dormInfo.roomNo }}</span>
              <el-tag :type="dormInfo.status === 'available' ? 'success' : 'danger'" size="small">
                {{ dormInfo.status === 'available' ? '正常' : '已满' }}
              </el-tag>
            </div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="床号">
                <el-tag type="primary">{{ dormInfo.bedNo }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                {{ dormInfo.gender === 'M' ? '👨 男生' : '👩 女生' }}
              </el-descriptions-item>
              <el-descriptions-item label="楼层">{{ dormInfo.floorNo }} 层</el-descriptions-item>
              <el-descriptions-item label="入住时间">{{ dormInfo.startDate }}</el-descriptions-item>
              <el-descriptions-item label="床位">
                {{ dormInfo.occupied }} / {{ dormInfo.capacity }}
              </el-descriptions-item>
              <el-descriptions-item label="空床位">
                <el-tag :type="dormInfo.capacity - dormInfo.occupied > 0 ? 'success' : 'danger'">
                  {{ dormInfo.capacity - dormInfo.occupied }} 个
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="空调" :span="2">
                {{ dormInfo.hasAirConditioner ? '✅ 有' : '❌ 无' }}
              </el-descriptions-item>
              <el-descriptions-item label="独立卫浴" :span="2">
                {{ dormInfo.hasPrivateBathroom ? '✅ 有' : '❌ 无' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>

        <!-- 右侧：入住进度 -->
        <el-col :xs="24" :md="10">
          <div class="dorm-status">
            <h4>入住情况</h4>
            <div class="progress-container">
              <el-progress
                :percentage="Math.round((dormInfo.occupied / dormInfo.capacity) * 100)"
                :color="getProgressColor()"
                :stroke-width="20"
                striped
                striped-flow
              />
            </div>
            <div class="bed-status">
              <div class="bed-item" v-for="i in dormInfo.capacity" :key="i">
                <span class="bed-label">床 {{ String.fromCharCode(64 + i) }}</span>
                <el-tag :type="i <= dormInfo.occupied ? 'success' : 'info'" size="small">
                  {{ i <= dormInfo.occupied ? '已入住' : '空床' }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- ===== 室友信息 ===== -->
      <div class="roommates-section">
        <h4>👥 室友信息</h4>
        <el-table :data="roommates" border stripe style="width: 100%">
          <el-table-column prop="studentNo" label="学号" width="140" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="bedNo" label="床号" width="80" align="center" />
          <el-table-column prop="phone" label="手机号" width="140" />
          <el-table-column prop="major" label="专业" />
        </el-table>
        <el-empty v-if="roommates.length === 0" description="暂无室友信息" :image-size="60" />
      </div>

      <!-- ===== 操作按钮 ===== -->
      <div class="action-buttons">
        <el-button type="warning" @click="handleTransfer">申请换宿舍</el-button>
        <el-button type="danger" @click="handleLeaveDorm">退宿</el-button>
      </div>
    </el-card>

    <!-- ===== 未入住状态 ===== -->
    <el-empty v-else description="您还没有入住宿舍">
      <el-button type="primary" @click="goToSelect">去选宿舍</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyDorm, leaveDorm } from '@/api/assignment'

const router = useRouter()

// ============ 状态 ============
const hasDorm = ref(false)
const dormInfo = reactive({
  id: null,
  buildingNo: '',
  roomNo: '',
  bedNo: '',
  gender: '',
  floorNo: '',
  capacity: 0,
  occupied: 0,
  status: '',
  hasAirConditioner: false,
  hasPrivateBathroom: false,
  startDate: ''
})
const roommates = ref([])

// ============ 获取进度颜色 ============
const getProgressColor = () => {
  const rate = Math.round((dormInfo.occupied / dormInfo.capacity) * 100)
  if (rate >= 100) return '#F56C6C'
  if (rate >= 80) return '#E6A23C'
  return '#67C23A'
}

// ============ 加载数据 ============
const loadData = async () => {
  try {
    const res = await getMyDorm()
    hasDorm.value = Boolean(res.data?.dorm)
    if (!hasDorm.value) return
    Object.assign(dormInfo, res.data.dorm, {
      bedNo: res.data.assignment?.bedNo,
      startDate: res.data.assignment?.startDate
    })
    roommates.value = (res.data.roommates || [])
      .filter(item => item.assignment?.id !== res.data.assignment?.id)
      .map(item => ({ ...item.student, bedNo: item.assignment?.bedNo }))
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

// ============ 去选宿舍 ============
const goToSelect = () => {
  router.push('/student/select')
}

// ============ 申请换宿舍 ============
const handleTransfer = () => {
  router.push('/student/transfer')
}

// ============ 退宿 ============
const handleLeaveDorm = () => {
  ElMessageBox.confirm('确认退宿吗？退宿后将无法恢复，请谨慎操作。', '退宿确认', {
    confirmButtonText: '确认退宿',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await leaveDorm()
    ElMessage.success('退宿成功')
    hasDorm.value = false
  }).catch(() => {})
}

// ============ 初始化 ============
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.my-dorm {
  padding: 4px 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 22px;
  color: #303133;
  margin: 0 0 4px 0;
}

.page-header .subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.dorm-card {
  border-radius: 8px;
}

.dorm-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.dorm-title .building {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.dorm-title .room {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

/* ===== 宿舍状态 ===== */
.dorm-status {
  padding: 10px;
}

.dorm-status h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 16px;
  text-align: center;
}

.progress-container {
  margin: 20px 0;
}

.bed-status {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  margin-top: 16px;
}

.bed-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
  min-width: 60px;
}

.bed-label {
  font-size: 12px;
  color: #909399;
}

/* ===== 室友信息 ===== */
.roommates-section {
  margin-top: 20px;
}

.roommates-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

/* ===== 操作按钮 ===== */
.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>
