<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from './components/AppHeader.vue'

const router = useRouter()
const canvasRef = ref<HTMLCanvasElement | null>(null)
const transitionName = ref('page-ink')
const isTransitioning = ref(false)

// ==================== 过渡动画监听 ====================
router.beforeEach((to, from, next) => {
  if (from && from.path !== to.path) {
    isTransitioning.value = true
  }
  next()
})
router.afterEach(() => {
  setTimeout(() => { isTransitioning.value = false }, 600)
})

// ==================== 花瓣粒子 ====================
interface Petal { x: number; y: number; vx: number; vy: number; size: number; rotation: number; rotationSpeed: number; opacity: number; color: string; life: number }
let petals: Petal[] = []
let goldDusts: { x: number; y: number; size: number; opacity: number; id: number; speed: number; offset: number }[] = []
let animId = 0
let ctx: CanvasRenderingContext2D | null = null
let mouseX = -1000; let mouseY = -1000

function createPetals(count: number) {
  const colors = ['#e8c84a55', '#c9a96e44', '#d4a84b33', '#f0e6c022', '#e8d5a330']
  for (let i = 0; i < count; i++) {
    petals.push({
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight - window.innerHeight,
      vx: (Math.random() - 0.5) * 0.4,
      vy: 0.3 + Math.random() * 1.0,
      size: 3 + Math.random() * 6,
      rotation: Math.random() * Math.PI * 2,
      rotationSpeed: (Math.random() - 0.5) * 0.015,
      opacity: 0.12 + Math.random() * 0.25,
      color: colors[Math.floor(Math.random() * colors.length)],
      life: 300 + Math.random() * 600
    })
  }
}

function createGoldDusts(count: number) {
  for (let i = 0; i < count; i++) {
    goldDusts.push({
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      size: 1 + Math.random() * 2.5,
      opacity: 0,
      id: i,
      speed: 0.3 + Math.random() * 1.5,
      offset: Math.random() * Math.PI * 2
    })
  }
}

function drawPetal(ctx: CanvasRenderingContext2D, p: Petal) {
  ctx.save()
  ctx.translate(p.x, p.y)
  ctx.rotate(p.rotation)
  ctx.globalAlpha = p.opacity
  ctx.fillStyle = p.color
  ctx.beginPath()
  ctx.moveTo(0, -p.size * 0.6)
  ctx.bezierCurveTo(p.size * 0.4, -p.size * 0.3, p.size * 0.5, 0, 0, p.size * 0.7)
  ctx.bezierCurveTo(-p.size * 0.5, 0, -p.size * 0.4, -p.size * 0.3, 0, -p.size * 0.6)
  ctx.fill()
  ctx.restore()
}

function drawGoldDust(ctx: CanvasRenderingContext2D, g: typeof goldDusts[0], time: number) {
  const cycle = time * 0.001 * g.speed + g.offset
  g.opacity = 0.15 + Math.sin(cycle) * 0.15
  g.y -= g.speed * 0.15
  if (g.y < -10) { g.y = window.innerHeight + 10; g.x = Math.random() * window.innerWidth }
  
  ctx.beginPath()
  ctx.arc(g.x, g.y, g.size, 0, Math.PI * 2)
  ctx.fillStyle = `rgba(201,169,110,${g.opacity})`
  ctx.fill()
  // 外层光晕
  ctx.beginPath()
  ctx.arc(g.x, g.y, g.size * 3, 0, Math.PI * 2)
  ctx.fillStyle = `rgba(201,169,110,${g.opacity * 0.15})`
  ctx.fill()
}

// 涟漪
interface Ripple { x: number; y: number; r: number; opacity: number }
let ripples: Ripple[] = []

function handleClick(e: MouseEvent) {
  if (!ctx) return
  ripples.push({ x: e.clientX, y: e.clientY, r: 0, opacity: 0.45 })
}

function frame(time: number) {
  if (!ctx) return
  ctx.clearRect(0, 0, window.innerWidth, window.innerHeight)

  // 花瓣
  for (const p of petals) {
    const dx = p.x - mouseX; const dy = p.y - mouseY
    const dist = Math.sqrt(dx * dx + dy * dy)
    if (dist < 120) { p.vx += (dx / dist) * 0.12; p.vy += (dy / dist) * 0.12 }
    p.vx *= 0.999; p.vy *= 0.999
    p.x += p.vx + (Math.random() - 0.5) * 0.15
    p.y += p.vy
    p.rotation += p.rotationSpeed
    if (p.y > window.innerHeight + 20) { p.y = -20; p.x = Math.random() * window.innerWidth }
    if (p.x < -20) p.x = window.innerWidth + 20
    if (p.x > window.innerWidth + 20) p.x = -20
    drawPetal(ctx, p)
  }

  // 金粉
  for (const g of goldDusts) {
    drawGoldDust(ctx, g, time)
  }

  // 涟漪
  for (let i = ripples.length - 1; i >= 0; i--) {
    const rp = ripples[i]
    rp.r += 1.8
    rp.opacity -= 0.006
    ctx.beginPath()
    ctx.arc(rp.x, rp.y, rp.r, 0, Math.PI * 2)
    ctx.strokeStyle = `rgba(201,169,110,${rp.opacity})`
    ctx.lineWidth = 1.2
    ctx.stroke()
    if (rp.opacity <= 0) ripples.splice(i, 1)
  }

  animId = requestAnimationFrame(frame)
}

function onMouseMove(e: MouseEvent) { mouseX = e.clientX; mouseY = e.clientY }

function handleResize() {
  if (canvasRef.value) {
    canvasRef.value.width = window.innerWidth
    canvasRef.value.height = window.innerHeight
  }
}

onMounted(() => {
  if (canvasRef.value) {
    canvasRef.value.width = window.innerWidth
    canvasRef.value.height = window.innerHeight
    ctx = canvasRef.value.getContext('2d')
    createPetals(22)
    createGoldDusts(35)
    requestAnimationFrame(frame)
  }
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('click', handleClick)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  cancelAnimationFrame(animId)
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('click', handleClick)
  window.removeEventListener('resize', handleResize)
})
</script>

<template>
  <div class="min-h-screen bg-[#060504] relative">
    <!-- 噪点纹理层 -->
    <div class="noise-overlay"></div>

    <!-- Canvas：花瓣 + 金粉 + 涟漪 -->
    <canvas ref="canvasRef" class="fixed inset-0 z-0 pointer-events-none" />

    <!-- 烟雾背景 -->
    <div class="fixed inset-0 z-0 pointer-events-none overflow-hidden opacity-25">
      <div class="absolute w-[500px] h-[500px] rounded-full bg-amber-900/8 blur-3xl"
           style="animation: smoke1 22s ease-in-out infinite; left: 5%; top: 55%;"></div>
      <div class="absolute w-[400px] h-[400px] rounded-full bg-stone-700/6 blur-3xl"
           style="animation: smoke2 26s ease-in-out infinite 4s; left: 65%; top: 35%;"></div>
      <div class="absolute w-[350px] h-[350px] rounded-full bg-amber-800/5 blur-3xl"
           style="animation: smoke3 24s ease-in-out infinite 8s; left: 30%; top: 70%;"></div>
    </div>

    <!-- 主体内容 -->
    <div class="relative z-10">
      <AppHeader />
      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-20">
        <router-view v-slot="{ Component }">
          <transition name="page-ink" mode="out-in">
            <component :is="Component" :key="$route.fullPath" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style>
/* ============================================
   页面过渡 —— 墨滴晕染扩散
   ============================================ */
.page-ink-enter-active {
  animation: pageIn 0.55s cubic-bezier(0.22, 1, 0.36, 1) forwards;
}
.page-ink-leave-active {
  animation: pageOut 0.35s cubic-bezier(0.55, 0, 1, 0.45) forwards;
}

@keyframes pageIn {
  0% {
    clip-path: circle(0% at 50% 40%);
    opacity: 0;
    filter: brightness(0.4) blur(8px);
  }
  60% {
    clip-path: circle(120% at 50% 40%);
    opacity: 1;
    filter: brightness(1.05) blur(0);
  }
  100% {
    clip-path: circle(200% at 50% 40%);
    opacity: 1;
    filter: brightness(1) blur(0);
  }
}

@keyframes pageOut {
  0% {
    opacity: 1;
    filter: blur(0);
    transform: scale(1);
  }
  100% {
    opacity: 0;
    filter: blur(6px) brightness(0.3);
    transform: scale(0.97);
  }
}
</style>