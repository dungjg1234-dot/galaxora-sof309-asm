import { lazy, Suspense, useEffect, useRef, useState, type MouseEvent } from 'react'
import { cacKhuVucVuTru, laKhuVucVuTru } from './khuVucVuTru.ts'
import './khamPha.css'
import ThanhDauTrang from '../giaodien/ThanhDauTrang.tsx'

const HeMatTroi = lazy(() => import('./TrangKhamPha.tsx'))
const KhuVucVuTru = lazy(() => import('./TrangKhuVucVuTru.tsx'))
const DUONG_DAN = '/kham-pha'
const cacKhuVuc = [
  { ma: '', ten: 'Hệ Mặt Trời' },
  ...Object.keys(cacKhuVucVuTru).filter(laKhuVucVuTru).map(ma => ({ ma, ten: cacKhuVucVuTru[ma].ten })),
]

function docKhuVuc() {
  return window.location.pathname.split('/')[2] ?? ''
}

export default function KhungKhamPha() {
  const [khuVuc, datKhuVuc] = useState(docKhuVuc)
  const [toanManHinh, datToanManHinh] = useState(false)
  const [loi, datLoi] = useState('')
  const khung = useRef<HTMLDivElement>(null)
  const noiDung = useRef<HTMLElement>(null)

  useEffect(() => {
    const capNhatKhuVuc = () => datKhuVuc(docKhuVuc())
    const capNhatManHinh = () => datToanManHinh(document.fullscreenElement === khung.current)
    window.addEventListener('popstate', capNhatKhuVuc)
    document.addEventListener('fullscreenchange', capNhatManHinh)
    return () => {
      window.removeEventListener('popstate', capNhatKhuVuc)
      document.removeEventListener('fullscreenchange', capNhatManHinh)
    }
  }, [])

  function chuyen(suKien: MouseEvent<HTMLAnchorElement>, ma: string) {
    if (suKien.button || suKien.ctrlKey || suKien.metaKey || suKien.shiftKey || suKien.altKey) return
    suKien.preventDefault()
    if (ma === khuVuc) return
    window.history.pushState(null, '', suKien.currentTarget.href)
    datKhuVuc(ma)
    noiDung.current?.focus({ preventScroll: true })
  }

  async function doiManHinh() {
    datLoi('')
    try {
      if (document.fullscreenElement === khung.current) await document.exitFullscreen()
      else await khung.current?.requestFullscreen()
    } catch {
      datLoi('Trình duyệt chưa cho phép ẩn thanh công cụ.')
    }
  }

  return <div className="kp gx-khung-trang" ref={khung}>
    <main className="kp-than" ref={noiDung} tabIndex={-1} aria-label="Khám phá vũ trụ 3D">
      <div key={khuVuc} className="kp-chuyen-canh">
        <Suspense fallback={<p className="kp-dang-tai" role="status">Đang mở khu vực…</p>}>
          {laKhuVucVuTru(khuVuc) ? <KhuVucVuTru khuVuc={khuVuc} /> : <HeMatTroi />}
        </Suspense>
      </div>
    </main>
    <ThanhDauTrang kieu="kham-pha" muc="/kham-pha" tuyChon={
      document.fullscreenEnabled && <button type="button" className="gx-nut gx-nut-phu kp-toan-man-hinh"
        aria-pressed={toanManHinh} onClick={() => void doiManHinh()}>
        <span aria-hidden="true">⛶</span> {toanManHinh ? 'Thoát toàn màn hình' : 'Toàn màn hình'}
      </button>} />
    {loi && <p className="kp-loi-man-hinh" role="status">{loi}</p>}
    <nav className="kp-khu-vuc" aria-label="Khu vực khám phá">
      {cacKhuVuc.map(({ ma, ten }) => <a key={ma} href={DUONG_DAN + (ma ? '/' + ma : '')}
        aria-current={khuVuc === ma ? 'page' : undefined} onClick={e => chuyen(e, ma)}>
        <span className={'kp-bieu-tuong kp-bieu-tuong-' + (ma || 'he-mat-troi')} aria-hidden="true" />
        <span>{ten}</span>
      </a>)}
    </nav>
  </div>
}
