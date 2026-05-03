<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const searchQuery = ref('')
const user = ref<any>(null)

const navLinks = [
  { path: '/project/1/browse',     label: '道统' },
  { path: '/project/1/realms',    label: '修炼' },
  { path: '/project/1/characters',label: '人物' },
  { path: '/project/1/factions',  label: '势力' },
  { path: '/project/1/techniques',label: '功法' },
  { path: '/project/1/graph',     label: '图谱' },
]

onMounted(() => {
  const stored = localStorage.getItem('user')
  if (stored) { try { user.value = JSON.parse(stored) } catch {} }
})

function doSearch() {
  if (searchQuery.value.trim()) {
    router.push({ name: 'search', params: { id: 1 }, query: { q: searchQuery.value } })
  }
}
function logout() {
  localStorage.removeItem('user')
  user.value = null
  router.push('/')
}
function isActive(path: string): boolean {
  return route.path === path || route.path.startsWith(path + '/')
}
</script>

<template>
  <header class="sticky top-0 z-50 border-b border-white/[0.03] glass-card !rounded-none !border-t-0 !border-l-0 !border-r-0">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between gap-3">
      
      <!-- Logo -->
      <router-link to="/" class="flex items-center gap-2.5 group flex-shrink-0">
        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-xuan-gold/35 to-xuan-gold/5 
                    flex items-center justify-center text-xuan-gold text-lg font-bold
                    border border-xuan-gold/20 group-hover:border-xuan-gold/50 
                    group-hover:shadow-lg group-hover:shadow-xuan-gold/10
                    group-hover:scale-105 transition-all duration-500">
          ☯
        </div>
        <span class="text-lg font-semibold tracking-wide hidden lg:inline">
          <span class="text-xuan-gold">玄鉴</span>
          <span class="text-xuan-muted ml-1.5 text-sm font-light">道统图鉴</span>
        </span>
      </router-link>

      <!-- 导航 -->
      <nav class="hidden md:flex items-center gap-0.5">
        <router-link v-for="link in navLinks" :key="link.path" :to="link.path"
                     class="relative px-3.5 py-2 rounded-lg text-sm transition-all duration-300"
                     :class="isActive(link.path)
                       ? 'text-xuan-gold bg-xuan-gold/8 font-medium'
                       : 'text-xuan-muted hover:text-xuan-text hover:bg-white/[0.03]'">
          {{ link.label }}
          <div v-if="isActive(link.path)" class="nav-indicator"></div>
        </router-link>
      </nav>

      <!-- 右侧 -->
      <div class="flex items-center gap-3">
        <form @submit.prevent="doSearch" class="relative hidden sm:block">
          <input v-model="searchQuery" type="text" placeholder="搜索…"
                 class="w-32 lg:w-44 h-9 pl-9 pr-4 rounded-xl bg-white/[0.04] border border-white/[0.06] 
                        text-sm text-xuan-text placeholder:text-xuan-muted/25
                        focus:outline-none focus:border-xuan-gold/25 focus:bg-white/[0.06] 
                        transition-all duration-300" />
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-xuan-muted/25" 
               fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
        </form>

        <template v-if="user">
          
          <div class="flex items-center gap-2">
            <span class="text-sm text-xuan-gold hidden sm:inline">{{ user.username }}</span>
            <router-link :to="{ name: 'admin', params: { id: 1 } }"
             class="text-xs px-3 py-1.5 rounded-lg bg-xuan-gold/8 text-xuan-gold 
                    border border-xuan-gold/20 hover:bg-xuan-gold/15 hover:scale-105 btn-candle
                    transition-all duration-300">
            管理
          </router-link>
            <button @click="logout" 
                    class="text-xs px-3 py-1.5 rounded-lg text-xuan-muted border border-white/[0.06] 
                           hover:text-rose-400 hover:border-rose-400/25 transition-all duration-300">
              退出
            </button>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" 
             class="text-xs px-3.5 py-1.5 rounded-lg text-xuan-muted border border-white/[0.06] 
                    hover:text-xuan-gold hover:border-xuan-gold/25 transition-all duration-300">
            登录
          </router-link>
          <router-link to="/register" 
             class="text-xs px-3.5 py-1.5 rounded-lg bg-xuan-gold/10 text-xuan-gold 
                    border border-xuan-gold/25 hover:bg-xuan-gold/20 hover:scale-105 btn-candle
                    transition-all duration-300 hidden sm:inline">
            注册
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>