<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getFactionRelations, getFactions, type Faction, type FactionRelation } from '../api/faction'
import FactionCard from '../components/FactionCard.vue'
import FactionDetailPanel from '../components/FactionDetailPanel.vue'
import RelationshipGraph from '../components/RelationshipGraph.vue'

const route = useRoute()
const projectId = Number(route.params.id)

const factions = ref<Faction[]>([])
const relations = ref<FactionRelation[]>([])
const loading = ref(false)
const selectedId = ref<number | null>(null)

onMounted(async () => {
  loading.value = true
  try {
    const [fRes, rRes] = await Promise.all([
      getFactions(projectId),
      getFactionRelations(projectId)
    ])
    if (fRes.code === 200) factions.value = fRes.data
    if (rRes.code === 200) relations.value = rRes.data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="pt-6">
    <h2 class="text-xl font-bold text-xuan-text mb-2">势力格局</h2>
    <p class="text-xs text-xuan-muted mb-8">点击势力卡片查看详情</p>

    <div v-if="loading" class="text-center text-xuan-muted py-20">加载中…</div>

    <template v-else>
      <!-- 势力关系图谱 -->
      <RelationshipGraph 
        :relations="relations" 
        :factions="factions.map(f => ({ id: f.id, name: f.name, color: f.color || '#c9a96e' }))"
        @select="selectedId = $event"
        class="mb-8" />

      <!-- 势力卡片 -->
      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <FactionCard v-for="f in factions" :key="f.id" :faction="f" @select="selectedId = $event" />
      </div>
    </template>

    <FactionDetailPanel :project-id="projectId" :faction-id="selectedId" @close="selectedId = null" />
  </div>
</template>