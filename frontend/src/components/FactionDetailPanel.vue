<script setup lang="ts">
import { ref, watch } from 'vue';
import type { Faction } from '../api/faction';
import { getFactions } from '../api/faction';

const props = defineProps<{ projectId: number; factionId: number | null }>()
const emit = defineEmits<{ (e: 'close'): void }>()

const detail = ref<Faction | null>(null)

const relationLabel: Record<string, string> = {
  alliance: '同盟', enemy: '敌对', monitor: '监控', puppet: '附庸', neutral: '中立', heritage: '传承'
}
const relationBg: Record<string, string> = {
  alliance: 'bg-green-900/30 border-green-700/30',
  enemy: 'bg-red-900/30 border-red-700/30',
  monitor: 'bg-amber-900/30 border-amber-700/30',
  puppet: 'bg-gray-800/30 border-gray-600/30',
  neutral: 'bg-blue-900/30 border-blue-700/30',
  heritage: 'bg-purple-900/30 border-purple-700/30',
}

watch(() => props.factionId, async (id) => {
  if (!id) { detail.value = null; return }
  const res = await getFactions(props.projectId)
  if (res.code === 200) detail.value = res.data.find(f => f.id === id) || null
}, { immediate: true })
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="factionId" class="fixed inset-0 bg-black/60 z-50 backdrop-blur-sm" @click="emit('close')" />
    </Transition>
    <Transition name="slide">
      <div v-if="factionId" class="fixed top-0 right-0 h-full w-full max-w-lg bg-xuan-card border-l border-xuan-border z-50 overflow-y-auto shadow-2xl">
        <div v-if="detail" class="p-6">
          <button @click="emit('close')" class="absolute top-4 right-4 w-8 h-8 rounded-full bg-xuan-bg border border-xuan-border flex items-center justify-center text-xuan-muted hover:text-xuan-text transition-colors">✕</button>

          <!-- 色条 + 名称 -->
          <div class="mb-6">
            <div class="h-1.5 w-16 rounded-full mb-4" :style="{ background: detail.color || '#c9a96e' }"></div>
            <h2 class="text-2xl font-bold mb-1" :style="{ color: detail.color || '#c9a96e' }">{{ detail.name }}</h2>
            <div class="text-xs text-xuan-muted">首领：{{ detail.leaderName || '未知' }} · 最高境界：{{ detail.highestRealm || '未知' }}</div>
          </div>

          <!-- 领地 -->
          <div v-if="detail.territory" class="mb-4 text-sm text-xuan-muted">📍 {{ detail.territory }}</div>

          <!-- 描述 -->
          <div v-if="detail.description" class="mb-5 bg-xuan-bg/50 rounded-xl p-4 border border-xuan-border/50">
            <h4 class="text-xs text-xuan-muted/60 mb-1">简介</h4>
            <p class="text-sm text-xuan-text/80 leading-relaxed">{{ detail.description }}</p>
          </div>

          <!-- 真实目的 -->
          <div v-if="detail.trueGoal" class="mb-5 bg-amber-950/30 rounded-xl p-4 border border-amber-800/30">
            <h4 class="text-xs text-amber-400/60 mb-1">真实目的</h4>
            <p class="text-sm text-amber-400/80 leading-relaxed">{{ detail.trueGoal }}</p>
          </div>

          <!-- 成员 -->
          <div v-if="detail.members && detail.members.length" class="mb-5">
            <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-3">成员（{{ detail.memberCount }}人）</h4>
            <div class="space-y-2">
              <div v-for="m in detail.members" :key="m.id"
                   class="flex items-center gap-3 p-3 rounded-xl bg-xuan-bg/30 border border-xuan-border/30">
                <span class="w-2 h-2 rounded-full" :class="m.status === '存活' ? 'bg-green-400' : m.status === '已故' ? 'bg-gray-500' : 'bg-purple-400'"></span>
                <span class="text-sm text-xuan-text">{{ m.name }}</span>
                <span class="text-[10px] text-xuan-muted ml-auto">{{ m.realm || '未知' }}</span>
              </div>
            </div>
          </div>

          <!-- 势力关系 -->
          <div v-if="detail.relations && detail.relations.length">
            <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-3">势力关系</h4>
            <div class="space-y-2">
              <div v-for="rel in detail.relations" :key="rel.targetId"
                   class="p-3 rounded-xl border" :class="relationBg[rel.relationType] || 'bg-xuan-bg/30 border-xuan-border/30'">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-sm font-medium text-xuan-text">{{ rel.targetName }}</span>
                  <span class="text-[10px] px-1.5 py-0.5 rounded bg-xuan-bg border border-xuan-border text-xuan-muted">
                    {{ relationLabel[rel.relationType] || rel.relationType }}
                  </span>
                </div>
                <p class="text-xs text-xuan-muted/70">{{ rel.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.slide-enter-active { transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-leave-active { transition: transform 0.2s ease-in; }
.slide-enter-from, .slide-leave-to { transform: translateX(100%); }
</style>