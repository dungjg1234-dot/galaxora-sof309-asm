import { useState } from 'react'
import { ghi, thamSo } from './api.ts'
import NutXoaQuanTri from './NutXoaQuanTri.tsx'
import { Khoi, nhap, PhanHoi, PhanTrang, soTuyChon, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'
import { cacThienThe } from '../khampha/thienThe.ts'
import { cacKhuVucVuTru } from '../khampha/khuVucVuTru.ts'

const goc = '/api/quan-tri'
const tenNhiemVu = {
  KHAM_PHA: 'Khám phá thiên thể', HANH_TRINH: 'Hành trình Hệ Mặt Trời', VAT_THE_AN: 'Nhiệm vụ bí mật',
  CHUONG_1: 'Chương 1 · Hành tinh đá', CHUONG_2: 'Chương 2 · Hành tinh ngoài', CHUONG_3: 'Chương 3 · Mặt Trăng và Pluto',
  HUY_HIEU_KHAM_PHA: 'Huy hiệu · Khám phá thiên thể', HUY_HIEU_HANH_TRINH: 'Huy hiệu · Hành trình',
  HUY_HIEU_VAT_THE_AN: 'Huy hiệu · Nhiệm vụ bí mật',
}
export function NhatKyQuanTri() {
  const [loc, datLoc] = useState<Record<string, string>>({})
  const [trang, datTrang] = useState(0)
  const ds = useDuLieu<{ id: number; adminId: number; thaoTac: string; duongDan: string; thoiDiem: string }[]>(
    goc + '/nhat-ky' + thamSo({ ...loc, trang }))
  return <Khoi tieuDe="Nhật ký quản trị" tieuDeChinh>
    <form className="bh-hang" onSubmit={e => {
      e.preventDefault()
      const f = new FormData(e.currentTarget)
      datTrang(0)
      datLoc({ tu: nhap(f, 'tu'), den: nhap(f, 'den'), thaoTac: nhap(f, 'thaoTac'), adminId: nhap(f, 'adminId') })
    }}>
      <label>Từ ngày (UTC)<input name="tu" type="date" /></label>
      <label>Đến ngày (UTC)<input name="den" type="date" /></label>
      <label>Thao tác<input name="thaoTac" maxLength={80} /></label>
      <label>Mã Admin<input name="adminId" type="number" min={1} /></label>
      <button>Lọc nhật ký</button>
    </form>
    <TrangThaiTai {...ds} />
    {ds.duLieu?.length === 0 && <p>Không có mục khớp bộ lọc.</p>}
    {ds.duLieu?.map(d => <article key={d.id} className="bh-dong">
      <strong>{d.thaoTac}</strong> · Admin {d.adminId} · {d.thoiDiem.replace('T', ' ')} UTC
      <p><code>{d.duongDan}</code></p>
    </article>)}
    <PhanTrang trang={trang} coTrangSau={ds.duLieu?.length === 50} datTrang={datTrang} />
  </Khoi>
}

export function TaiKhoanQuanTri() {
  const [tuKhoa, datTuKhoa] = useState('')
  const [trang, datTrang] = useState(0)
  const ds = useDuLieu<{ id: number; hoTen: string; email: string; dangKhoa: boolean }[]>(
    goc + '/tai-khoan' + thamSo({ tuKhoa, trang }))
  const tacVu = useThaoTac()
  return <Khoi tieuDe="Tài khoản người dùng" tieuDeChinh>
    <form className="bh-hang" onSubmit={e => {
      e.preventDefault()
      datTrang(0)
      datTuKhoa(nhap(new FormData(e.currentTarget), 'tuKhoa'))
    }}>
      <label>Tên, email hoặc số điện thoại<input name="tuKhoa" maxLength={100} /></label>
      <button>Tìm tài khoản</button>
    </form>
    <TrangThaiTai {...ds} />
    <PhanHoi {...tacVu} />
    {ds.duLieu?.length === 0 && <p>Không có tài khoản khớp.</p>}
    {ds.duLieu?.map(t => <article className="bh-dong bh-hang" key={t.id}>
      <div><strong>{t.hoTen}</strong> · #{t.id}<p>{t.email}</p><p>{t.dangKhoa ? 'Đang khóa' : 'Đang hoạt động'}</p></div>
      <button disabled={tacVu.dangGui} onClick={() => {
        if (!window.confirm((t.dangKhoa ? 'Mở khóa ' : 'Khóa ') + t.hoTen + '?')) return
        void tacVu.lam(async () => { await ghi(goc + '/tai-khoan/' + t.id + '/khoa', 'PUT', { khoa: !t.dangKhoa }); ds.taiLai() })
      }}>{t.dangKhoa ? 'Mở khóa' : 'Khóa'}</button>
    </article>)}
    <PhanTrang trang={trang} coTrangSau={ds.duLieu?.length === 50} datTrang={datTrang} />
  </Khoi>
}

export function NoiDungQuanTri() {
  const [loai, datLoai] = useState('kham-pha')
  const [ma, datMa] = useState('sun')
  const ds = useDuLieu<{ ma: string; ten: string; moTa: string }[]>(goc + '/noi-dung/' + loai)
  const tacVu = useThaoTac()
  const bangTen: Record<string,string> = loai === 'nhiem-vu' ? tenNhiemVu :
    Object.fromEntries([...cacThienThe.map(t => [t.ma,t.ten]), ...Object.entries(cacKhuVucVuTru).map(([m,t]) => [m,t.ten])])
  const cu = ds.duLieu?.find(d => d.ma === ma)
  return <Khoi tieuDe="Nội dung khám phá và nhiệm vụ" tieuDeChinh>
    <div className="bh-hang">
      <label>Nhóm<select value={loai} onChange={e => { datLoai(e.target.value); datMa(e.target.value === 'nhiem-vu' ? 'KHAM_PHA' : 'sun') }}>
        <option value="kham-pha">Khám phá</option><option value="nhiem-vu">Nhiệm vụ</option>
      </select></label>
      <label>Nội dung<select value={ma} onChange={e => datMa(e.target.value)}>
        {Object.entries(bangTen).map(([key,ten]) => <option key={key} value={key}>{ten}</option>)}
      </select></label>
    </div>
    <TrangThaiTai {...ds} />
    {ds.duLieu && <form className="bh-form" key={loai + ma + (cu?.moTa ?? '')} onSubmit={e => {
      e.preventDefault()
      const f = new FormData(e.currentTarget)
      void tacVu.lam(async () => {
        await ghi(goc + '/noi-dung/' + loai + '/' + ma, 'PUT', { ten: nhap(f,'ten'), moTa: nhap(f,'moTa') })
        ds.taiLai()
      })
    }}>
      <label>Tên hiển thị<input name="ten" defaultValue={cu?.ten ?? bangTen[ma]} required maxLength={160} /></label>
      <label>Mô tả<textarea name="moTa" defaultValue={cu?.moTa ?? ''} required maxLength={1000} /></label>
      <button disabled={tacVu.dangGui}>Lưu và áp dụng</button>
      {cu && <NutXoaQuanTri nhan="Xóa bản tùy chỉnh" doiTuong={cu.ten} duongDan={goc + '/noi-dung/' + loai + '/' + ma}
        sauXoa={ds.taiLai} disabled={tacVu.dangGui} />}
    </form>}
    <PhanHoi {...tacVu} />
  </Khoi>
}

type MauVoucher = {
  loai: string; giaTri: number; giamToiDa: number | null; donToiThieu: number | null
  batDau: string; ketThuc: string; luotToiDa: number; kichHoat: boolean
}

export function VoucherHanhTrinh() {
  const url = goc + '/voucher-hanh-trinh'
  const ds = useDuLieu<CauHinhVoucher[]>(url)
  return <Khoi tieuDe="Voucher hành trình Hệ Mặt Trời">
    <TrangThaiTai {...ds} />
    {ds.duLieu && <BieuMauVoucher url={url} cu={ds.duLieu[0]} taiLai={ds.taiLai} />}
  </Khoi>
}

type CauHinhVoucher = { mau: MauVoucher; luotDaCap: number }

function BieuMauVoucher({ url, cu, taiLai }: { url: string; cu?: CauHinhVoucher | null; taiLai: () => void }) {
  const tacVu = useThaoTac()
  const m = cu?.mau
  return <>
    {cu && <p>Đã cấp {cu.luotDaCap} / {cu.mau.luotToiDa} voucher.</p>}
    <form className="bh-form" onSubmit={e => {
      e.preventDefault()
      const f = new FormData(e.currentTarget)
      void tacVu.lam(async () => {
        await ghi(url, 'PUT', {
          loai: nhap(f,'loai'), giaTri: Number(nhap(f,'giaTri')), giamToiDa: soTuyChon(f,'giamToiDa'),
          donToiThieu: soTuyChon(f,'donToiThieu'), batDau: nhap(f,'batDau'), ketThuc: nhap(f,'ketThuc'),
          luotToiDa: Number(nhap(f,'luotToiDa')), kichHoat: f.has('kichHoat'),
        })
        taiLai()
      })
    }}>
      <label>Loại giảm<select name="loai" defaultValue={m?.loai}><option value="SO_TIEN">Số tiền</option>
        <option value="PHAN_TRAM">Phần trăm</option></select></label>
      <label>Giá trị<input name="giaTri" type="number" step=".01" min=".01" required defaultValue={m?.giaTri} /></label>
      <label>Giảm tối đa (VND)<input name="giamToiDa" type="number" step=".01" min={0} defaultValue={m?.giamToiDa ?? ''} /></label>
      <label>Đơn tối thiểu (VND)<input name="donToiThieu" type="number" step=".01" min={0} defaultValue={m?.donToiThieu ?? ''} /></label>
      <label>Bắt đầu (UTC)<input name="batDau" type="datetime-local" required defaultValue={m?.batDau.slice(0,16)} /></label>
      <label>Kết thúc (UTC)<input name="ketThuc" type="datetime-local" required defaultValue={m?.ketThuc.slice(0,16)} /></label>
      <label>Tổng số lượt cấp<input name="luotToiDa" type="number" min={Math.max(1, cu?.luotDaCap ?? 0)} required defaultValue={m?.luotToiDa} /></label>
      <label className="bh-check"><input name="kichHoat" type="checkbox" defaultChecked={m?.kichHoat} /> Cho phép cấp voucher</label>
      <button disabled={tacVu.dangGui}>Lưu cấu hình voucher</button>
      {cu && <NutXoaQuanTri nhan="Xóa cấu hình voucher" doiTuong="cấu hình voucher"
        duongDan={url} sauXoa={taiLai} disabled={tacVu.dangGui} />}
    </form>
    <PhanHoi {...tacVu} />
  </>
}

const dieuKienNhiemVu = {
  THIEN_THE: 'Khám phá thiên thể', CHUONG: 'Hoàn thành chương', DAU_HIEU_AN: 'Tìm dấu hiệu ẩn',
}
type LoaiDieuKien = keyof typeof dieuKienNhiemVu
type NhiemVuQuanTri = {
  id: number; voucher: CauHinhVoucher | null
  duLieu: {
    ten: string; moTa: string; loaiDieuKien: LoaiDieuKien; maDieuKien: string | null
    xp: number; tenHuyHieu: string | null; coVoucher: boolean; kichHoat: boolean
  }
}

export function NhiemVuQuanTri() {
  const url = goc + '/nhiem-vu'
  const ds = useDuLieu<NhiemVuQuanTri[]>(url)
  const tacVu = useThaoTac()
  const [loai, datLoai] = useState<LoaiDieuKien>('THIEN_THE')
  return <Khoi tieuDe="Quản lý nhiệm vụ">
    <TrangThaiTai {...ds} />
    <form className="bh-form" onSubmit={e => {
      e.preventDefault()
      const form = e.currentTarget
      const f = new FormData(form)
      void tacVu.lam(async () => {
        await ghi(url, 'POST', {
          ten: nhap(f,'ten'), moTa: nhap(f,'moTa'), loaiDieuKien: loai, maDieuKien: nhap(f,'maDieuKien') || null,
          xp: Number(nhap(f,'xp')), tenHuyHieu: nhap(f,'tenHuyHieu') || null,
          coVoucher: f.has('coVoucher'), kichHoat: f.has('kichHoat'),
        })
        form.reset()
        datLoai('THIEN_THE')
        ds.taiLai()
      })
    }}>
      <label>Tên nhiệm vụ<input name="ten" required maxLength={160} /></label>
      <label>Mô tả<textarea name="moTa" required maxLength={1000} /></label>
      <label>Điều kiện<select value={loai} onChange={e => datLoai(e.target.value as LoaiDieuKien)}>
        {Object.entries(dieuKienNhiemVu).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
      </select></label>
      {loai === 'THIEN_THE' && <label>Thiên thể<select name="maDieuKien">
        {cacThienThe.filter(t => t.ma !== 'sun').map(t => <option key={t.ma} value={t.ma}>{t.ten}</option>)}
      </select></label>}
      {loai === 'CHUONG' && <label>Chương<select name="maDieuKien">
        {[1,2,3].map(so => <option key={so} value={so}>{so}</option>)}
      </select></label>}
      <label>XP<input name="xp" type="number" min={0} max={2147483647} defaultValue={0} required /></label>
      <label>Tên huy hiệu (nếu có)<input name="tenHuyHieu" maxLength={160} /></label>
      <label className="bh-check"><input name="coVoucher" type="checkbox" /> Có phần thưởng voucher</label>
      <label className="bh-check"><input name="kichHoat" type="checkbox" /> Kích hoạt</label>
      <button disabled={tacVu.dangGui}>Tạo nhiệm vụ</button>
    </form>
    <PhanHoi {...tacVu} />
    {ds.duLieu?.length === 0 && <p>Chưa có nhiệm vụ do Admin tạo.</p>}
    {ds.duLieu?.map(n => <details key={n.id} className="bh-dong">
      <summary>{n.duLieu.ten} · {n.duLieu.kichHoat ? 'Đang mở' : 'Đã tắt'}</summary>
      <p>{dieuKienNhiemVu[n.duLieu.loaiDieuKien]}{n.duLieu.maDieuKien &&
        ' · ' + (cacThienThe.find(t => t.ma === n.duLieu.maDieuKien)?.ten ?? n.duLieu.maDieuKien)}</p>
      <p>{[n.duLieu.xp > 0 && n.duLieu.xp + ' XP', n.duLieu.tenHuyHieu, n.duLieu.coVoucher && 'Voucher'].filter(Boolean).join(' · ')}</p>
      <form className="bh-form" onSubmit={e => {
        e.preventDefault()
        const f = new FormData(e.currentTarget)
        void tacVu.lam(async () => {
          await ghi(url + '/' + n.id, 'PUT', { ten: nhap(f,'ten'), moTa: nhap(f,'moTa'), kichHoat: f.has('kichHoat') })
          ds.taiLai()
        })
      }}>
        <label>Tên nhiệm vụ<input name="ten" defaultValue={n.duLieu.ten} required maxLength={160} /></label>
        <label>Mô tả<textarea name="moTa" defaultValue={n.duLieu.moTa} required maxLength={1000} /></label>
        <label className="bh-check"><input name="kichHoat" type="checkbox" defaultChecked={n.duLieu.kichHoat} /> Kích hoạt</label>
        <button disabled={tacVu.dangGui}>Lưu nhiệm vụ</button>
        <NutXoaQuanTri nhan="Xóa nhiệm vụ" doiTuong={n.duLieu.ten} duongDan={url + '/' + n.id}
          sauXoa={ds.taiLai} disabled={tacVu.dangGui} />
      </form>
      {n.duLieu.coVoucher && <BieuMauVoucher url={url + '/' + n.id + '/voucher'} cu={n.voucher} taiLai={ds.taiLai} />}
    </details>)}
  </Khoi>
}

type SuKien = { id: number; ten: string; moTa: string; batDau: string; ketThuc: string; hienThi: boolean }
export function SuKienQuanTri() {
  const url = goc + '/su-kien'
  const ds = useDuLieu<SuKien[]>(url)
  const [id, datId] = useState(0)
  const tacVu = useThaoTac()
  const d = ds.duLieu?.find(s => s.id === id)
  return <Khoi tieuDe="Sự kiện khám phá">
    <TrangThaiTai {...ds} />
    {ds.duLieu && <>
      <label>Sự kiện<select value={id} onChange={e => datId(Number(e.target.value))}>
        <option value={0}>Tạo sự kiện</option>
        {ds.duLieu.map(s => <option key={s.id} value={s.id}>{s.ten}</option>)}
      </select></label>
      <form className="bh-form" key={id} onSubmit={e => {
        e.preventDefault()
        const form = e.currentTarget
        const f = new FormData(form)
        void tacVu.lam(async () => {
          await ghi(id ? url + '/' + id : url, id ? 'PUT' : 'POST', {
            ten: nhap(f,'ten'), moTa: nhap(f,'moTa'), batDau: nhap(f,'batDau'),
            ketThuc: nhap(f,'ketThuc'), hienThi: f.has('hienThi'),
          })
          if (!id) form.reset()
          ds.taiLai()
        })
      }}>
        <label>Tên sự kiện<input name="ten" required maxLength={160} defaultValue={d?.ten} /></label>
        <label>Mô tả<textarea name="moTa" required maxLength={1000} defaultValue={d?.moTa} /></label>
        <label>Bắt đầu (UTC)<input name="batDau" type="datetime-local" required defaultValue={d?.batDau.slice(0,16)} /></label>
        <label>Kết thúc (UTC)<input name="ketThuc" type="datetime-local" required defaultValue={d?.ketThuc.slice(0,16)} /></label>
        <label className="bh-check"><input name="hienThi" type="checkbox" defaultChecked={d?.hienThi ?? true} /> Hiển thị trong thời gian sự kiện</label>
        <button disabled={tacVu.dangGui}>{id ? 'Lưu sự kiện' : 'Tạo sự kiện'}</button>
        {d && <NutXoaQuanTri nhan="Xóa sự kiện" doiTuong={d.ten} duongDan={url + '/' + id}
          sauXoa={() => { datId(0); ds.taiLai() }} disabled={tacVu.dangGui} />}
      </form>
    </>}
    <PhanHoi {...tacVu} />
  </Khoi>
}
