import { useRef, useState, type FormEvent } from 'react'
import { ghi } from './api.ts'
import { dinhDangGio, nhanBienDong, nhanKho, type ChiTietPhieu, type DongNhap, type GiaoDich, type Phieu, type TonKho } from './duLieu.ts'
import { Khoi, nhap, PhanHoi, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'

const api = '/api/quan-tri/kho'

function PhieuNhap({ ton, sauLuu }: { ton: TonKho[]; sauLuu: () => void }) {
  const [dong, datDong] = useState<DongNhap[]>([{ sanPhamId: 0, soLuong: 1 }])
  const khoa = useRef<string | null>(null)
  const tacVu = useThaoTac()
  function sua(i: number, ten: keyof DongNhap, giaTri: number) {
    datDong(cu => cu.map((d, viTri) => viTri === i ? { ...d, [ten]: giaTri } : d))
  }
  function gui(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget
    const f = new FormData(form)
    void tacVu.lam(async () => {
      khoa.current ??= crypto.randomUUID()
      await ghi<{ id: number }>(api + '/phieu-nhap', 'POST',
        { khoaYeuCau: khoa.current, lyDo: nhap(f, 'lyDo'), chiTiet: dong })
      khoa.current = null
      form.reset()
      datDong([{ sanPhamId: 0, soLuong: 1 }])
      sauLuu()
    }, 'Đã nhập kho.')
  }
  return <><form className="bh-form" onSubmit={gui}>
    {dong.map((d, i) => <div key={i} className="bh-hang">
      <label>Sản phẩm<select value={d.sanPhamId || ''} required onChange={e => sua(i, 'sanPhamId', Number(e.target.value))}>
        <option value="">Chọn sản phẩm</option>
        {ton.map(t => <option key={t.sanPhamId} value={t.sanPhamId}>{t.sku} · {t.ten}</option>)}
      </select></label>
      <label>Số lượng<input className="bh-so" type="number" min={1} step={1} value={d.soLuong || ''}
        required onChange={e => sua(i, 'soLuong', Number(e.target.value))} /></label>
      {dong.length > 1 && <button type="button" onClick={() => datDong(dong.filter((_, j) => i !== j))}>Bỏ dòng</button>}
    </div>)}
    <div className="bh-hang"><button type="button" disabled={dong.length >= Math.min(100, ton.length)}
      onClick={() => datDong([...dong, { sanPhamId: 0, soLuong: 1 }])}>Thêm dòng nhập</button></div>
    <label>Lý do nhập<textarea name="lyDo" maxLength={500} required /></label>
    <button className="bh-chinh" disabled={tacVu.dangGui}>Lưu phiếu nhập</button>
  </form><PhanHoi {...tacVu} /></>
}

function DieuChinh({ id, sauLuu }: { id: number; sauLuu: () => void }) {
  const tacVu = useThaoTac()
  const khoa = useRef<string | null>(null)
  return <><form className="bh-form" onSubmit={e => {
    e.preventDefault()
    const form = e.currentTarget
    const f = new FormData(form)
    if (!window.confirm('Xác nhận cập nhật tồn kho?')) return
    void tacVu.lam(async () => {
      khoa.current ??= crypto.randomUUID()
      await ghi(api + '/ton/' + id + '/dieu-chinh', 'POST', { khoaYeuCau: khoa.current,
        thayDoi: Number(f.get('thayDoi')), xuatKho: f.has('xuatKho'), lyDo: nhap(f, 'lyDo') })
      khoa.current = null
      form.reset(); sauLuu()
    })
  }}>
    <label>Số thay đổi (âm hoặc dương)<input type="number" name="thayDoi" step={1} required /></label>
    <label className="bh-check"><input type="checkbox" name="xuatKho" /> Ghi là xuất kho</label>
    <label>Lý do<textarea name="lyDo" maxLength={500} required /></label>
    <button disabled={tacVu.dangGui}>Ghi biến động</button>
  </form><PhanHoi {...tacVu} /></>
}

export default function QuanTriKho() {
  const [sanPham, datSanPham] = useState<number | null>(null)
  const [phieuId, datPhieuId] = useState<number | null>(null)
  const ton = useDuLieu<TonKho[]>(api + '/ton')
  const nguong = useDuLieu<{ giaTri: number | null }>(api + '/nguong')
  const dsPhieu = useDuLieu<Phieu[]>(api + '/phieu-nhap')
  const phieu = useDuLieu<ChiTietPhieu>(phieuId ? api + '/phieu-nhap/' + phieuId : null)
  const lichSu = useDuLieu<GiaoDich[]>(sanPham ? api + '/ton/' + sanPham + '/lich-su' : null)
  const tacVu = useThaoTac()
  const tenSanPham = ton.duLieu?.find(t => t.sanPhamId === sanPham)?.ten
  function taiLai() { ton.taiLai(); dsPhieu.taiLai(); lichSu.taiLai() }
  return <>
    <div className="bh-dau-trang"><h1>Quản lý kho</h1></div>
    <TrangThaiTai {...ton} />
    {ton.duLieu && <>
      <Khoi tieuDe="Ngưỡng cảnh báo chung">
        <TrangThaiTai {...nguong} />
        {nguong.duLieu && <form className="bh-hang" onSubmit={e => {
          e.preventDefault()
          const giaTri = Number(new FormData(e.currentTarget).get('giaTri'))
          void tacVu.lam(async () => { await ghi(api + '/nguong', 'PUT', { giaTri }); taiLai(); nguong.taiLai() })
        }}><label>Số lượng khả dụng<input type="number" name="giaTri" min={0} step={1}
          defaultValue={nguong.duLieu.giaTri ?? ''} required /></label>
          <button disabled={tacVu.dangGui}>Lưu ngưỡng</button></form>}
        <PhanHoi {...tacVu} />
      </Khoi>
      <Khoi tieuDe="Tồn kho">
        {ton.duLieu.length === 0 ? <p>Chưa có sản phẩm. <a href="/quan-tri/cua-hang">Thêm sản phẩm</a></p> :
          <div className="bh-bang"><table><thead><tr>
            <th>Sản phẩm</th><th>Thực tế</th><th>Đang giữ</th><th>Khả dụng</th><th>Cảnh báo</th><th>Thao tác</th>
          </tr></thead><tbody>{ton.duLieu.map(t => <tr key={t.sanPhamId}>
            <th scope="row">{t.sku} · {t.ten}</th><td>{t.thucTe}</td><td>{t.dangGiu}</td><td>{t.khaDung}</td>
            <td className={t.canhBao === 'CON_HANG' ? '' : 'bh-canh-bao'}>{nhanKho[t.canhBao]}</td>
            <td><button onClick={() => datSanPham(t.sanPhamId)}>Chi tiết kho</button></td>
          </tr>)}</tbody></table></div>}
      </Khoi>
      {sanPham && <Khoi tieuDe={tenSanPham ?? 'Chi tiết kho'}>
        <DieuChinh key={sanPham} id={sanPham} sauLuu={taiLai} />
        <h3>Lịch sử kho</h3><TrangThaiTai {...lichSu} />
        {lichSu.duLieu?.length === 0 && <p>Chưa có biến động.</p>}
        {lichSu.duLieu?.map(g => <div className="bh-dong" key={g.id}><strong>{nhanBienDong[g.loai]}</strong>
          {' · ' + dinhDangGio(g.thoiDiem)}
          <p>Thực tế {g.thayDoiThucTe} → {g.thucTeSau} · Đang giữ {g.thayDoiDangGiu} → {g.dangGiuSau}</p>
          <p>{g.lyDo}{g.donHangId ? ' · Đơn #' + g.donHangId : ''}{g.phieuNhapId ? ' · Phiếu #' + g.phieuNhapId : ''}</p>
        </div>)}
      </Khoi>}
      {ton.duLieu.length > 0 && <Khoi tieuDe="Phiếu nhập mới"><PhieuNhap ton={ton.duLieu} sauLuu={taiLai} /></Khoi>}
      <Khoi tieuDe="Phiếu nhập đã lưu">
        <TrangThaiTai {...dsPhieu} />
        {dsPhieu.duLieu?.length === 0 && <p>Chưa có phiếu nhập.</p>}
        {dsPhieu.duLieu?.map(p => <div key={p.id} className="bh-dong bh-hang bh-can">
          <span>Phiếu #{p.id} · {p.lyDo} · {dinhDangGio(p.thoiDiem)}</span>
          <button onClick={() => datPhieuId(p.id)}>Xem phiếu</button></div>)}
        <TrangThaiTai {...phieu} />
        {phieu.duLieu && <div className="bh-dong"><h3>Phiếu #{phieu.duLieu.phieu.id}</h3>
          {phieu.duLieu.chiTiet.map(d => <p key={d.sanPhamId}>{ton.duLieu?.find(t => t.sanPhamId === d.sanPhamId)?.ten
            ?? 'Sản phẩm #' + d.sanPhamId} × {d.soLuong}</p>)}
        </div>}
      </Khoi>
    </>}
  </>
}
