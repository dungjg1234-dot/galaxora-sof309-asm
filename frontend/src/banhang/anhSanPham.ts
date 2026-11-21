import boAnh from '../trangchu/anhSanPham.json'

export type AnhThuVien = typeof boAnh[number]
export const thuVienAnh = boAnh
const tienTo = '/anh-san-pham/'

export function chuanHoaAnh(ten: string) {
  return ten.toLocaleLowerCase('vi').normalize('NFD').replace(/\p{M}/gu, '').replace(/đ/g, 'd')
}

export function duongDanAnh(anh: AnhThuVien, anhNho = false) {
  return `${tienTo}${anh.nhom}/${anh.ma}${anhNho ? '-480' : ''}.webp`
}

const anhTheoMa = new Map(boAnh.map(anh => [anh.ma, anh]))
const anhTheoDuongDan = new Map(boAnh.map(anh => [duongDanAnh(anh), anh]))

export function timAnhSanPham({ anh, ten, sku }: { anh?: string | null; ten: string; sku: string }) {
  if (anh && (!anh.startsWith(tienTo) || anhTheoDuongDan.has(anh))) return anh
  const maTen = chuanHoaAnh(ten).replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '')
  const muc = anhTheoMa.get(sku.toLowerCase()) ?? anhTheoMa.get(maTen)
  return muc ? duongDanAnh(muc) : null
}

export function anhKichThuocNho(duongDan: string) {
  const anh = anhTheoDuongDan.get(duongDan)
  return anh ? duongDanAnh(anh, true) : duongDan
}
