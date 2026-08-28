<template>
  <div class="p-4">
    <a-card>
      <!-- 查询条件 -->
      <a-form :model="queryForm" layout="inline" @submit="handleSearch">
        <a-form-item label="用户名" field="username">
          <a-input v-model="queryForm.username" placeholder="请输入用户名" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="状态" field="status">
          <a-select v-model="queryForm.status" placeholder="全部" allow-clear style="width: 120px">
            <a-option :value="1">成功</a-option>
            <a-option :value="0">失败</a-option>
          </a-select>
        </a-form-item>
        <a-form-item label="登录时间" field="loginTime">
          <a-range-picker v-model="queryForm.loginTime" style="width: 280px" />
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
          <a-button status="danger" @click="handleClear">
            <template #icon><icon-delete /></template>
            清空日志
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
          <a-table-column title="用户名" data-index="username" width="120" />
          <a-table-column title="IP地址" data-index="ip" width="140" />
          <a-table-column title="地点" data-index="location" width="160" />
          <a-table-column title="浏览器" data-index="browser" width="140" />
          <a-table-column title="操作系统" data-index="os" width="140" />
          <a-table-column title="状态" data-index="status" width="100">
            <template #cell="{ record }">
              <a-tag :color="record.status === 1 ? 'green' : 'red'">
                {{ record.status === 1 ? '成功' : '失败' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="提示消息" data-index="message" />
          <a-table-column title="登录时间" data-index="loginTime" width="180" />
          <a-table-column title="操作" width="100" fixed="right">
            <template #cell="{ record }">
              <a-space size="small">
                <a-button type="text" status="danger" size="small" @click="handleDelete(record)">删除</a-button>
              </a-space>
            </template>
          </a-table-column>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getLoginLogPage, deleteLoginLog, clearLoginLog } from '@/api/system/loginLog'

const loading = ref(false)
const tableData = ref([])

const queryForm = reactive({
  username: '',
  status: null,
  loginTime: []
})

const pagination = reactive({
  total: 0,
  pageSize: 10,
  current: 1,
  pageSizeOptions: ['10', '20', '50', '100']
})

function formatDate(d) {
  if (!d) return ''
  const date = new Date(d)
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      username: queryForm.username,
      status: queryForm.status,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    }
    if (queryForm.loginTime && queryForm.loginTime.length === 2) {
      params.startTime = formatDate(queryForm.loginTime[0])
      params.endTime = formatDate(queryForm.loginTime[1])
    }
    const res = await getLoginLogPage(params)
    tableData.value = res.list
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  fetchData()
}

function handleReset() {
  queryForm.username = ''
  queryForm.status = null
  queryForm.loginTime = []
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

function handleDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这条登录日志吗？',
    status: 'warning',
    onOk: async () => {
      await deleteLoginLog(record.id)
      Message.success('删除成功')
      fetchData()
    }
  })
}

function handleClear() {
  Modal.confirm({
    title: '确认清空',
    content: '确定要清空所有登录日志吗？此操作不可恢复！',
    status: 'warning',
    onOk: async () => {
      await clearLoginLog()
      Message.success('清空成功')
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>
