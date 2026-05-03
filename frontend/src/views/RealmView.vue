<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import type { CultivationPath, PathStage, Realm } from '../api/realm'
import { getPaths, getPathStages, getRealms } from '../api/realm'
import AbilityCombination from '../components/AbilityCombination.vue'
import PathSelector from '../components/PathSelector.vue'
import RealmDetail from '../components/RealmDetail.vue'
import RealmTimeline from '../components/RealmTimeline.vue'

const route = useRoute()
const projectId = Number(route.params.id)

const activeTab = ref<'realms' | 'paths' | 'abilities'>('realms')
const realms = ref<Realm[]>([])
const selectedRealm = ref<Realm | null>(null)
const paths = ref<CultivationPath[]>([])
const selectedPath = ref<CultivationPath | null>(null)
const pathStages = ref<PathStage[]>([])
const allItems = ref<{ id: number; name: string; badge: string }[]>([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const [realmsRes, pathsRes, itemsRes] = await Promise.all([
      getRealms(projectId),
      getPaths(projectId),
      // 获取所有果位（用于五神通下拉）
      fetch(`/api/items?page=1&size=200&projectId=${projectId}`).then(r => r.json())
    ])
    if (realmsRes.code === 200) realms.value = realmsRes.data
    if (pathsRes.code === 200) paths.value = pathsRes.data
    if (itemsRes.code === 200 && itemsRes.data?.records) {
      allItems.value = itemsRes.data.records.map((i: any) => ({
        id: i.id, name: i.name, badge: i.badge
      }))
    }
  } finally {
    loading.value = false
  }
})

function selectRealm(realm: Realm) {
  selectedRealm.value = selectedRealm.value?.id === realm.id ? null : realm
}

async function selectPath(path: CultivationPath) {
  if (selectedPath.value?.id === path.id) {
    selectedPath.value = null
    pathStages.value = []
    return
  }
  selectedPath.value = path
  const res = await getPathStages(path.id)
  if (res.code === 200) pathStages.value = res.data
}
</script>

<template>
  <div class="pt-6">
    <!-- 顶部标签 -->
    <div class="flex gap-2 mb-8">
      <button v-for="tab in [
        { key: 'realms' as const, label: '境界体系' },
        { key: 'paths' as const, label: '道统分支' },
        { key: 'abilities' as const, label: '五神通组合' }
      ]" :key="tab.key"
              class="px-5 py-2.5 rounded-xl text-sm font-medium transition-all duration-200"
              :class="activeTab === tab.key
                ? 'text-xuan-gold bg-xuan-gold/10 border border-xuan-gold/20'
                : 'text-xuan-muted bg-xuan-card border border-xuan-border hover:text-xuan-text hover:border-xuan-gold/10'"
              @click="activeTab = tab.key">
        {{ tab.label }}
      </button>
    </div>

    <div v-if="loading" class="text-center text-xuan-muted py-20">加载中…</div>

    <!-- 境界体系 Tab -->
    <div v-else-if="activeTab === 'realms'">
      <h2 class="text-xl font-bold text-xuan-text mb-2">修炼境界路线</h2>
      <p class="text-xs text-xuan-muted mb-6">点击境界节点查看详情</p>

      <RealmTimeline :realms="realms" :active-id="selectedRealm?.id ?? null" @select="selectRealm" />
      <RealmDetail :realm="selectedRealm" />
    </div>

    <!-- 道统分支 Tab -->
    <div v-else-if="activeTab === 'paths'">
      <h2 class="text-xl font-bold text-xuan-text mb-2">修炼道统选择</h2>
      <p class="text-xs text-xuan-muted mb-6">选择一个道统查看各境界表现</p>

      <PathSelector :paths="paths" :selected-path="selectedPath" :path-stages="pathStages" @select="selectPath" />
    </div>

    <!-- 五神通组合 Tab -->
    <div v-else-if="activeTab === 'abilities'">
      <h2 class="text-xl font-bold text-xuan-text mb-2">果位五神通配置</h2>
      <p class="text-xs text-xuan-muted mb-6">选择果位查看其仙基、神通和金性</p>

      <AbilityCombination :items="allItems" />
    </div>
  </div>
</template>