<template>
  <div class="p-4">
    <a-card>
      <a-tabs v-model:active-key="activeTab" type="card">
        <a-tab-pane key="info" title="基本信息">
          <a-form :model="profileForm" layout="vertical" ref="formRef" style="max-width: 500px">
            <a-form-item label="用户名">
              <a-input v-model="profileForm.username" disabled />
            </a-form-item>
            <a-form-item label="昵称" field="nickname">
              <a-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </a-form-item>
            <a-form-item label="手机号" field="phone">
              <a-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </a-form-item>
            <a-form-item label="邮箱" field="email">
              <a-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" :loading="submitLoading" @click="handleUpdateProfile">
                  保存修改
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-tab-pane>
        <a-tab-pane key="password" title="修改密码">
          <a-form :model="passwordForm" layout="vertical" ref="passwordFormRef" style="max-width: 500px">
            <a-form-item label="旧密码" field="oldPassword" :rules="[{ required: true, message: '请输入旧密码' }]">
              <a-input-password v-model="passwordForm.oldPassword" placeholder="请输入旧密码" />
            </a-form-item>
            <a-form-item label="新密码" field="newPassword" :rules="[{ required: true, message: '请输入新密码' }]">
              <a-input-password v-model="passwordForm.newPassword" placeholder="请输入新密码" />
            </a-form-item>
            <a-form-item label="确认密码" field="confirmPassword" :rules="confirmPasswordRules">
              <a-input-password v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
                  确认修改
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { getProfile, updateProfile, changePassword } from '@/api/system/profile'

const route = useRoute()
const activeTab = ref('info')

const profileForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const formRef = ref(null)
const passwordFormRef = ref(null)
const submitLoading = ref(false)
const passwordLoading = ref(false)

const confirmPasswordRules = computed(() => [
  { required: true, message: '请再次输入新密码' },
  {
    validator: (value, callback) => {
      if (value !== passwordForm.newPassword) {
        callback('两次输入的密码不一致')
      } else {
        callback()
      }
    }
  }
])

async function fetchProfile() {
  const data = await getProfile()
  profileForm.username = data.username
  profileForm.nickname = data.nickname || ''
  profileForm.phone = data.phone || ''
  profileForm.email = data.email || ''
}

async function handleUpdateProfile() {
  submitLoading.value = true
  try {
    await updateProfile({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email
    })
    Message.success('修改成功')
  } finally {
    submitLoading.value = false
  }
}

async function handleChangePassword() {
  try {
    await passwordFormRef.value.validate()
  } catch (_) {
    return
  }
  passwordLoading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    Message.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } finally {
    passwordLoading.value = false
  }
}

onMounted(() => {
  fetchProfile()
  if (route.query.tab === 'password') {
    activeTab.value = 'password'
  }
})
</script>
