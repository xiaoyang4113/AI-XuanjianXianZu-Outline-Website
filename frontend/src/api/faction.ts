import api from './index'

export interface Faction {
  id: number
  name: string
  leaderName: string | null
  highestRealm: string | null
  territory: string | null
  trueGoal: string | null
  description: string | null
  color: string | null
  memberCount: number
  members: { id: number; name: string; realm: string; status: string }[]
  relations: FactionRelation[]
}

export interface FactionRelation {
  targetId: number
  targetName: string
  targetColor: string
  relationType: string
  description: string
  direction: string
}

export function getFactions(projectId: number) {
  return api.get<any, { data: Faction[]; code: number }>('/factions', { params: { projectId } })
}

export function getFactionRelations(projectId: number) {
  return api.get<any, { data: any[]; code: number }>('/factions/relations', { params: { projectId } })
}