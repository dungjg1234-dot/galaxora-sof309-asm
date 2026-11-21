import { useRef, useState, type FormEvent } from 'react'
import { ghi, thamSo } from './api.ts'
import AnhSanPham from './AnhSanPham.tsx'
import { timAnhSanPham } from './anhSanPham.ts'
import { dinhDangTien, type DiaChi, type Gio, type TongTien } from './duLieu.ts'
import { ChiPhi, Khoi, nhap, PhanHoi, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'

export default function GioHang({ gio }: { gio: ReturnType<typeof useDuLieu<Gio>> }) {
  const soDiaChi = useDuLieu<DiaChi[]>(gio.duLieu?.chiTiet.length ? '/api/tai-khoan/dia-chi' : null)
  const tacVu = useThaoTac()
  const khoa = useRef<string | null>(null)
  const [voucher, datVoucher] = useState('')
  const [baoGia, datBaoGia] = useState<TongTien | null>(null)
  const [diaChiId, datDiaChiId] = useState('')
  const diaChiDaLuu = soDiaChi.duLieu?.find(d => String(d.id) === diaChiId)

  function datDon(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const f = new FormData(e.currentTarget)
    if (!baoGia || !window.confirm('Đặt đơn COD với tổng tiền ' + dinhDangTien(baoGia.tongTien) + '?')) return
    void tacVu.lam(async () => {
      khoa.current ??= crypto.randomUUID()
      const don = await ghi<{ id: number }>('/api/don-hang', 'POST', {
        khoaYeuCau: khoa.current, nguoiNhan: nhap(f, 'nguoiNhan'), soDienThoai: nhap(f, 'soDienThoai'),
        diaChi: nhap(f, 'diaChi'), ghiChu: nhap(f, 'ghiChu'), maVoucher: voucher, tongTienDaXacNhan: baoGia.tongTien,
      })
      window.location.assign('/don-hang/' + don.id)
    })
  }

  return <><div className="bh-dau-trang"><h1>Giỏ hàng</h1></div><TrangThaiTai {...gio} />
    {gio.duLieu?.chiTiet.length === 0 && <p className="bh-trong">Giỏ hàng đang trống. <a href="/cua-hang">Xem sản phẩm</a></p>}
    {!!gio.duLieu?.chiTiet.length && <div className="bh-hai-cot">
      <Khoi tieuDe="Sản phẩm">
        {gio.duLieu.chiTiet.map(d => <div className="bh-dong bh-dong-gio" key={d.sanPhamId}>
          <a className="bh-anh" href={'/cua-hang/san-pham/' + d.sanPhamId} aria-label={`Xem chi tiết ${d.ten}`}>
            <AnhSanPham anh={timAnhSanPham(d)} ten="" />
          </a>
          <div>
            <strong><a href={'/cua-hang/san-pham/' + d.sanPhamId}>{d.ten}</a></strong>
            <p>{dinhDangTien(d.gia)} · {d.dangBan ? 'Có thể mua ' + d.khaDung : 'Ngừng bán'}</p>
            <form className="bh-hang" onSubmit={e => {
              e.preventDefault()
              const sl = Number(new FormData(e.currentTarget).get('soLuong'))
              void tacVu.lam(async () => {
                await ghi('/api/gio-hang/' + d.sanPhamId, 'PUT', { soLuong: sl })
                datBaoGia(null); gio.taiLai()
              })
            }}><label>Số lượng <input key={d.soLuong} type="number" name="soLuong" defaultValue={d.soLuong} min={1} className="bh-so" required /></label>
              <button disabled={tacVu.dangGui}>Cập nhật</button>
              <button type="button" disabled={tacVu.dangGui} onClick={() => void tacVu.lam(async () => {
                await ghi('/api/gio-hang/' + d.sanPhamId, 'PUT', { soLuong: 0 })
                datBaoGia(null); gio.taiLai()
              })}>Xóa</button>
            </form>
          </div>
        </div>)}
        <p>Tạm tính {dinhDangTien(gio.duLieu.tamTinh)}</p>
      </Khoi>
      <Khoi tieuDe="Đặt hàng COD">
        <form className="bh-hang" onSubmit={e => {
          e.preventDefault()
          datBaoGia(null)
          void tacVu.lam(async () => {
            const duongDan = '/api/gio-hang/tinh-tien' + thamSo({ maVoucher: voucher })
            const r = await fetch(duongDan, { cache: 'no-store' })
            if (!r.ok) {
              const loi = await r.json().catch(() => null) as { detail?: string } | null
              throw new Error(loi?.detail ?? 'Không tính được tổng tiền.')
            }
            datBaoGia(await goiApiTuPhanHoi(r))
          }, 'Đã tính tổng tiền.')
        }}>
          <label>Mã voucher<input maxLength={40} value={voucher} disabled={tacVu.dangGui}
            onChange={e => { datVoucher(e.target.value); datBaoGia(null) }} /></label>
          <button disabled={tacVu.dangGui}>Tính tổng tiền</button>
        </form>
        {baoGia && <ChiPhi tien={baoGia} />}
        {!!soDiaChi.duLieu?.length && <div className="bh-form">
          <label>Địa chỉ đã lưu<select value={diaChiId} onChange={e => datDiaChiId(e.target.value)}>
            <option value="">Nhập địa chỉ giao hàng</option>
            {soDiaChi.duLieu.map(d => <option key={d.id} value={d.id}>{d.nguoiNhan} · {d.diaChi}</option>)}
          </select></label>
        </div>}
        {soDiaChi.loi && <p role="status">Chưa tải được sổ địa chỉ. Bạn có thể nhập thông tin giao hàng bên dưới.</p>}
        <form className="bh-form" onSubmit={datDon}>
          <label>Người nhận<input key={'ten-' + diaChiId} name="nguoiNhan" defaultValue={diaChiDaLuu?.nguoiNhan} required maxLength={160} autoComplete="shipping name" /></label>
          <label>Số điện thoại<input key={'dt-' + diaChiId} type="tel" name="soDienThoai" defaultValue={diaChiDaLuu?.soDienThoai} required pattern="[+]?[0-9]{8,15}" autoComplete="shipping tel" /></label>
          <label>Địa chỉ giao hàng<textarea key={'dc-' + diaChiId} name="diaChi" defaultValue={diaChiDaLuu?.diaChi} required maxLength={500} autoComplete="shipping street-address" /></label>
          <label>Ghi chú<textarea name="ghiChu" maxLength={500} /></label>
          <button className="bh-chinh" disabled={!baoGia || tacVu.dangGui}>Đặt hàng COD</button>
        </form>
      </Khoi>
    </div>}
    <PhanHoi {...tacVu} />
  </>
}

async function goiApiTuPhanHoi(r: Response): Promise<TongTien> {
  return await r.json() as TongTien
}
