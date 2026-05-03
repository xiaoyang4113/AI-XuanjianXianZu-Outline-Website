import api from './index';
export function generateAI(prompt: string) {
  return api.post<any, { data: { content: string }; code: number }>('/ai/generate', { prompt })
}