<template>
  <div class="p-4">
    <a-card>
      <!-- 操作栏 -->
      <div class="flex justify-between items-center mb-4">
        <a-input
          v-model="searchKeyword"
          placeholder="请输入权限名称搜索"
          allow-clear
          style="width: 260px"
          @search="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix><icon-search /></template>
        </a-input>
        <a-button v-if="hasPerm('system:permission:add')" type="primary" @click="handleAdd">
          <template #icon><icon-plus /></template>
          新增权限
        </a-button>
      </div>

      <!-- 树形表格 -->
      <a-table
        :data="tableData"
        :loading="loading"
        :pagination="false"
        row-key="id"
        :bordered="false"
        :tree-props="{ children: 'children' }"
        :default-expand-all="true"
      >
        <template #columns>
          <a-table-column title="权限名称" data-index="name" tree-cell />
          <a-table-column title="类型" data-index="type" width="100">
            <template #cell="{ record }">
              <a-tag v-if="record.type === 1" color="blue">目录</a-tag>
              <a-tag v-else-if="record.type === 2" color="green">菜单</a-tag>
              <a-tag v-else-if="record.type === 3" color="orange">按钮</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="路由地址" data-index="path" />
          <a-table-column title="组件路径" data-index="component" />
          <a-table-column title="权限标识" data-index="perms" />
          <a-table-column title="排序" data-index="sort" width="80" />
          <a-table-column title="状态" data-index="status" width="100">
            <template #cell="{ record }">
              <a-switch
                :checked="record.status === 1"
                size="small"
                @change="(v) => handleToggleStatus(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column title="操作" width="200" fixed="right">
            <template #cell="{ record }">
              <a-space size="small">
                <a-button v-if="hasPerm('system:permission:add')" type="text" size="small" @click="handleAddChild(record)">添加子级</a-button>
                <a-button v-if="hasPerm('system:permission:edit')" type="text" size="small" @click="handleEdit(record)">编辑</a-button>
                <a-button
                  v-if="hasPerm('system:permission:delete')"
                  type="text"
                  status="danger"
                  size="small"
                  @click="handleDelete(record)"
                >删除</a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑权限' : (isAddChild ? '新增子权限' : '新增权限')"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
      width="600px"
    >
      <a-form :model="permForm" layout="vertical" ref="formRef">
        <a-form-item label="上级权限" field="parentId">
          <a-tree-select
            v-model="permForm.parentId"
            :data="permTreeData"
            :field-names="{ key: 'id', title: 'name', children: 'children' }"
            placeholder="请选择上级权限"
            allow-clear
            :disabled="isEdit"
          />
        </a-form-item>
        <a-form-item label="权限类型" field="type" :rules="[{ required: true, message: '请选择权限类型' }]">
          <a-radio-group v-model="permForm.type">
            <a-radio :value="1">目录</a-radio>
            <a-radio :value="2">菜单</a-radio>
            <a-radio :value="3">按钮</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="权限名称" field="name" :rules="[{ required: true, message: '请输入权限名称' }]">
          <a-input v-model="permForm.name" placeholder="请输入权限名称" />
        </a-form-item>
        <a-form-item v-if="permForm.type !== 3" label="路由地址" field="path">
          <a-input v-model="permForm.path" placeholder="请输入路由地址" />
        </a-form-item>
        <a-form-item v-if="permForm.type === 2" label="组件路径" field="component">
          <a-input v-model="permForm.component" placeholder="请输入组件路径" />
        </a-form-item>
        <a-form-item v-if="permForm.type !== 3" label="图标" field="icon">
          <a-input v-model="permForm.icon" placeholder="如：icon-settings" />
        </a-form-item>
        <a-form-item v-if="permForm.type === 3" label="权限标识" field="perms">
          <a-input v-model="permForm.perms" placeholder="如：system:user:add" />
        </a-form-item>
        <a-form-item label="排序" field="sort">
          <a-input-number v-model="permForm.sort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态" field="status">
          <a-radio-group v-model="permForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import {
  getPermissionTree,
  addPermission,
  updatePermission,
  deletePermission
} from '@/api/system/permission'
import { hasPermission } from '@/utils/permission'

const hasPerm = hasPermission

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const permTreeData = ref([])
const searchKeyword = ref('')

const modalVisible = ref(false)
const isEdit = ref(false)
const isAddChild = ref(false)
const currentPermId = ref(null)

const permForm = reactive({
  parentId: null,
  type: 1,
  name: '',
  path: '',
  component: '',
  icon: '',
  perms: '',
  sort: 0,
  status: 1
})

const formRef = ref(null)

function filterTree(list, keyword) {
  if (!keyword) return list
  const result = []
  for (const item of list) {
    if (item.name.includes(keyword)) {
      result.push(item)
      continue
    }
    if (item.children && item.children.length) {
      const filteredChildren = filterTree(item.children, keyword)
      if (filteredChildren.length) {
        result.push({ ...item, children: filteredChildren })
      }
    }
  }
  return result
}

async function fetchData() {
  loading.value = true
  try {
    const tree = await getPermissionTree()
    tableData.value = tree
    permTreeData.value = tree
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  // 前端过滤树形数据
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    tableData.value = permTreeData.value
    return
  }
  tableData.value = filterTree(permTreeData.value, keyword)
}

function resetForm() {
  Object.assign(permForm, {
    parentId: null,
    type: 1,
    name: '',
    path: '',
    component: '',
    icon: '',
    perms: '',
    sort: 0,
    status: 1
  })
}

function handleAdd() {
  isEdit.value = false
  isAddChild.value = false
  currentPermId.value = null
  resetForm()
  modalVisible.value = true
}

function handleAddChild(record) {
  isEdit.value = false
  isAddChild.value = true
  currentPermId.value = null
  resetForm()
  permForm.parentId = record.id
  // 子级类型：目录下可以是菜单或目录，菜单下只能是按钮
  if (record.type === 2) {
    permForm.type = 3
  } else if (record.type === 1) {
    permForm.type = 2
  }
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  isAddChild.value = false
  currentPermId.value = record.id
  Object.assign(permForm, {
    parentId: record.parentId || null,
    type: record.type,
    name: record.name,
    path: record.path || '',
    component: record.component || '',
    icon: record.icon || '',
    perms: record.perms || '',
    sort: record.sort || 0,
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
      await updatePermission({ id: currentPermId.value, ...permForm })
      Message.success('修改成功')
    } else {
      await addPermission(permForm)
      Message.success('新增成功')
    }
    modalVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(record) {
  if (record.children && record.children.length > 0) {
    Message.warning('该权限下存在子级权限，无法删除')
    return
  }
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除权限「${record.name}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deletePermission(record.id)
      Message.success('删除成功')
      fetchData()
    }
  })
}

async function handleToggleStatus(record, value) {
  const oldValue = record.status
  record.status = value ? 1 : 0
  try {
    await updatePermission({ id: record.id, status: value ? 1 : 0 })
    Message.success('操作成功')
  } catch (e) {
    record.status = oldValue
    throw e
  }
}

onMounted(() => {
  fetchData()
})
</script>
