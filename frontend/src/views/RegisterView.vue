<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()
const username = ref('')
const password = ref('')
const email = ref('')
const error = ref('')

async function doRegister() {
  error.value = ''
  const res = await register(username.value, password.value, email.value)
  if (res.code === 200) {
    router.push('/login')
  } else {
    error.value = res.message
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <div class="bg-xuan-card border border-xuan-border rounded-2xl p-8 w-full max-w-sm">
      <h2 class="text-xl font-bold text-xuan-gold text-center mb-6">注册</h2>
      <form @submit.prevent="doRegister">
        <input v-model="username" placeholder="用户名" class="w-full h-10 px-4 rounded-xl bg-xuan-bg border border-xuan-border text-sm text-xuan-text mb-3 focus:outline-none focus:border-xuan-gold/40" />
        <input v-model="email" placeholder="邮箱（可选）" class="w-full h-10 px-4 rounded-xl bg-xuan-bg border border-xuan-border text-sm text-xuan-text mb-3 focus:outline-none focus:border-xuan-gold/40" />
        <input v-model="password" type="password" placeholder="密码" class="w-full h-10 px-4 rounded-xl bg-xuan-bg border border-xuan-border text-sm text-xuan-text mb-4 focus:outline-none focus:border-xuan-gold/40" />
        <div v-if="error" class="text-xs text-red-400 mb-3">{{ error }}</div>
        <button class="w-full h-10 rounded-xl bg-xuan-gold/20 text-xuan-gold text-sm font-medium hover:bg-xuan-gold/30 transition-all">注册</button>
      </form>
      <p class="text-xs text-xuan-muted text-center mt-4">已有账号？ <router-link to="/login" class="text-xuan-gold">登录</router-link></p>
    </div>
  </div>
</template>