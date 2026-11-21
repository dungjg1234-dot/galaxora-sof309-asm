import { Vector3 } from 'three'

export type QuyDao = {
  banKinh: number
  doLechTam: number
  doNghieng: number
  chuKyMoPhong: number
  phaDau: number
}

const VONG = Math.PI * 2

// Dùng dị thường trung bình để thiên thể đi nhanh hơn khi ở gần vật thể trung tâm.
export function viTriQuyDao(quyDao: QuyDao, pha: number, diem: Vector3): Vector3 {
  const trungBinh = (pha % VONG + VONG) % VONG
  let tamSai = trungBinh
  for (let i = 0; i < 5; i++) {
    tamSai -= (tamSai - quyDao.doLechTam * Math.sin(tamSai) - trungBinh)
      / (1 - quyDao.doLechTam * Math.cos(tamSai))
  }
  const x = quyDao.banKinh * (Math.cos(tamSai) - quyDao.doLechTam)
  const z = quyDao.banKinh * Math.sqrt(1 - quyDao.doLechTam ** 2) * Math.sin(tamSai)
  const nghieng = quyDao.doNghieng * Math.PI / 180
  return diem.set(x, -z * Math.sin(nghieng), z * Math.cos(nghieng))
}

export function phaQuyDao(quyDao: QuyDao, thoiGian: number) {
  return quyDao.phaDau + VONG * thoiGian / quyDao.chuKyMoPhong
}
