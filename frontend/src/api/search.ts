import api from './index'
import type { SearchResult } from '../types'

export function searchItems(projectId: number, keyword: string) {
    return api.get<any, { data: SearchResult[]; code: number }>('/search', {
        params: { projectId, q: keyword }
    })
}