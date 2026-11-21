import { useState, type FormEvent } from 'react'
import { ghi } from '../banhang/api.ts'
import { nhap, PhanHoi, TrangThaiTai, useDuLieu, useThaoTac } from '../banhang/dungChung.tsx'
import type { DiaChi } from '../banhang/duLieu.ts'

const api = '/api/tai-khoan/dia-chi'

export default function SoDiaChi() {
  const ds = useDuLieu<DiaChi[]>(api)
  const tacVu = useThaoTac()
  const [dangSua, datDangSua] = useState<DiaChi | null>(null)

  function luu(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget
    const f = new FormData(form)
    const duLieu = { nguoiNhan: nhap(f, 'nguoiNhan'), soDienThoai: nhap(f, 'soDienThoai'), diaChi: nhap(f, 'diaChi') }
    void tacVu.lam(async () => {
      await ghi(dangSua ? api + '/' + dangSua.id : api, dangSua ? 'PUT' : 'POST', duLieu)
      if (!dangSua) form.reset()
      datDangSua(null)
      ds.taiLai()
    }, 'Đã lưu địa chỉ.')
  }

  function xoa(d: DiaChi) {
    if (!window.confirm('Xóa địa chỉ nhận hàng của ' + d.nguoiNhan + '?')) return
    void tacVu.lam(async () => {
      await ghi(api + '/' + d.id, 'DELETE')
      if (dangSua?.id === d.id) datDangSua(null)
      ds.taiLai()
    }, 'Đã xóa địa chỉ.')
  }

  return <section className="tai-khoan-kham-pha tk-so-dia-chi" aria-labelledby="tk-so-dia-chi">
    <h2 id="tk-so-dia-chi">Sổ địa chỉ</h2>
    <TrangThaiTai {...ds} />
    {ds.loi && <button type="button" onClick={ds.taiLai}>Thử lại</button>}
    {ds.duLieu?.length === 0 && <p>Bạn chưa lưu địa chỉ nhận hàng.</p>}
    {!!ds.duLieu?.length && <ul className="tk-danh-sach-dia-chi">
      {ds.duLieu.map(d => <li key={d.id}>
        <strong>{d.nguoiNhan}</strong>
        <p>{d.soDienThoai} · {d.diaChi}</p>
        <div className="tk-dia-chi-nut">
          <button type="button" disabled={tacVu.dangGui} onClick={() => datDangSua(d)}>Sửa</button>
          <button type="button" disabled={tacVu.dangGui} onClick={() => xoa(d)}>Xóa</button>
        </div>
      </li>)}
    </ul>}
    {ds.duLieu && <form key={dangSua?.id ?? 'them'} onSubmit={luu}>
      <h3>{dangSua ? 'Sửa địa chỉ' : 'Thêm địa chỉ'}</h3>
      <label>Người nhận<input name="nguoiNhan" defaultValue={dangSua?.nguoiNhan} required maxLength={160} autoComplete="shipping name" /></label>
      <label>Số điện thoại<input name="soDienThoai" type="tel" defaultValue={dangSua?.soDienThoai} required pattern="[+]?[0-9]{8,15}" autoComplete="shipping tel" /></label>
      <label>Địa chỉ giao hàng<textarea name="diaChi" defaultValue={dangSua?.diaChi} required maxLength={500} autoComplete="shipping street-address" /></label>
      <div className="tk-dia-chi-nut">
        <button className="gx-nut gx-nut-chinh" disabled={tacVu.dangGui}>Lưu địa chỉ</button>
        {dangSua && <button type="button" disabled={tacVu.dangGui} onClick={() => datDangSua(null)}>Hủy sửa</button>}
      </div>
    </form>}
    <PhanHoi {...tacVu} />
  </section>
}
