export const duongDanTaiKhoan = {
  dangNhap: '/tai-khoan/dang-nhap',
  dangKy: '/tai-khoan/dang-ky',
  phien: '/tai-khoan/phien',
} as const

export type TrangTaiKhoan = keyof typeof duongDanTaiKhoan

export function duongDanDangNhap(troVe: string) {
  return duongDanTaiKhoan.dangNhap + '?' + new URLSearchParams({ troVe })
}

export function noiQuayVeSauDangNhap(timKiem: string) {
  const troVe = new URLSearchParams(timKiem).get('troVe')
  if (troVe === '/#san-pham' || troVe === '/cua-hang' || troVe === '/gio-hang') return troVe
  return troVe && /^\/cua-hang\/san-pham\/[1-9]\d*$/.test(troVe) ? troVe : '/'
}

export function timTrangTaiKhoan(duongDan: string): TrangTaiKhoan | undefined {
  const chuanHoa = duongDan.replace(/\/$/, '')
  return (Object.keys(duongDanTaiKhoan) as TrangTaiKhoan[])
    .find((trang) => duongDanTaiKhoan[trang] === chuanHoa)
}
