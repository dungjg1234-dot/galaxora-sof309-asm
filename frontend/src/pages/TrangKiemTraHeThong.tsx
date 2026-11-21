import { useEffect, useState } from 'react'
import { layTrangThaiBackend } from '../services/heThongService.ts'
import { duongDanTaiKhoan } from './duongDanTaiKhoan.ts'
import KhungTrang from '../giaodien/KhungTrang.tsx'
import ThanhDauTrang from '../giaodien/ThanhDauTrang.tsx'

type KetQua =
  | { loai: 'dangKiemTra' }
  | { loai: 'hoatDong' }
  | { loai: 'loi'; thongBao: string }

// Trang kỹ thuật tạm, chỉ để xác nhận frontend gọi được backend. Không phải giao diện sản phẩm.
export default function TrangKiemTraHeThong() {
  const [ketQua, setKetQua] = useState<KetQua>({ loai: 'dangKiemTra' })

  useEffect(() => {
    const boHuy = new AbortController()
    layTrangThaiBackend(boHuy.signal)
      .then((duLieu) => {
        setKetQua(
          duLieu.trangThai === 'HOAT_DONG'
            ? { loai: 'hoatDong' }
            : { loai: 'loi', thongBao: `Trạng thái không mong đợi: ${duLieu.trangThai}` },
        )
      })
      .catch((loi: unknown) => {
        if (boHuy.signal.aborted) return
        setKetQua({
          loai: 'loi',
          thongBao: loi instanceof Error ? loi.message : 'Không kết nối được backend',
        })
      })
    return () => boHuy.abort()
  }, [])

  return (
    <KhungTrang lopNoiDung="gx-tien-ich" chanTrangGon thanhDau={<ThanhDauTrang kieu="gon" />}>
      <section className="gx-tien-ich-noi-dung gx-kinh">
        <h1>GALAXORA</h1>
        <p>Kiểm tra kết nối frontend và backend.</p>
        <p role="status" className={ketQua.loai === 'loi' ? 'gx-thong-bao gx-loi' : 'gx-thong-bao'}>
          {ketQua.loai === 'dangKiemTra' && 'Đang kiểm tra backend…'}
          {ketQua.loai === 'hoatDong' && 'Backend đang hoạt động.'}
          {ketQua.loai === 'loi' && `Không kết nối được backend: ${ketQua.thongBao}`}
        </p>
        <div className="gx-tien-ich-lien-ket">
          <a className="gx-nut gx-nut-phu" href={duongDanTaiKhoan.dangNhap}>Đến trang đăng nhập</a>
          <a className="gx-nut gx-nut-phu" href="/cua-hang">Mở cửa hàng</a>
          <a className="gx-nut gx-nut-phu" href="/kham-pha">Khám phá 3D</a>
        </div>
      </section>
    </KhungTrang>
  )
}
