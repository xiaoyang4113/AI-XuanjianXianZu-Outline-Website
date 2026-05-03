<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchItems } from '../api/search'
import EmptyState from '../components/EmptyState.vue'
import TagChip from '../components/TagChip.vue'
import type { SearchResult } from '../types'

const route = useRoute()
const router = useRouter()
const keyword = ref((route.query.q as string) || '')
const results = ref<SearchResult[]>([])
const loading = ref(false)

const badgeColors: Record<string, string> = {
  '阳': 'bg-amber-800/60 text-amber-200 border border-amber-700/40',
  '阴': 'bg-purple-900/60 text-purple-200 border border-purple-700/40',
  '金': 'bg-yellow-900/50 text-yellow-200 border border-yellow-700/40',
  '木': 'bg-green-900/50 text-green-200 border border-green-700/40',
  '水': 'bg-blue-900/50 text-blue-200 border border-blue-700/40',
  '火': 'bg-red-900/50 text-red-200 border border-red-700/40',
  '土': 'bg-amber-900/40 text-amber-100 border border-amber-700/30',
  '炁': 'bg-violet-900/50 text-violet-200 border border-violet-700/40',
  '雷': 'bg-cyan-900/50 text-cyan-200 border border-cyan-700/40',
  '巫': 'bg-rose-950/50 text-rose-200 border border-rose-800/40',
  '祝': 'bg-pink-900/50 text-pink-200 border border-pink-700/40',
  '素': 'bg-gray-800/60 text-gray-300 border border-gray-600/40',
  '杂': 'bg-gray-900/60 text-gray-400 border border-gray-700/40',
}

async function doSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const res = await searchItems(1, keyword.value)
    if (res.code === 200) results.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (keyword.value) doSearch()
})

watch(() => route.query.q, (q) => {
  if (q) { keyword.value = q as string; doSearch() }
})
</script>

<template>
  <div class="pt-8">
    <button @click="router.back()" class="text-sm text-xuan-muted hover:text-xuan-gold mb-6 inline-block">
      ← 返回
    </button>

    <!-- 搜索框 -->
    <form @submit.prevent="doSearch" class="mb-8">
      <div class="relative max-w-xl">
        <input
            v-model="keyword"
            type="text"
            placeholder="搜索仙基、神通、金性…"
            class="w-full h-12 pl-12 pr-4 rounded-xl bg-xuan-card border border-xuan-border
                 text-base text-xuan-text placeholder-xuan-muted/50
                 focus:outline-none focus:border-xuan-gold/40 transition-colors"
        />
        <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-xuan-muted/50"
             fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
      </div>
    </form>

    <!-- 结果 -->
    <div v-if="loading" class="text-center text-xuan-muted py-10">搜索中…</div>

    <div v-else-if="results.length === 0 && keyword" class="py-10">
      <EmptyState />
      <p class="text-center text-sm text-xuan-muted mt-2">未找到「{{ keyword }}」相关结果</p>
    </div>

    <div v-else class="space-y-3">
      <div class="text-sm text-xuan-muted mb-4">
        共找到 <span class="text-xuan-gold">{{ results.length }}</span> 个结果
      </div>

      <div v-for="item in results" :key="item.id"
           class="bg-xuan-card border border-xuan-border rounded-xl p-5
                  hover:border-xuan-gold/15 transition-all">
        <div class="flex items-start justify-between mb-2">
          <div class="flex items-center gap-3">
            <h3 class="text-lg font-medium text-xuan-text">{{ item.name }}</h3>
            <span v-if="item.badge"
                  class="badge-circle"
                  :class="badgeColors[item.badge] || badgeColors['杂']">
              {{ item.badge }}
            </span>
          </div>
          <span class="text-xs text-xuan-muted bg-xuan-bg rounded-full px-2 py-0.5 border border-xuan-border">
            {{ item.type }}
          </span>
        </div>

        <div v-if="item.tags && item.tags.length" class="flex flex-wrap gap-2 mt-3">
          <TagChip
              v-for="tag in item.tags"
              :key="tag.id"
              :name="tag.name"
              :substitute="tag.substitute"
          />
        </div>

        <div v-if="item.goldenNature" class="text-xs text-xuan-gold/60 mt-2">
          金性：{{ item.goldenNature }}
        </div>
      </div>
    </div>
  </div>
</template>