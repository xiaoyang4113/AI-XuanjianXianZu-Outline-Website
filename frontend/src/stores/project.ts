import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Project, ProjectStats, CategoryWithItems } from '../types'
import { getProjects, getProjectStats } from '../api/project'
import { getCategories } from '../api/category'

export const useProjectStore = defineStore('project', () => {
    const projects = ref<Project[]>([])
    const currentProject = ref<Project | null>(null)
    const stats = ref<ProjectStats | null>(null)
    const categories = ref<CategoryWithItems[]>([])
    const loading = ref(false)

    async function fetchProjects() {
        loading.value = true
        try {
            const res = await getProjects()
            if (res.code === 200) projects.value = res.data
        } finally {
            loading.value = false
        }
    }

    async function fetchStats(projectId: number) {
        const res = await getProjectStats(projectId)
        if (res.code === 200) stats.value = res.data
    }

    async function fetchCategories(projectId: number) {
        loading.value = true
        try {
            const res = await getCategories(projectId)
            if (res.code === 200) categories.value = res.data
        } finally {
            loading.value = false
        }
    }

    return { projects, currentProject, stats, categories, loading, fetchProjects, fetchStats, fetchCategories }
})