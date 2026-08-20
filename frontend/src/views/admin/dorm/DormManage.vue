<template>
  <div class="dorm-manage">
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
        <el-form-item label="性别">
          <el-select v-model="searchForm.gender" placeholder="全部" clearable style="width:120px">
            <el-option label="男" value="M" />
            <el-option label="女" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:140px">
            <el-option label="可用" value="available" />
            <el-option label="已满" value="full" />
            <el-option label="维修中" value="maintenance" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>

        <el-form-item style="float: right;">
          <el-button 
            type="danger" 
            :disabled="selectedIds.size === 0"
            @click="handleBatchDelete"
          >
            批量删除（{{ selectedIds.size }}）
          </el-button>
          <el-button type="primary" @click="handleAdd">新增宿舍</el-button>
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
        style="width: 100%; overflow-x: auto;"
        @sort-change="handleSortChange"
        :max-height="500"
        ref="tableRef"
        @select="handleSelect"
        @select-all="handleSelectAll"
        row-key="id"
      >
        <el-table-column type="selection" width="55" align="center" fixed="left" />

        <el-table-column label="序号" width="70" align="center" fixed="left">
          <template #default="{ $index }">
            {{ (pageNum - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>

        <el-table-column 
          prop="buildingNo" 
          label="楼栋" 
          min-width="100" 
          sortable="custom"
        />
        <el-table-column 
          prop="floorNo" 
          label="楼层" 
          width="80" 
          align="center" 
          sortable="custom"
        />
        <el-table-column 
          prop="roomNo" 
          label="房间号" 
          width="100" 
          sortable="custom"
        />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'M' ? 'primary' : 'danger'">
              {{ row.gender === 'M' ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="总床位" width="90" align="center" />
        <el-table-column prop="occupied" label="已入住" width="90" align="center" />
        <el-table-column label="空床位" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.capacity - row.occupied > 0 ? 'success' : 'danger'">
              {{ row.capacity - row.occupied }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="入住率" width="100" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.round((row.occupied / row.capacity) * 100)"
              :color="getProgressColor(row)"
              :stroke-width="8"
            />
          </template>
        </el-table-column>
        <el-table-column prop="roomType" label="房间类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.roomType === 'standard' ? 'info' : 'success'">
              {{ row.roomType === 'standard' ? '标准间' : '套间' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="设施" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.hasAirConditioner" size="small" type="primary" style="margin-right:4px;">空调</el-tag>
            <el-tag v-if="row.hasPrivateBathroom" size="small" type="success">卫浴</el-tag>
            <span v-if="!row.hasAirConditioner && !row.hasPrivateBathroom" style="color:#909399;">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
              :disabled="row.occupied > 0"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- ===== 新增/编辑弹窗 ===== -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼栋" prop="buildingNo">
              <el-input v-model="formData.buildingNo" placeholder="如：1栋、A栋" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floorNo">
              <el-input-number v-model="formData.floorNo" :min="1" :max="30" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNo">
              <el-input v-model="formData.roomNo" placeholder="如：101" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formData.gender" placeholder="请选择" style="width:100%">
                <el-option label="男" value="M" />
                <el-option label="女" value="F" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总床位数" prop="capacity">
              <el-input-number v-model="formData.capacity" :min="2" :max="6" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="formData.roomType" placeholder="请选择" style="width:100%">
                <el-option label="标准间" value="standard" />
                <el-option label="套间" value="suite" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="空调">
              <el-switch v-model="formData.hasAirConditioner" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="独立卫浴">
              <el-switch v-model="formData.hasPrivateBathroom" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="每学期费用" prop="pricePerTerm">
              <el-input-number v-model="formData.pricePerTerm" :min="0" :step="100" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择" style="width:100%">
                <el-option label="可用" value="available" />
                <el-option label="已满" value="full" />
                <el-option label="维修中" value="maintenance" />
                <el-option label="已关闭" value="closed" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDorms, addDorm, updateDorm, deleteDorm } from '@/api/dorm'

// ============ 状态 ============
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// ============ 跨页选择 ============
const selectedIds = ref(new Set())

// ============ 排序状态 ============
const sortOrder = ref({
  orderBy: 'id',
  orderDir: 'asc'
})

const searchForm = reactive({
  buildingNo: '',
  gender: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增宿舍')
const isEdit = ref(false)

const formRef = ref(null)
const formData = reactive({
  id: null,
  buildingNo: '',
  floorNo: 1,
  roomNo: '',
  gender: 'M',
  capacity: 4,
  roomType: 'standard',
  hasAirConditioner: 0,
  hasPrivateBathroom: 0,
  pricePerTerm: 0,
  status: 'available',
  description: ''
})

const formRules = {
  buildingNo: [
    { required: true, message: '请输入楼栋号', trigger: 'blur' }
  ],
  floorNo: [
    { required: true, message: '请输入楼层', trigger: 'blur' }
  ],
  roomNo: [
    { required: true, message: '请输入房间号', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  capacity: [
    { required: true, message: '请选择床位数', trigger: 'change' }
  ]
}

const tableRef = ref(null)

// ============ 状态映射 ============
const getStatusLabel = (status) => {
  const map = {
    available: '可用',
    full: '已满',
    maintenance: '维修中',
    closed: '已关闭'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    available: 'success',
    full: 'danger',
    maintenance: 'warning',
    closed: 'info'
  }
  return map[status] || 'info'
}

const getProgressColor = (row) => {
  const rate = Math.round((row.occupied / row.capacity) * 100)
  if (rate >= 100) return '#F56C6C'
  if (rate >= 80) return '#E6A23C'
  return '#67C23A'
}

// ============ 选择事件 ============
const handleSelect = (selection, row) => {
  if (selection.includes(row)) {
    selectedIds.value.add(row.id)
  } else {
    selectedIds.value.delete(row.id)
  }
}

const handleSelectAll = (selection) => {
  const currentPageIds = tableData.value.map(row => row.id)
  if (selection.length === tableData.value.length) {
    currentPageIds.forEach(id => selectedIds.value.add(id))
  } else {
    currentPageIds.forEach(id => selectedIds.value.delete(id))
  }
}

// ============ 批量删除 ============
const handleBatchDelete = () => {
  if (selectedIds.value.size === 0) {
    ElMessage.warning('请先选择要删除的宿舍')
    return
  }

  const ids = Array.from(selectedIds.value)
  
  ElMessageBox.confirm(
    `确认删除选中的 ${ids.length} 间宿舍吗？\n\n（注意：选中跨页数据共 ${ids.length} 条）`,
    '批量删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      for (const id of ids) {
        await deleteDorm(id)
      }
      ElMessage.success(`成功删除 ${ids.length} 条数据`)
      selectedIds.value.clear()
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '批量删除失败')
    }
  }).catch(() => {})
}

// ============ 排序变化 ============
const handleSortChange = ({ prop, order }) => {
  if (order === 'ascending') {
    sortOrder.value.orderBy = prop
    sortOrder.value.orderDir = 'asc'
  } else if (order === 'descending') {
    sortOrder.value.orderBy = prop
    sortOrder.value.orderDir = 'desc'
  } else {
    sortOrder.value.orderBy = 'id'
    sortOrder.value.orderDir = 'asc'
  }
  pageNum.value = 1
  selectedIds.value.clear()
  loadData()
}

// ============ 加载数据 ============
const loadData = async () => {
  loading.value = true
  try {
    const res = await getDorms({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      buildingNo: searchForm.buildingNo,
      gender: searchForm.gender,
      status: searchForm.status,
      orderBy: sortOrder.value.orderBy,
      orderDir: sortOrder.value.orderDir
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// ============ 搜索 ============
const handleSearch = () => {
  pageNum.value = 1
  selectedIds.value.clear()
  loadData()
}

const resetSearch = () => {
  searchForm.buildingNo = ''
  searchForm.gender = ''
  searchForm.status = ''
  sortOrder.value.orderBy = 'id'
  sortOrder.value.orderDir = 'asc'
  selectedIds.value.clear()
  pageNum.value = 1
  loadData()
}

// ============ 新增 ============
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增宿舍'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑宿舍'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  if (row.occupied > 0) {
    ElMessage.warning('该宿舍已有人入住，无法删除')
    return
  }
  ElMessageBox.confirm(`确认删除宿舍 "${row.buildingNo}-${row.roomNo}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDorm(row.id)
      ElMessage.success('删除成功')
      selectedIds.value.delete(row.id)
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateDorm(formData)
      ElMessage.success('更新成功')
    } else {
      await addDorm(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  formData.id = null
  formData.buildingNo = ''
  formData.floorNo = 1
  formData.roomNo = ''
  formData.gender = 'M'
  formData.capacity = 4
  formData.roomType = 'standard'
  formData.hasAirConditioner = 0
  formData.hasPrivateBathroom = 0
  formData.pricePerTerm = 0
  formData.status = 'available'
  formData.description = ''
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dorm-manage {
  padding: 4px 0;
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