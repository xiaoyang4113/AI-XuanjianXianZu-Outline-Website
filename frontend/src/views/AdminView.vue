<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const projectId = Number(route.params.id)
const activeTab = ref('characters')
const stats = ref<any>({})
const showForm = ref(false)
const editingItem = ref<any>(null)
const data = ref<any[]>([])
const loading = ref(false)
const search = ref('')
const avatarFile = ref<File | null>(null)

const tabs = [
  { key: 'characters', label: '人物' },
  { key: 'factions', label: '势力' },
  { key: 'techniques', label: '功法' },
  { key: 'artifacts', label: '灵物' },
  { key: 'entityItems', label: '果位' },
]

const categoryOptions = [
  { id: 1, name: '阴阳' }, { id: 2, name: '金德' }, { id: 3, name: '木德' },
  { id: 4, name: '水德' }, { id: 5, name: '火德' }, { id: 6, name: '土德' },
  { id: 7, name: '十二炁' }, { id: 8, name: '三雷' }, { id: 9, name: '并古一系' },
]
const badgeOptions = ['阳', '阴', '金', '木', '水', '火', '土', '炁', '雷', '巫', '祝', '素', '杂']

const form = reactive<Record<string, any>>({})

const columns: Record<string, { field: string; label: string }[]> = {
  characters: [
    { field: 'name', label: '姓名' }, { field: 'title', label: '尊号' },
    { field: 'realm', label: '境界' }, { field: 'status', label: '状态' },
  ],
  factions: [
    { field: 'name', label: '名称' }, { field: 'highestRealm', label: '最高境界' },
    { field: 'territory', label: '势力范围' },
  ],
  techniques: [
    { field: 'name', label: '功法名称' }, { field: 'grade', label: '品级' },
    { field: 'category', label: '道统' },
  ],
  artifacts: [
    { field: 'name', label: '名称' }, { field: 'type', label: '类型' },
    { field: 'creator', label: '创造者' },
  ],
  entityItems: [
    { field: 'name', label: '名称' }, { field: 'badge', label: '元素' },
    { field: 'categoryId', label: '分类' }, { field: 'isEmptyCertification', label: '空证' },
  ],
}

const filteredData = computed(() => {
  if (!search.value.trim()) return data.value
  const kw = search.value.toLowerCase()
  return data.value.filter((item: any) =>
    Object.values(item).some(v => v && String(v).toLowerCase().includes(kw))
  )
})

async function loadData() {
  loading.value = true
  const endpoints: Record<string, string> = {
    characters: `/api/admin/characters?projectId=${projectId}`,
    factions: `/api/admin/factions?projectId=${projectId}`,
    techniques: `/api/admin/techniques?projectId=${projectId}`,
    artifacts: `/api/admin/artifacts?projectId=${projectId}`,
    entityItems: `/api/admin/entity-items?projectId=${projectId}`,
  }
  try {
    const res = await fetch(endpoints[activeTab.value])
    const json = await res.json()
    data.value = json.code === 200 ? (json.data?.records || json.data) : []
  } catch { data.value = [] }
  loading.value = false
}

async function loadStats() {
  try {
    const res = await fetch(`/api/admin/stats/${projectId}`)
    const json = await res.json()
    if (json.code === 200) stats.value = json.data
  } catch { }
}

function openCreate() {
  editingItem.value = null
  avatarFile.value = null
  Object.keys(form).forEach(k => delete form[k])
  if (activeTab.value === 'characters') { form.projectId = projectId; form.isKeyFigure = 0 }
  else if (activeTab.value === 'factions') form.projectId = projectId
  else if (activeTab.value === 'techniques') form.projectId = projectId
  else if (activeTab.value === 'artifacts') form.projectId = projectId
  else if (activeTab.value === 'entityItems') { form.projectId = projectId; form.badge = '杂'; form.sortOrder = 0 }
  showForm.value = true
}

function openEdit(item: any) {
  editingItem.value = item
  avatarFile.value = null
  Object.keys(form).forEach(k => delete form[k])
  Object.assign(form, { ...item })
  showForm.value = true
}

async function save() {
  if (!form.name?.trim()) { alert('名称不能为空'); return }
  const isEdit = !!editingItem.value
  const baseEndpoint = activeTab.value === 'entityItems' ? 'entity-items' : activeTab.value
  const url = isEdit ? `/api/admin/${baseEndpoint}/${editingItem.value.id}` : `/api/admin/${baseEndpoint}`
  const method = isEdit ? 'PUT' : 'POST'
  try {
    const res = await fetch(url, {
      method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form)
    })
    const json = await res.json()
    if (json.code === 200) {
      const savedId = isEdit ? editingItem.value.id : json.data?.id
      if (activeTab.value === 'characters' && avatarFile.value && savedId) {
        const fd = new FormData(); fd.append('file', avatarFile.value)
        await fetch(`/api/file/avatar/${savedId}`, { method: 'POST', body: fd })
      }
      showForm.value = false; avatarFile.value = null; loadData(); loadStats()
    } else { alert('保存失败：' + json.message) }
  } catch (e: any) { alert('保存失败：' + e.message) }
}

async function remove(item: any) {
  if (!confirm(`确定删除「${item.name}」？`)) return
  const baseEndpoint = activeTab.value === 'entityItems' ? 'entity-items' : activeTab.value
  try {
    await fetch(`/api/admin/${baseEndpoint}/${item.id}`, { method: 'DELETE' })
    loadData(); loadStats()
  } catch { }
}

function onAvatarChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (file) avatarFile.value = file
}

function init() { data.value = []; showForm.value = false; avatarFile.value = null; loadData() }
onMounted(() => { loadData(); loadStats() })

// 果位表格显示分类名
function categoryName(id: number | null): string {
  return categoryOptions.find(c => c.id === id)?.name || '-'
}
</script>

<template>
  <div class="flex min-h-[calc(100vh-4rem)]">
    <!-- 侧边栏 -->
    <aside class="w-52 bg-white/[0.015] border-r border-white/[0.04] flex-shrink-0 p-4">
      <h3 class="text-sm font-bold text-xuan-gold mb-4">管理后台</h3>
      <nav class="space-y-1">
        <button v-for="tab in tabs" :key="tab.key"
          class="w-full text-left px-3 py-2 rounded-lg text-sm transition-all duration-300"
          :class="activeTab === tab.key ? 'bg-xuan-gold/10 text-xuan-gold border border-xuan-gold/20' : 'text-xuan-muted hover:text-xuan-text hover:bg-white/[0.03]'"
          @click="activeTab = tab.key; init()">{{ tab.label }}</button>
      </nav>
      <div class="mt-6 pt-4 border-t border-white/[0.04]">
        <div class="text-[10px] text-xuan-muted/40 mb-2">数据统计</div>
        <div class="space-y-1 text-[11px]">
          <div v-for="(val, key) in stats" :key="key" class="flex justify-between">
            <span class="text-xuan-muted/50">{{ String(key) }}</span>
            <span class="text-xuan-text/60 font-mono">{{ val }}</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- 主内容 -->
    <main class="flex-1 p-6 overflow-auto">
      <div class="flex items-center justify-between mb-4">
        <input v-model="search" placeholder="搜索…"
          class="w-64 h-9 px-4 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text placeholder:text-xuan-muted/30 focus:outline-none focus:border-xuan-gold/30 transition-all" />
        <button @click="openCreate"
          class="px-4 py-2 rounded-xl bg-xuan-gold/15 text-xuan-gold text-sm font-medium border border-xuan-gold/20 hover:bg-xuan-gold/25 hover:scale-105 transition-all duration-300">+
          新增</button>
      </div>

      <!-- 表格 -->
      <div class="bg-white/[0.015] border border-white/[0.04] rounded-2xl overflow-hidden">
        <div v-if="loading" class="text-center text-xuan-muted py-12 text-sm">加载中…</div>
        <table v-else class="w-full text-sm">
          <thead>
            <tr class="border-b border-white/[0.04]">
              <th class="text-left px-4 py-3 text-[11px] text-xuan-muted/40 font-medium">#</th>
              <th v-for="col in columns[activeTab]" :key="col.field"
                class="text-left px-4 py-3 text-[11px] text-xuan-muted/40 font-medium">{{ col.label }}</th>
              <th class="text-right px-4 py-3 text-[11px] text-xuan-muted/40 font-medium">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in filteredData" :key="item.id"
              class="border-b border-white/[0.02] hover:bg-white/[0.02] transition-colors">
              <td class="px-4 py-2.5 text-xuan-muted/30 text-xs">{{ idx + 1 }}</td>
              <td v-for="col in columns[activeTab]" :key="col.field"
                class="px-4 py-2.5 text-xuan-text/70 text-xs max-w-[200px] truncate">
                <template v-if="col.field === 'name' && activeTab === 'characters'">
                  <div class="flex items-center gap-2">
                    <img :src="`/api/file/image/avatar/thumb/${item.id}.png`"
                      class="w-7 h-7 rounded-xl object-cover border border-white/[0.04] bg-white/[0.02]"
                      @error="($event.target as HTMLImageElement).style.display = 'none'" />
                    {{ item.name }}
                  </div>
                </template>
                <template v-else-if="col.field === 'categoryId' && activeTab === 'entityItems'">
                  {{ categoryName(item.categoryId) }}
                </template>
                <template v-else-if="col.field === 'color' && item[col.field]">
                  <span class="inline-flex items-center gap-1.5">
                    <span class="w-3 h-3 rounded-full border border-white/[0.1]"
                      :style="{ background: item[col.field] }"></span>{{ item[col.field] }}
                  </span>
                </template>
                <template v-else>{{ item[col.field] || '-' }}</template>
              </td>
              <td class="px-4 py-2.5 text-right space-x-2">
                <button @click="openEdit(item)"
                  class="text-[11px] text-sky-400 hover:text-sky-300 transition-colors">编辑</button>
                <button @click="remove(item)"
                  class="text-[11px] text-rose-400 hover:text-rose-300 transition-colors">删除</button>
              </td>
            </tr>
            <tr v-if="filteredData.length === 0">
              <td :colspan="(columns[activeTab]?.length || 0) + 2"
                class="px-4 py-12 text-center text-xuan-muted/25 text-xs">暂无数据
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ==================== 新增/编辑弹窗 ==================== -->
      <Teleport to="body">
        <Transition name="fade">
          <div v-if="showForm" class="fixed inset-0 bg-black/60 z-50 flex items-center justify-center backdrop-blur-sm"
            @click.self="showForm = false">
            <div
              class="bg-[#0d0d0d] border border-white/[0.06] rounded-2xl w-full max-w-lg max-h-[85vh] overflow-y-auto shadow-2xl p-6">
              <h3 class="text-lg font-bold text-xuan-text mb-4">
                {{ editingItem ? '编辑' : '新增' }} {{tabs.find(t => t.key === activeTab)?.label || ''}}
              </h3>

              <!-- 头像上传（仅人物） -->
              <div v-if="activeTab === 'characters'" class="mb-4">
                <label class="text-[11px] text-xuan-muted/40 block mb-1.5">头像</label>
                <div class="flex items-center gap-4">
                  <img v-if="editingItem?.id" :src="`/api/file/image/avatar/thumb/${editingItem.id}.png`"
                    class="w-16 h-16 rounded-2xl object-cover border border-white/[0.06] bg-white/[0.02]"
                    @error="($event.target as HTMLImageElement).style.display = 'none'" />
                  <div v-else
                    class="w-16 h-16 rounded-2xl border border-white/[0.06] bg-white/[0.02] flex items-center justify-center text-xuan-muted/20 text-2xl">
                    +</div>
                  <label
                    class="cursor-pointer text-xs px-3 py-1.5 rounded-lg border border-white/[0.06] text-xuan-muted hover:text-xuan-gold hover:border-xuan-gold/30 transition-all duration-300">
                    {{ avatarFile ? avatarFile.name : '选择图片' }}
                    <input type="file" accept="image/*" class="hidden" @change="onAvatarChange" />
                  </label>
                </div>
              </div>

              <!-- ====== 通用表单字段 ====== -->
              <div class="space-y-3">

                <!-- ========== 人物 ========== -->
                <template v-if="activeTab === 'characters'">
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">姓名<span
                        class="text-rose-400">*</span></label><input v-model="form.name"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">尊号</label><input v-model="form.title"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div class="grid grid-cols-2 gap-3">
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">境界</label><input v-model="form.realm"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">道统</label><input
                        v-model="form.daoTradition"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                  </div>
                  <div class="grid grid-cols-3 gap-3">
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">状态</label><input v-model="form.status"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">辈分</label><input
                        v-model="form.lineage"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">脉系</label><input v-model="form.branch"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">配偶</label><input v-model="form.spouse"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">结局/死因</label><input
                      v-model="form.causeOfDeath"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">简介</label><textarea
                      v-model="form.description" rows="2"
                      class="w-full p-2.5 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 resize-none transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">光辉战绩</label><textarea
                      v-model="form.notableEvents" rows="2"
                      class="w-full p-2.5 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 resize-none transition-all" />
                  </div>
                </template>

                <!-- ========== 势力 ========== -->
                <template v-if="activeTab === 'factions'">
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">名称<span
                        class="text-rose-400">*</span></label><input v-model="form.name"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">首领</label><input
                      v-model="form.leaderName"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div class="grid grid-cols-2 gap-3">
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">最高境界</label><input
                        v-model="form.highestRealm"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">势力类型</label><input
                        v-model="form.territoryType"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">势力范围</label><input
                      v-model="form.territory"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">颜色(hex)</label><input
                      v-model="form.color" placeholder="#c9a96e"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">真实目的</label><textarea
                      v-model="form.trueGoal" rows="2"
                      class="w-full p-2.5 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 resize-none transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">简介</label><textarea
                      v-model="form.description" rows="2"
                      class="w-full p-2.5 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 resize-none transition-all" />
                  </div>
                </template>

                <!-- ========== 功法 ========== -->
                <template v-if="activeTab === 'techniques'">
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">功法名称<span
                        class="text-rose-400">*</span></label><input v-model="form.name"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div class="grid grid-cols-2 gap-3">
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">品级</label><input v-model="form.grade"
                        placeholder="六品/五品/四品"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">道统</label><input
                        v-model="form.category"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                  </div>
                  <div class="grid grid-cols-2 gap-3">
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">对应仙基</label><input
                        v-model="form.relatedFoundation"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">对应神通</label><input
                        v-model="form.relatedAbility"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">已知修炼者</label><input
                      v-model="form.knownUsers"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">描述</label><textarea
                      v-model="form.description" rows="2"
                      class="w-full p-2.5 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 resize-none transition-all" />
                  </div>
                </template>

                <!-- ========== 灵物 ========== -->
                <template v-if="activeTab === 'artifacts'">
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">名称<span
                        class="text-rose-400">*</span></label><input v-model="form.name"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div class="grid grid-cols-2 gap-3">
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">类型</label><input v-model="form.type"
                        placeholder="法器/丹药/灵物/术法/契约"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                    <div><label class="text-[11px] text-xuan-muted/40 block mb-1">创造者</label><input
                        v-model="form.creator"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                    </div>
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">作用</label><input v-model="form.effect"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">相关人物</label><input
                      v-model="form.relatedCharacters"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">描述</label><textarea
                      v-model="form.description" rows="2"
                      class="w-full p-2.5 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 resize-none transition-all" />
                  </div>
                </template>

                <!-- ========== 果位 ========== -->
                <template v-if="activeTab === 'entityItems'">
                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">名称<span
                        class="text-rose-400">*</span></label><input v-model="form.name"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>

                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">所属分类</label>
                    <select v-model="form.categoryId"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all">
                      <option v-for="c in categoryOptions" :key="c.id" :value="c.id">{{ c.name }}</option>
                    </select>
                  </div>

                  <!-- 子分类：只有并古一系(9)才显示 -->
                  <div v-if="Number(form.categoryId) === 9">
                    <label class="text-[11px] text-xuan-muted/40 block mb-1">子分类</label>
                    <select v-model="form.subCategoryId"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all">
                      <option :value="null">无</option>
                      <option :value="1">三巫</option>
                      <option :value="2">二祝</option>
                      <option :value="3">素德其余</option>
                      <option :value="4">其他</option>
                    </select>
                  </div>

                  <div class="grid grid-cols-2 gap-3">
                    <div>
                      <label class="text-[11px] text-xuan-muted/40 block mb-1">元素标识</label>
                      <select v-model="form.badge"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all">
                        <option v-for="b in badgeOptions" :key="b" :value="b">{{ b }}</option>
                      </select>
                    </div>
                    <div>
                      <label class="text-[11px] text-xuan-muted/40 block mb-1">空证</label>
                      <select v-model="form.isEmptyCertification"
                        class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all">
                        <option :value="false">否</option>
                        <option :value="true">是 ✦ 空证</option>
                      </select>
                    </div>
                  </div>

                  <div><label class="text-[11px] text-xuan-muted/40 block mb-1">金性</label><input
                      v-model="form.goldenNature"
                      class="w-full h-9 px-3 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 transition-all" />
                  </div>

                  <div>
                    <label class="text-[11px] text-xuan-muted/40 block mb-1">附带神通/仙基（逗号或换行分隔）</label>
                    <textarea v-model="form.tagNames" rows="3" placeholder="例：谒天门, 赤断镞, 君蹈危"
                      class="w-full p-2.5 rounded-xl bg-white/[0.03] border border-white/[0.05] text-sm text-xuan-text focus:outline-none focus:border-xuan-gold/30 resize-none transition-all" />
                  </div>
                </template>
              </div>

              <div class="flex justify-end gap-3 mt-6">
                <button @click="showForm = false"
                  class="px-4 py-2 rounded-xl border border-white/[0.06] text-xuan-muted text-sm hover:text-xuan-text hover:bg-white/[0.03] transition-all">取消</button>
                <button @click="save"
                  class="px-6 py-2 rounded-xl bg-xuan-gold/20 text-xuan-gold text-sm font-medium hover:bg-xuan-gold/30 hover:scale-105 transition-all">保存</button>
              </div>
            </div>
          </div>
        </Transition>
      </Teleport>
    </main>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

select option {
  background: #1a1a1a !important;
  color: #ddd !important;
}

select {
  color-scheme: dark;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>