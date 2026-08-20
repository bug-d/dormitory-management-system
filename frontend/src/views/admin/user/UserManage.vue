<template>
  <div class="user-manage">
    <!-- ===== 搜索栏 ===== -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" size="default">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/姓名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable>
            <el-option label="系统管理员" value="admin" />
            <el-option label="宿舍管理员" value="manager" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
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
          prop="username" 
          label="用户名" 
          min-width="120" 
          sortable="custom"
        />
        <el-table-column 
          prop="realName" 
          label="姓名" 
          min-width="100" 
          sortable="custom"
        />
        <el-table-column 
          prop="role" 
          label="角色" 
          min-width="120" 
          align="center" 
          sortable="custom"
        >
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="保护" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.protected ? 'danger' : 'info'" size="small">
              {{ row.protected ? '已锁定' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" min-width="170" />

        <el-table-column label="操作" width="380" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="row.protected ? 'success' : 'warning'"
              @click="toggleProtect(row)"
            >
              {{ row.protected ? '解锁' : '锁定' }}
            </el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
              :disabled="row.protected && row.status === 1"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
              :disabled="row.protected"
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width:100%">
            <el-option label="系统管理员" value="admin" />
            <el-option label="宿舍管理员" value="manager" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        <el-form-item label="保护锁定">
          <el-switch
            v-model="formData.protected"
            :active-value="true"
            :inactive-value="false"
            active-text="已锁定"
            inactive-text="正常"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- ===== 重置密码弹窗 ===== -->
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="420px">
      <el-form :model="resetPwdForm" label-width="100px">
        <el-form-item label="用户名">
          <span>{{ resetPwdForm.username }}</span>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetPwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="resetPwdLoading" @click="confirmResetPassword">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, addUser, updateUser, deleteUser, enableUser, disableUser, resetPassword } from '@/api/user'

// ============ 状态 ============
const loading = ref(false)
const submitLoading = ref(false)
const resetPwdLoading = ref(false)
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

// ============ 搜索条件 ============
const searchForm = reactive({
  keyword: '',
  role: '',
  status: undefined
})

// ============ 弹窗 ============
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const isEdit = ref(false)

const resetPwdVisible = ref(false)
const resetPwdForm = reactive({
  userId: null,
  username: '',
  newPassword: ''
})

const formRef = ref(null)
const formData = reactive({
  id: null,
  username: '',
  realName: '',
  role: '',
  email: '',
  phone: '',
  password: '',
  status: 1,
  protected: false
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于 6 位', trigger: 'blur' }
  ]
}

const tableRef = ref(null)

const getRoleLabel = (role) => {
  const map = { admin: '系统管理员', manager: '宿舍管理员', student: '学生' }
  return map[role] || role
}

const getRoleType = (role) => {
  const map = { admin: 'danger', manager: 'warning', student: 'success' }
  return map[role] || 'info'
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

// ============ 锁定/解锁 ============
const toggleProtect = (row) => {
  const action = row.protected ? '解锁' : '锁定'
  ElMessageBox.confirm(
    `确认${action}用户 "${row.username}" 吗？\n${row.protected ? '解锁后该用户可以被删除' : '锁定后该用户将不能被删除'}`,
    '提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    row.protected = !row.protected
    ElMessage.success(`${action}成功`)
  }).catch(() => {})
}

// ============ 批量删除 ============
const handleBatchDelete = () => {
  if (selectedIds.value.size === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }

  const ids = Array.from(selectedIds.value)
  
  ElMessageBox.confirm(
    `确认删除选中的 ${ids.length} 名用户吗？\n\n（注意：选中跨页数据共 ${ids.length} 条）`,
    '批量删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      for (const id of ids) {
        await deleteUser(id)
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
    const res = await getUsers({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      role: searchForm.role,
      status: searchForm.status,
      orderBy: sortOrder.value.orderBy,
      orderDir: sortOrder.value.orderDir
    })
    tableData.value = (res.data?.records || []).map(item => ({
      ...item,
      protected: item.protected || false
    }))
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
  searchForm.keyword = ''
  searchForm.role = ''
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
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(formData, row)
  formData.password = ''
  dialogVisible.value = true
}

const handleDelete = (row) => {
  if (row.protected) {
    ElMessage.warning('该用户已被锁定，无法删除')
    return
  }
  ElMessageBox.confirm(`确认删除用户 "${row.username}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      selectedIds.value.delete(row.id)
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const toggleStatus = (row) => {
  if (row.protected && row.status === 1) {
    ElMessage.warning('该用户已被锁定，无法禁用')
    return
  }
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确认${action}用户 "${row.username}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      if (row.status === 1) {
        await disableUser(row.id)
      } else {
        await enableUser(row.id)
      }
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
      await updateUser(formData)
      ElMessage.success('更新成功')
    } else {
      await addUser(formData)
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
  formData.username = ''
  formData.realName = ''
  formData.role = ''
  formData.email = ''
  formData.phone = ''
  formData.password = ''
  formData.status = 1
  formData.protected = false
  formRef.value?.clearValidate()
}

// ============ 重置密码 ============
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确认重置用户 "${row.username}" 的密码吗？\n\n重置后密码将恢复为默认密码（123456）`,
    '重置密码确认',
    {
      confirmButtonText: '确认重置',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await resetPassword(row.id)
      ElMessage.success(`密码已重置为默认密码：${res.data}`)
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '重置失败')
    }
  }).catch(() => {})
}

const confirmResetPassword = async () => {
  if (!resetPwdForm.newPassword || resetPwdForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度不少于 6 位')
    return
  }
  resetPwdLoading.value = true
  try {
    await resetPassword(resetPwdForm.userId)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch (error) {
    ElMessage.error('重置失败')
  } finally {
    resetPwdLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-manage {
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