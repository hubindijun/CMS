<template>
  <div class="p-4">
    <a-row :gutter="16">
      <!-- 左侧：字典类型 -->
      <a-col :span="8">
        <a-card>
          <template #title>字典类型</template>
          <template #extra>
            <a-button type="primary" size="small" @click="handleTypeAdd">
              <template #icon><icon-plus /></template>
              新增
            </a-button>
          </template>
          <a-table
            :data="typeList"
            :loading="typeLoading"
            :pagination="typePagination"
            @page-change="handleTypePageChange"
            @page-size-change="handleTypeSizeChange"
            @row-click="onTypeRowClick"
            :row-class="(record) => record.dictType === currentDictType ? 'row-active' : ''"
            size="small"
            :bordered="false"
          >
            <template #columns>
              <a-table-column title="字典名称" data-index="dictName" />
              <a-table-column title="字典类型" data-index="dictType" />
              <a-table-column title="状态" data-index="status" width="70">
                <template #cell="{ record }">
                  <a-tag :color="record.status === 1 ? 'green' : 'red'" size="small">
                    {{ record.status === 1 ? '启用' : '禁用' }}
                  </a-tag>
                </template>
              </a-table-column>
              <a-table-column title="操作" width="120">
                <template #cell="{ record }">
                  <a-space size="mini">
                    <a-button type="text" size="small" @click="handleTypeEdit(record)">编辑</a-button>
                    <a-button type="text" status="danger" size="small" @click="handleTypeDelete(record)">删除</a-button>
                  </a-space>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-card>
      </a-col>

      <!-- 右侧：字典数据 -->
      <a-col :span="16">
        <a-card>
          <template #title>
            字典数据
            <a-tag v-if="currentDictType" color="arcoblue" style="margin-left: 8px">{{ currentDictType }}</a-tag>
          </template>
          <template #extra>
            <a-button type="primary" size="small" :disabled="!currentDictType" @click="handleDataAdd">
              <template #icon><icon-plus /></template>
              新增
            </a-button>
          </template>
          <a-table
            :data="dataList"
            :loading="dataLoading"
            :pagination="dataPagination"
            @page-change="handleDataPageChange"
            @page-size-change="handleDataSizeChange"
            size="small"
            :bordered="false"
          >
            <template #columns>
              <a-table-column title="字典标签" data-index="dictLabel" width="120" />
              <a-table-column title="字典键值" data-index="dictValue" width="120" />
              <a-table-column title="排序" data-index="dictSort" width="80" />
              <a-table-column title="样式属性" data-index="cssClass" width="120" />
              <a-table-column title="表格回显样式" data-index="listClass" width="120" />
              <a-table-column title="状态" data-index="status" width="80">
                <template #cell="{ record }">
                  <a-tag :color="record.status === 1 ? 'green' : 'red'" size="small">
                    {{ record.status === 1 ? '启用' : '禁用' }}
                  </a-tag>
                </template>
              </a-table-column>
              <a-table-column title="备注" data-index="remark" />
              <a-table-column title="操作" width="120" fixed="right">
                <template #cell="{ record }">
                  <a-space size="mini">
                    <a-button type="text" size="small" @click="handleDataEdit(record)">编辑</a-button>
                    <a-button type="text" status="danger" size="small" @click="handleDataDelete(record)">删除</a-button>
                  </a-space>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <!-- 字典类型弹窗 -->
    <a-modal
      v-model:visible="typeModalVisible"
      :title="isTypeEdit ? '编辑字典类型' : '新增字典类型'"
      @ok="handleTypeSubmit"
      :confirm-loading="typeSubmitLoading"
      width="500px"
    >
      <a-form :model="typeForm" layout="vertical" ref="typeFormRef">
        <a-form-item label="字典名称" field="dictName" :rules="[{ required: true, message: '请输入字典名称' }]">
          <a-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
        </a-form-item>
        <a-form-item label="字典类型" field="dictType" :rules="[{ required: true, message: '请输入字典类型' }]">
          <a-input v-model="typeForm.dictType" placeholder="请输入字典类型" :disabled="isTypeEdit" />
        </a-form-item>
        <a-form-item label="状态" field="status">
          <a-radio-group v-model="typeForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="备注" field="remark">
          <a-textarea v-model="typeForm.remark" :auto-size="{ minRows: 2, maxRows: 4 }" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 字典数据弹窗 -->
    <a-modal
      v-model:visible="dataModalVisible"
      :title="isDataEdit ? '编辑字典数据' : '新增字典数据'"
      @ok="handleDataSubmit"
      :confirm-loading="dataSubmitLoading"
      width="600px"
    >
      <a-form :model="dataForm" layout="vertical" ref="dataFormRef">
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="字典标签" field="dictLabel" :rules="[{ required: true, message: '请输入字典标签' }]">
              <a-input v-model="dataForm.dictLabel" placeholder="请输入字典标签" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="字典键值" field="dictValue" :rules="[{ required: true, message: '请输入字典键值' }]">
              <a-input v-model="dataForm.dictValue" placeholder="请输入字典键值" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="排序" field="dictSort">
              <a-input-number v-model="dataForm.dictSort" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" field="status">
              <a-radio-group v-model="dataForm.status">
                <a-radio :value="1">启用</a-radio>
                <a-radio :value="0">禁用</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="样式属性" field="cssClass">
              <a-input v-model="dataForm.cssClass" placeholder="样式属性" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="表格回显样式" field="listClass">
              <a-select v-model="dataForm.listClass" allow-clear placeholder="请选择">
                <a-option value="default">default</a-option>
                <a-option value="primary">primary</a-option>
                <a-option value="success">success</a-option>
                <a-option value="warning">warning</a-option>
                <a-option value="danger">danger</a-option>
                <a-option value="info">info</a-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注" field="remark">
          <a-textarea v-model="dataForm.remark" :auto-size="{ minRows: 2, maxRows: 4 }" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import {
  getDictTypePage,
  addDictType,
  updateDictType,
  deleteDictType,
  getDictDataPage,
  addDictData,
  updateDictData,
  deleteDictData
} from '@/api/system/dict'

// 字典类型
const typeLoading = ref(false)
const typeSubmitLoading = ref(false)
const typeList = ref([])
const currentDictType = ref('')
const currentTypeId = ref(null)

const typePagination = reactive({
  total: 0,
  pageSize: 10,
  current: 1,
  pageSizeOptions: ['10', '20', '50']
})

const typeModalVisible = ref(false)
const isTypeEdit = ref(false)
const typeFormRef = ref(null)
const typeForm = reactive({
  id: null,
  dictName: '',
  dictType: '',
  status: 1,
  remark: ''
})

// 字典数据
const dataLoading = ref(false)
const dataSubmitLoading = ref(false)
const dataList = ref([])

const dataPagination = reactive({
  total: 0,
  pageSize: 10,
  current: 1,
  pageSizeOptions: ['10', '20', '50']
})

const dataModalVisible = ref(false)
const isDataEdit = ref(false)
const dataFormRef = ref(null)
const dataForm = reactive({
  id: null,
  dictSort: 0,
  dictLabel: '',
  dictValue: '',
  dictType: '',
  cssClass: '',
  listClass: '',
  status: 1,
  remark: ''
})

// ============ 字典类型 ============
async function fetchTypeList() {
  typeLoading.value = true
  try {
    const params = {
      pageNum: typePagination.current,
      pageSize: typePagination.pageSize
    }
    const res = await getDictTypePage(params)
    typeList.value = res.list
    typePagination.total = res.total
    // 默认选中第一个
    if (res.list.length > 0 && !currentDictType.value) {
      selectDictType(res.list[0])
    }
  } finally {
    typeLoading.value = false
  }
}

function selectDictType(record) {
  currentDictType.value = record.dictType
  currentTypeId.value = record.id
  dataPagination.current = 1
  fetchDataList()
}

function handleTypePageChange(page) {
  typePagination.current = page
  fetchTypeList()
}

function handleTypeSizeChange(size) {
  typePagination.pageSize = size
  typePagination.current = 1
  fetchTypeList()
}

function handleTypeAdd() {
  isTypeEdit.value = false
  typeForm.id = null
  typeForm.dictName = ''
  typeForm.dictType = ''
  typeForm.status = 1
  typeForm.remark = ''
  typeModalVisible.value = true
}

function handleTypeEdit(record) {
  isTypeEdit.value = true
  typeForm.id = record.id
  typeForm.dictName = record.dictName
  typeForm.dictType = record.dictType
  typeForm.status = record.status
  typeForm.remark = record.remark || ''
  typeModalVisible.value = true
}

async function handleTypeSubmit() {
  try {
    await typeFormRef.value.validate()
  } catch (_) {
    return
  }
  typeSubmitLoading.value = true
  try {
    if (isTypeEdit.value) {
      await updateDictType({ ...typeForm })
      Message.success('修改成功')
    } else {
      await addDictType({ ...typeForm })
      Message.success('新增成功')
    }
    typeModalVisible.value = false
    fetchTypeList()
  } finally {
    typeSubmitLoading.value = false
  }
}

function handleTypeDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除字典类型「${record.dictName}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deleteDictType(record.id)
      Message.success('删除成功')
      if (currentDictType.value === record.dictType) {
        currentDictType.value = ''
        dataList.value = []
      }
      fetchTypeList()
    }
  })
}

// ============ 字典数据 ============
async function fetchDataList() {
  if (!currentDictType.value) return
  dataLoading.value = true
  try {
    const params = {
      dictType: currentDictType.value,
      pageNum: dataPagination.current,
      pageSize: dataPagination.pageSize
    }
    const res = await getDictDataPage(params)
    dataList.value = res.list
    dataPagination.total = res.total
  } finally {
    dataLoading.value = false
  }
}

function handleDataPageChange(page) {
  dataPagination.current = page
  fetchDataList()
}

function handleDataSizeChange(size) {
  dataPagination.pageSize = size
  dataPagination.current = 1
  fetchDataList()
}

function handleDataAdd() {
  isDataEdit.value = false
  dataForm.id = null
  dataForm.dictSort = 0
  dataForm.dictLabel = ''
  dataForm.dictValue = ''
  dataForm.dictType = currentDictType.value
  dataForm.cssClass = ''
  dataForm.listClass = ''
  dataForm.status = 1
  dataForm.remark = ''
  dataModalVisible.value = true
}

function handleDataEdit(record) {
  isDataEdit.value = true
  dataForm.id = record.id
  dataForm.dictSort = record.dictSort
  dataForm.dictLabel = record.dictLabel
  dataForm.dictValue = record.dictValue
  dataForm.dictType = record.dictType
  dataForm.cssClass = record.cssClass || ''
  dataForm.listClass = record.listClass || ''
  dataForm.status = record.status
  dataForm.remark = record.remark || ''
  dataModalVisible.value = true
}

async function handleDataSubmit() {
  try {
    await dataFormRef.value.validate()
  } catch (_) {
    return
  }
  dataSubmitLoading.value = true
  try {
    if (isDataEdit.value) {
      await updateDictData({ ...dataForm })
      Message.success('修改成功')
    } else {
      await addDictData({ ...dataForm })
      Message.success('新增成功')
    }
    dataModalVisible.value = false
    fetchDataList()
  } finally {
    dataSubmitLoading.value = false
  }
}

function handleDataDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除字典数据「${record.dictLabel}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deleteDictData(record.id)
      Message.success('删除成功')
      fetchDataList()
    }
  })
}

// 监听类型列表行点击
function onTypeRowClick(record) {
  selectDictType(record)
}

onMounted(() => {
  fetchTypeList()
})
</script>

<style scoped>
:deep(.arco-table-body .row-active > td) {
  background-color: var(--color-primary-light-1, #e8f3ff);
}
</style>
