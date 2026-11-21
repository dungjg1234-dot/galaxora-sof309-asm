import { BieuTuong } from '../giaodien/BieuTuong.tsx'

export default function TheChaoMung({ trangThai, taiLai }: {
  trangThai: 'dang-tai' | 'khach' | 'loi' | 'da-dang-nhap'; taiLai: () => void
}) {
  if (trangThai === 'dang-tai') return <p className="gx-chao-trang-thai gx-kinh" role="status">Đang kiểm tra phiên…</p>
  if (trangThai === 'loi') return <div className="gx-chao-trang-thai gx-kinh">
    <p role="alert">Không kiểm tra được phiên đăng nhập.</p>
    <button className="gx-nut gx-nut-phu" type="button" onClick={taiLai}>Thử lại</button>
  </div>
  if (trangThai === 'da-dang-nhap') return null

  return <aside className="gx-chao gx-kinh" aria-labelledby="gx-chao-tieu-de">
    <h2 id="gx-chao-tieu-de">Chào mừng trở lại!</h2>
    <p>Đăng nhập để tiếp tục hành trình cùng GALAXORA</p>
    <a className="gx-nut gx-nut-chinh" href="/tai-khoan/dang-nhap"><BieuTuong ten="nguoi" />Đăng nhập</a>
    <a className="gx-nut gx-nut-phu" href="/tai-khoan/dang-ky"><BieuTuong ten="dangKy" />Đăng ký</a>
  </aside>
}
