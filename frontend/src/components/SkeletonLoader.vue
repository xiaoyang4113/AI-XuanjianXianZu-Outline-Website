<script setup lang="ts">
defineProps<{
  rows?: number
  type?: 'card' | 'list' | 'detail'
}>()
</script>

<template>
  <!-- 卡片骨架 -->
  <div v-if="type === 'card'" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
    <div v-for="i in (rows || 6)" :key="i" class="rounded-2xl p-5 space-y-3">
      <div class="flex items-center gap-3">
        <div class="skeleton w-14 h-14 rounded-2xl flex-shrink-0"></div>
        <div class="flex-1 space-y-2">
          <div class="skeleton h-4 w-24 rounded"></div>
          <div class="skeleton h-3 w-16 rounded opacity-50"></div>
        </div>
      </div>
      <div class="flex gap-2">
        <div class="skeleton h-5 w-14 rounded-md"></div>
        <div class="skeleton h-5 w-18 rounded-md"></div>
      </div>
      <div class="skeleton h-3 w-full rounded opacity-30"></div>
    </div>
  </div>

  <!-- 列表骨架 -->
  <div v-else-if="type === 'list'" class="space-y-3">
    <div v-for="i in (rows || 5)" :key="i" class="flex items-center gap-3 p-3 rounded-xl">
      <div class="skeleton w-10 h-10 rounded-xl flex-shrink-0"></div>
      <div class="flex-1 space-y-2">
        <div class="skeleton h-4 w-32 rounded"></div>
        <div class="skeleton h-3 w-48 rounded opacity-40"></div>
      </div>
    </div>
  </div>

  <!-- 详情骨架 -->
  <div v-else class="space-y-4">
    <div class="skeleton h-8 w-40 rounded mb-4"></div>
    <div class="skeleton h-20 w-full rounded-2xl"></div>
    <div class="space-y-2">
      <div class="skeleton h-4 w-full rounded"></div>
      <div class="skeleton h-4 w-5/6 rounded"></div>
      <div class="skeleton h-4 w-4/6 rounded"></div>
    </div>
  </div>
</template>