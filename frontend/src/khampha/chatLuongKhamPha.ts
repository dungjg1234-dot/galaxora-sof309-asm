const CAC_MUC = [1, .85, .70, .58]
const GIOI_HAN_DIEM_ANH = 2_300_000
const GIOI_HAN_CANH = 4_096

export function taoChatLuongKhamPha(tiLeToiDa: number) {
  let muc = 0
  let chamMs = 0
  let nhanhMs = 0

  return {
    tiLe(rong: number, cao: number, tiLeThietBi: number) {
      const gioiHan = Math.min(tiLeThietBi, tiLeToiDa, GIOI_HAN_CANH / Math.max(rong, cao),
        Math.sqrt(GIOI_HAN_DIEM_ANH / (rong * cao)))
      return gioiHan * CAC_MUC[muc]
    },
    doKhoangKhung(khoangMs: number) {
      chamMs = khoangMs >= 24 ? chamMs + Math.min(khoangMs, 50) : 0
      nhanhMs = khoangMs > 0 && khoangMs <= 19 ? nhanhMs + khoangMs : 0
      // Hạ sớm khi chậm theo chuỗi, tăng chậm để tránh thay đổi liên tục.
      if (chamMs >= 550 && muc < CAC_MUC.length - 1) muc++
      else if (nhanhMs >= 3000 && muc > 0) muc--
      else return false
      chamMs = 0
      nhanhMs = 0
      return true
    },
  }
}
