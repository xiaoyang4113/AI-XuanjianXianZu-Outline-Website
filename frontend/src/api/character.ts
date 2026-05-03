import api from './index'

export interface Character {
  id: number
  name: string
  title: string | null
  realm: string | null
  daoTradition: string | null
  factionId: number | null
  factionName: string | null
  factionColor: string | null
  status: string
  lineage: string | null
  branch: string | null
  causeOfDeath: string | null
  description: string
  notableEvents: string | null
  spouse: string | null
  isKeyFigure: boolean
  relations: CharacterRelation[]
}

export interface CharacterRelation {
  targetId: number
  targetName: string
  targetTitle: string | null
  targetStatus: string | null
  relationType: string
  description: string
}

export function getCharacters(projectId: number, status?: string, lineage?: string) {
  const params: any = { projectId }
  if (status) params.status = status
  if (lineage) params.lineage = lineage
  return api.get<any, { data: Character[]; code: number }>('/characters', { params })
}