<script setup lang="ts">
import { ref } from 'vue';
import type { Character } from '../api/character';

const props = defineProps<{ character: Character }>()
const emit = defineEmits<{ (e: 'select', id: number): void }>()

const imgError = ref(false)
const thumbUrl = `/api/file/image/avatar/thumb/${props.character.id}.png`

const statusConfig: Record<string, { color: string; dot: string; label: string }> = {
  '存活':     { color: 'text-emerald-400',  dot: 'bg-emerald-400',  label: '存活' },
  '已故':     { color: 'text-zinc-500',    dot: 'bg-zinc-500',     label: '已故' },
  '转世':     { color: 'text-sky-400',     dot: 'bg-sky-400',      label: '转世' },
  '被镇压':   { color: 'text-rose-400',    dot: 'bg-rose-400',     label: '被镇压' },
  '残魂':     { color: 'text-violet-400',  dot: 'bg-violet-400',   label: '残魂' },
  '生死不明': { color: 'text-amber-400',   dot: 'bg-amber-400',    label: '生死不明' },
}

const branchColors: Record<string, string> = {
  '伯': 'border-l-emerald-500', '仲': 'border-l-sky-500',
  '叔': 'border-l-rose-500',   '季': 'border-l-violet-500',
}
</script>

<template>
  <div class="ink-card relative rounded-2xl p-5 cursor-pointer
              hover:-translate-y-1 hover:shadow-xl hover:shadow-black/30
              transition-all duration-500 group"
       :class="character.lineage && character.branch 
         ? 'border-l-2 ' + (branchColors[character.branch.replace('(归宗)','')] || '') 
         : ''"
       @click="emit('select', character.id)">
    
    <!-- 水墨晕染hover背景 -->
    <div class="absolute inset-0 rounded-2xl bg-gradient-to-br from-xuan-gold/3 to-transparent 
                opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none"></div>

    <!-- 头像 + 信息 -->
    <div class="flex items-center gap-4 mb-3 relative z-10">
      <div class="relative flex-shrink-0 w-14 h-14 rounded-2xl overflow-hidden border-2 border-white/[0.04] 
                  group-hover:border-xuan-gold/40 group-hover:shadow-lg group-hover:shadow-xuan-gold/10
                  transition-all duration-500 bg-gradient-to-br from-white/[0.03] to-transparent">
        <img v-if="!imgError" :src="thumbUrl" @error="imgError = true"
             class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" />
        <div v-else class="w-full h-full flex items-center justify-center text-2xl opacity-25 select-none">
          {{ character.name.charAt(0) }}
        </div>
      </div>
      <div class="flex-1 min-w-0">
        <h3 class="text-base font-bold text-xuan-text group-hover:text-xuan-gold transition-colors duration-300 truncate">
          {{ character.name }}
        </h3>
        <div v-if="character.title" class="text-[11px] text-xuan-gold/40 truncate mt-0.5">
          {{ character.title }}
        </div>
        <div class="flex items-center gap-1.5 mt-1">
          <span class="w-1.5 h-1.5 rounded-full" :class="(statusConfig[character.status] || statusConfig['存活']).dot"></span>
          <span class="text-[10px]" :class="(statusConfig[character.status] || statusConfig['存活']).color">
            {{ (statusConfig[character.status] || statusConfig['存活']).label }}
          </span>
        </div>
      </div>
    </div>

    <div class="flex flex-wrap gap-1.5 relative z-10">
      <span v-if="character.realm" class="text-[10px] px-2 py-0.5 rounded-md bg-black/20 border border-white/[0.03] text-xuan-text/50">
        {{ character.realm }}
      </span>
      <span v-if="character.daoTradition" class="text-[10px] px-2 py-0.5 rounded-md border"
            :style="{ borderColor: (character.factionColor||'#c9a96e')+'15', color: character.factionColor||'#c9a96e', background: (character.factionColor||'#c9a96e')+'08' }">
        {{ character.daoTradition }}
      </span>
      <span v-if="character.lineage" class="text-[10px] px-2 py-0.5 rounded-md bg-black/20 border border-white/[0.03] text-xuan-muted/40">
        {{ character.lineage }}
      </span>
    </div>

    <div class="mt-3 pt-2 border-t border-white/[0.03] flex items-center justify-between text-[10px] text-xuan-muted/25 relative z-10">
      <span v-if="character.factionName">{{ character.factionName }}</span>
      <span>{{ character.relations?.length || 0 }} 条关系</span>
    </div>
  </div>
</template>