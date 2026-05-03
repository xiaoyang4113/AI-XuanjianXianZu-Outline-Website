<script setup lang="ts">
import type { CultivationPath, PathStage } from '../api/realm';

const props = defineProps<{
  paths: CultivationPath[]
  selectedPath: CultivationPath | null
  pathStages: PathStage[]
}>()

const emit = defineEmits<{
  (e: 'select', path: CultivationPath): void
}>()
</script>

<template>
  <div>
    <!-- 道统选择卡片 -->
    <div class="grid grid-cols-2 sm:grid-cols-5 gap-3 mb-8">
      <button v-for="path in paths" :key="path.id"
              class="p-4 rounded-xl border text-left transition-all duration-300"
              :class="selectedPath?.id === path.id
                ? 'bg-xuan-card border-xuan-gold/30 shadow-lg'
                : 'bg-xuan-card/50 border-xuan-border hover:border-xuan-gold/15'"
              @click="emit('select', path)">
        <div class="text-2xl mb-2">{{ path.icon }}</div>
        <div class="text-sm font-medium" :style="{ color: path.color }">{{ path.name }}</div>
        <div class="text-[10px] text-xuan-muted mt-1">{{ path.representative?.split('（')[0] }}</div>
      </button>
    </div>

    <!-- 选中道统详情 -->
    <Transition name="fade">
      <div v-if="selectedPath" class="bg-xuan-card border border-xuan-border rounded-2xl p-6 sm:p-8">
        <!-- 头部 -->
        <div class="mb-6">
          <h2 class="text-2xl font-bold mb-2" :style="{ color: selectedPath.color }">
            {{ selectedPath.name }}
          </h2>
          <div class="text-xs text-xuan-muted">代表人物：{{ selectedPath.representative }}</div>
        </div>

        <!-- 核心特征 -->
        <div class="mb-5">
          <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-2">核心特征</h4>
          <p class="text-sm text-xuan-text/80 leading-relaxed">{{ selectedPath.feature }}</p>
        </div>

        <!-- 缺点 -->
        <div class="mb-5">
          <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-2">缺点</h4>
          <p class="text-sm text-red-400/70 leading-relaxed">{{ selectedPath.weakness }}</p>
        </div>

        <!-- 突破方式 -->
        <div class="mb-6 bg-xuan-bg/50 rounded-xl p-4 border border-xuan-border/30">
          <h4 class="text-xs text-xuan-gold/60 mb-2">突破方式</h4>
          <p class="text-sm text-xuan-gold/80 leading-relaxed">{{ selectedPath.breakthroughMethod }}</p>
        </div>

        <!-- 各境界表现 -->
        <div v-if="pathStages.length">
          <h4 class="text-xs text-xuan-muted/60 uppercase tracking-wider mb-3">各境界表现</h4>
          <div class="space-y-3">
            <div v-for="ps in pathStages" :key="ps.realmId"
                 class="flex items-start gap-3 p-3 rounded-xl bg-xuan-bg/30 border border-xuan-border/30">
              <div class="flex-shrink-0 w-16 text-center">
                <div class="text-sm font-medium text-xuan-gold">{{ ps.realmName }}</div>
              </div>
              <div class="flex-1">
                <p class="text-xs text-xuan-muted/80 leading-relaxed">{{ ps.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.fade-enter-active { transition: all 0.3s ease; }
.fade-leave-active { transition: all 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(8px); }
</style>