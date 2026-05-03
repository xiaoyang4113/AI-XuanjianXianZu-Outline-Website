<script setup lang="ts">
import type { CategoryWithItems } from '../types'

const props = defineProps<{
  categories: CategoryWithItems[]
  activeId: number
  totalCount: number
}>()

const emit = defineEmits<{
  (e: 'select', id: number): void
}>()
</script>

<template>
  <div class="sticky top-16 z-40 bg-xuan-bg/95 backdrop-blur-sm border-b border-xuan-border">
    <div class="flex overflow-x-auto gap-1 py-2 px-1 scrollbar-none">
      <!-- 总览 -->
      <button
          class="flex-shrink-0 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200"
          :class="activeId === 0
          ? 'text-xuan-gold bg-xuan-gold/10 border border-xuan-gold/20'
          : 'text-xuan-muted hover:text-xuan-text hover:bg-xuan-card border border-transparent'"
          @click="emit('select', 0)"
      >
        玄鉴
      </button>

      <!-- 各分类 -->
      <button
          v-for="cat in categories"
          :key="cat.id"
          class="flex-shrink-0 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200"
          :class="activeId === cat.id
          ? 'text-xuan-gold bg-xuan-gold/10 border border-xuan-gold/20'
          : 'text-xuan-muted hover:text-xuan-text hover:bg-xuan-card border border-transparent'"
          @click="emit('select', cat.id)"
      >
        {{ cat.name }}
        <span class="ml-1 text-xs opacity-60">{{ cat.itemCount }}</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.scrollbar-none::-webkit-scrollbar { display: none; }
.scrollbar-none { -ms-overflow-style: none; scrollbar-width: none; }
</style>