<script setup lang="ts">
import type { Realm } from '../api/realm';

defineProps<{
  realms: Realm[]
  activeId: number | null
}>()

const emit = defineEmits<{
  (e: 'select', realm: Realm): void
}>()

function shortName(name: string): string {
  return name.replace(/（.*）/, '')
}

// 卡片边框渐变（未选中时的微妙色彩）
const realmBg: Record<string, string> = {
  '胎息': 'from-amber-900/30 to-amber-800/15 border-amber-700/25',
  '练气': 'from-green-900/30 to-green-800/15 border-green-700/25',
  '筑基': 'from-blue-900/30 to-blue-800/15 border-blue-700/25',
  '紫府': 'from-purple-900/30 to-purple-800/15 border-purple-700/25',
  '金丹': 'from-yellow-900/30 to-yellow-800/15 border-yellow-700/25',
  '道胎': 'from-cyan-900/30 to-cyan-800/15 border-cyan-700/25',
  '仙君': 'from-rose-900/20 to-amber-900/10 border-amber-600/20',
}

// 文字颜色
const realmAccent: Record<string, string> = {
  '胎息': 'text-amber-400',
  '练气': 'text-green-400',
  '筑基': 'text-blue-400',
  '紫府': 'text-purple-400',
  '金丹': 'text-yellow-400',
  '道胎': 'text-cyan-400',
  '仙君': 'text-amber-200',
}

// 节点圆点颜色
const dotColors: Record<string, string> = {
  '胎息': 'bg-amber-500',
  '练气': 'bg-green-500',
  '筑基': 'bg-blue-500',
  '紫府': 'bg-purple-500',
  '金丹': 'bg-yellow-500',
  '道胎': 'bg-cyan-500',
  '仙君': 'bg-gradient-to-r from-amber-300 to-yellow-200',
}

// 连接线颜色
const lineColors: Record<string, string> = {
  '胎息': 'bg-amber-500',
  '练气': 'bg-green-500',
  '筑基': 'bg-blue-500',
  '紫府': 'bg-purple-500',
  '金丹': 'bg-yellow-500',
  '道胎': 'bg-cyan-500',
  '仙君': 'bg-amber-300',
}

// 仙君发光效果
function isXianJun(name: string): boolean {
  return shortName(name) === '仙君'
}
</script>

<template>
  <div class="relative">
    <div class="flex items-center overflow-x-auto pb-4 gap-0 scrollbar-none">
      <template v-for="(realm, idx) in realms" :key="realm.id">
        <!-- 连接线 -->
        <div v-if="idx > 0" 
             class="flex-shrink-0 w-12 sm:w-20 h-0.5"
             :class="(lineColors[shortName(realm.name)] || 'bg-gray-600')"
             style="opacity: 0.3"></div>

        <!-- 节点 -->
        <button class="flex-shrink-0 flex flex-col items-center gap-2 group cursor-pointer"
                @click="emit('select', realm)">
          <!-- 圆点 -->
          <div class="w-5 h-5 rounded-full border-2 transition-all duration-300 flex items-center justify-center"
               :class="[
                 activeId === realm.id
                   ? 'scale-125 ' + (dotColors[shortName(realm.name)] || 'bg-gray-500') + ' border-transparent shadow-lg'
                   : 'bg-xuan-card border-gray-600 group-hover:border-gray-400',
                 isXianJun(realm.name) && activeId === realm.id ? 'shadow-[0_0_20px_#fbbf24aa]' : '',
                 isXianJun(realm.name) ? 'border-amber-500/50' : ''
               ]">
            <div v-if="activeId === realm.id" 
                 class="w-2 h-2 rounded-full"
                 :class="isXianJun(realm.name) ? 'bg-amber-100' : 'bg-white'"></div>
            <!-- 仙君特有：外发光环 -->
            <div v-if="isXianJun(realm.name) && activeId === realm.id"
                 class="absolute w-8 h-8 rounded-full border border-amber-400/40 animate-ping"></div>
          </div>

          <!-- 名称 -->
          <div class="text-center relative">
            <div class="text-sm font-medium transition-colors"
                 :class="activeId === realm.id 
                   ? (realmAccent[shortName(realm.name)] || 'text-xuan-gold')
                   : 'text-xuan-muted group-hover:text-xuan-text'">
              {{ realm.name }}
            </div>
            <div class="text-[10px] text-xuan-muted/50">{{ realm.title }}</div>
          </div>
        </button>
      </template>
    </div>

    <!-- 进度条背景线 -->
    <div class="absolute top-[10px] left-0 right-0 h-0.5 bg-xuan-border -z-10"></div>
  </div>
</template>

<style scoped>
.scrollbar-none::-webkit-scrollbar { display: none; }
.scrollbar-none { -ms-overflow-style: none; scrollbar-width: none; }
@keyframes ping {
  75%, 100% { transform: scale(2); opacity: 0; }
}
.animate-ping { animation: ping 2s cubic-bezier(0, 0, 0.2, 1) infinite; }
</style>