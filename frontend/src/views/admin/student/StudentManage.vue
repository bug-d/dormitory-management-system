<template>
  <div class="student-manage">
    <!-- ===== 搜索栏 ===== -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" size="default">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="学号/姓名"
            clearable
            @keyup.enter="handleSearch"
            style="width:180px"
          />
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="searchForm.grade" placeholder="全部" clearable style="width:120px">
            <el-option label="2024级" value="2024" />
            <el-option label="2023级" value="2023" />
            <el-option label="2022级" value="2022" />
            <el-option label="2021级" value="2021" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="searchForm.gender" placeholder="全部" clearable style="width:100px">
            <el-option label="男" value="M" />
            <el-option label="女" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:100px">
            <el-option label="在读" :value="1" />
            <el-option label="已毕业" :value="0" />
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
          <el-button type="primary" @click="handleAdd">新增学生</el-button>
          <el-button type="success" @click="handleImport">导入Excel</el-button>
          <el-button type="warning" @click="handleExport">导出Excel</el-button>
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
          prop="studentNo" 
          label="学号" 
          min-width="130" 
          sortable="custom"
        />
        <el-table-column 
          prop="name" 
          label="姓名" 
          min-width="80" 
          sortable="custom"
        />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'M' ? 'primary' : 'danger'">
              {{ row.gender === 'M' ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column 
          prop="grade" 
          label="年级" 
          width="80" 
          align="center" 
          sortable="custom"
        />
        <el-table-column prop="major" label="专业" min-width="130" />
        <el-table-column prop="className" label="班级" min-width="100" />
        <el-table-column prop="idCard" label="身份证号" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="emergencyContact" label="紧急联系人" min-width="100" />
        <el-table-column prop="emergencyPhone" label="紧急电话" min-width="130" />
        <el-table-column prop="isNew" label="新生" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isNew === 'Y' ? 'success' : 'info'">
              {{ row.isNew === 'Y' ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在读' : '已毕业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="toggleStatus(row)">
              {{ row.status === 1 ? '毕业' : '恢复' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="formData.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formData.gender" placeholder="请选择" style="width:100%">
                <el-option label="男" value="M" />
                <el-option label="女" value="F" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-input v-model="formData.grade" placeholder="如：2024" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="formData.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="formData.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急电话" prop="emergencyPhone">
              <el-input v-model="formData.emergencyPhone" placeholder="请输入紧急电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="是否新生" prop="isNew">
          <el-switch
            v-model="formData.isNew"
            active-value="Y"
            inactive-value="N"
            active-text="是"
            inactive-text="否"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- ===== 导入弹窗 ===== -->
    <el-dialog v-model="importVisible" title="导入学生" width="480px" destroy-on-close>
      <el-upload
        ref="uploadRef"
        drag
        action="#"
        :before-upload="handleBeforeUpload"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="fileList"
        accept=".xlsx,.xls"
        :limit="1"
        :auto-upload="false"
      >
        <el-icon class="upload-icon"><Upload /></el-icon>
        <div class="upload-text">点击或拖拽上传 Excel 文件</div>
        <div class="upload-hint">仅支持 .xlsx .xls 格式</div>
      </el-upload>
      <div v-if="uploadFileName" style="margin-top: 10px; color: #67C23A;">
        已选择文件：{{ uploadFileName }}
      </div>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" @click="confirmImport" :disabled="!uploadFileData">
          确认导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import {
  getStudents,
  addStudent,
  updateStudent,
  deleteStudent,
  graduateStudent,
  batchDeleteStudents,
  importStudents,
  exportStudents
} from '@/api/student'

// ============ 状态 ============
const loading = ref(false)
const submitLoading = ref(false)
const importLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// ============ 跨页选择核心：使用 Set 存储所有选中的 ID ============
const selectedIds = ref(new Set())

// ============ 排序状态 ============
const sortOrder = ref({
  orderBy: 'id',
  orderDir: 'asc'
})

// ============ 搜索条件 ============
const searchForm = reactive({
  keyword: '',
  grade: '',
  gender: '',
  status: undefined
})

// ============ 弹窗 ============
const dialogVisible = ref(false)
const dialogTitle = ref('新增学生')
const isEdit = ref(false)

const importVisible = ref(false)
const uploadRef = ref(null)
const uploadFileData = ref(null)
const uploadFileName = ref('')
const fileList = ref([])

const formRef = ref(null)
const formData = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: 'M',
  grade: '',
  major: '',
  className: '',
  phone: '',
  idCard: '',
  emergencyContact: '',
  emergencyPhone: '',
  isNew: 'Y'
})

const formRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  grade: [{ required: true, message: '请输入年级', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }]
}

// ============ 表格引用 ============
const tableRef = ref(null)

// ============ 选择单个行 ============
const handleSelect = (selection, row) => {
  if (selection.includes(row)) {
    selectedIds.value.add(row.id)
  } else {
    selectedIds.value.delete(row.id)
  }
}

// ============ 全选/取消全选 ============
const handleSelectAll = (selection) => {
  const currentPageIds = tableData.value.map(row => row.id)
  if (selection.length === tableData.value.length) {
    // 全选：添加当前页所有 ID
    currentPageIds.forEach(id => {
      selectedIds.value.add(id)
    })
  } else {
    // 取消全选：移除当前页所有 ID
    currentPageIds.forEach(id => {
      selectedIds.value.delete(id)
    })
  }
}

// ============ 加载数据 ============
const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudents({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      grade: searchForm.grade,
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

// ============ 批量删除 ============
const handleBatchDelete = () => {
  if (selectedIds.value.size === 0) {
    ElMessage.warning('请先选择要删除的学生')
    return
  }

  const ids = Array.from(selectedIds.value)
  
  ElMessageBox.confirm(
    `确认删除选中的 ${ids.length} 名学生吗？\n\n（注意：选中跨页数据共 ${ids.length} 条）`,
    '批量删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await batchDeleteStudents(ids)
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

// ============ 搜索 ============
const handleSearch = () => {
  pageNum.value = 1
  selectedIds.value.clear()
  loadData()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.grade = ''
  searchForm.gender = ''
  searchForm.status = undefined
  sortOrder.value.orderBy = 'id'
  sortOrder.value.orderDir = 'asc'
  selectedIds.value.clear()
  pageNum.value = 1
  loadData()
}

// ============ 新增 ============
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增学生'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑学生'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除学生 "${row.name}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStudent(row.id)
      ElMessage.success('删除成功')
      selectedIds.value.delete(row.id)
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const toggleStatus = (row) => {
  const action = row.status === 1 ? '毕业' : '恢复'
  ElMessageBox.confirm(`确认将学生 "${row.name}" 标记为${action}吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await graduateStudent(row.id)
      row.status = row.status === 1 ? 0 : 1
      ElMessage.success(`${action}成功`)
      loadData()
    } catch (error) {
      ElMessage.error(error.message || `${action}失败`)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateStudent(formData)
      ElMessage.success('更新成功')
    } else {
      await addStudent(formData)
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
  formData.studentNo = ''
  formData.name = ''
  formData.gender = 'M'
  formData.grade = ''
  formData.major = ''
  formData.className = ''
  formData.phone = ''
  formData.idCard = ''
  formData.emergencyContact = ''
  formData.emergencyPhone = ''
  formData.isNew = 'Y'
  formRef.value?.clearValidate()
}

// ============ 导入方法 ============
const handleImport = () => {
  importVisible.value = true
  uploadFileData.value = null
  uploadFileName.value = ''
  fileList.value = []
  uploadRef.value?.clearFiles()
}

const handleBeforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                  file.type === 'application/vnd.ms-excel' ||
                  file.name.endsWith('.xlsx') ||
                  file.name.endsWith('.xls')
  if (!isExcel) {
    ElMessage.error('请上传 Excel 文件（.xlsx 或 .xls）')
    return false
  }
  uploadFileData.value = file
  uploadFileName.value = file.name
  fileList.value = [file]
  ElMessage.success(`已选择文件：${file.name}`)
  return false
}

const handleFileChange = (file) => {
  uploadFileData.value = file.raw
  uploadFileName.value = file.name
  fileList.value = [file]
}

const handleFileRemove = () => {
  uploadFileData.value = null
  uploadFileName.value = ''
  fileList.value = []
}

const confirmImport = async () => {
  if (!uploadFileData.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  importLoading.value = true
  try {
    const res = await importStudents(uploadFileData.value)
    ElMessage.success(`导入成功！共导入 ${res.data || 0} 条数据`)
    importVisible.value = false
    uploadFileData.value = null
    uploadFileName.value = ''
    fileList.value = []
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '导入失败，请检查文件格式')
  } finally {
    importLoading.value = false
  }
}

// ============ 导出 ============
const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }

  const headers = ['学号', '姓名', '性别', '年级', '专业', '班级', '身份证号', '手机号', '紧急联系人', '紧急电话', '是否新生', '状态']
  const rows = tableData.value.map(row => [
    row.studentNo || '',
    row.name || '',
    row.gender === 'M' ? '男' : '女',
    row.grade || '',
    row.major || '',
    row.className || '',
    row.idCard ? '\t' + row.idCard : '',
    row.phone ? '\t' + row.phone : '',
    row.emergencyContact || '',
    row.emergencyPhone ? '\t' + row.emergencyPhone : '',
    row.isNew === 'Y' ? '是' : '否',
    row.status === 1 ? '在读' : '已毕业'
  ])

  let csv = '\uFEFF' + headers.join(',') + '\n'
  rows.forEach(row => {
    csv += row.join(',') + '\n'
  })

  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `学生列表_${new Date().toLocaleDateString()}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.student-manage {
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
.upload-icon {
  font-size: 48px;
  color: #409EFF;
  display: block;
  margin-bottom: 12px;
}
.upload-text {
  font-size: 14px;
  color: #606266;
}
.upload-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>