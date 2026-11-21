import type { QuyDao } from './quyDao.ts'

export type MaThienThe = 'sun' | 'mercury' | 'venus' | 'earth' | 'moon' | 'mars'
  | 'jupiter' | 'saturn' | 'uranus' | 'neptune' | 'pluto'

export type ThienThe = {
  ma: MaThienThe
  ten: string
  loai: string
  mau: string
  banKinh: number
  truc: number
  vongXoayMoPhong: number
  quyDao?: QuyDao
  quayQuanh?: MaThienThe
}

function quyDao(banKinh: number, doLechTam: number, doNghieng: number,
  chuKyMoPhong: number, phaDau: number): QuyDao {
  return { banKinh, doLechTam, doNghieng, chuKyMoPhong, phaDau }
}

// Kích thước, pha và nhịp chuyển động được chọn cho mô phỏng trực quan.
export const cacThienThe: readonly ThienThe[] = [
  { ma: 'sun', ten: 'Mặt Trời', loai: 'Ngôi sao', mau: '#ffb457', banKinh: 2.45, truc: 7.25, vongXoayMoPhong: 53 },
  { ma: 'mercury', ten: 'Sao Thủy', loai: 'Hành tinh đá', mau: '#b7a49a', banKinh: .36, truc: .034, vongXoayMoPhong: 42,
    quyDao: quyDao(4.7, .206, 7, 34, .55) },
  { ma: 'venus', ten: 'Sao Kim', loai: 'Hành tinh đá', mau: '#e9b574', banKinh: .59, truc: 177.4, vongXoayMoPhong: 66,
    quyDao: quyDao(7.2, .007, 3.4, 54, 2.5) },
  { ma: 'earth', ten: 'Trái Đất', loai: 'Hành tinh đá', mau: '#66baff', banKinh: .65, truc: 23.4, vongXoayMoPhong: 16,
    quyDao: quyDao(10.1, .017, 0, 72, .05) },
  { ma: 'moon', ten: 'Mặt Trăng', loai: 'Vệ tinh tự nhiên', mau: '#c2c7d1', banKinh: .22, truc: 6.7, vongXoayMoPhong: 24,
    quyDao: quyDao(1.17, .055, 5.1, 24, 1.55), quayQuanh: 'earth' },
  { ma: 'mars', ten: 'Sao Hỏa', loai: 'Hành tinh đá', mau: '#d67b55', banKinh: .46, truc: 25.2, vongXoayMoPhong: 17,
    quyDao: quyDao(13.3, .094, 1.8, 100, 3.35) },
  { ma: 'jupiter', ten: 'Sao Mộc', loai: 'Hành tinh khí khổng lồ', mau: '#dcad88', banKinh: 1.54, truc: 3.1, vongXoayMoPhong: 11,
    quyDao: quyDao(18.8, .049, 1.3, 230, -1.9) },
  { ma: 'saturn', ten: 'Sao Thổ', loai: 'Hành tinh khí khổng lồ', mau: '#dfbf8d', banKinh: 1.31, truc: 26.7, vongXoayMoPhong: 12,
    quyDao: quyDao(25, .052, 2.5, 340, .36) },
  { ma: 'uranus', ten: 'Sao Thiên Vương', loai: 'Hành tinh băng khổng lồ', mau: '#98d7de', banKinh: .91, truc: 97.8, vongXoayMoPhong: 14,
    quyDao: quyDao(31, .047, .8, 490, 2.93) },
  { ma: 'neptune', ten: 'Sao Hải Vương', loai: 'Hành tinh băng khổng lồ', mau: '#6696de', banKinh: .88, truc: 28.3, vongXoayMoPhong: 14,
    quyDao: quyDao(37.5, .01, 1.8, 650, 5.14) },
  { ma: 'pluto', ten: 'Pluto', loai: 'Hành tinh lùn', mau: '#c5a58d', banKinh: .28, truc: 119.5, vongXoayMoPhong: 28,
    quyDao: quyDao(46, .244, 17.2, 810, 2.3) },
]

export const theoMa = Object.fromEntries(
  cacThienThe.map(thienThe => [thienThe.ma, thienThe]),
) as Record<MaThienThe, ThienThe>
