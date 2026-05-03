<script setup lang="ts">
import type { Faction } from '../api/faction';

defineProps<{ faction: Faction }>()
const emit = defineEmits<{ (e: 'select', id: number): void }>()

const relationLabel: Record<string, string> = {
  alliance: '同盟', enemy: '敌对', monitor: '监控', puppet: '附庸', neutral: '中立', heritage: '传承'
}
const relationDot: Record<string, string> = {
  alliance: 'bg-green-400', enemy: 'bg-red-400', monitor: 'bg-amber-400',
  puppet: 'bg-gray-400', neutral: 'bg-blue-400', heritage: 'bg-purple-400'
}
</script>

<template>
  <div class="group bg-xuan-card border border-xuan-border rounded-2xl overflow-hidden cursor-pointer
              hover:shadow-xl hover:shadow-black/30 transition-all duration-300"
       @click="emit('select', faction.id)">
    <!-- 顶部色条 -->
    <div class="h-1" :style="{ background: faction.color || '#c9a96e' }"></div>

    <div class="p-5">
      <!-- 名称 + 境界 -->
      <div class="flex items-start justify-between mb-3">
        <div>
          <h3 class="text-lg font-bold text-xuan-text group-hover:text-xuan-gold transition-colors">
            {{ faction.name }}
          </h3>
          <div v-if="faction.highestRealm" class="text-xs text-xuan-muted mt-0.5">
            最高境界：{{ faction.highestRealm }}
          </div>
        </div>
        <div class="text-2xl opacity-30 group-hover:opacity-60 transition-opacity">→</div>
      </div>

      <!-- 领地 -->
      <div v-if="faction.territory" class="text-xs text-xuan-muted mb-3">
        📍 {{ faction.territory }}
      </div>

      <!-- 成员数 -->
      <div class="text-xs text-xuan-muted mb-3">
        成员 <span class="text-xuan-text font-medium">{{ faction.memberCount }}</span> 人
      </div>

      <!-- 关系 -->
      <div v-if="faction.relations && faction.relations.length" class="flex flex-wrap gap-1.5">
        <span v-for="rel in faction.relations" :key="rel.targetId"
              class="inline-flex items-center gap-1 text-[10px] px-2 py-0.5 rounded-full bg-xuan-bg border border-xuan-border">
          <span class="w-1.5 h-1.5 rounded-full" :class="relationDot[rel.relationType] || 'bg-gray-500'"></span>
          <span class="text-xuan-text/70">{{ rel.targetName }}</span>
          <span class="text-xuan-muted/50">{{ relationLabel[rel.relationType] || rel.relationType }}</span>
        </span>
      </div>
    </div>
  </div>
</template>