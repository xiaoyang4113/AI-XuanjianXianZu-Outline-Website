<script setup lang="ts">
import { GraphChart } from 'echarts/charts'
import { LegendComponent, TooltipComponent } from 'echarts/components'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { onMounted, ref } from 'vue'
import VChart from 'vue-echarts'
import { useRoute } from 'vue-router'
import { getFactions } from '../api/faction'

use([GraphChart, TooltipComponent, LegendComponent, CanvasRenderer])

const route = useRoute()
const projectId = Number(route.params.id)
const option = ref<any>(null)

const relationLabel: Record<string, string> = {
  alliance: '同盟', enemy: '敌对', monitor: '监控', heritage: '传承', neutral: '中立'
}
const relationColor: Record<string, string> = {
  alliance: '#22c55e', enemy: '#ef4444', monitor: '#f59e0b', heritage: '#a855f7', neutral: '#3b82f6'
}

onMounted(async () => {
  const res = await getFactions(projectId)
  const factions = res.code === 200 ? res.data : []

  const nodes: any[] = factions.map((f: any) => ({
    name: f.name,
    symbolSize: Math.min(52, Math.max(28, (f.memberCount || 1) * 4 + 22)),
    category: 0,
    itemStyle: {
      color: f.color || '#c9a96e',
      borderColor: (f.color || '#c9a96e') + 'cc',
      borderWidth: 2,
      shadowBlur: 18,
      shadowColor: (f.color || '#c9a96e') + '66'
    },
    label: { show: true, color: '#ddd', fontSize: 11, position: 'bottom', distance: 6 }
  }))

  const links: any[] = []
  for (const f of factions) {
    for (const rel of f.relations || []) {
      links.push({
        source: f.name,
        target: rel.targetName,
        lineStyle: {
          color: relationColor[rel.relationType] || '#444',
          curveness: 0.25,
          width: rel.relationType === 'enemy' ? 2 : 1.5
        },
        label: {
          show: true,
          formatter: relationLabel[rel.relationType] || '',
          color: relationColor[rel.relationType] || '#888',
          fontSize: 8
        }
      })
    }
  }

  option.value = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: '#141414ee',
      borderColor: '#333',
      textStyle: { color: '#eee', fontSize: 12 }
    },
    legend: {
      data: ['势力'],
      textStyle: { color: '#666', fontSize: 10 },
      bottom: 10
    },
    animationDuration: 1500,
    animationEasingUpdate: 'quinticInOut',
    series: [{
      type: 'graph',
      layout: 'force',
      roam: true,
      zoom: 0.9,
      draggable: true,
      categories: [
        { name: '势力', itemStyle: { color: '#c9a96e' } }
      ],
      data: nodes,
      links: links,
      force: {
        repulsion: 360,
        gravity: 0.12,
        edgeLength: [60, 200],
        friction: 0.6
      },
      edgeSymbol: ['none', 'arrow'],
      edgeSymbolSize: [0, 6],
      emphasis: {
        focus: 'adjacency',
        lineStyle: { width: 3, shadowBlur: 12, shadowColor: '#c9a96e88' },
        itemStyle: { shadowBlur: 30 }
      }
    }]
  }
})
</script>

<template>
  <div class="pt-6">
    <div class="flex items-center justify-between mb-4">
      <div>
        <h2 class="text-xl font-bold text-xuan-text">势力关系图谱</h2>
        <p class="text-xs text-xuan-muted mt-1">拖拽节点 · 滚轮缩放 · 悬停高亮关联</p>
      </div>
      <div class="flex gap-3 text-[10px] text-xuan-muted">
        <span class="flex items-center gap-1"><span class="w-3 h-0.5 rounded bg-green-500 inline-block"></span>同盟</span>
        <span class="flex items-center gap-1"><span class="w-3 h-0.5 rounded bg-red-500 inline-block"></span>敌对</span>
        <span class="flex items-center gap-1"><span class="w-3 h-0.5 rounded bg-amber-500 inline-block"></span>监控</span>
        <span class="flex items-center gap-1"><span class="w-3 h-0.5 rounded bg-purple-500 inline-block"></span>传承</span>
      </div>
    </div>
    <div class="bg-xuan-card border border-xuan-border rounded-2xl overflow-hidden" style="height: 560px;" v-if="option">
      <v-chart :option="option" autoresize />
    </div>
    <div v-else class="bg-xuan-card border border-xuan-border rounded-2xl overflow-hidden flex items-center justify-center text-xuan-muted text-sm" style="height: 560px;">
      加载中…
    </div>
  </div>
</template>