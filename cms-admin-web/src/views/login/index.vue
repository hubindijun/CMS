<template>
  <div class="login-container">
    <div class="login-left">
      <div class="login-logo">
        <icon-apps class="logo-icon" />
        <span class="logo-text">CMS 管理后台</span>
      </div>
      <p class="login-slogan">高效、安全、易用的内容管理系统</p>
    </div>
    <div class="login-right">
      <a-card class="login-card">
        <h2 class="login-title">欢迎登录</h2>
        <p class="login-subtitle">请输入您的账号信息</p>
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          layout="vertical"
          @submit="handleLogin"
        >
          <a-form-item field="username" label="用户名">
            <a-input
              v-model="formData.username"
              placeholder="请输入用户名"
              :size="'large'"
            >
              <template #prefix>
                <icon-user />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item field="password" label="密码">
            <a-input-password
              v-model="formData.password"
              placeholder="请输入密码"
              :size="'large'"
            >
              <template #prefix>
                <icon-lock />
              </template>
            </a-input-password>
          </a-form-item>
          <a-form-item field="captcha" label="验证码">
            <div class="captcha-row">
              <a-input
                v-model="formData.captcha"
                placeholder="请输入验证码"
                :size="'large'"
                class="captcha-input"
              >
                <template #prefix>
                  <icon-shield />
                </template>
              </a-input>
              <div class="captcha-img" @click="refreshCaptcha" title="点击刷新">
                <img v-if="captchaImg" :src="captchaImg" alt="验证码" />
                <span v-else>加载中...</span>
              </div>
            </div>
          </a-form-item>
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              :loading="loading"
              long
              :size="'large'"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </a-button>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const captchaImg = ref('')
const captchaKey = ref('')

const formData = reactive({
  username: '',
  password: '',
  captcha: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名' }
  ],
  password: [
    { required: true, message: '请输入密码' }
  ],
  captcha: [
    { required: true, message: '请输入验证码' }
  ]
}

async function refreshCaptcha() {
  try {
    const data = await getCaptcha()
    captchaImg.value = data.img
    captchaKey.value = data.key
  } catch (e) {
    // error handled by interceptor
  }
}

async function handleLogin() {
  try {
    await formRef.value.validate()
  } catch (_) {
    return
  }
  loading.value = true
  try {
    await userStore.login({
      username: formData.username,
      password: formData.password,
      captcha: formData.captcha,
      captchaKey: captchaKey.value
    })
    Message.success('登录成功')
    const redirect = route.query.redirect
    const safeRedirect = typeof redirect === 'string' && redirect.startsWith('/') && !redirect.startsWith('//')
      ? redirect
      : '/dashboard'
    router.push(safeRedirect)
  } catch (e) {
    // error handled by interceptor, refresh captcha
    refreshCaptcha()
    formData.captcha = ''
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  width: 100%;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #165dff 0%, #4080ff 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
}

.login-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 48px;
}

.logo-text {
  font-size: 32px;
  font-weight: 600;
}

.login-slogan {
  font-size: 18px;
  opacity: 0.9;
}

.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f8fa;
}

.login-card {
  width: 400px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #1d2129;
}

.login-subtitle {
  font-size: 14px;
  color: #86909c;
  margin: 0 0 24px 0;
}

.captcha-row {
  display: flex;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  width: 120px;
  height: 40px;
  border: 1px solid #e5e6eb;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  flex-shrink: 0;
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-img:hover {
  border-color: #165dff;
}
</style>
