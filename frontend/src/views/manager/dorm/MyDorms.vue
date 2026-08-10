<template>
  <div class="my-dorms">
    <!-- ===== 统计卡片 ===== -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">管辖宿舍</div>
            <div class="stats-value" style="color: #409EFF;">{{ stats.total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">总床位</div>
            <div class="stats-value" style="color: #67C23A;">{{ stats.beds }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">已入住</div>
            <div class="stats-value" style="color: #E6A23C;">{{ stats.occupied }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">入住率</div>
            <div class="stats-value" style="color: #F56C6C;">{{ stats.rate }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 搜索栏 ===== -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" size="default">
        <el-form-item label="楼栋">
          <el-input
            v-model="searchForm.buildingNo"
            placeholder="请输入楼栋号"
            clearable
            style="width:150px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:140px">
            <el-option label="可用" value="available" />
            <el-option label="已满" value="full" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ===== 宿舍卡片列表 ===== -->
    <el-row :gutter="20" v-loading="loading">
      <el-col
        v-for="dorm in tableData"
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
            <el-tag :type="getStatusType(dorm.status)" size="small">
              {{ getStatusLabel(dorm.status) }}
            </el-tag>
          </div>
          <div class="dorm-body">
            <div class="dorm-info">
              <span class="label">性别</span>
              <span>{{ dorm.gender === 'M' ? '男' : '女' }}</span>
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
              />
            </div>
            <div class="dorm-info" v-if="dorm.description">
              <span class="label">备注</span>
              <span class="desc">{{ dorm.description }}</span>
            </div>
          </div>
          <div class="dorm-footer">
            <el-button size="small" type="primary" @click="viewDetail(dorm)">查看详情</el-button>
            <el-button size="small" type="warning" @click="viewResidents(dorm)">入住人员</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 分页 ===== -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[6, 12, 24, 48]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- ===== 详情弹窗 ===== -->
    <el-dialog v-model="detailVisible" title="宿舍详情" width="560px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="楼栋">{{ detailData.buildingNo }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ detailData.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ detailData.floorNo }}层</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender === 'M' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="总床位">{{ detailData.capacity }}</el-descriptions-item>
        <el-descriptions-item label="已入住">{{ detailData.occupied }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusLabel(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="房间类型">{{ detailData.roomType === 'standard' ? '标准间' : '套间' }}</el-descriptions-item>
        <el-descriptions-item label="空调">{{ detailData.hasAirConditioner ? '有' : '无' }}</el-descriptions-item>
        <el-descriptions-item label="独立卫浴">{{ detailData.hasPrivateBathroom ? '有' : '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.description || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ===== 入住人员弹窗 ===== -->
    <el-dialog v-model="residentsVisible" title="入住人员" width="520px">
      <el-table :data="residentsData" border stripe>
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="bedNo" label="床号" width="80" align="center" />
        <el-table-column prop="phone" label="手机号" />
      </el-table>
      <template #footer>
        <el-button @click="residentsVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getManagedDorms, getManagedDormStats, getDormResidents } from '@/api/manager'

// ============ 状态 ============
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(6)

// ============ 统计 ============
const stats = reactive({
  total: 0,
  beds: 0,
  occupied: 0,
  rate: 0
})

// ============ 搜索条件 ============
const searchForm = reactive({
  buildingNo: '',
  status: ''
})

// ============ 弹窗 ============
const detailVisible = ref(false)
const detailData = reactive({
  id: null,
  buildingNo: '',
  floorNo: '',
  roomNo: '',
  gender: '',
  capacity: 0,
  occupied: 0,
  status: '',
  roomType: '',
  hasAirConditioner: 0,
  hasPrivateBathroom: 0,
  description: ''
})

const residentsVisible = ref(false)
const residentsData = ref([])

// ============ 状态映射 ============
const getStatusLabel = (status) => {
  const map = { available: '可用', full: '已满', maintenance: '维修中', closed: '已关闭' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { available: 'success', full: 'danger', maintenance: 'warning', closed: 'info' }
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
    const [listRes, statsRes] = await Promise.all([
      getManagedDorms({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm }),
      getManagedDormStats()
    ])
    tableData.value = listRes.data.records
    total.value = Number(listRes.data.total)
    stats.total = statsRes.data.dormCount
    stats.beds = statsRes.data.totalBeds
    stats.occupied = statsRes.data.occupiedBeds
    stats.rate = Math.round(statsRes.data.occupancyRate || 0)
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
  searchForm.buildingNo = ''
  searchForm.status = ''
  handleSearch()
}

// ============ 查看详情 ============
const viewDetail = (row) => {
  Object.assign(detailData, row)
  detailVisible.value = true
}

// ============ 查看入住人员 ============
const viewResidents = async (row) => {
  try {
    const res = await getDormResidents(row.id)
    residentsData.value = res.data || []
    residentsVisible.value = true
  } catch (error) {
    ElMessage.error(error.message || '加载入住人员失败')
  }
}

// ============ 初始化 ============
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.my-dorms {
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

.dorm-info .desc {
  color: #E6A23C;
  font-size: 12px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
