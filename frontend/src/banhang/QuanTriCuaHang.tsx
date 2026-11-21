import { useState, type FormEvent } from 'react'
import { duongDanAnh, thuVienAnh } from './anhSanPham.ts'
import NutXoaQuanTri from './NutXoaQuanTri.tsx'
import { ghi } from './api.ts'
import { dinhDangTien, type Anh, type ChiTietSanPham, type DanhGia, type DanhMuc, type MoHinh,
  type PhanTrang as Trang, type SanPham } from './duLieu.ts'
import { Khoi, nhap, PhanHoi, PhanTrang, soTuyChon, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'

const api = '/api/quan-tri/cua-hang'
const anhThuVien = thuVienAnh.map(anh => ({ duongDan: duongDanAnh(anh), moTa: anh.ten }))

function MauDanhMuc({ d, taiLai }: { d?: DanhMuc; taiLai: () => void }) {
  const tacVu = useThaoTac()
  function luu(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget
    const f = new FormData(form)
    void tacVu.lam(async () => {
      await ghi(api + '/danh-muc' + (d ? '/' + d.id : ''), d ? 'PUT' : 'POST',
        { ten: nhap(f, 'ten'), hienThi: f.has('hienThi') })
      if (!d) form.reset()
      taiLai()
    })
  }
  return <div className="bh-dong"><form className="bh-hang" onSubmit={luu}>
    <label>Tên danh mục<input name="ten" required maxLength={160} defaultValue={d?.ten} /></label>
    <label className="bh-check"><input type="checkbox" name="hienThi" defaultChecked={d?.hienThi ?? true} /> Hiển thị</label>
    <button disabled={tacVu.dangGui}>{d ? 'Lưu danh mục' : 'Tạo danh mục'}</button>
    {d && <NutXoaQuanTri nhan="Xóa danh mục" doiTuong={d.ten} duongDan={api + '/danh-muc/' + d.id}
      sauXoa={taiLai} disabled={tacVu.dangGui} />}
  </form><PhanHoi {...tacVu} /></div>
}

function MauSanPham({ chiTiet, danhMuc, taiLai }: { chiTiet?: ChiTietSanPham; danhMuc: DanhMuc[]; taiLai: () => void }) {
  const s = chiTiet?.sanPham
  const [anh, datAnh] = useState<Anh[]>(chiTiet?.hinhAnh ?? [])
  const tacVu = useThaoTac()
  function suaAnh(i: number, truong: keyof Anh, giaTri: string) {
    datAnh(cu => cu.map((a, vt) => vt === i ? { ...a, [truong]: giaTri } : a))
  }
  function luu(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget
    const f = new FormData(form)
    void tacVu.lam(async () => {
      await ghi(api + '/san-pham' + (s ? '/' + s.id : ''), s ? 'PUT' : 'POST', {
        danhMucId: Number(f.get('danhMucId')), sku: nhap(f, 'sku'), ten: nhap(f, 'ten'),
        moTaNgan: nhap(f, 'moTaNgan'), moTa: nhap(f, 'moTa'), gia: Number(f.get('gia')),
        giaSoSanh: soTuyChon(f, 'giaSoSanh'), hienThi: f.has('hienThi'), kinhDoanh: f.has('kinhDoanh'),
        hinhAnh: anh.map(a => ({ duongDan: a.duongDan.trim(), moTa: a.moTa.trim() })),
      })
      if (!s) { form.reset(); datAnh([]) }
      taiLai()
    })
  }
  return <><form className="bh-form" onSubmit={luu}>
    <div className="bh-hai-cot">
      <label>Tên sản phẩm<input name="ten" required maxLength={200} defaultValue={s?.ten} /></label>
      <label>SKU<input name="sku" required maxLength={64} defaultValue={s?.sku} /></label>
      <label>Danh mục<select name="danhMucId" required defaultValue={s?.danhMucId ?? ''}>
        <option value="" disabled>Chọn danh mục</option>
        {danhMuc.map(d => <option key={d.id} value={d.id}>{d.ten}{d.hienThi ? '' : ' (ẩn)'}</option>)}
      </select></label>
      <label>Giá bán (VND)<input type="number" name="gia" required min="0" step="0.01" defaultValue={s?.gia} /></label>
      <label>Giá so sánh (VND)<input type="number" name="giaSoSanh" min="0" step="0.01" defaultValue={s?.giaSoSanh ?? ''} /></label>
    </div>
    <label>Mô tả ngắn<textarea name="moTaNgan" maxLength={500} defaultValue={s?.moTaNgan} /></label>
    <label>Mô tả chi tiết<textarea name="moTa" maxLength={16000} defaultValue={s?.moTa} /></label>
    <div className="bh-hang">
      <label className="bh-check"><input type="checkbox" name="hienThi" defaultChecked={s?.hienThi ?? true} /> Hiển thị</label>
      <label className="bh-check"><input type="checkbox" name="kinhDoanh" defaultChecked={s?.kinhDoanh ?? true} /> Đang bán</label>
    </div>
    <fieldset className="bh-form"><legend>Ảnh sản phẩm</legend>
      {anh.map((a, i) => <div key={i} className="bh-hang bh-dong">
        {a.duongDan.startsWith('/anh-san-pham/')
          ? <label>Ảnh nội bộ<input value={anhThuVien.find(muc => muc.duongDan === a.duongDan)?.moTa ?? a.duongDan} readOnly /></label>
          : <label>URL HTTPS<input type="url" maxLength={2048} required value={a.duongDan} onChange={e => suaAnh(i, 'duongDan', e.target.value)} /></label>}
        <label>Mô tả ảnh<input value={a.moTa} maxLength={250} onChange={e => suaAnh(i, 'moTa', e.target.value)} /></label>
        {i === 0 ? <span className="bh-nho">Ảnh bìa</span>
          : <button type="button" onClick={() => datAnh([a, ...anh.filter((_, j) => i !== j)])}>Dùng làm ảnh bìa</button>}
        <button type="button" onClick={() => datAnh(anh.filter((_, j) => i !== j))}>Bỏ ảnh</button>
      </div>)}
      <label>Chọn ảnh từ bộ ảnh GALAXORA<select aria-label="Chọn ảnh từ bộ ảnh GALAXORA" value="" disabled={anh.length >= 12} onChange={e => {
        const muc = anhThuVien.find(a => a.duongDan === e.target.value)
        if (muc) datAnh([...anh, muc])
      }}>
        <option value="">Chọn ảnh</option>
        {anhThuVien.map(a => <option key={a.duongDan} value={a.duongDan}
          disabled={anh.some(muc => muc.duongDan === a.duongDan)}>{a.moTa}</option>)}
      </select></label>
      <button type="button" disabled={anh.length >= 12} onClick={() => datAnh([...anh, { duongDan: '', moTa: '' }])}>Thêm ảnh bằng URL</button>
    </fieldset>
    <button className="bh-chinh" disabled={!danhMuc.length || tacVu.dangGui}>{s ? 'Lưu sản phẩm' : 'Tạo sản phẩm'}</button>
  </form><PhanHoi {...tacVu} /></>
}

function QuanTriMoHinh({ id, sauLuu }: { id: number; sauLuu: () => void }) {
  const url = api + '/san-pham/' + id + '/mo-hinh'
  const meta = useDuLieu<MoHinh>(url)
  const tacVu = useThaoTac()
  const m = meta.duLieu
  return <Khoi tieuDe="Mô hình sản phẩm">
    {m && <div className="bh-dong"><p>{m.tenTep} · {m.giayPhep} · {m.tacGia} · {m.daDuyet ? 'Đã duyệt' : 'Chờ duyệt'}</p>
      <p>{m.ghiCong} · <a href={m.nguon} target="_blank" rel="noreferrer">Nguồn mô hình</a></p>
      <button disabled={tacVu.dangGui} onClick={() => void tacVu.lam(async () => {
        await ghi(url, 'PUT', { daDuyet: !m.daDuyet }); meta.taiLai(); sauLuu()
      })}>{m.daDuyet ? 'Ẩn mô hình' : 'Duyệt hiển thị'}</button>
      <NutXoaQuanTri nhan="Xóa mô hình 3D" doiTuong={m.tenTep} duongDan={url}
        sauXoa={() => { meta.taiLai(); sauLuu() }} disabled={tacVu.dangGui} />
    </div>}
    <form className="bh-form" onSubmit={e => {
      e.preventDefault()
      const form = e.currentTarget
      const f = new FormData(form)
      void tacVu.lam(async () => { await ghi(url, 'POST', f); form.reset(); meta.taiLai(); sauLuu() }, 'Đã tải lên. Mô hình chờ duyệt.')
    }}>
      <label>Tệp GLB/glTF tự chứa, tối đa 8 MiB<input type="file" name="tep" accept=".glb,.gltf" required /></label>
      <div className="bh-hai-cot">
        <label>Giấy phép<input name="giayPhep" maxLength={255} defaultValue={m?.giayPhep} required /></label>
        <label>URL nguồn<input name="nguon" type="url" maxLength={2048} defaultValue={m?.nguon} required /></label>
        <label>Tác giả<input name="tacGia" maxLength={250} defaultValue={m?.tacGia} required /></label>
        <label>Ghi công<input name="ghiCong" maxLength={500} defaultValue={m?.ghiCong} required /></label>
      </div>
      <button disabled={tacVu.dangGui}>{m ? 'Thay tệp và metadata' : 'Tải mô hình'}</button>
    </form><PhanHoi {...tacVu} />
  </Khoi>
}

function QuanTriDanhGia({ id }: { id: number }) {
  const ds = useDuLieu<DanhGia[]>(api + '/san-pham/' + id + '/danh-gia')
  const tacVu = useThaoTac()
  return <Khoi tieuDe="Kiểm duyệt đánh giá"><TrangThaiTai {...ds} />
    {ds.duLieu?.length === 0 && <p>Chưa có đánh giá.</p>}
    {ds.duLieu?.map(d => <div className="bh-dong" key={d.donHangId}>
      <strong>{d.tacGia} · {d.soSao}/5 sao</strong><p>{d.noiDung}</p>
      <button disabled={tacVu.dangGui} onClick={() => void tacVu.lam(async () => {
        await ghi(api + '/danh-gia/' + d.donHangId + '/' + id + '/hien-thi', 'PUT', { hienThi: !d.hienThi })
        ds.taiLai()
      })}>{d.hienThi ? 'Ẩn đánh giá' : 'Hiện đánh giá'}</button>
    </div>)}<PhanHoi {...tacVu} />
  </Khoi>
}

export default function QuanTriCuaHang() {
  const [trang, datTrang] = useState(0)
  const [chon, datChon] = useState<number | null>(null)
  const dm = useDuLieu<DanhMuc[]>(api + '/danh-muc')
  const ds = useDuLieu<Trang<SanPham>>(api + '/san-pham?trang=' + trang)
  const ct = useDuLieu<ChiTietSanPham>(chon ? api + '/san-pham/' + chon : null)
  function taiLai() { ds.taiLai(); ct.taiLai() }
  return <><div className="bh-dau-trang"><h1>Quản lý cửa hàng</h1></div>
    <TrangThaiTai {...dm} />
    {dm.duLieu && <>
      <Khoi tieuDe="Danh mục">
        {dm.duLieu.map(d => <MauDanhMuc key={d.id} d={d} taiLai={dm.taiLai} />)}
        <MauDanhMuc taiLai={dm.taiLai} />
      </Khoi>
      <Khoi tieuDe="Sản phẩm">
        <TrangThaiTai {...ds} />
        {ds.duLieu?.noiDung.length === 0 && <p>Chưa có sản phẩm.</p>}
        {ds.duLieu?.noiDung.map(s => <div className="bh-dong bh-hang bh-can" key={s.id}>
          <span>{s.sku} · {s.ten} · {dinhDangTien(s.gia)}{s.hienThi ? '' : ' · Đang ẩn'}</span>
          <div className="bh-hang">
            <button onClick={() => datChon(s.id)}>Sửa</button>
            <NutXoaQuanTri nhan="Xóa sản phẩm" doiTuong={s.ten} duongDan={api + '/san-pham/' + s.id}
              sauXoa={() => {
                if (chon === s.id) datChon(null)
                if (trang > 0 && ds.duLieu?.noiDung.length === 1) datTrang(trang - 1)
                ds.taiLai()
              }} />
          </div></div>)}
        {ds.duLieu && <PhanTrang trang={trang} coTrangSau={(trang + 1) * ds.duLieu.kichThuoc < ds.duLieu.tong} datTrang={datTrang} />}
        {chon && <button onClick={() => datChon(null)}>Thêm sản phẩm mới</button>}
      </Khoi>
      <Khoi tieuDe={chon ? 'Sửa sản phẩm' : 'Sản phẩm mới'}>
        <TrangThaiTai {...ct} />
        {(!chon || ct.duLieu) && <MauSanPham key={chon ?? 'moi'} danhMuc={dm.duLieu} chiTiet={ct.duLieu} taiLai={taiLai} />}
      </Khoi>
      {chon && ct.duLieu && <><QuanTriMoHinh key={chon} id={chon} sauLuu={ds.taiLai} /><QuanTriDanhGia id={chon} /></>}
    </>}
  </>
}
