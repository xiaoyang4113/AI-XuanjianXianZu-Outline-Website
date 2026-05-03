import api from './index';

export interface Technique {
  id: number; name: string; grade: string | null; category: string | null;
  relatedFoundation: string | null; relatedAbility: string | null;
  knownUsers: string | null; description: string | null;
}
export interface Artifact {
  id: number; name: string; type: string | null; creator: string | null;
  effect: string | null; relatedCharacters: string | null; description: string | null;
}
export function getTechniques(projectId: number) {
  return api.get<any, { data: Technique[]; code: number }>('/techniques', { params: { projectId } })
}
export function getArtifacts(projectId: number) {
  return api.get<any, { data: Artifact[]; code: number }>('/artifacts', { params: { projectId } })
}