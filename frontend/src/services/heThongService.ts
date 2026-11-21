import { goiApi } from './apiClient.ts'

export interface TrangThaiBackend {
  trangThai: string
}

export function layTrangThaiBackend(tinHieu?: AbortSignal): Promise<TrangThaiBackend> {
  return goiApi<TrangThaiBackend>('/api/he-thong/suc-khoe', { signal: tinHieu })
}
