<script setup lang="ts">
import { ref, watch } from 'vue'
import { getItemDetail } from '../api/item'
import type { ItemDetail } from '../types'
import TagChip from './TagChip.vue'

const props = defineProps<{
  itemId: number | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const detail = ref<ItemDetail | null>(null)
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

watch(() => props.itemId, async (id) => {
  if (!id) { detail.value = null; return }
  loading.value = true
  try {
    const res = await getItemDetail(id)
    if (res.code === 200) detail.value = res.data
  } finally {
    loading.value = false
  }
}, { immediate: true })
</script>

<template>
  <Teleport to="body">
    <!-- 遮罩 -->
    <Transition name="fade">
      <div v-if="itemId" class="fixed inset-0 bg-black/60 z-50 backdrop-blur-sm"
           @click="emit('close')" />
    </Transition>

    <!-- 面板 -->
    <Transition name="slide">
      <div v-if="itemId"
           class="fixed top-0 right-0 h-full w-full max-w-lg bg-xuan-card border-l border-xuan-border
                  z-50 overflow-y-auto shadow-2xl">
        <!-- 加载中 -->
        <div v-if="loading" class="flex items-center justify-center h-64">
          <div class="text-xuan-muted animate-pulse">加载中…</div>
        </div>

        <!-- 详情内容 -->
        <div v-else-if="detail" class="p-6">
          <!-- 关闭按钮 -->
          <button @click="emit('close')"
                  class="absolute top-4 right-4 w-8 h-8 rounded-full bg-xuan-bg border border-xuan-border
                         flex items-center justify-center text-xuan-muted hover:text-xuan-text transition-colors">
            ✕
          </button>

          <!-- 分类信息 -->
          <div v-if="detail.categoryName" class="text-xs text-xuan-muted mb-4">
            {{ detail.categoryName }}
            <span v-if="detail.subCategoryName"> › {{ detail.subCategoryName }}</span>
          </div>

          <!-- 名称 + 徽章 -->
          <div class="flex items-center gap-3 mb-6">
            <h2 class="text-2xl font-bold text-xuan-text">{{ detail.name }}</h2>
            <span v-if="detail.badge"
                  class="badge-circle text-base"
                  :class="badgeColors[detail.badge] || badgeColors['杂']">
              {{ detail.badge }}
            </span>
          </div>

          <!-- 标签 -->
          <div v-if="detail.tags && detail.tags.length" class="mb-6">
            <div class="flex flex-wrap gap-2">
              <TagChip
                  v-for="tag in detail.tags"
                  :key="tag.id"
                  :name="tag.name"
                  :substitute="tag.substitute"
              />
            </div>
          </div>

          <!-- 金性 -->
          <div v-if="detail.goldenNature"
               class="bg-xuan-bg/50 rounded-lg p-4 mb-6 border border-xuan-border/50">
            <div class="text-xs text-xuan-gold/60 mb-1">金性</div>
            <div class="text-sm text-xuan-gold">{{ detail.goldenNature }}</div>
          </div>

          <!-- 替参关系 -->
          <div v-if="detail.substituteNotes && detail.substituteNotes.length" class="mb-6">
            <div class="text-xs text-xuan-muted mb-2">替参关系</div>
            <div class="space-y-1">
              <div v-for="(note, idx) in detail.substituteNotes" :key="idx"
                   class="text-sm text-xuan-muted">
                <span class="text-xuan-text/70">{{ note.source }}</span>
                <span class="text-xuan-gold/50 mx-2">→</span>
                <span class="text-xuan-text/70">{{ note.target }}</span>
              </div>
            </div>
          </div>

          <!-- 备注 -->
          <div v-if="detail.notes"
               class="bg-xuan-bg/50 rounded-lg p-4 border border-xuan-border/50">
            <div class="text-xs text-xuan-muted mb-1">备注</div>
            <div class="text-sm text-xuan-muted/80">{{ detail.notes }}</div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.slide-enter-active { transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-leave-active { transition: transform 0.2s ease-in; }
.slide-enter-from, .slide-leave-to { transform: translateX(100%); }
</style>