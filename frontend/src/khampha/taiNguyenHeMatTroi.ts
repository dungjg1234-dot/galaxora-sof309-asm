import { timMoHinhKhamPha } from './taiMoHinhKhamPha.ts'
import type { MaThienThe } from './thienThe.ts'
import ghiCongGoc from '../../../assets/3d/solar-system/ghi-cong.json'

const cacAnh = import.meta.glob<string>('../../../assets/3d/solar-system/*/be-mat.webp', {
  eager: true, query: '?url&no-inline', import: 'default',
})
const thuMuc = '../../../assets/3d/solar-system/'

export function anhBeMat(ma: MaThienThe) {
  return cacAnh[thuMuc + ma + '/be-mat.webp']
}

export function moHinhChiTiet(ma: MaThienThe) {
  return timMoHinhKhamPha('solar-system/' + ma)
}

export const ghiCong = ghiCongGoc
export const giayPhep = 'https://creativecommons.org/licenses/by/4.0/'
export const ghiCongBanDo = {
  title: 'Solar System animation',
  author: 'Samer_Arab_S5',
  source: 'https://sketchfab.com/3d-models/solar-system-animation-b7c69a6b655b47c99f871d5ec5aee854',
}
