import api from './index'
import type { Project, ProjectStats } from '../types'

export function getProjects() {
    return api.get<any, { data: Project[]; code: number }>('/projects')
}

export function getProject(id: number) {
    return api.get<any, { data: Project; code: number }>(`/projects/${id}`)
}

export function getProjectStats(id: number) {
    return api.get<any, { data: ProjectStats; code: number }>(`/projects/${id}/stats`)
}