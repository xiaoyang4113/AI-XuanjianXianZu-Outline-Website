import api from './index'
export function login(username: string, password: string) {
  return api.post<any, any>('/auth/login', { username, password })
}
export function register(username: string, password: string, email: string) {
  return api.post<any, any>('/auth/register', { username, password, email })
}