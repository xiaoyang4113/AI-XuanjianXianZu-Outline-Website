<script setup lang="ts">
import { ref, watch } from 'vue';
import { getItemDetail } from '../api/item';
import type { ItemDetail } from '../types';

const props = defineProps<{
  items: { id: number; name: string; badge: string }[]
}>()

const selectedId = ref<number | null>(null)
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

watch(selectedId, async (id) => {
  if (!id) { detail.value = null; return }
  loading.value = true
  try {
    const res = await getItemDetail(id)
    if (res.code === 200) detail.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <!-- 选择下拉 -->
  <div class="mb-8">
    <label class="text-xs text-xuan-muted/60 uppercase tracking-wider block mb-2">选择果位/道统</label>
    <select v-model="selectedId" class="w-full sm:w-80 h-10 px-4 rounded-xl bg-xuan-card border border-xuan-border 
                   text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/40 transition-colors">
      <option :value="null" disabled>请选择一个果位…</option>
      <option v-for="item in items" :key="item.id" :value="item.id">
        {{ item.name }}
      </option>
    </select>
  </div>

  <!-- 加载中 -->
  <div v-if="loading" class="text-center text-xuan-muted py-10">加载中…</div>

  <!-- 详情展示 -->
  <Transition name="fade">
    <div v-if="detail && !loading" class="bg-xuan-card border border-xuan-border rounded-2xl p-6 sm:p-8">
      <!-- 头部 -->
      <div class="flex items-center gap-3 mb-6">
        <h2 class="text-2xl font-bold text-xuan-text">
          {{ detail.name }}
          <span v-if="detail.isEmptyCertification" class="inline-block text-[10px] px-2 py-0.5 ml-2 rounded-full 
               bg-xuan-gold/15 text-xuan-gold border border-xuan-gold/30 
               align-middle">
            空证
          </span>
        </h2>

        <span v-if="detail.badge" class="badge-circle text-base" :class="badgeColors[detail.badge] || badgeColors['杂']">
          {{ detail.badge }}
        </span>
        <span v-if="detail.categoryName"
          class="text-xs text-xuan-muted bg-xuan-bg px-2 py-1 rounded-full border border-xuan-border">
          {{ detail.categoryName }}
        </span>
      </div>

      <!-- 神通/仙基标签 -->
      <div class="mb-6">
        <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-3">仙基 / 神通</h4>
        <div class="flex flex-wrap gap-2">
          <div v-for="tag in detail.tags" :key="tag.id" class="bg-xuan-bg border border-xuan-border rounded-lg px-4 py-2.5 
                      hover:border-xuan-gold/20 transition-all duration-200">
            <div class="flex items-center gap-1.5">
              <span v-if="tag.substitute" class="substitute-badge">替</span>
              <span class="text-sm text-xuan-text/90">{{ tag.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 金性 -->
      <div v-if="detail.goldenNature" class="bg-xuan-bg/50 rounded-xl p-4 mb-5 border border-xuan-gold/10">
        <h4 class="text-xs text-xuan-gold/50 mb-1">金性</h4>
        <p class="text-sm text-xuan-gold/80">{{ detail.goldenNature }}</p>
      </div>

      <!-- 替参关系 -->
      <div v-if="detail.substituteNotes && detail.substituteNotes.length" class="mb-5">
        <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-2">替参关系</h4>
        <div class="space-y-1">
          <div v-for="(note, idx) in detail.substituteNotes" :key="idx"
            class="text-sm text-xuan-muted flex items-center gap-2">
            <span class="text-xuan-text/70">{{ note.source }}</span>
            <span class="text-xuan-gold/50">→</span>
            <span class="text-xuan-text/70">{{ note.target }}</span>
          </div>
        </div>
      </div>

      <!-- 备注 -->
      <div v-if="detail.notes" class="bg-xuan-bg/50 rounded-xl p-4 border border-xuan-border/30">
        <h4 class="text-xs text-xuan-muted/60 mb-1">备注</h4>
        <p class="text-sm text-xuan-muted/80">{{ detail.notes }}</p>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.fade-enter-active {
  transition: all 0.3s ease;
}

.fade-leave-active {
  transition: all 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>