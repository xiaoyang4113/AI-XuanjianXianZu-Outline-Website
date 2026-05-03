<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getArtifacts, getTechniques, type Artifact, type Technique } from '../api/technique'

const route = useRoute()
const projectId = Number(route.params.id)
const activeTab = ref<'techniques' | 'artifacts' | 'ai'>('techniques')
const techniques = ref<Technique[]>([])
const artifacts = ref<Artifact[]>([])
const aiPrompt = ref('')
const aiResult = ref('')
const aiLoading = ref(false)
const filterCategory = ref('全部')

onMounted(async () => {
  const [tRes, aRes] = await Promise.all([
    getTechniques(projectId), getArtifacts(projectId)
  ])
  if (tRes.code === 200) techniques.value = tRes.data
  if (aRes.code === 200) artifacts.value = aRes.data
})

const categories = ['全部']
const allCats = new Set(techniques.value.map(t => t.category).filter(Boolean))
allCats.forEach(c => categories.push(c!))

const filteredTech = ref(techniques)
function filterByCategory(cat: string) {
  filterCategory.value = cat
  filteredTech.value = cat === '全部' ? techniques.value : techniques.value.filter(t => t.category === cat)
}

const gradeColor: Record<string, string> = {
  '六品': 'text-yellow-400', '五品': 'text-orange-400', '四品': 'text-blue-400'
}

const typeColor: Record<string, string> = {
  '法器': 'text-purple-400', '丹药': 'text-green-400', '术法': 'text-cyan-400',
  '灵物': 'text-amber-400', '契约': 'text-red-400',
}

async function doAI() {
  if (!aiPrompt.value.trim()) return
  aiLoading.value = true
  try {
    const { generateAI } = await import('../api/ai')
    const res = await generateAI(aiPrompt.value)
    if (res.code === 200) aiResult.value = res.data.content
  } catch (e: any) { aiResult.value = '请求失败：' + e.message }
  finally { aiLoading.value = false }
}
</script>

<template>
  <div class="pt-6">
    <div class="flex gap-2 mb-6">
      <button v-for="tab in [{k:'techniques',l:'功法典籍'},{k:'artifacts',l:'灵物丹药'},{k:'ai',l:'AI灵感'}]"
              :key="tab.k" @click="activeTab = tab.k as any"
              class="px-5 py-2.5 rounded-xl text-sm font-medium transition-all"
              :class="activeTab === tab.k ? 'text-xuan-gold bg-xuan-gold/10 border border-xuan-gold/20' : 'text-xuan-muted bg-xuan-card border border-xuan-border hover:text-xuan-text'">
        {{ tab.l }}
      </button>
    </div>

    <!-- 功法 -->
    <div v-if="activeTab === 'techniques'">
      <div class="flex flex-wrap gap-2 mb-6">
        <button v-for="cat in categories" :key="cat"
                class="px-3 py-1.5 rounded-lg text-xs border transition-all"
                :class="filterCategory === cat ? 'text-xuan-gold bg-xuan-gold/10 border-xuan-gold/20' : 'text-xuan-muted bg-xuan-card border-xuan-border'"
                @click="filterByCategory(cat)">{{ cat }}</button>
      </div>
      <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <div v-for="t in filteredTech" :key="t.id"
             class="bg-xuan-card border border-xuan-border rounded-xl p-4 hover:border-xuan-gold/20 transition-all">
          <div class="flex items-start justify-between mb-2">
            <h3 class="text-sm font-bold text-xuan-text">{{ t.name }}</h3>
            <span v-if="t.grade" class="text-[10px] px-1.5 py-0.5 rounded bg-xuan-bg border border-xuan-border"
                  :class="gradeColor[t.grade]">{{ t.grade }}</span>
          </div>
          <div class="text-[10px] text-xuan-muted mb-1">{{ t.category }}</div>
          <div v-if="t.relatedFoundation" class="text-[10px] text-xuan-gold/60">仙基：{{ t.relatedFoundation }}</div>
          <div v-if="t.knownUsers" class="text-[10px] text-xuan-muted mt-1">修炼者：{{ t.knownUsers }}</div>
          <div v-if="t.description" class="text-[10px] text-xuan-muted/60 mt-2 line-clamp-2">{{ t.description }}</div>
        </div>
      </div>
    </div>

    <!-- 灵物 -->
    <div v-if="activeTab === 'artifacts'">
      <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <div v-for="a in artifacts" :key="a.id"
             class="bg-xuan-card border border-xuan-border rounded-xl p-4 hover:border-xuan-gold/20 transition-all">
          <div class="flex items-start justify-between mb-2">
            <h3 class="text-sm font-bold text-xuan-text">{{ a.name }}</h3>
            <span class="text-[10px] px-1.5 py-0.5 rounded bg-xuan-bg border border-xuan-border"
                  :class="typeColor[a.type || '']">{{ a.type }}</span>
          </div>
          <div v-if="a.creator" class="text-[10px] text-xuan-muted">创造者：{{ a.creator }}</div>
          <div v-if="a.effect" class="text-[10px] text-xuan-gold/60 mt-1">{{ a.effect }}</div>
          <div v-if="a.relatedCharacters" class="text-[10px] text-xuan-muted mt-1">相关：{{ a.relatedCharacters }}</div>
          <div v-if="a.description" class="text-[10px] text-xuan-muted/60 mt-2 line-clamp-2">{{ a.description }}</div>
        </div>
      </div>
    </div>

    <!-- AI -->
    <div v-if="activeTab === 'ai'">
      <div class="bg-xuan-card border border-xuan-border rounded-2xl p-6">
        <h3 class="text-sm font-bold text-xuan-text mb-1">AI 创作助手</h3>
        <p class="text-xs text-xuan-muted mb-4">基于 DeepSeek，输入提示词获取灵感</p>
        <textarea v-model="aiPrompt" rows="3" placeholder="例如：帮我设计一个新的果位，要求与雷法相关，适合剑修…"
                  class="w-full p-3 rounded-xl bg-xuan-bg border border-xuan-border text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/40 resize-none mb-3"></textarea>
        <button @click="doAI" :disabled="aiLoading"
                class="px-6 py-2 rounded-xl bg-xuan-gold/20 text-xuan-gold text-sm font-medium hover:bg-xuan-gold/30 transition-all disabled:opacity-50">
          {{ aiLoading ? '生成中…' : '✨ 生成灵感' }}
        </button>
        <div v-if="aiResult" class="mt-4 p-4 rounded-xl bg-xuan-bg/50 border border-xuan-border/50 text-sm text-xuan-text/80 leading-relaxed whitespace-pre-wrap">{{ aiResult }}</div>
      </div>
    </div>
  </div>
</template>