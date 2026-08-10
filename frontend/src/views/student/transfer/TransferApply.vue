<template>
  <div class="transfer-apply">
    <!-- ===== 页面标题 ===== -->
    <div class="page-header">
      <h2>🔄 换宿舍申请</h2>
      <p class="subtitle">选择您想换入的宿舍，提交申请后等待管理员审核</p>
    </div>

    <!-- ===== 当前宿舍信息 ===== -->
    <el-card class="current-dorm-card" v-if="hasDorm">
      <template #header>
        <span>当前宿舍</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="dorm-info-item">
            <span class="label">楼栋</span>
            <span class="value">{{ currentDorm.buildingNo }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="dorm-info-item">
            <span class="label">房间</span>
            <span class="value">{{ currentDorm.roomNo }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="dorm-info-item">
            <span class="label">床号</span>
            <span class="value">{{ currentDorm.bedNo }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="dorm-info-item">
            <span class="label">状态</span>
            <el-tag type="success" size="small">已入住</el-tag>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- ===== 选择目标宿舍 ===== -->
    <el-card class="select-dorm-card">
      <template #header>
        <span>选择目标宿舍</span>
        <span style="float: right; font-size: 13px; color: #909399;">
          剩余床位：{{ totalAvailableBeds }} 个
        </span>
      </template>

      <!-- ===== 搜索栏 ===== -->
      <el-form :inline="true" :model="searchForm" size="default">
        <el-form-item label="楼栋">
          <el-input
            v-model="searchForm.buildingNo"
            placeholder="请输入楼栋号"
            clearable
            style="width:150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- ===== 宿舍列表 ===== -->
      <el-row :gutter="20" v-loading="loading">
        <el-col
          v-for="dorm in availableDorms"
          :key="dorm.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          class="dorm-card-col"
        >
          <el-card
            class="dorm-card"
            shadow="hover"
            :class="{ 'selected': selectedDormId === dorm.id }"
            @click="selectDorm(dorm)"
          >
            <div class="dorm-header">
              <div class="dorm-title">
                <span class="building">{{ dorm.buildingNo }}</span>
                <span class="room">{{ dorm.roomNo }}</span>
              </div>
              <el-tag type="success" size="small">可入住</el-tag>
            </div>
            <div class="dorm-body">
              <div class="dorm-info">
                <span class="label">性别</span>
                <span>{{ dorm.gender === 'M' ? '👨 男生' : '👩 女生' }}</span>
              </div>
              <div class="dorm-info">
                <span class="label">床位</span>
                <span>{{ dorm.occupied }} / {{ dorm.capacity }}</span>
              </div>
              <div class="dorm-progress">
                <el-progress
                  :percentage="Math.round((dorm.occupied / dorm.capacity) * 100)"
                  :color="getProgressColor(dorm)"
                  :stroke-width="8"
                  :show-text="false"
                />
              </div>
              <div class="dorm-info" v-if="dorm.hasAirConditioner || dorm.hasPrivateBathroom">
                <span class="label">设施</span>
                <span>
                  <el-tag v-if="dorm.hasAirConditioner" size="small" type="primary">空调</el-tag>
                  <el-tag v-if="dorm.hasPrivateBathroom" size="small" type="success">独立卫浴</el-tag>
                </span>
              </div>
            </div>
            <div class="dorm-footer">
              <el-radio
                v-model="selectedDormId"
                :label="dorm.id"
                @click.stop
              >
                选择
              </el-radio>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && availableDorms.length === 0" description="暂无可用宿舍" />
    </el-card>

    <!-- ===== 提交区域 ===== -->
    <el-card class="submit-card" v-if="selectedDormId">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="目标宿舍">
          <el-tag type="success" size="large">
            {{ selectedDorm.buildingNo }} - {{ selectedDorm.roomNo }}
            （剩余床位：{{ selectedDorm.capacity - selectedDorm.occupied }} 个）
          </el-tag>
        </el-form-item>
        <el-form-item label="目标床位" prop="bedNo">
          <el-select v-model="formData.bedNo" placeholder="请选择床位" style="width: 220px">
            <el-option
              v-for="bed in ['A', 'B', 'C', 'D'].slice(0, selectedDorm.capacity || 0)"
              :key="bed"
              :label="`${bed} 床`"
              :value="bed"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请理由" prop="applyReason">
          <el-input
            v-model="formData.applyReason"
            type="textarea"
            :rows="4"
            placeholder="请填写换宿舍理由（建议填写，方便管理员审核）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="submitLoading" @click="handleSubmit">
            提交申请
          </el-button>
          <el-button size="large" @click="cancelSelect">取消选择</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ===== 申请记录 ===== -->
    <el-card class="history-card" v-if="historyList.length > 0">
      <template #header>
        <span>我的换宿舍申请记录</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="item in historyList"
          :key="item.id"
          :timestamp="item.createdAt"
          :type="item.status === 'approved' ? 'success' : item.status === 'rejected' ? 'danger' : 'warning'"
          :hollow="true"
        >
          <p>
            <strong>目标宿舍：</strong>{{ item.dormName }}
            <el-tag :type="getStatusType(item.status)" size="small">
              {{ getStatusLabel(item.status) }}
            </el-tag>
          </p>
          <p v-if="item.applyReason" style="color: #909399; font-size: 13px;">
            理由：{{ item.applyReason }}
          </p>
          <p v-if="item.auditRemark" style="color: #F56C6C; font-size: 13px;">
            驳回理由：{{ item.auditRemark }}
          </p>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAvailableStudentDorms,
  getMyDorm,
  getMyApplications,
  applyTransfer
} from '@/api/assignment'

// ============ 状态 ============
const loading = ref(false)
const submitLoading = ref(false)
const hasDorm = ref(false)
const currentDorm = reactive({
  id: null,
  buildingNo: '',
  roomNo: '',
  bedNo: '',
  gender: ''
})
const availableDorms = ref([])
const allAvailableDorms = ref([])
const historyList = ref([])
const selectedDormId = ref(null)
const selectedDorm = ref({})

// ============ 搜索条件 ============
const searchForm = reactive({
  buildingNo: ''
})

// ============ 表单 ============
const formRef = ref(null)
const formData = reactive({
  dormId: null,
  bedNo: '',
  applyReason: ''
})

// ============ 计算属性 ============
const totalAvailableBeds = computed(() => {
  return availableDorms.value.reduce((sum, d) => sum + (d.capacity - d.occupied), 0)
})

// ============ 状态映射 ============
const getStatusLabel = (status) => {
  const map = { pending: '待审核', approved: '已通过', rejected: '已驳回' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

const getProgressColor = (dorm) => {
  const rate = Math.round((dorm.occupied / dorm.capacity) * 100)
  if (rate >= 100) return '#F56C6C'
  if (rate >= 80) return '#E6A23C'
  return '#67C23A'
}

// ============ 加载数据 ============
const loadData = async () => {
  loading.value = true
  try {
    const [dormsRes, historyRes] = await Promise.all([
      getAvailableStudentDorms(),
      getMyApplications()
    ])
    const applications = historyRes.data || []
    const active = applications.find(item => item.status === 'active')
    hasDorm.value = Boolean(active)
    if (hasDorm.value) {
      const dormRes = await getMyDorm()
      Object.assign(currentDorm, dormRes.data.dorm, {
        bedNo: dormRes.data.assignment?.bedNo
      })
    }
    allAvailableDorms.value = (dormsRes.data || []).filter(item => item.id !== currentDorm.id)
    availableDorms.value = [...allAvailableDorms.value]
    historyList.value = applications
      .filter(item => item.type === 'transfer')
      .map(item => ({ ...item, dormName: item.dormName || `宿舍 #${item.dormId}` }))
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// ============ 搜索 ============
const handleSearch = () => {
  const keyword = searchForm.buildingNo.trim()
  availableDorms.value = keyword
    ? allAvailableDorms.value.filter(item => item.buildingNo.includes(keyword))
    : [...allAvailableDorms.value]
}

const resetSearch = () => {
  searchForm.buildingNo = ''
  handleSearch()
}

// ============ 选择宿舍 ============
const selectDorm = (dorm) => {
  if (selectedDormId.value === dorm.id) {
    selectedDormId.value = null
    selectedDorm.value = {}
    return
  }
  selectedDormId.value = dorm.id
  selectedDorm.value = dorm
  formData.dormId = dorm.id
  formData.bedNo = ''
}

// ============ 取消选择 ============
const cancelSelect = () => {
  selectedDormId.value = null
  selectedDorm.value = {}
  formData.dormId = null
  formData.bedNo = ''
}

// ============ 提交申请 ============
const handleSubmit = async () => {
  if (!selectedDormId.value) {
    ElMessage.warning('请选择目标宿舍')
    return
  }

  submitLoading.value = true
  try {
    if (!formData.bedNo) {
      ElMessage.warning('请选择床位')
      return
    }
    await applyTransfer({ ...formData })
    ElMessage.success('换宿舍申请提交成功，请等待管理员审核')
    cancelSelect()
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitLoading.value = false
  }
}

// ============ 初始化 ============
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.transfer-apply {
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

.current-dorm-card,
.select-dorm-card,
.submit-card,
.history-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.dorm-info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4px 0;
}

.dorm-info-item .label {
  color: #909399;
  font-size: 13px;
}

.dorm-info-item .value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

/* ===== 宿舍卡片 ===== */
.dorm-card-col {
  margin-bottom: 20px;
}

.dorm-card {
  border-radius: 10px;
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
  cursor: pointer;
  border: 2px solid transparent;
}

.dorm-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.dorm-card.selected {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.dorm-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
  margin-bottom: 12px;
}

.dorm-title .building {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.dorm-title .room {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-left: 4px;
}

.dorm-body {
  padding: 4px 0;
}

.dorm-info {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 14px;
}

.dorm-info .label {
  color: #909399;
}

.dorm-progress {
  margin: 8px 0;
}

.dorm-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}
</style>
