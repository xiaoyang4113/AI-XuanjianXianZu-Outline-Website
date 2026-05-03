<script setup lang="ts">
defineProps<{
  value: number | string
  label: string
  color?: string
}>()
</script>

<template>
  <div class="bg-xuan-card border border-xuan-border rounded-xl p-5 text-center
              hover:border-xuan-gold/20 transition-all duration-300">
    <div class="text-3xl font-bold mb-1" :class="color || 'text-xuan-gold'">
      {{ value }}
    </div>
    <div class="text-xs text-xuan-muted tracking-wider">{{ label }}</div>
  </div>
</template>