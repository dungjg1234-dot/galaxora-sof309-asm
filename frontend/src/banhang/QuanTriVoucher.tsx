import { useState, type FormEvent } from 'react'
import { ghi } from './api.ts'
import NutXoaQuanTri from './NutXoaQuanTri.tsx'
import { dinhDangGio, dinhDangTien, type Voucher } from './duLieu.ts'
import { Khoi, nhap, PhanHoi, soTuyChon, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'

const api = '/api/quan-tri/voucher'
type LanDung = { donHangId: number; ma: string; giamGia: number; thoiDiem: string }

function ngayNhap(utc?: string) {
  if (!utc) return ''
  const luc = new Date(utc + 'Z')
  return new Date(luc.getTime() - luc.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
}

function MauVoucher({ v, taiLai }: { v?: Voucher; taiLai: () => void }) {
  const tacVu = useThaoTac()
  function luu(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget
    const f = new FormData(form)
    void tacVu.lam(async () => {
      await ghi(api + (v ? '/' + v.id : ''), v ? 'PUT' : 'POST', {
        ma: nhap(f, 'ma'), loai: nhap(f, 'loai'), giaTri: Number(f.get('giaTri')),
        giamToiDa: soTuyChon(f, 'giamToiDa'), donToiThieu: soTuyChon(f, 'donToiThieu'),
        batDau: new Date(nhap(f, 'batDau')).toISOString().slice(0, -1),
        ketThuc: new Date(nhap(f, 'ketThuc')).toISOString().slice(0, -1),
        luotToiDa: Number(f.get('luotToiDa')), kichHoat: f.has('kichHoat'), chuSoHuuId: soTuyChon(f, 'chuSoHuuId'),
      })
      if (!v) form.reset()
      taiLai()
    })
  }
  return <><form className="bh-form" onSubmit={luu}>
    <div className="bh-hai-cot">
      <label>Mã voucher<input name="ma" required maxLength={40} defaultValue={v?.ma} /></label>
      <label>Loại<select name="loai" defaultValue={v?.loai}><option value="SO_TIEN">Số tiền</option>
        <option value="PHAN_TRAM">Phần trăm</option></select></label>
      <label>Giá trị giảm<input type="number" name="giaTri" required min="0.01" step="0.01" defaultValue={v?.giaTri} /></label>
      <label>Giảm tối đa (VND)<input type="number" name="giamToiDa" min="0" step="0.01" defaultValue={v?.giamToiDa ?? ''} /></label>
      <label>Đơn tối thiểu (VND)<input type="number" name="donToiThieu" min="0" step="0.01" defaultValue={v?.donToiThieu ?? ''} /></label>
      <label>Lượt tối đa<input type="number" name="luotToiDa" min={Math.max(1, v?.luotDaDung ?? 1)} step={1} required defaultValue={v?.luotToiDa} /></label>
      <label>Bắt đầu<input type="datetime-local" name="batDau" required defaultValue={ngayNhap(v?.batDau)} /></label>
      <label>Kết thúc<input type="datetime-local" name="ketThuc" required defaultValue={ngayNhap(v?.ketThuc)} /></label>
      <label>ID người sở hữu (bỏ trống để dùng chung)<input type="number" name="chuSoHuuId" min="1" step={1} defaultValue={v?.chuSoHuuId ?? ''} /></label>
    </div>
    <label className="bh-check"><input type="checkbox" name="kichHoat" defaultChecked={v?.kichHoat ?? true} /> Cho phép sử dụng</label>
    <button className="bh-chinh" disabled={tacVu.dangGui}>{v ? 'Lưu voucher' : 'Tạo voucher'}</button>
  </form><PhanHoi {...tacVu} /></>
}

export default function QuanTriVoucher() {
  const ds = useDuLieu<Voucher[]>(api)
  const [chon, datChon] = useState<number | null>(null)
  const v = ds.duLieu?.find(v => v.id === chon)
  const lichSu = useDuLieu<LanDung[]>(chon ? api + '/' + chon + '/lich-su' : null)
  return <><div className="bh-dau-trang"><h1>Quản lý voucher</h1></div><TrangThaiTai {...ds} />
    {ds.duLieu && <>
      <Khoi tieuDe="Voucher đã tạo">
        {ds.duLieu.length === 0 && <p>Chưa có voucher.</p>}
        {ds.duLieu.map(v => <div key={v.id} className="bh-dong bh-hang bh-can">
          <span>{v.ma} · Đã dùng {v.luotDaDung}/{v.luotToiDa} · {v.kichHoat ? 'Đang bật' : 'Đã khóa'}</span>
          <div className="bh-hang">
            <button onClick={() => datChon(v.id)}>Sửa voucher</button>
            <NutXoaQuanTri nhan="Xóa voucher" doiTuong={v.ma} duongDan={api + '/' + v.id}
              sauXoa={() => { if (chon === v.id) datChon(null); ds.taiLai() }} />
          </div>
        </div>)}
        {v && <button onClick={() => datChon(null)}>Tạo voucher khác</button>}
      </Khoi>
      <Khoi tieuDe={v ? 'Sửa ' + v.ma : 'Voucher mới'}><MauVoucher key={v?.id ?? 'moi'} v={v} taiLai={ds.taiLai} /></Khoi>
      {v && <Khoi tieuDe="Lịch sử sử dụng"><TrangThaiTai {...lichSu} />
        {lichSu.duLieu?.length === 0 && <p>Chưa có lượt sử dụng.</p>}
        {lichSu.duLieu?.map(l => <div className="bh-dong" key={l.donHangId}>
          <a href={'/quan-tri/don-hang/' + l.donHangId}>Đơn #{l.donHangId}</a> · {l.ma} · {dinhDangTien(l.giamGia)} · {dinhDangGio(l.thoiDiem)}
        </div>)}
      </Khoi>}
    </>}
  </>
}
