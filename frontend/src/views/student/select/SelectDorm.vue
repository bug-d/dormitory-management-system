<template>
  <div class="select-dorm">
    <!-- ===== 页面标题 ===== -->
    <div class="page-header">
      <h2>🏠 选宿舍</h2>
      <p class="subtitle">请选择你心仪的宿舍，提交申请后等待管理员审核</p>
    </div>

    <!-- ===== 统计信息 ===== -->
    <el-card class="info-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="info-item">
            <span class="label">可选宿舍</span>
            <span class="value">{{ availableDorms.length }} 间</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <span class="label">可选床位</span>
            <span class="value">{{ totalBeds }} 个</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <span class="label">申请状态</span>
            <span class="value" :style="{ color: statusColor }">{{ applicationStatus }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

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
        <el-card class="dorm-card" shadow="hover">
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
                :stroke-width="10"
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
            <el-button
              type="primary"
              size="small"
              :disabled="dorm.occupied >= dorm.capacity || hasPending || hasActive"
              @click="showSelectDialog(dorm)"
            >
              选择此宿舍
            </el-button>
            <span v-if="dorm.occupied >= dorm.capacity" class="full-tip">已满</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 空状态 ===== -->
    <el-empty v-if="!loading && availableDorms.length === 0" description="暂无可用宿舍" />

    <!-- ===== 选宿舍弹窗 ===== -->
    <el-dialog v-model="dialogVisible" title="选择宿舍" width="480px">
      <div class="dialog-info">
        <p><strong>宿舍：</strong>{{ selectedDorm.buildingNo }} - {{ selectedDorm.roomNo }}</p>
        <p><strong>剩余床位：</strong>{{ selectedDorm.capacity - selectedDorm.occupied }} 个</p>
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
          <el-form-item label="选择床号" prop="bedNo">
            <el-select v-model="formData.bedNo" placeholder="请选择床号" style="width:100%">
              <el-option
                v-for="bed in availableBeds"
                :key="bed"
                :label="bed"
                :value="bed"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="申请理由" prop="applyReason">
            <el-input
              v-model="formData.applyReason"
              type="textarea"
              :rows="3"
              placeholder="请输入申请理由（选填）"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="confirmSelect">确认选择</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAvailableStudentDorms,
  getMyApplications,
  applyCheckin
} from '@/api/assignment'

// ============ 状态 ============
const loading = ref(false)
const submitLoading = ref(false)
const availableDorms = ref([])
const dialogVisible = ref(false)
const selectedDorm = ref({})

// ============ 表单 ============
const formRef = ref(null)
const formData = reactive({
  dormId: null,
  bedNo: '',
  applyReason: ''
})

// ============ 表单校验规则 ============
const formRules = {
  bedNo: [
    { required: true, message: '请选择床号', trigger: 'change' }
  ]
}

// ============ 申请状态 ============
const hasPending = ref(false)
const hasActive = ref(false)
const applicationStatus = ref('未申请')
const statusColor = ref('#909399')

// ============ 计算属性 ============
const totalBeds = computed(() => {
  return availableDorms.value.reduce((sum, d) => sum + (d.capacity - d.occupied), 0)
})

const availableBeds = computed(() => {
  const dorm = selectedDorm.value
  if (!dorm || !dorm.capacity) return []
  const bedLetters = ['A', 'B', 'C', 'D', 'E', 'F']
  const occupiedBeds = [] // 从后端获取已占用的床号
  return bedLetters
    .slice(0, dorm.capacity)
    .filter(bed => !occupiedBeds.includes(bed))
})

// ============ 获取进度颜色 ============
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
    const [dormRes, applicationRes] = await Promise.all([
      getAvailableStudentDorms(),
      getMyApplications()
    ])
    availableDorms.value = dormRes.data || []
    const applications = applicationRes.data || []
    hasPending.value = applications.some(item => item.status === 'pending')
    hasActive.value = applications.some(item => item.status === 'active')
    applicationStatus.value = hasActive.value ? '已入住' : hasPending.value ? '审核中' : '未申请'
    statusColor.value = hasActive.value ? '#67C23A' : hasPending.value ? '#E6A23C' : '#909399'
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// ============ 显示选择弹窗 ============
const showSelectDialog = (dorm) => {
  if (hasPending.value) {
    ElMessage.warning('您已有待审核的申请，请等待审核完成')
    return
  }
  if (hasActive.value) {
    ElMessage.warning('您已入住宿舍，如需更换请到"换宿舍申请"页面')
    return
  }
  selectedDorm.value = dorm
  formData.dormId = dorm.id
  formData.bedNo = ''
  formData.applyReason = ''
  dialogVisible.value = true
}

// ============ 确认选择 ============
const confirmSelect = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await applyCheckin({ ...formData })
    ElMessage.success('申请提交成功，请等待管理员审核')
    dialogVisible.value = false
    hasPending.value = true
    applicationStatus.value = '审核中'
    statusColor.value = '#E6A23C'
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '申请失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.select-dorm {
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

.info-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 0;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
}

.info-item .value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 4px;
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
}

.dorm-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
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
  justify-content: space-between;
  align-items: center;
}

.full-tip {
  color: #F56C6C;
  font-size: 13px;
}

/* ===== 弹窗 ===== */
.dialog-info p {
  margin: 8px 0;
  font-size: 14px;
}
</style>
