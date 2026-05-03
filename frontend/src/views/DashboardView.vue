<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProjectStore } from '../stores/project'
import StatsCard from '../components/StatsCard.vue'

const route = useRoute()
const router = useRouter()
const store = useProjectStore()
const projectId = Number(route.params.id)

onMounted(async () => {
  await store.fetchStats(projectId)
})
</script>

<template>
  <div class="pt-8">
    <button @click="router.push('/')" class="text-sm text-xuan-muted hover:text-xuan-gold mb-6 inline-block">
      ← 返回
    </button>

    <h1 class="text-2xl font-bold mb-8">
      <span class="text-xuan-gold">{{ store.stats?.projectName || '加载中' }}</span>
      <span class="text-xuan-muted text-sm ml-3 font-normal">仪表盘</span>
    </h1>

    <div v-if="store.stats" class="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">
      <StatsCard :value="store.stats.totalItems" label="道统总计" />
      <StatsCard :value="store.stats.totalTags" label="已知神通" />
      <StatsCard :value="store.stats.goldenNaturesCount" label="金性已录" />
      <StatsCard :value="store.stats.totalCategories" label="分类数" />
    </div>

    <!-- 分类明细 -->
    <div v-if="store.stats?.categoryCounts" class="grid grid-cols-2 sm:grid-cols-3 gap-3">
      <div v-for="(count, name) in store.stats.categoryCounts" :key="name"
           class="bg-xuan-card border border-xuan-border rounded-xl p-4 text-center
                  hover:border-xuan-gold/20 transition-all">
        <div class="text-2xl font-bold text-xuan-gold">{{ count }}</div>
        <div class="text-xs text-xuan-muted mt-1">{{ name }}</div>
      </div>
    </div>

    <div class="mt-8 text-center">
      <router-link
          :to="{ name: 'browse', params: { id: projectId } }"
          class="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-xuan-gold/10
               border border-xuan-gold/20 text-xuan-gold text-sm
               hover:bg-xuan-gold/20 transition-all">
        进入道统图鉴 →
      </router-link>
    </div>
  </div>
</template>