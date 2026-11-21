import { useEffect, type ReactNode } from 'react'
import { BieuTuong, BieuTuongThuongHieu } from '../giaodien/BieuTuong.tsx'
import { taiKhoanService, type ThongTinPhien } from '../services/taiKhoanService.ts'
import { ghi } from './api.ts'
import { DanhSachDon, TrangChiTietDon } from './DonHang.tsx'
import { Khoi, PhanHoi, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'
import QuanTriCuaHang from './QuanTriCuaHang.tsx'
import {
  NhatKyQuanTri, NhiemVuQuanTri, NoiDungQuanTri, SuKienQuanTri, TaiKhoanQuanTri, VoucherHanhTrinh,
} from './QuanTriHeThong.tsx'
import QuanTriKho from './QuanTriKho.tsx'
import QuanTriVoucher from './QuanTriVoucher.tsx'
import TongQuanQuanTri, { type ThongBaoQuanTri } from './TongQuanQuanTri.tsx'
import './quanTri.css'

const cacMuc = [
  { duongDan: '/quan-tri', ten: 'Tổng quan', icon: 'nha' },
  { duongDan: '/quan-tri/cua-hang', ten: 'Sản phẩm và danh mục', icon: 'hop' },
  { duongDan: '/quan-tri/don-hang', ten: 'Đơn hàng và phí COD', icon: 'gio' },
  { duongDan: '/quan-tri/kho', ten: 'Kho và phiếu nhập', icon: 'kho' },
  { duongDan: '/quan-tri/tai-khoan', ten: 'Tài khoản', icon: 'nguoi' },
  { duongDan: '/quan-tri/voucher', ten: 'Voucher', icon: 've' },
  { duongDan: '/quan-tri/noi-dung', ten: 'Nội dung và nhiệm vụ', icon: 'tenLua' },
  { duongDan: '/quan-tri/nhat-ky', ten: 'Nhật ký hoạt động', icon: 'tep' },
] as const

function PhiVanChuyen() {
  const url = '/api/quan-tri/don-hang/phi-van-chuyen'
  const phi = useDuLieu<{ giaTri: number | null }>(url)
  const tacVu = useThaoTac()
  return <Khoi tieuDe="Phí COD toàn quốc"><TrangThaiTai {...phi} />
    {phi.duLieu && <form className="bh-hang" onSubmit={e => {
      e.preventDefault()
      const giaTri = Number(new FormData(e.currentTarget).get('giaTri'))
      void tacVu.lam(async () => { await ghi(url, 'PUT', { giaTri }); phi.taiLai() })
    }}><label>Phí giao hàng (VND)<input name="giaTri" type="number" step="0.01" min={0}
      defaultValue={phi.duLieu.giaTri ?? ''} required /></label>
      <button disabled={tacVu.dangGui}>Lưu phí</button></form>}
    <PhanHoi {...tacVu} />
  </Khoi>
}

function KhungQuanTri({ trang, phien, children }: { trang: string; phien: ThongTinPhien; children?: ReactNode }) {
  const idDon = /^\/quan-tri\/don-hang\/([1-9]\d*)$/.exec(trang)
  const muc = cacMuc.find(muc => muc.duongDan === trang || (idDon && muc.duongDan === '/quan-tri/don-hang'))
  const thongBao = useDuLieu<ThongBaoQuanTri[]>('/api/quan-tri/thong-bao')
  const dangXuat = useThaoTac()
  const soThongBao = thongBao.duLieu?.length ?? 0

  useEffect(() => {
    document.title = `${muc?.ten ?? 'Quản trị'} · GALAXORA`
  }, [muc])

  const lienKet = <>{cacMuc.map(({ duongDan, ten, icon }) =>
    <a key={duongDan} href={duongDan} aria-current={muc?.duongDan === duongDan ? 'page' : undefined}>
      <BieuTuong ten={icon} /><span>{ten}</span>
    </a>)}</>

  return <div className="gx-khung-trang gx-khung-quan-tri qt">
    <a className="qt-bo-qua" href="#noi-dung-quan-tri">Đi đến nội dung chính</a>
    <aside className="qt-ben">
      <a className="qt-logo" href="/quan-tri"><BieuTuongThuongHieu />GALAXORA</a>
      <nav className="qt-menu qt-menu-may-tinh" aria-label="Điều hướng quản trị">{lienKet}</nav>
    </aside>
    <div className="qt-than">
      <header className="qt-dau">
        <details className="qt-menu-di-dong">
          <summary><BieuTuong ten="menu" /> Menu</summary>
          <nav className="qt-menu" aria-label="Điều hướng quản trị">{lienKet}</nav>
        </details>
        <span className="qt-duong-dan">Quản trị <span aria-hidden="true">/</span> {muc?.ten ?? 'Không tìm thấy trang'}</span>
        <div className="qt-tac-vu">
          <a className="qt-nut-bieu-tuong" href="/quan-tri#thong-bao-quan-tri"
            aria-label={soThongBao ? `Thông báo quản trị, ${soThongBao} mục` : 'Thông báo quản trị'}>
            <BieuTuong ten="chuong" />
            {soThongBao > 0 && <span className="qt-dem" aria-hidden="true">{soThongBao}</span>}
          </a>
          <span className="qt-danh-tinh"><BieuTuong ten="nguoi" /><span>Quản trị viên<small>Tài khoản #{phien.nguoiDungId}</small></span></span>
          <button className="qt-dang-xuat" type="button" disabled={dangXuat.dangGui} onClick={() => {
            void dangXuat.lam(async () => {
              await taiKhoanService.dangXuat()
              window.location.replace('/tai-khoan/dang-nhap')
            })
          }}><BieuTuong ten="thoat" /><span>Đăng xuất</span></button>
        </div>
      </header>
      <main className="bh qt-noi-dung" id="noi-dung-quan-tri" tabIndex={-1}>
        <PhanHoi {...dangXuat} />
        {trang === '/quan-tri' ? <TongQuanQuanTri thongBao={thongBao} /> : children}
      </main>
    </div>
  </div>
}

export default function TrangQuanTri({ duongDan }: { duongDan: string }) {
  const phien = useDuLieu<ThongTinPhien>('/api/tai-khoan/phien')
  const trang = duongDan.replace(/\/$/, '')
  if (!phien.duLieu?.vaiTro.includes('ADMIN')) {
    return <div className="gx-khung-trang gx-khung-quan-tri qt-chan-truy-cap">
      <main className="bh bh-khoi">
        <h1>Quản trị GALAXORA</h1>
        {phien.duLieu ? <p className="bh-loi" role="alert">Tài khoản không có quyền truy cập khu vực quản trị.</p>
          : <TrangThaiTai {...phien} />}
        <a href="/">Về Trang chủ</a>
      </main>
    </div>
  }
  const idDon = /^\/quan-tri\/don-hang\/([1-9]\d*)$/.exec(trang)
  let noiDung: ReactNode
  if (idDon) {
    noiDung = <TrangChiTietDon id={Number(idDon[1])} quanTri />
  } else {
    switch (trang) {
      case '/quan-tri': break
      case '/quan-tri/cua-hang': noiDung = <QuanTriCuaHang />; break
      case '/quan-tri/kho': noiDung = <QuanTriKho />; break
      case '/quan-tri/don-hang': noiDung = <><DanhSachDon quanTri /><PhiVanChuyen /></>; break
      case '/quan-tri/voucher': noiDung = <><VoucherHanhTrinh /><QuanTriVoucher /></>; break
      case '/quan-tri/noi-dung': noiDung = <><NoiDungQuanTri /><NhiemVuQuanTri /><SuKienQuanTri /></>; break
      case '/quan-tri/nhat-ky': noiDung = <NhatKyQuanTri />; break
      case '/quan-tri/tai-khoan': noiDung = <TaiKhoanQuanTri />; break
      default: noiDung = <section className="bh-khoi">
        <h1>Không tìm thấy trang.</h1><a href="/quan-tri">Về Tổng quan</a>
      </section>
    }
  }
  return <KhungQuanTri trang={trang} phien={phien.duLieu}>{noiDung}</KhungQuanTri>
}
