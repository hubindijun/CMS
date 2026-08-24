<template>
  <div class="p-4">
    <a-card>
      <!-- 查询条件 -->
      <a-form :model="queryForm" layout="inline" @submit="handleSearch">
        <a-form-item label="用户名" field="username">
          <a-input v-model="queryForm.username" placeholder="请输入用户名" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="手机号" field="phone">
          <a-input v-model="queryForm.phone" placeholder="请输入手机号" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="状态" field="status">
          <a-select v-model="queryForm.status" placeholder="全部" allow-clear style="width: 120px">
            <a-option :value="1">启用</a-option>
            <a-option :value="0">禁用</a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit">
              <template #icon><icon-search /></template>
              查询
            </a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <!-- 操作栏 -->
      <div class="flex justify-between items-center mb-4 mt-4">
        <a-space>
          <a-button v-if="hasPerm('system:user:add')" type="primary" @click="handleAdd">
            <template #icon><icon-plus /></template>
            新增用户
          </a-button>
        </a-space>
      </div>

      <!-- 表格 -->
      <a-table
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @page-size-change="handleSizeChange"
        :bordered="false"
      >
        <template #columns>
          <a-table-column title="用户名" data-index="username" />
          <a-table-column title="昵称" data-index="nickname" />
          <a-table-column title="手机号" data-index="phone" />
          <a-table-column title="邮箱" data-index="email" />
          <a-table-column title="角色">
            <template #cell="{ record }">
              <a-tag v-for="name in record.roleNames" :key="name" color="blue">{{ name }}</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="状态" data-index="status">
            <template #cell="{ record }">
              <a-switch
                :checked="record.status === 1"
                :disabled="record.username === 'root'"
                @change="(v) => handleToggleStatus(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column title="创建时间" data-index="createTime" width="180" />
          <a-table-column title="操作" width="280" fixed="right">
            <template #cell="{ record }">
              <a-space size="small">
                <a-button v-if="hasPerm('system:user:edit')" type="text" size="small" @click="handleEdit(record)">编辑</a-button>
                <a-button v-if="hasPerm('system:user:role')" type="text" size="small" @click="handleAssignRole(record)">分配角色</a-button>
                <a-button v-if="hasPerm('system:user:resetPwd')" type="text" size="small" @click="handleResetPwd(record)">重置密码</a-button>
                <a-button v-if="hasPerm('system:user:delete')" type="text" status="danger" size="small" :disabled="record.username === 'root'" @click="handleDelete(record)">删除</a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
      width="600px"
    >
      <a-form :model="userForm" layout="vertical" ref="formRef">
        <a-form-item label="用户名" field="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model="userForm.username" :disabled="isEdit" />
        </a-form-item>
        <a-form-item v-if="!isEdit" label="密码" field="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model="userForm.password" />
        </a-form-item>
        <a-form-item label="昵称" field="nickname">
          <a-input v-model="userForm.nickname" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="手机号" field="phone">
              <a-input v-model="userForm.phone" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱" field="email">
              <a-input v-model="userForm.email" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="状态" field="status">
          <a-radio-group v-model="userForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="角色" field="roleIds">
          <a-checkbox-group v-model="userForm.roleIds">
            <a-checkbox v-for="role in roleList" :key="role.id" :value="role.id">
              {{ role.name }}
            </a-checkbox>
          </a-checkbox-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配角色弹窗 -->
    <a-modal
      v-model:visible="roleModalVisible"
      title="分配角色"
      @ok="handleAssignRoleSubmit"
      width="400px"
    >
      <a-checkbox-group v-model="selectedRoleIds">
        <a-checkbox v-for="role in roleList" :key="role.id" :value="role.id">
          {{ role.name }}
        </a-checkbox>
      </a-checkbox-group>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getUserPage, addUser, updateUser, deleteUser, toggleUserStatus, resetPassword } from '@/api/system/user'
import { getRoleList } from '@/api/system/role'
import { hasPermission } from '@/utils/permission'

const hasPerm = hasPermission

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const roleList = ref([])

const queryForm = reactive({
  username: '',
  phone: '',
  status: null
})

const pagination = reactive({
  total: 0,
  pageNum: 1,
  pageSize: 10,
  current: 1,
  pageSizeOptions: ['10', '20', '50', '100']
})

const modalVisible = ref(false)
const roleModalVisible = ref(false)
const isEdit = ref(false)
const currentUserId = ref(null)
const selectedRoleIds = ref([])

const userForm = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  status: 1,
  roleIds: []
})

const formRef = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    }
    const res = await getUserPage(params)
    tableData.value = res.list
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

async function fetchRoles() {
  roleList.value = await getRoleList()
}

function handleSearch() {
  pagination.current = 1
  fetchData()
}

function handleReset() {
  queryForm.username = ''
  queryForm.phone = ''
  queryForm.status = null
  handleSearch()
}

function handlePageChange(page) {
  pagination.current = page
  fetchData()
}

function handleSizeChange(size) {
  pagination.pageSize = size
  pagination.current = 1
  fetchData()
}

function handleAdd() {
  isEdit.value = false
  currentUserId.value = null
  Object.assign(userForm, {
    username: '',
    password: '',
    nickname: '',
    phone: '',
    email: '',
    status: 1,
    roleIds: []
  })
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  currentUserId.value = record.id
  Object.assign(userForm, {
    username: record.username,
    nickname: record.nickname,
    phone: record.phone,
    email: record.email,
    status: record.status,
    roleIds: record.roleIds || []
  })
  modalVisible.value = true
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateUser({ id: currentUserId.value, ...userForm })
      Message.success('修改成功')
    } else {
      await addUser(userForm)
      Message.success('新增成功')
    }
    modalVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除用户「${record.username}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deleteUser(record.id)
      Message.success('删除成功')
      fetchData()
    }
  })
}

async function handleToggleStatus(record, value) {
  await toggleUserStatus(record.id, value ? 1 : 0)
  Message.success('操作成功')
  fetchData()
}

function handleResetPwd(record) {
  Modal.confirm({
    title: '重置密码',
    content: `确定要将用户「${record.username}」的密码重置为 123456 吗？`,
    onOk: async () => {
      await resetPassword(record.id, '123456')
      Message.success('重置成功')
    }
  })
}

function handleAssignRole(record) {
  currentUserId.value = record.id
  selectedRoleIds.value = [...(record.roleIds || [])]
  roleModalVisible.value = true
}

async function handleAssignRoleSubmit() {
  await updateUser({ id: currentUserId.value, roleIds: selectedRoleIds.value })
  Message.success('分配成功')
  roleModalVisible.value = false
  fetchData()
}

onMounted(() => {
  fetchData()
  fetchRoles()
})
</script>
