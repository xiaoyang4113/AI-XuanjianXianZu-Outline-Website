<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getCharacters, type Character } from '../api/character'
import CharacterCard from '../components/CharacterCard.vue'
import CharacterDetailPanel from '../components/CharacterDetailPanel.vue'
import FamilyTreeView from '../components/FamilyTreeView.vue'

const route = useRoute()
const projectId = Number(route.params.id)
const characters = ref<Character[]>([])
const loading = ref(false)
const activeTab = ref<'all' | 'family'>('all')
const activeFilter = ref('全部')
const activeLineage = ref('全部')
const selectedId = ref<number | null>(null)

const statusFilters = ['全部', '存活', '已故', '转世', '残魂', '生死不明', '被镇压']
const lineageFilters = ['全部', '始祖', '第1代', '第2代', '第3代', '第4代', '第5代', '第6代', '第7代']

const filtered = computed(() => {
  let list = characters.value
  if (activeFilter.value !== '全部') list = list.filter(c => c.status === activeFilter.value)
  if (activeLineage.value !== '全部') list = list.filter(c => c.lineage === activeLineage.value)
  return list
})

const statusColors: Record<string, string> = {
  '存活': 'border-green-700/40 text-green-400 bg-green-400/10',
  '已故': 'border-gray-600/40 text-gray-400 bg-gray-400/10',
  '转世': 'border-blue-700/40 text-blue-400 bg-blue-400/10',
  '残魂': 'border-purple-700/40 text-purple-400 bg-purple-400/10',
  '生死不明': 'border-amber-700/40 text-amber-400 bg-amber-400/10',
  '被镇压': 'border-red-700/40 text-red-400 bg-red-400/10',
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getCharacters(projectId)
    if (res.code === 200) characters.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="pt-6">
    <!-- Tab 切换 -->
    <div class="flex gap-2 mb-6">
      <button v-for="tab in [{ key: 'all' as const, label: '全部人物' }, { key: 'family' as const, label: '李家族谱' }]"
              :key="tab.key"
              class="px-5 py-2.5 rounded-xl text-sm font-medium transition-all duration-200"
              :class="activeTab === tab.key
                ? 'text-xuan-gold bg-xuan-gold/10 border border-xuan-gold/20'
                : 'text-xuan-muted bg-xuan-card border border-xuan-border hover:text-xuan-text'"
              @click="activeTab = tab.key">
        {{ tab.label }}
      </button>
    </div>

    <div v-if="loading" class="text-center text-xuan-muted py-20">加载中…</div>

    <!-- 全部人物 Tab -->
    <template v-else-if="activeTab === 'all'">
      <!-- 状态筛选 -->
      <div class="flex flex-wrap gap-2 mb-4">
        <button v-for="f in statusFilters" :key="f"
                class="px-4 py-2 rounded-xl text-xs font-medium border transition-all duration-200"
                :class="activeFilter === f
                  ? (statusColors[f] || 'text-xuan-gold bg-xuan-gold/10 border-xuan-gold/30')
                  : 'text-xuan-muted bg-xuan-card border-xuan-border hover:border-xuan-gold/15'"
                @click="activeFilter = f">
          {{ f }}
        </button>
      </div>

      <!-- 辈分筛选 -->
      <div class="flex flex-wrap gap-2 mb-8">
        <button v-for="l in lineageFilters" :key="l"
                class="px-3 py-1.5 rounded-lg text-[11px] font-medium border transition-all duration-200"
                :class="activeLineage === l
                  ? 'text-xuan-gold bg-xuan-gold/10 border-xuan-gold/20'
                  : 'text-xuan-muted/70 bg-xuan-card/50 border-xuan-border/50 hover:text-xuan-text'"
                @click="activeLineage = l">
          {{ l }}
        </button>
      </div>

      <!-- 人物网格 -->
      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <CharacterCard v-for="c in filtered" :key="c.id" :character="c" @select="selectedId = $event" />
      </div>

      <div v-if="filtered.length === 0" class="text-center text-xuan-muted/50 py-20">暂无此条件的人物</div>
    </template>

    <!-- 李家族谱 Tab -->
    <template v-else-if="activeTab === 'family'">
      <div class="mb-4">
        <h3 class="text-lg font-bold text-xuan-text mb-1">李家八代谱系</h3>
        <p class="text-xs text-xuan-muted">点击人物卡片查看详情 · 左侧色条代表脉系</p>
      </div>
      <FamilyTreeView :characters="characters" @select="selectedId = $event" />
    </template>

    <CharacterDetailPanel :project-id="projectId" :character-id="selectedId" @close="selectedId = null" />
  </div>
</template>