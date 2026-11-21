import { goiApi } from './apiClient.ts'

export type HoSoKhamPha = {
  tongXp: number
  capDo: { cap: number; mocCapHienTai: number; mocCapTiepTheo: number }
  lichSuXp: { nguon: string; maNoiDung: string; soXp: number; ghiNhanLuc: string }[]
  phanThuong: { ma: string; loai: 'HUY_HIEU' | 'VOUCHER'; maVoucher: string | null; nhanLuc: string; tenHuyHieu: string | null }[]
}

export function layHoSoKhamPha(signal: AbortSignal) {
  return goiApi<HoSoKhamPha>('/api/nhiem-vu/ho-so', { signal, cache: 'no-store' })
}
