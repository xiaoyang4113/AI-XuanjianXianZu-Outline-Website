<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import CategoryTabs from '../components/CategoryTabs.vue'
import ItemCard from '../components/ItemCard.vue'
import ItemDetailPanel from '../components/ItemDetailPanel.vue'
import { useProjectStore } from '../stores/project'

const route = useRoute()
const store = useProjectStore()
const projectId = Number(route.params.id)
const activeCategoryId = ref(0)
const selectedItemId = ref<number | null>(null)
const contentVisible = ref(true)

onMounted(async () => {
  await Promise.all([
    store.fetchStats(projectId),
    store.fetchCategories(projectId)
  ])
})

const displayCategories = computed(() => {
  if (activeCategoryId.value === 0) return store.categories
  return store.categories.filter(c => c.id === activeCategoryId.value)
})

const totalOverview = computed(() => store.stats)

function handleTabSelect(id: number) {
  contentVisible.value = false
  setTimeout(() => {
    activeCategoryId.value = id
    contentVisible.value = true
  }, 150)
}
</script>

<template>
  <div>
    <!-- 标签栏 -->
    <CategoryTabs 
      :categories="store.categories"
      :active-id="activeCategoryId"
      :total-count="store.stats?.totalItems || 0"
      @select="handleTabSelect"
    />

    <div v-if="store.loading" class="text-center text-xuan-muted py-20">加载中…</div>

    <!-- 内容区域（带过渡） -->
    <div v-else v-show="contentVisible" class="content-fade" :class="{ 'content-visible': contentVisible }">

      <!-- ========== 总览 Tab ========== -->
      <div v-if="activeCategoryId === 0" class="mt-8">
        <!-- 统计概览 -->
        <div v-if="totalOverview" class="mb-10">
          <div class="text-center mb-8">
            <h2 class="text-2xl font-bold text-xuan-text mb-1">道统神通仙基总览</h2>
            <p class="text-sm text-xuan-muted">{{ totalOverview.projectName }}</p>
          </div>
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-8">
            <div class="ink-card text-center p-5">
              <div class="text-3xl font-bold text-xuan-gold">{{ totalOverview.totalItems }}</div>
              <div class="text-[10px] text-xuan-muted/60 mt-1">道统总计</div>
            </div>
            <div v-for="(count, name) in totalOverview.categoryCounts" :key="name" 
                 class="ink-card text-center p-5">
              <div class="text-3xl font-bold text-xuan-gold">{{ count }}</div>
              <div class="text-[10px] text-xuan-muted/60 mt-1">{{ name }}</div>
            </div>
            <div class="ink-card text-center p-5">
              <div class="text-3xl font-bold text-xuan-gold">{{ totalOverview.totalTags }}+</div>
              <div class="text-[10px] text-xuan-muted/60 mt-1">已知神通</div>
            </div>
            <div class="ink-card text-center p-5">
              <div class="text-3xl font-bold text-xuan-gold">{{ totalOverview.goldenNaturesCount }}</div>
              <div class="text-[10px] text-xuan-muted/60 mt-1">金性已录</div>
            </div>
          </div>
        </div>

        <!-- 所有分类内容 -->
        <div v-for="cat in store.categories" :key="cat.id" class="mb-10">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-8 h-8 rounded-lg bg-xuan-gold/10 border border-xuan-gold/20 
                        flex items-center justify-center text-xuan-gold text-sm font-bold">
              {{ cat.name.charAt(0) }}
            </div>
            <h3 class="text-lg font-bold text-xuan-text">{{ cat.name }}</h3>
            <span class="text-xs text-xuan-muted/50">{{ cat.itemCount }}</span>
          </div>

          <template v-if="cat.subCategories && cat.subCategories.length">
            <div v-for="sub in cat.subCategories" :key="sub.id" class="mb-5 ml-4">
              <div class="flex items-center gap-2 mb-3">
                <div class="w-1 h-4 rounded bg-xuan-gold/30"></div>
                <span class="text-sm text-xuan-muted font-medium">{{ sub.name }}</span>
              </div>
              <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                <ItemCard v-for="item in sub.items" :key="item.id" :item="item" @select="selectedItemId = $event" />
              </div>
            </div>
          </template>
          <div v-else class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            <ItemCard v-for="item in cat.items" :key="item.id" :item="item" @select="selectedItemId = $event" />
          </div>
        </div>
      </div>

      <!-- ========== 单分类 Tab ========== -->
      <div v-else class="mt-8">
        <div v-for="cat in displayCategories" :key="cat.id">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-8 h-8 rounded-lg bg-xuan-gold/10 border border-xuan-gold/20 
                        flex items-center justify-center text-xuan-gold text-sm font-bold">
              {{ cat.name.charAt(0) }}
            </div>
            <h3 class="text-lg font-bold text-xuan-text">{{ cat.name }}</h3>
            <span class="text-xs text-xuan-muted/50">{{ cat.itemCount }}</span>
          </div>

          <template v-if="cat.subCategories && cat.subCategories.length">
            <div v-for="sub in cat.subCategories" :key="sub.id" class="mb-5 ml-4">
              <div class="flex items-center gap-2 mb-3">
                <div class="w-1 h-4 rounded bg-xuan-gold/30"></div>
                <span class="text-sm text-xuan-muted font-medium">{{ sub.name }}</span>
              </div>
              <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                <ItemCard v-for="item in sub.items" :key="item.id" :item="item" @select="selectedItemId = $event" />
              </div>
            </div>
          </template>
          <div v-else class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            <ItemCard v-for="item in cat.items" :key="item.id" :item="item" @select="selectedItemId = $event" />
          </div>
        </div>
      </div>
    </div>

    <!-- 详情面板 -->
    <ItemDetailPanel :project-id="projectId" :item-id="selectedItemId" @close="selectedItemId = null" />
  </div>
</template>

<style scoped>
.content-fade {
  opacity: 0;
  transform: translateY(12px);
  transition: opacity 0.35s ease, transform 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}
.content-fade.content-visible {
  opacity: 1;
  transform: translateY(0);
}
</style>