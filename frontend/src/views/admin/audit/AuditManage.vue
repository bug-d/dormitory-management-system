<template>
  <div class="audit-manage">
    <!-- ===== 统计卡片 ===== -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">待审核</div>
            <div class="stats-value" style="color: #E6A23C;">{{ stats.pending }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">已通过</div>
            <div class="stats-value" style="color: #67C23A;">{{ stats.approved }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">已驳回</div>
            <div class="stats-value" style="color: #F56C6C;">{{ stats.rejected }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">已入住</div>
            <div class="stats-value" style="color: #409EFF;">{{ stats.active }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 搜索栏 ===== -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" size="default">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:140px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
            <el-option label="已入住" value="active" />
            <el-option label="已退宿" value="left" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width:140px">
            <el-option label="新生入住" value="new_checkin" />
            <el-option label="调宿" value="transfer" />
            <el-option label="毕业离校" value="graduate_leave" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right;" v-if="pendingIds.length > 0">
          <el-button type="success" @click="batchApprove">批量通过</el-button>
          <el-button type="danger" @click="batchReject">批量驳回</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ===== 数据表格 ===== -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="申请ID" width="80" align="center" />
        <el-table-column prop="studentName" label="学生" min-width="80" />
        <el-table-column prop="studentNo" label="学号" min-width="120" />
        <el-table-column prop="dormName" label="目标宿舍" min-width="100" />
        <el-table-column prop="bedNo" label="床号" width="70" align="center" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyReason" label="申请理由" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="申请时间" min-width="160" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button size="small" type="success" @click="handleApprove(row)">通过</el-button>
              <el-button size="small" type="danger" @click="handleReject(row)">驳回</el-button>
            </template>
            <el-button size="small" type="primary" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- ===== 分页 ===== -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- ===== 审核弹窗（驳回） ===== -->
    <el-dialog v-model="rejectVisible" title="驳回申请" width="480px">
      <el-form ref="rejectFormRef" :model="rejectForm" label-width="80px">
        <el-form-item label="驳回理由" prop="remark">
          <el-input
            v-model="rejectForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请填写驳回理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitLoading" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>

    <!-- ===== 详情弹窗 ===== -->
    <el-dialog v-model="detailVisible" title="申请详情" width="560px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(detailData.status)">{{ getStatusLabel(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="学生">{{ detailData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ detailData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="目标宿舍">{{ detailData.dormName }}</el-descriptions-item>
        <el-descriptions-item label="床号">{{ detailData.bedNo }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ getTypeLabel(detailData.type) }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailData.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="申请理由" :span="2">{{ detailData.applyReason || '无' }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detailData.auditorName || '未审核' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detailData.auditTime || '未审核' }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ detailData.auditRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPendingList,
  getStatsByStatus,
  audit,
  batchApprove as approveBatch,
  batchReject as rejectBatch
} from '@/api/audit'

// ============ 状态 ============
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])

// ============ 统计 ============
const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  active: 0
})

// ============ 搜索条件 ============
const searchForm = reactive({
  status: '',
  type: ''
})

// ============ 驳回弹窗 ============
const rejectVisible = ref(false)
const rejectFormRef = ref(null)
const rejectForm = reactive({
  id: null,
  remark: ''
})

// ============ 详情弹窗 ============
const detailVisible = ref(false)
const detailData = reactive({
  id: null,
  studentName: '',
  studentNo: '',
  dormName: '',
  bedNo: '',
  type: '',
  status: '',
  applyReason: '',
  createdAt: '',
  auditorName: '',
  auditTime: '',
  auditRemark: ''
})

// ============ 计算属性 ============
const pendingIds = computed(() => {
  return selectedRows.value
    .filter(row => row.status === 'pending')
    .map(row => row.id)
})

// ============ 标签映射 ============
const getStatusLabel = (status) => {
  const map = { pending: '待审核', approved: '已通过', rejected: '已驳回', active: '已入住', left: '已退宿' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger', active: 'primary', left: 'info' }
  return map[status] || 'info'
}

const getTypeLabel = (type) => {
  const map = { new_checkin: '新生入住', transfer: '调宿', graduate_leave: '毕业离校' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { new_checkin: 'success', transfer: 'warning', graduate_leave: 'info' }
  return map[type] || 'info'
}

// ============ 加载数据 ============
const loadData = async () => {
  loading.value = true
  try {
    const [listRes, statsRes] = await Promise.all([getPendingList(), getStatsByStatus()])
    const rows = listRes.data || []
    tableData.value = rows
      .filter(row => !searchForm.status || row.status === searchForm.status)
      .filter(row => !searchForm.type || row.type === searchForm.type)
      .map(row => ({
        ...row,
        studentName: row.studentName || `学生 #${row.studentId}`,
        studentNo: row.studentNo || '-',
        dormName: row.dormName || `宿舍 #${row.dormId}`
      }))
    total.value = tableData.value.length
    Object.assign(stats, { pending: 0, approved: 0, rejected: 0, active: 0 })
    ;(statsRes.data || []).forEach(item => {
      if (Object.prototype.hasOwnProperty.call(stats, item.status)) {
        stats[item.status] = Number(item.count)
      }
    })
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// ============ 搜索 ============
const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const resetSearch = () => {
  searchForm.status = ''
  searchForm.type = ''
  handleSearch()
}

// ============ 选择 ============
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// ============ 通过 ============
const handleApprove = (row) => {
  ElMessageBox.confirm(
    `确认通过「${row.studentName}」的入住申请吗？`,
    '审核确认',
    {
      confirmButtonText: '确定通过',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await audit({ 
        assignmentId: row.id, 
        action: 'approve', 
        remark: '同意入住' 
      })
      row.status = 'approved'
      ElMessage.success('审核通过')
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '审核失败')
    }
  }).catch(() => {})
}

// ============ 驳回 ============
const handleReject = (row) => {
  rejectForm.id = row.id
  rejectForm.remark = ''
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.remark.trim()) {
    ElMessage.warning('请填写驳回理由')
    return
  }
  submitLoading.value = true
  try {
    await audit({ assignmentId: rejectForm.id, action: 'reject', remark: rejectForm.remark })
    ElMessage.success('驳回成功')
    rejectVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('驳回失败')
  } finally {
    submitLoading.value = false
  }
}

// ============ 批量通过 ============
const batchApprove = () => {
  const count = pendingIds.value.length
  ElMessageBox.confirm(`确认批量通过 ${count} 条申请吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await approveBatch(pendingIds.value)
    ElMessage.success(`成功通过 ${count} 条申请`)
    loadData()
  }).catch(() => {})
}

// ============ 批量驳回 ============
const batchReject = () => {
  const count = pendingIds.value.length
  ElMessageBox.confirm(`确认批量驳回 ${count} 条申请吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await rejectBatch({ ids: pendingIds.value, remark: '批量驳回' })
    ElMessage.success(`成功驳回 ${count} 条申请`)
    loadData()
  }).catch(() => {})
}

// ============ 详情 ============
const handleDetail = (row) => {
  Object.assign(detailData, row)
  detailVisible.value = true
}

// ============ 初始化 ============
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.audit-manage {
  padding: 4px 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
}

.stats-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 0;
}

.stats-label {
  color: #909399;
  font-size: 14px;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  margin-top: 4px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
