<script setup lang="ts">
import type { Realm } from '../api/realm';

defineProps<{
  realm: Realm | null
}>()

function shortName(name: string): string {
  return name.replace(/（.*）/, '')
}

const realmAccent: Record<string, string> = {
  '胎息': 'text-amber-400 border-amber-700/40',
  '练气': 'text-green-400 border-green-700/40',
  '筑基': 'text-blue-400 border-blue-700/40',
  '紫府': 'text-purple-400 border-purple-700/40',
  '金丹': 'text-yellow-400 border-yellow-700/40',
  '道胎': 'text-cyan-400 border-cyan-700/40',
  '仙君': 'text-amber-200 border-amber-500/30',
}

const dotColors: Record<string, string> = {
  '胎息': 'bg-amber-500',
  '练气': 'bg-green-500',
  '筑基': 'bg-blue-500',
  '紫府': 'bg-purple-500',
  '金丹': 'bg-yellow-500',
  '道胎': 'bg-cyan-500',
  '仙君': 'bg-gradient-to-r from-amber-300 to-yellow-200',
}

const bgGradients: Record<string, string> = {
  '胎息': 'from-amber-950/20 to-transparent',
  '练气': 'from-green-950/20 to-transparent',
  '筑基': 'from-blue-950/20 to-transparent',
  '紫府': 'from-purple-950/20 to-transparent',
  '金丹': 'from-yellow-950/20 to-transparent',
  '道胎': 'from-cyan-950/20 to-transparent',
  '仙君': 'from-amber-950/20 via-rose-950/10 to-transparent',
}
</script>

<template>
  <Transition name="fade">
    <div v-if="realm"
         class="relative mt-8 bg-xuan-card border border-xuan-border rounded-2xl p-6 sm:p-8 overflow-hidden"
         :class="[bgGradients[shortName(realm.name)] || '']">
      
      <!-- 仙君专属背景光晕 -->
      <div v-if="shortName(realm.name) === '仙君'" 
           class="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-amber-400/5 blur-3xl pointer-events-none"></div>

      <!-- 头部 -->
      <div class="flex items-start justify-between mb-6 relative z-10">
        <div>
          <h2 class="text-2xl font-bold mb-1" :class="realmAccent[shortName(realm.name)] || 'text-xuan-gold'">
            {{ realm.name }}
            <!-- 仙君特殊标记 -->
            <span v-if="shortName(realm.name) === '仙君'" 
                  class="ml-2 text-[10px] px-2 py-0.5 rounded-full bg-amber-400/15 text-amber-300 border border-amber-500/30 align-middle">
              ✦ 金仙
            </span>
          </h2>
          <div class="flex items-center gap-3 text-sm text-xuan-muted">
            <span>称号：{{ realm.title }}</span>
            <span class="text-xuan-border">|</span>
            <span>寿命：{{ realm.lifespan }}</span>
            <span v-if="realm.isCriticalPoint" 
                  class="text-amber-400 bg-amber-400/10 px-2 py-0.5 rounded-full text-xs border border-amber-700/30">
              ⚠ 关键分岔点
            </span>
          </div>
        </div>
      </div>

      <!-- 核心特征 -->
      <div class="mb-6 relative z-10">
        <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-2">核心特征</h4>
        <p class="text-sm text-xuan-text/80 leading-relaxed">{{ realm.description }}</p>
      </div>

      <!-- 晋升条件 -->
      <div class="mb-6 rounded-xl p-4 border relative z-10"
           :class="shortName(realm.name) === '仙君' 
             ? 'bg-amber-950/20 border-amber-800/30' 
             : 'bg-xuan-bg/50 border-xuan-border/30'">
        <h4 class="text-xs mb-2"
            :class="shortName(realm.name) === '仙君' ? 'text-amber-400/60' : 'text-xuan-gold/60'">
          {{ shortName(realm.name) === '仙君' ? '✦ 成道之路' : '晋升条件' }}
        </h4>
        <p class="text-sm leading-relaxed"
           :class="shortName(realm.name) === '仙君' ? 'text-amber-300/80' : 'text-xuan-gold/80'">
          {{ realm.promotionCondition }}
        </p>
      </div>

      <!-- 子阶段列表 -->
      <div v-if="realm.stages && realm.stages.length" class="relative z-10">
        <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-3">子阶段</h4>
        <div class="space-y-3">
          <div v-for="stage in realm.stages" :key="stage.id"
               class="flex items-start gap-3 p-3 rounded-xl transition-all"
               :class="stage.isCritical 
                 ? 'bg-amber-950/30 border border-amber-800/30' 
                 : 'bg-xuan-bg/30 border border-xuan-border/30'">
            <div class="w-2.5 h-2.5 rounded-full mt-1.5 flex-shrink-0"
                 :class="stage.isCritical ? 'bg-amber-500' : ((dotColors[shortName(realm.name)] || 'bg-gray-500').split(' ')[0])"
                 :style="stage.isCritical ? '' : 'opacity: 0.7'"></div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <span class="text-sm font-medium text-xuan-text">{{ stage.name }}</span>
                <span v-if="stage.isCritical" 
                      class="text-[10px] text-amber-400 bg-amber-400/10 px-1.5 py-0.5 rounded">
                  关键
                </span>
              </div>
              <p class="text-xs text-xuan-muted mt-1 leading-relaxed">{{ stage.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.fade-enter-active { transition: all 0.3s ease; }
.fade-leave-active { transition: all 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(8px); }
</style>