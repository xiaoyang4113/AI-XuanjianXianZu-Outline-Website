<script setup lang="ts">
import { computed } from 'vue';
import type { Character } from '../api/character';

const props = defineProps<{ characters: Character[] }>()
const emit = defineEmits<{ (e: 'select', id: number): void }>()

// 始祖单独拎出来
const ancestor = computed(() => props.characters.find(c => c.lineage === '始祖'))

// 从第1代开始的后代
const generations = ['第1代', '第2代', '第3代', '第4代', '第5代', '第6代', '第7代']
const branches = ['伯', '仲', '叔', '季']

const grouped = computed(() => {
  const map: Record<string, Record<string, Character[]>> = {}
  for (const gen of generations) {
    map[gen] = { '伯': [], '仲': [], '叔': [], '季': [] }
  }
  for (const c of props.characters) {
    if (!c.lineage || c.lineage === '始祖') continue
    const branch = (c.branch || '').replace('(归宗)', '')
    if (map[c.lineage] && map[c.lineage][branch]) {
      map[c.lineage][branch].push(c)
    }
  }
  return map
})

const statusDot: Record<string, string> = {
  '存活': 'bg-emerald-400', '已故': 'bg-zinc-500', '转世': 'bg-sky-400',
  '生死不明': 'bg-amber-400', '残魂': 'bg-violet-400', '被镇压': 'bg-rose-400',
}

const branchBg: Record<string, string> = {
  '伯': 'border-l-emerald-500/50 bg-emerald-950/8',
  '仲': 'border-l-sky-500/50 bg-sky-950/8',
  '叔': 'border-l-rose-500/50 bg-rose-950/8',
  '季': 'border-l-violet-500/50 bg-violet-950/8',
}
</script>

<template>
  <div class="overflow-x-auto">
    <!-- ========== 始祖：居中展示 ========== -->
    <div v-if="ancestor" class="flex justify-center mb-8">
      <div class="relative">
        <!-- 光晕 -->
        <div class="absolute -inset-8 rounded-full bg-xuan-gold/5 blur-2xl pointer-events-none"></div>
        <!-- 连接线 -->
        <div class="absolute top-full left-1/2 -translate-x-1/2 w-0.5 h-8 bg-gradient-to-b from-xuan-gold/50 to-transparent"></div>

        <div class="relative flex flex-col items-center cursor-pointer group"
             @click="emit('select', ancestor.id)">
          <!-- 始祖头像 -->
          <div class="w-20 h-20 rounded-full border-3 flex items-center justify-center text-2xl font-bold
                      transition-all duration-500 group-hover:scale-110 group-hover:shadow-2xl"
               :style="{
                 borderColor: '#c9a96e',
                 background: 'linear-gradient(135deg, rgba(201,169,110,0.2), rgba(201,169,110,0.05))',
                 boxShadow: '0 0 40px rgba(201,169,110,0.15)'
               }">
            <span class="text-xuan-gold">{{ ancestor.name.charAt(0) }}</span>
          </div>
          <div class="mt-3 text-center">
            <div class="text-lg font-bold text-xuan-gold">{{ ancestor.name }}</div>
            <div class="text-[10px] text-xuan-gold/40 mt-0.5">奠基始祖</div>
            <div v-if="ancestor.spouse" class="text-[10px] text-xuan-muted/30 mt-1">配 {{ ancestor.spouse }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 后代世代 ========== -->
    <!-- 图例 -->
    <div class="flex justify-center gap-5 mb-6">
      <div v-for="b in branches" :key="b" class="flex items-center gap-2 text-xs text-xuan-muted/50">
        <div class="w-3 h-3 rounded-sm border-l-2" :class="branchBg[b]"></div>
        <span>{{ b }}脉</span>
      </div>
    </div>

    <div class="space-y-6">
      <div v-for="gen in generations" :key="gen">
        <!-- 世代标签 -->
        <div class="flex items-center gap-3 mb-3">
          <div class="text-sm font-bold text-xuan-gold/70 w-14 flex-shrink-0 text-right">{{ gen }}</div>
          <div class="flex-1 h-px bg-white/[0.04]"></div>
        </div>

        <!-- 四脉网格 -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 ml-14">
          <div v-for="b in branches" :key="b" class="min-w-0">
            <template v-if="grouped[gen] && grouped[gen][b] && grouped[gen][b].length">
              <div v-for="char in grouped[gen][b]" :key="char.id"
                   class="border-l-2 rounded-r-xl p-3 mb-2 cursor-pointer transition-all duration-300
                          hover:shadow-lg hover:-translate-y-0.5"
                   :class="branchBg[b]"
                   @click="emit('select', char.id)">
                <div class="flex items-center gap-2 mb-1">
                  <span class="w-2 h-2 rounded-full flex-shrink-0" :class="statusDot[char.status] || 'bg-zinc-500'"></span>
                  <span class="text-sm font-medium text-xuan-text truncate">{{ char.name }}</span>
                </div>
                <div v-if="char.title" class="text-[10px] text-xuan-gold/40 truncate ml-4">{{ char.title }}</div>
                <div class="flex flex-wrap gap-1 mt-1.5 ml-4">
                  <span v-if="char.realm" class="text-[10px] px-1.5 py-0.5 rounded bg-black/20 text-xuan-muted/50">
                    {{ char.realm }}
                  </span>
                  <span v-if="char.daoTradition" class="text-[10px] px-1.5 py-0.5 rounded" 
                        :style="{ color: (char.factionColor || '#c9a96e'), background: (char.factionColor || '#c9a96e') + '10' }">
                    {{ char.daoTradition }}
                  </span>
                </div>
              </div>
            </template>
            <div v-else class="h-6"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>