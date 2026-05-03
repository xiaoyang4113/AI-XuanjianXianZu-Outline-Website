<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useProjectStore } from '../stores/project'

const router = useRouter()
const store = useProjectStore()
const loaded = ref(false)

onMounted(async () => {
  await store.fetchProjects()
  setTimeout(() => { loaded.value = true }, 120)
})
</script>

<template>
  <div class="pt-12 pb-20">
    <!-- Hero -->
    <div class="text-center mb-24 relative">
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 
                  rounded-full bg-amber-800/4 blur-3xl pointer-events-none"></div>
      <div class="absolute top-1/3 left-1/4 w-72 h-72 rounded-full bg-stone-600/3 blur-3xl pointer-events-none"
           style="animation: smoke1 22s ease-in-out infinite 3s;"></div>

      <div class="relative z-10">
        <Transition name="hero-item">
          <div v-if="loaded" class="text-8xl mb-6 select-none" style="opacity: 0.15;">☯</div>
        </Transition>
        <Transition name="hero-item">
          <h1 v-if="loaded" class="text-5xl font-bold mb-3 tracking-wide">
            <span class="text-xuan-gold">玄鉴</span>
            <span class="text-xuan-text/75">仙族</span>
          </h1>
        </Transition>
        <Transition name="hero-item">
          <p v-if="loaded" class="text-xuan-muted/45 text-sm max-w-lg mx-auto leading-relaxed mb-10">
            道统图鉴 · 五十九果位 · 一百九十余神通 · 完整修仙体系
          </p>
        </Transition>
        <Transition name="hero-item">
          <div v-if="loaded" class="flex justify-center gap-5 flex-wrap">
            <router-link :to="{ name: 'browse', params: { id: 1 } }"
               class="px-7 py-3.5 rounded-xl bg-xuan-gold/8 text-xuan-gold text-sm font-medium
                      border border-xuan-gold/20 hover:bg-xuan-gold/15 hover:scale-105 hover:shadow-lg hover:shadow-xuan-gold/10
                      transition-all duration-500 btn-candle">
              进入道统图鉴 →
            </router-link>
            <router-link :to="{ name: 'realms', params: { id: 1 } }"
               class="px-7 py-3.5 rounded-xl bg-white/[0.02] text-xuan-muted text-sm 
                      border border-white/[0.04] hover:text-xuan-text hover:border-xuan-gold/15 
                      hover:scale-105 transition-all duration-500">
              修炼体系
            </router-link>
          </div>
        </Transition>
      </div>
    </div>

    <div v-if="store.loading" class="text-center text-xuan-muted py-10">加载中…</div>

    <div v-else class="grid gap-5 max-w-xl mx-auto">
      <TransitionGroup name="list">
        <div v-for="project in store.projects" :key="project.id"
             class="glass-card rounded-2xl p-6 cursor-pointer group"
             @click="router.push({ name: 'browse', params: { id: project.id } })">
          <div class="flex items-start justify-between">
            <div>
              <h2 class="text-xl font-bold text-xuan-text group-hover:text-xuan-gold transition-colors duration-300">
                {{ project.name }}
              </h2>
              <p class="text-sm text-xuan-muted mt-1">{{ project.author }} · {{ project.genre }}</p>
            </div>
            <div class="text-xuan-gold/20 group-hover:text-xuan-gold/50 group-hover:translate-x-1.5 transition-all duration-500 text-2xl">
              →
            </div>
          </div>
          <p v-if="project.description" class="text-xs text-xuan-muted/40 mt-3 line-clamp-2">
            {{ project.description }}
          </p>
        </div>
      </TransitionGroup>
    </div>
  </div>
</template>

<style scoped>
.hero-item-enter-active { transition: all 0.7s cubic-bezier(0.16, 1, 0.3, 1); }
.hero-item-leave-active { transition: all 0.3s ease; }
.hero-item-enter-from { opacity: 0; transform: translateY(24px); filter: blur(6px); }

.list-enter-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.list-leave-active { transition: all 0.3s ease; }
.list-enter-from { opacity: 0; transform: translateY(40px); }
.list-leave-to { opacity: 0; transform: translateX(-30px); }
.list-move { transition: transform 0.5s ease; }
</style>