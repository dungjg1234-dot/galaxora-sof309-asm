import { useEffect, useRef, type ReactNode } from 'react'
import ChanTrang from './ChanTrang.tsx'
import type { MucDieuHuong } from './ThanhDauTrang.tsx'

export default function KhungTrang({ lop, lopNoiDung, thanhDau, hienChanTrang = true, chanTrangGon, muc, children }: {
  lop?: string
  lopNoiDung?: string
  thanhDau: ReactNode
  hienChanTrang?: boolean
  chanTrangGon?: boolean
  muc?: MucDieuHuong
  children: ReactNode
}) {
  const noiDung = useRef<HTMLElement>(null)
  useEffect(() => {
    const vung = noiDung.current
    if (!vung || lop === 'gx-khung-quan-tri' || !('IntersectionObserver' in window)) return
    const giamChuyenDong = window.matchMedia('(prefers-reduced-motion: reduce)')
    if (giamChuyenDong.matches) return
    const quanSat = new IntersectionObserver((muc) => {
      for (const the of muc) {
        if (!the.isIntersecting) continue
        quanSat.unobserve(the.target)
        if (!giamChuyenDong.matches) the.target.setAttribute('data-gx-hien', '')
      }
    }, { threshold: .07 })
    vung.querySelectorAll('.gx-cac-khu, .gx-tong-quan, .bh-luoi-sp, .bh-khoi, .tk-ho-so-luoi, .gx-tien-ich-noi-dung')
      .forEach(the => quanSat.observe(the))
    return () => quanSat.disconnect()
  }, [lop, lopNoiDung])

  return <div className={lop ? `gx-khung-trang ${lop}` : 'gx-khung-trang'} id="gx-dau-trang" tabIndex={-1}>
    <div className="gx-vu-tru" aria-hidden="true" />
    {thanhDau}
    <main className={lopNoiDung} ref={noiDung}>{children}</main>
    {hienChanTrang && <ChanTrang muc={muc} gon={chanTrangGon} />}
  </div>
}
