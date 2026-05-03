import api from './index'
import type { CategoryWithItems } from '../types'

export function getCategories(projectId: number) {
    return api.get<any, { data: CategoryWithItems[]; code: number }>('/categories', {
        params: { projectId }
    })
}