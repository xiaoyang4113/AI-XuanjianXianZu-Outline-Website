<script setup lang="ts">
import type { FactionRelation } from '../api/faction';

defineProps<{ relations: FactionRelation[]; factions: { id: number; name: string; color: string }[] }>()
const emit = defineEmits<{ (e: 'select', id: number): void }>()

const relationLabel: Record<string, string> = {
  alliance: '同盟', enemy: '敌对', monitor: '监控', puppet: '附庸', neutral: '中立', heritage: '传承'
}
const relationStroke: Record<string, string> = {
  alliance: '#22c55e', enemy: '#ef4444', monitor: '#f59e0b',
  puppet: '#6b7280', neutral: '#3b82f6', heritage: '#a855f7'
}
const relationDash: Record<string, string> = {
  monitor: '6,4', puppet: '4,4', heritage: '8,4'
}
</script>

<template>
  <div class="bg-xuan-card border border-xuan-border rounded-2xl p-6 overflow-x-auto">
    <h3 class="text-sm text-xuan-muted/60 uppercase tracking-wider mb-4">势力关系图谱</h3>
    <div class="flex flex-wrap gap-4 justify-center">
      <!-- 势力节点 -->
      <div v-for="f in factions" :key="f.id"
           class="flex flex-col items-center gap-2 cursor-pointer group"
           @click="emit('select', f.id)">
        <div class="w-20 h-20 rounded-2xl flex items-center justify-center text-lg font-bold border-2 transition-all duration-300 group-hover:scale-110 group-hover:shadow-lg"
             :style="{
               borderColor: f.color + '60',
               background: f.color + '15',
               color: f.color,
               boxShadow: '0 0 20px ' + f.color + '20'
             }">
          {{ f.name.charAt(0) }}
        </div>
        <span class="text-xs text-xuan-muted group-hover:text-xuan-text transition-colors">{{ f.name }}</span>
      </div>
    </div>

    <!-- 关系列表 -->
    <div class="mt-6 space-y-2">
      <div v-for="rel in relations" :key="rel.sourceId + '-' + rel.targetId + '-' + rel.relationType"
           class="flex items-center gap-3 p-2.5 rounded-xl bg-xuan-bg/30 border border-xuan-border/30">
        <span class="text-sm font-medium text-xuan-text" :style="{ color: rel.sourceColor }">{{ rel.sourceName }}</span>
        <div class="flex-1 flex items-center gap-2">
          <div class="flex-1 h-px" :style="{ background: relationStroke[rel.relationType] || '#666' }"></div>
          <span class="text-[10px] px-2 py-0.5 rounded-full border whitespace-nowrap"
                :style="{ borderColor: (relationStroke[rel.relationType] || '#666') + '40', color: relationStroke[rel.relationType] || '#999' }">
            {{ relationLabel[rel.relationType] || rel.relationType }}
          </span>
          <div class="flex-1 h-px" :style="{ background: relationStroke[rel.relationType] || '#666' }"></div>
        </div>
        <span class="text-sm font-medium text-xuan-text" :style="{ color: rel.targetColor }">{{ rel.targetName }}</span>
      </div>
    </div>
  </div>
</template>