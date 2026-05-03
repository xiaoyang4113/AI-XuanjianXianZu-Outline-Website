<script setup lang="ts">
import type { ItemBrief } from '../types';
import TagChip from './TagChip.vue';

const props = defineProps<{ item: ItemBrief; index?: number }>()
const emit = defineEmits<{ (e: 'select', id: number): void }>()

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
</script>

<template>
  <div class="ink-card relative rounded-xl p-5 cursor-pointer
              hover:-translate-y-1 hover:shadow-xl hover:shadow-black/30
              transition-all duration-500 group card-enter"
       :style="{ animationDelay: (index || 0) * 60 + 'ms' }"
       @click="emit('select', item.id)">
    
    <!-- Hover 墨韵光晕 -->
    <div class="absolute inset-0 rounded-xl bg-gradient-to-br from-xuan-gold/3 to-transparent 
                opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none"></div>

    <!-- 头部 -->
    <div class="flex items-start justify-between mb-3 relative z-10">
      <div class="flex-1 min-w-0">
        <h3 class="text-lg font-medium text-xuan-text group-hover:text-xuan-gold transition-colors duration-300">
          {{ item.name }}
          <span v-if="item.isEmptyCertification" 
                class="inline-block text-[9px] px-1.5 py-0.5 ml-1.5 rounded-full 
                       bg-amber-400/15 text-amber-300 border border-amber-500/30 align-middle animate-pulse">
            空证
          </span>
        </h3>
        <div v-if="item.goldenNature" class="text-xs text-xuan-gold/40 mt-1">{{ item.goldenNature }}</div>
      </div>
      <span v-if="item.badge" class="badge-circle text-xs flex-shrink-0 w-7 h-7 rounded-full flex items-center justify-center font-bold ml-2"
            :class="badgeColors[item.badge] || badgeColors['杂']">
        {{ item.badge }}
      </span>
    </div>

    <!-- 标签 -->
    <div v-if="item.tags && item.tags.length" class="flex flex-wrap gap-2 mb-3 relative z-10">
      <TagChip v-for="tag in item.tags" :key="tag.id" :name="tag.name" :substitute="tag.substitute" :badge="item.badge" />
    </div>

    <!-- 替参关系 -->
    <div v-if="item.substituteNotes && item.substituteNotes.length" 
         class="text-[10px] text-xuan-muted/30 mt-2 relative z-10">
      <span v-for="(note, idx) in item.substituteNotes" :key="idx">
        <span v-if="idx > 0"> | </span>{{ note.source }} → {{ note.target }}
      </span>
    </div>

    <div v-if="item.notes" class="text-xs text-xuan-muted/40 mt-2 italic relative z-10">{{ item.notes }}</div>
  </div>
</template>

<style scoped>
.card-enter {
  animation: cardFadeIn 0.4s ease backwards;
}
@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>