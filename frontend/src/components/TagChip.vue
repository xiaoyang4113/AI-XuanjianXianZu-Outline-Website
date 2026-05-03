<script setup lang="ts">
defineProps<{
  name: string
  substitute?: boolean
  badge?: string
}>()

// 道统淡色映射 —— 只有边框和文字有颜色，背景统一暗色
const colorMap: Record<string, { border: string; text: string }> = {
  '阳': { border: 'border-l-amber-400/50',  text: 'text-amber-300/70' },
  '阴': { border: 'border-l-purple-400/50', text: 'text-purple-300/70' },
  '金': { border: 'border-l-yellow-400/50', text: 'text-yellow-300/70' },
  '木': { border: 'border-l-emerald-400/50',text: 'text-emerald-300/70' },
  '水': { border: 'border-l-sky-400/50',    text: 'text-sky-300/70' },
  '火': { border: 'border-l-red-400/50',    text: 'text-red-300/70' },
  '土': { border: 'border-l-orange-400/50', text: 'text-orange-300/70' },
  '炁': { border: 'border-l-violet-400/50', text: 'text-violet-300/70' },
  '雷': { border: 'border-l-cyan-400/50',   text: 'text-cyan-300/70' },
  '巫': { border: 'border-l-rose-400/50',   text: 'text-rose-300/70' },
  '祝': { border: 'border-l-pink-400/50',   text: 'text-pink-300/70' },
  '素': { border: 'border-l-stone-400/50',  text: 'text-stone-300/70' },
  '杂': { border: 'border-l-zinc-400/50',   text: 'text-zinc-300/70' },
}

const fallback = { border: 'border-l-green-400/50', text: 'text-green-300/70' }

function getStyle(badge?: string) {
  if (!badge) return fallback
  return colorMap[badge] || fallback
}
</script>

<template>
  <span class="inline-flex items-center px-3 py-1 rounded text-xs transition-all duration-300 cursor-default
               bg-white/[0.03] border border-white/[0.04] border-l-2
               hover:bg-white/[0.06] hover:border-l-[3px] hover:scale-[1.02]"
        :class="[getStyle(badge).border, getStyle(badge).text]">
    <span v-if="substitute" class="inline-flex items-center justify-center w-3.5 h-3.5 rounded-full text-[8px] font-bold
                                   bg-amber-700/30 text-amber-400/70 mr-1.5">替</span>
    <span>{{ name }}</span>
  </span>
</template>