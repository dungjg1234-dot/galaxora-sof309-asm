export function taoLichVeCanh(canvas: HTMLCanvasElement, ve: (luc: number, delta: number, khoangMs: number) => boolean) {
  let khung: number | undefined
  let lucTruoc: number | undefined
  let xepLichLuc = 0
  let veXongLuc = 0
  let lapLai = false
  let trongTamNhin = false
  let daDong = false

  function yeuCauVe() {
    if (!daDong && trongTamNhin && !document.hidden && khung === undefined) {
      xepLichLuc = performance.now()
      khung = requestAnimationFrame(phat)
    }
  }

  function phat(luc: number) {
    const lienTiep = lapLai || xepLichLuc - veXongLuc <= 32
    const khoangMs = lucTruoc !== undefined && lienTiep ? Math.max(0, luc - lucTruoc) : 0
    const delta = lapLai ? Math.min(khoangMs / 1000, .05) : 0
    // Giữ mã khung trong lúc vẽ để gộp các sự kiện change do camera phát sinh.
    lapLai = ve(luc, delta, khoangMs)
    khung = undefined
    lucTruoc = luc
    veXongLuc = performance.now()
    if (lapLai) yeuCauVe()
  }

  function dungKhung() {
    if (khung !== undefined) cancelAnimationFrame(khung)
    khung = undefined
    lucTruoc = undefined
    lapLai = false
  }

  function capNhatTamNhin() {
    if (document.hidden || !trongTamNhin) dungKhung()
    else yeuCauVe()
  }

  const quanSat = new IntersectionObserver(([muc]) => {
    trongTamNhin = muc.isIntersecting
    capNhatTamNhin()
  })
  quanSat.observe(canvas)
  document.addEventListener('visibilitychange', capNhatTamNhin)

  return {
    yeuCauVe,
    dong() {
      daDong = true
      dungKhung()
      quanSat.disconnect()
      document.removeEventListener('visibilitychange', capNhatTamNhin)
    },
  }
}
