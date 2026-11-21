import { useEffect, useRef } from 'react'
import { LuoiSanPham, type QuyenMua } from '../banhang/CuaHang.tsx'
import { thamSo } from '../banhang/api.ts'
import { useDuLieu } from '../banhang/dungChung.tsx'
import type { PhanTrang, SanPham } from '../banhang/duLieu.ts'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'
import '../banhang/banHang.css'
import './sanPhamTrangChu.css'

const duongDanSanPham = '/api/cua-hang/san-pham' + thamSo({ trang: 0, kichThuoc: 4, sapXep: 'moi' })

export default function SanPhamTrangChu(quyenMua: QuyenMua) {
  const sanPham = useDuLieu<PhanTrang<SanPham>>(duongDanSanPham)
  const khuSanPham = useRef<HTMLElement>(null)
  useEffect(() => {
    if (window.location.hash === '#san-pham') khuSanPham.current?.scrollIntoView()
  }, [])

  return <section ref={khuSanPham} className="gx-khung gx-bo-suu-tap" id="san-pham" aria-labelledby="gx-san-pham-tieu-de">
    <div className="gx-bo-suu-tap-dau">
      <div>
        <h2 id="gx-san-pham-tieu-de">Sản phẩm</h2>
        <p>Mô hình sưu tầm, công nghệ, thời trang và đồ trang trí lấy cảm hứng từ vũ trụ.</p>
      </div>
      <a href="/cua-hang" className="gx-nut gx-nut-phu"><BieuTuong ten="gio" /> Đến cửa hàng <BieuTuong ten="muiTen" /></a>
    </div>
    {sanPham.dangTai && <p className="gx-thong-bao" role="status">Đang tải sản phẩm…</p>}
    {sanPham.loi && <p className="gx-thong-bao" role="alert">Không tải được sản phẩm. <button type="button" onClick={sanPham.taiLai}>Thử lại</button></p>}
    {sanPham.duLieu?.noiDung.length === 0 && <p className="gx-thong-bao" role="status">Cửa hàng chưa có sản phẩm được đăng.</p>}
    {!!sanPham.duLieu?.noiDung.length && <div className="bh"><LuoiSanPham danhSach={sanPham.duLieu.noiDung} quyen={quyenMua} /></div>}
  </section>
}

