import { lazy, Suspense } from 'react'
import TrangKiemTraHeThong from '../pages/TrangKiemTraHeThong.tsx'
import TrangChu from '../pages/TrangChu.tsx'
import { timTrangTaiKhoan } from '../pages/duongDanTaiKhoan.ts'
import TrangBanHang, { laTrangBanHang } from '../banhang/TrangBanHang.tsx'
import { laKhuVucVuTru } from '../khampha/khuVucVuTru.ts'
import KhungTrang from '../giaodien/KhungTrang.tsx'
import ThanhDauTrang from '../giaodien/ThanhDauTrang.tsx'

const KhungKhamPha = lazy(() => import('../khampha/KhungKhamPha.tsx'))
const GiaoDienTaiKhoan = lazy(() => import('../pages/GiaoDienTaiKhoan.tsx'))
const TrangQuanTri = lazy(() => import('../banhang/TrangQuanTri.tsx'))

export default function App() {
  const duongDan = window.location.pathname
  if (duongDan === '/') return <TrangChu />
  if (duongDan === '/he-thong' || duongDan === '/he-thong/') return <TrangKiemTraHeThong />
  const khuVuc = /^\/kham-pha\/([^/]+)\/?$/.exec(duongDan)?.[1]
  if (/^\/kham-pha\/?$/.test(duongDan) || (khuVuc && laKhuVucVuTru(khuVuc))) {
    return <Suspense fallback={<p role="status">Đang tải khu vực khám phá…</p>}>
      <KhungKhamPha />
    </Suspense>
  }
  if (duongDan === '/quan-tri' || duongDan.startsWith('/quan-tri/')) {
    return <Suspense fallback={<p role="status">Đang tải khu vực quản trị…</p>}>
      <TrangQuanTri duongDan={duongDan} />
    </Suspense>
  }
  if (laTrangBanHang(duongDan)) return <TrangBanHang duongDan={duongDan} />
  const trangTaiKhoan = timTrangTaiKhoan(duongDan)
  return trangTaiKhoan ? <Suspense fallback={<p role="status">Đang tải trang tài khoản…</p>}>
    <GiaoDienTaiKhoan trangDau={trangTaiKhoan} />
  </Suspense> :
    <KhungTrang lopNoiDung="gx-tien-ich" thanhDau={<ThanhDauTrang />}>
      <section className="gx-tien-ich-noi-dung gx-kinh">
        <h1>Không tìm thấy trang</h1>
        <a className="gx-nut gx-nut-phu" href="/">Về trang chủ</a>
      </section>
    </KhungTrang>
}
