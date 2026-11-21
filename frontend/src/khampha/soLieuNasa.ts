import type { MaThienThe } from './thienThe.ts'

export const bangSoLieuNasa = 'https://nssdc.gsfc.nasa.gov/planetary/factsheet/'

type SoLieu = {
  duongKinhKm: number
  khoangCachTrieuKm?: number
  quyDaoNgay?: number
  tuQuayGio: number
  tuQuayNguoc?: boolean
  ghiChu: string
  nguon?: string
}

export const soLieuNasa: Record<MaThienThe, SoLieu> = {
  sun: { duongKinhKm: 1400000, tuQuayGio: 600, ghiChu: 'Ngôi sao ở trung tâm Hệ Mặt Trời. Chu kỳ tự quay ở đây tính tại xích đạo.',
    nguon: 'https://science.nasa.gov/sun/facts/' },
  mercury: { duongKinhKm: 4879, khoangCachTrieuKm: 57.9, quyDaoNgay: 88, tuQuayGio: 1407.6,
    ghiChu: 'Hành tinh nhỏ nhất và gần Mặt Trời nhất.' },
  venus: { duongKinhKm: 12104, khoangCachTrieuKm: 108.2, quyDaoNgay: 224.7, tuQuayGio: 5832.5, tuQuayNguoc: true,
    ghiChu: 'Sao Kim tự quay ngược chiều so với phần lớn các hành tinh.' },
  earth: { duongKinhKm: 12756, khoangCachTrieuKm: 149.6, quyDaoNgay: 365.2, tuQuayGio: 23.9,
    ghiChu: 'Hành tinh thứ ba tính từ Mặt Trời. Mặt Trăng là vệ tinh tự nhiên của Trái Đất.' },
  moon: { duongKinhKm: 3475, khoangCachTrieuKm: .3844, quyDaoNgay: 27.3, tuQuayGio: 655.7,
    ghiChu: 'Mặt Trăng quay quanh Trái Đất và gần như luôn hướng cùng một mặt về phía chúng ta.',
    nguon: 'https://science.nasa.gov/moon/facts/' },
  mars: { duongKinhKm: 6792, khoangCachTrieuKm: 228, quyDaoNgay: 687, tuQuayGio: 24.6,
    ghiChu: 'Hành tinh thứ tư, có bề mặt mang sắc đỏ đặc trưng.' },
  jupiter: { duongKinhKm: 142984, khoangCachTrieuKm: 778.5, quyDaoNgay: 4331, tuQuayGio: 9.9,
    ghiChu: 'Sao Mộc là hành tinh lớn nhất trong Hệ Mặt Trời.' },
  saturn: { duongKinhKm: 120536, khoangCachTrieuKm: 1432, quyDaoNgay: 10747, tuQuayGio: 10.7,
    ghiChu: 'Sao Thổ nổi bật với hệ vành đai chứa nhiều hạt băng.' },
  uranus: { duongKinhKm: 51118, khoangCachTrieuKm: 2867, quyDaoNgay: 30589, tuQuayGio: 17.2, tuQuayNguoc: true,
    ghiChu: 'Trục tự quay của Sao Thiên Vương nghiêng gần như nằm ngang.' },
  neptune: { duongKinhKm: 49528, khoangCachTrieuKm: 4515, quyDaoNgay: 59800, tuQuayGio: 16.1,
    ghiChu: 'Sao Hải Vương là hành tinh thứ tám và xa Mặt Trời nhất trong số tám hành tinh.' },
  pluto: { duongKinhKm: 2376, khoangCachTrieuKm: 5906.4, quyDaoNgay: 90560, tuQuayGio: 153.3, tuQuayNguoc: true,
    ghiChu: 'Pluto là một hành tinh lùn thuộc vành đai Kuiper.',
    nguon: 'https://science.nasa.gov/dwarf-planets/pluto/facts/' },
}
