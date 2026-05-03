import api from './index'
import type { ItemDetail } from '../types'

export function getItemDetail(id: number) {
    return api.get<any, { data: ItemDetail; code: number }>(`/items/${id}`)
}