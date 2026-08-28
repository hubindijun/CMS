<template>
  <div class="p-4">
    <a-card>
      <!-- 查询条件 -->
      <a-form :model="queryForm" layout="inline" @submit="handleSearch">
        <a-form-item label="角色名称" field="name">
          <a-input v-model="queryForm.name" placeholder="请输入角色名称" allow-clear style="width: 200px" />
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
          <a-button type="primary" @click="handleAdd">
            <template #icon><icon-plus /></template>
            新增角色
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
        <a-table-column title="角色名称" data-index="name" />
        <a-table-column title="角色编码" data-index="code" />
        <a-table-column title="描述" data-index="description" />
        <a-table-column title="状态" data-index="status" width="100">
          <template #cell="{ record }">
            <a-switch
              :checked="record.status === 1"
              :disabled="record.code === 'admin'"
              @change="(v) => handleToggleStatus(record, v)"
            />
          </template>
        </a-table-column>
        <a-table-column title="创建时间" data-index="createTime" width="180" />
        <a-table-column title="操作" width="260" fixed="right">
          <template #cell="{ record }">
            <a-space size="small">
              <a-button type="text" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-button type="text" size="small" @click="handleAssignPermission(record)">分配权限</a-button>
              <a-button
                type="text"
                status="danger"
                size="small"
                :disabled="record.code === 'admin'"
                @click="handleDelete(record)"
              >删除</a-button>
            </a-space>
          </template>
        </a-table-column>
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
      width="500px"
    >
      <a-form :model="roleForm" layout="vertical" ref="formRef">
        <a-form-item label="角色名称" field="name" :rules="[{ required: true, message: '请输入角色名称' }]">
          <a-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="角色编码" field="code" :rules="[{ required: true, message: '请输入角色编码' }]">
          <a-input v-model="roleForm.code" :disabled="isEdit" placeholder="请输入角色编码" />
        </a-form-item>
        <a-form-item label="描述" field="description">
          <a-textarea v-model="roleForm.description" :auto-size="{ minRows: 3 }" placeholder="请输入描述" />
        </a-form-item>
        <a-form-item label="状态" field="status">
          <a-radio-group v-model="roleForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配权限弹窗 -->
    <a-modal
      v-model:visible="permModalVisible"
      title="分配权限"
      @ok="handleAssignPermSubmit"
      :confirm-loading="permSubmitLoading"
      width="500px"
    >
      <a-tree
        v-model:checked-keys="checkedPermKeys"
        :data="permTreeData"
        checkable
        :default-expand-all="false"
        :field-names="{ key: 'id', title: 'name', children: 'children' }"
      >
        <template #title="{ record }">
          <span class="flex items-center gap-2">
            <span>{{ record.name }}</span>
            <a-tag v-if="record.type === 1" color="blue" size="small">目录</a-tag>
            <a-tag v-else-if="record.type === 2" color="green" size="small">菜单</a-tag>
            <a-tag v-else-if="record.type === 3" color="orange" size="small">按钮</a-tag>
          </span>
        </template>
      </a-tree>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import {
  getRolePage,
  addRole,
  updateRole,
  deleteRole,
  toggleRoleStatus,
  getRolePermissions,
  assignRolePermissions
} from '@/api/system/role'
import { getPermissionTree } from '@/api/system/permission'

const loading = ref(false)
const submitLoading = ref(false)
const permSubmitLoading = ref(false)
const tableData = ref([])
const permTreeData = ref([])

const queryForm = reactive({
  name: '',
  status: null
})

const pagination = reactive({
  total: 0,
  pageSize: 10,
  current: 1,
  pageSizeOptions: ['10', '20', '50', '100']
})

const modalVisible = ref(false)
const permModalVisible = ref(false)
const isEdit = ref(false)
const currentRoleId = ref(null)
const checkedPermKeys = ref([])

const roleForm = reactive({
  name: '',
  code: '',
  description: '',
  status: 1
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
    const res = await getRolePage(params)
    tableData.value = res.list
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

async function fetchPermTree() {
  permTreeData.value = await getPermissionTree()
}

function handleSearch() {
  pagination.current = 1
  fetchData()
}

function handleReset() {
  queryForm.name = ''
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
  currentRoleId.value = null
  Object.assign(roleForm, {
    name: '',
    code: '',
    description: '',
    status: 1
  })
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  currentRoleId.value = record.id
  Object.assign(roleForm, {
    name: record.name,
    code: record.code,
    description: record.description,
    status: record.status
  })
  modalVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch (_) {
    return
  }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateRole({ id: currentRoleId.value, ...roleForm })
      Message.success('修改成功')
    } else {
      await addRole(roleForm)
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
    content: `确定要删除角色「${record.name}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deleteRole(record.id)
      Message.success('删除成功')
      fetchData()
    }
  })
}

async function handleToggleStatus(record, value) {
  const oldValue = record.status
  record.status = value ? 1 : 0
  try {
    await toggleRoleStatus(record.id, value ? 1 : 0)
    Message.success('操作成功')
  } catch (e) {
    record.status = oldValue
    throw e
  }
}

async function handleAssignPermission(record) {
  currentRoleId.value = record.id
  checkedPermKeys.value = []
  permModalVisible.value = true
  try {
    const permIds = await getRolePermissions(record.id)
    checkedPermKeys.value = permIds || []
  } catch (e) {
    // ignore
  }
}

async function handleAssignPermSubmit() {
  permSubmitLoading.value = true
  try {
    await assignRolePermissions(currentRoleId.value, checkedPermKeys.value)
    Message.success('分配成功')
    permModalVisible.value = false
  } finally {
    permSubmitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
  fetchPermTree()
})
</script>

<style scoped>
.flex {
  display: flex;
}
.items-center {
  align-items: center;
}
.gap-2 {
  gap: 8px;
}
</style>
