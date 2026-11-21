import { type Gio } from '../banhang/duLieu.ts'
import { useDuLieu } from '../banhang/dungChung.tsx'
import KhungTrang from '../giaodien/KhungTrang.tsx'
import ThanhDauTrang from '../giaodien/ThanhDauTrang.tsx'
import CacKhuKhamPha from '../trangchu/CacKhuKhamPha.tsx'
import HanhTrinh from '../trangchu/HanhTrinh.tsx'
import SanPhamTrangChu from '../trangchu/SanPhamTrangChu.tsx'
import MoDau from '../trangchu/MoDau.tsx'
import TheChaoMung from '../trangchu/TheChaoMung.tsx'
import { usePhienTrangChu } from '../trangchu/usePhienTrangChu.ts'
import './trangChu.css'

export default function TrangChu() {
  const phien = usePhienTrangChu()
  const daDangNhap = phien.trangThai === 'da-dang-nhap'
  const laKhach = phien.trangThai === 'khach'
  const coQuyenMua = daDangNhap && phien.phien.vaiTro.includes('USER')
  const gio = useDuLieu<Gio>(coQuyenMua ? '/api/gio-hang' : null)
  const soGio = gio.duLieu?.chiTiet.reduce((tong, dong) => tong + dong.soLuong, 0)

  return <KhungTrang lop="gx-trang-chu" muc="/"
    thanhDau={<ThanhDauTrang muc="/" laKhach={laKhach} coGio={coQuyenMua} soGio={soGio}
      vaiTro={daDangNhap ? phien.phien.vaiTro : undefined} />}>
    <MoDau theChaoMung={<TheChaoMung trangThai={phien.trangThai} taiLai={phien.taiLai} />} />
    <SanPhamTrangChu coQuyen={coQuyenMua} laKhach={laKhach} gio={gio.duLieu} taiLaiGio={gio.taiLai} />
    <CacKhuKhamPha />
    {coQuyenMua && <div className="gx-khung gx-tong-quan"><HanhTrinh /></div>}
  </KhungTrang>
}
