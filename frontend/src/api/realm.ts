import api from './index'

export interface Realm {
  id: number
  name: string
  title: string
  lifespan: string
  description: string
  promotionCondition: string
  isCriticalPoint: boolean
  sortOrder: number
  stages: RealmStage[]
}

export interface RealmStage {
  id: number
  name: string
  description: string
  isCritical: boolean
}

export interface CultivationPath {
  id: number
  name: string
  feature: string
  weakness: string
  breakthroughMethod: string
  representative: string
  icon: string
  color: string
}

export interface PathStage {
  realmId: number
  realmName: string
  realmSort: number
  description: string
}

export function getRealms(projectId: number) {
  return api.get<any, { data: Realm[]; code: number }>('/realms', { params: { projectId } })
}

export function getPaths(projectId: number) {
  return api.get<any, { data: CultivationPath[]; code: number }>('/realms/paths', { params: { projectId } })
}

export function getPathStages(pathId: number) {
  return api.get<any, { data: PathStage[]; code: number }>(`/realms/paths/${pathId}/stages`)
}