import { type ReactNode } from 'react'
import { ChiTietCuaHang, DanhSachSanPham } from './CuaHang.tsx'
import { DanhSachDon, TrangChiTietDon } from './DonHang.tsx'
import type { Gio } from './duLieu.ts'
import GioHang from './GioHang.tsx'
import { useDuLieu } from './dungChung.tsx'
import type { ThongTinPhien } from '../services/taiKhoanService.ts'
import { LoiApi } from '../services/apiClient.ts'
import KhungTrang from '../giaodien/KhungTrang.tsx'
import ThanhDauTrang from '../giaodien/ThanhDauTrang.tsx'
import './banHang.css'

const cacTrang = ['/cua-hang', '/gio-hang', '/don-hang']
export function laTrangBanHang(duongDan: string) {
  return cacTrang.some(trang => duongDan === trang || duongDan.startsWith(trang + '/'))
}

export default function TrangBanHang({ duongDan }: { duongDan: string }) {
  const phien = useDuLieu<ThongTinPhien>('/api/tai-khoan/phien')
  const khachHang = phien.duLieu?.vaiTro.includes('USER') ?? false
  const laKhach = phien.loi instanceof LoiApi && phien.loi.maTrangThai === 401
  const trang = duongDan.replace(/\/$/, '')
  const gio = useDuLieu<Gio>(khachHang || trang === '/gio-hang' ? '/api/gio-hang' : null)
  const soGio = gio.duLieu?.chiTiet.reduce((tong, dong) => tong + dong.soLuong, 0)
  const quyen = { coQuyen: khachHang, laKhach, taiLaiGio: gio.taiLai, gio: gio.duLieu }
  const idSanPham = /^\/cua-hang\/san-pham\/([1-9]\d*)$/.exec(trang)
  const idDon = /^\/don-hang\/([1-9]\d*)$/.exec(trang)
  let noiDung: ReactNode
  if (idSanPham) {
    noiDung = <ChiTietCuaHang id={Number(idSanPham[1])} {...quyen} />
  } else if (idDon) {
    noiDung = <TrangChiTietDon id={Number(idDon[1])} quanTri={false} />
  } else {
    switch (trang) {
      case '/cua-hang': noiDung = <DanhSachSanPham {...quyen} />; break
      case '/cua-hang/yeu-thich': noiDung = <DanhSachSanPham {...quyen} yeuThich />; break
      case '/gio-hang': noiDung = <GioHang gio={gio} />; break
      case '/don-hang': noiDung = <DanhSachDon />; break
      default: noiDung = <section className="bh-trong gx-tien-ich-noi-dung">
        <h1>Không tìm thấy trang.</h1><a className="bh-nut" href="/cua-hang">Về cửa hàng</a>
      </section>
    }
  }
  return <KhungTrang muc="/cua-hang" lopNoiDung="bh bh-than"
    thanhDau={<ThanhDauTrang muc="/cua-hang" laKhach={!phien.duLieu} coGio={khachHang} soGio={soGio}
      vaiTro={phien.duLieu?.vaiTro} hienGioKhach={false} />}>
    {noiDung}
  </KhungTrang>
}
