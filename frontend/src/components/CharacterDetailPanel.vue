<script setup lang="ts">
import { computed, ref, watch } from 'vue'; // ← 加上 computed
import type { Character } from '../api/character';
import { getCharacters } from '../api/character';

const props = defineProps<{ projectId: number; characterId: number | null }>()
const emit = defineEmits<{ (e: 'close'): void }>()

const detail = ref<Character | null>(null)
const imgError = ref(false)
const showOriginal = ref(false)

const relationTypeLabel: Record<string, string> = {
  mentor: '师徒', ally: '同盟', enemy: '敌对', family: '家族',
  rival: '竞争', disciple: '弟子', unknown: '未知'
}
const relationTypeColor: Record<string, string> = {
  mentor: 'text-amber-400', ally: 'text-emerald-400', enemy: 'text-rose-400',
  family: 'text-sky-400', rival: 'text-orange-400', unknown: 'text-zinc-400'
}
const statusColor: Record<string, string> = {
  '存活': 'text-emerald-400', '已故': 'text-zinc-500', '转世': 'text-sky-400',
  '被镇压': 'text-rose-400', '残魂': 'text-violet-400', '生死不明': 'text-amber-400',
}

watch(() => props.characterId, async (id) => {
  if (!id) { detail.value = null; return }
  const res = await getCharacters(props.projectId)
  if (res.code === 200) detail.value = res.data.find((c: Character) => c.id === id) || null
  imgError.value = false
}, { immediate: true })

const thumbUrl = computed(() => `/api/file/image/avatar/thumb/${props.characterId}.png`)
const originalUrl = computed(() => `/api/file/image/avatar/${props.characterId}.png`)
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="characterId" class="fixed inset-0 bg-black/70 z-50 backdrop-blur-sm" @click="emit('close')" />
    </Transition>
    <Transition name="slide">
      <div v-if="characterId" class="fixed top-0 right-0 h-full w-full max-w-lg bg-[#0d0d0d] border-l border-white/[0.06] z-50 overflow-y-auto shadow-2xl">
        <div v-if="detail" class="p-6">
          <button @click="emit('close')" 
                  class="absolute top-4 right-4 w-9 h-9 rounded-xl bg-white/[0.03] border border-white/[0.06] 
                         flex items-center justify-center text-xuan-muted hover:text-xuan-text hover:bg-white/[0.06]
                         transition-all duration-300">
            ✕
          </button>

          <!-- 头像 + 名字区块 -->
          <div class="flex items-start gap-5 mb-6">
            <div class="relative flex-shrink-0 cursor-pointer group/avatar" @click="showOriginal = true">
              <div class="w-20 h-20 rounded-2xl overflow-hidden border-2 border-white/[0.06] 
                          group-hover/avatar:border-xuan-gold/40 transition-all duration-500 
                          bg-gradient-to-br from-white/[0.03] to-transparent
                          group-hover/avatar:shadow-xl group-hover/avatar:shadow-xuan-gold/10">
                <img v-if="!imgError" :src="thumbUrl" @error="imgError = true"
                     class="w-full h-full object-cover transition-transform duration-700 group-hover/avatar:scale-110" />
                <div v-else class="w-full h-full flex items-center justify-center text-3xl opacity-25 select-none">
                  {{ detail.name.charAt(0) }}
                </div>
              </div>
              <div class="absolute -bottom-1 -right-1 w-6 h-6 rounded-lg bg-xuan-gold/20 border border-xuan-gold/30 
                          flex items-center justify-center text-[10px] text-xuan-gold opacity-0 
                          group-hover/avatar:opacity-100 transition-all duration-300">
                🔍
              </div>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-3 mb-1">
                <h2 class="text-2xl font-bold text-xuan-text">{{ detail.name }}</h2>
                <span class="text-xs px-2 py-0.5 rounded-full border border-white/[0.06] font-medium"
                      :class="statusColor[detail.status] || 'text-xuan-muted'">
                  {{ detail.status }}
                </span>
              </div>
              <div v-if="detail.title" class="text-sm text-xuan-gold/60 mb-2">{{ detail.title }}</div>
              <div class="flex flex-wrap gap-1.5 text-[10px] text-xuan-muted">
                <span v-if="detail.lineage" class="bg-white/[0.03] border border-white/[0.04] rounded-md px-2 py-0.5">{{ detail.lineage }}</span>
                <span v-if="detail.branch" class="bg-white/[0.03] border border-white/[0.04] rounded-md px-2 py-0.5">{{ detail.branch }}脉</span>
                <span v-if="detail.realm" class="bg-white/[0.03] border border-white/[0.04] rounded-md px-2 py-0.5">{{ detail.realm }}</span>
                <span v-if="detail.daoTradition" class="rounded-md px-2 py-0.5 border"
                      :style="{ borderColor: (detail.factionColor||'#c9a96e')+'20', color: detail.factionColor||'#c9a96e', background: (detail.factionColor||'#c9a96e')+'08' }">
                  {{ detail.daoTradition }}
                </span>
                <span v-if="detail.factionName" class="bg-white/[0.03] border border-white/[0.04] rounded-md px-2 py-0.5">{{ detail.factionName }}</span>
                <span v-if="detail.spouse" class="bg-white/[0.03] border border-white/[0.04] rounded-md px-2 py-0.5">
                  配{{ detail.spouse.split('；')[0] }}
                </span>
              </div>
            </div>
          </div>

          <!-- 简介 -->
          <div v-if="detail.description" class="mb-5 bg-white/[0.02] rounded-2xl p-4 border border-white/[0.04]">
            <h4 class="text-[11px] text-xuan-muted/40 uppercase tracking-wider mb-1.5">简介</h4>
            <p class="text-sm text-xuan-text/70 leading-relaxed">{{ detail.description }}</p>
          </div>

          <!-- 光辉战绩 -->
          <div v-if="detail.notableEvents" class="mb-5 bg-amber-900/10 rounded-2xl p-4 border border-amber-500/[0.08]">
            <h4 class="text-[11px] text-amber-400/40 uppercase tracking-wider mb-1.5">光辉战绩</h4>
            <p class="text-sm text-amber-300/60 leading-relaxed">{{ detail.notableEvents }}</p>
          </div>

          <!-- 结局 -->
          <div v-if="detail.causeOfDeath" class="mb-5 bg-rose-900/10 rounded-2xl p-4 border border-rose-500/[0.08]">
            <h4 class="text-[11px] text-rose-400/40 uppercase tracking-wider mb-1.5">结局</h4>
            <p class="text-sm text-rose-300/60 leading-relaxed">{{ detail.causeOfDeath }}</p>
          </div>

          <!-- 人物关系 -->
          <div v-if="detail.relations && detail.relations.length">
            <h4 class="text-[11px] text-xuan-muted/40 uppercase tracking-wider mb-3">
              人物关系（{{ detail.relations.length }}）
            </h4>
            <div class="space-y-2">
              <div v-for="(rel, idx) in detail.relations" :key="idx"
                   class="flex items-start gap-3 p-3 rounded-xl bg-white/[0.015] border border-white/[0.03]
                          hover:border-xuan-gold/10 transition-all duration-300">
                <div class="w-2 h-2 rounded-full mt-1.5 flex-shrink-0"
                     :class="rel.relationType === 'enemy' ? 'bg-rose-400' : 
                            rel.relationType === 'ally' ? 'bg-emerald-400' : 
                            rel.relationType === 'family' ? 'bg-sky-400' : 
                            rel.relationType === 'mentor' ? 'bg-amber-400' : 'bg-zinc-400'"></div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-1">
                    <span class="text-sm font-medium text-xuan-text">{{ rel.targetName }}</span>
                    <span v-if="rel.targetTitle" class="text-[10px] text-xuan-gold/30">{{ rel.targetTitle }}</span>
                    <span class="text-[10px] px-1.5 py-0.5 rounded border border-white/[0.04]"
                          :class="relationTypeColor[rel.relationType] || 'text-xuan-muted'">
                      {{ relationTypeLabel[rel.relationType] || rel.relationType }}
                    </span>
                  </div>
                  <p class="text-xs text-xuan-muted/50">{{ rel.description }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 原图弹窗 -->
    <Transition name="fade">
      <div v-if="showOriginal" class="fixed inset-0 bg-black/90 z-[60] flex items-center justify-center p-8 backdrop-blur-md"
           @click="showOriginal = false">
        <button @click="showOriginal = false" 
                class="absolute top-6 right-6 w-10 h-10 rounded-xl bg-white/[0.05] border border-white/[0.08] 
                       flex items-center justify-center text-white/60 hover:text-white hover:bg-white/[0.1] 
                       transition-all duration-300 text-lg">✕</button>
        <img :src="originalUrl" class="max-w-full max-h-full rounded-2xl shadow-2xl object-contain" />
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.slide-enter-active { transition: transform 0.35s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-leave-active { transition: transform 0.2s ease-in; }
.slide-enter-from, .slide-leave-to { transform: translateX(100%); }
</style>