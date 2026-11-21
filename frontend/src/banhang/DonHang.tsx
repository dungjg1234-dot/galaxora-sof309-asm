import { useState, type FormEvent } from 'react'
import { ghi } from './api.ts'
import { dinhDangGio, dinhDangTien, nhanDon, nhanHuy, type ChiTietDon, type DanhGia, type Don,
  type DongDon, type TrangThaiDon, type YeuCauHuy } from './duLieu.ts'
import { ChiPhi, Khoi, nhap, PhanHoi, PhanTrang, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'

function duongDan(quanTri: boolean) { return quanTri ? '/quan-tri/don-hang' : '/don-hang' }

export function DanhSachDon({ quanTri = false }: { quanTri?: boolean }) {
  const [trang, datTrang] = useState(0)
  const url = duongDan(quanTri)
  const don = useDuLieu<Don[]>('/api' + url + '?trang=' + trang)
  return <>
    <div className="bh-dau-trang"><h1>{quanTri ? 'Quản lý đơn hàng' : 'Đơn hàng của bạn'}</h1></div>
    <TrangThaiTai {...don} />
    {don.duLieu?.length === 0 && <p className="bh-trong">Chưa có đơn hàng trên trang này.</p>}
    <div className="bh-khoi">
      {don.duLieu?.map(d => <div className="bh-dong bh-hang bh-can" key={d.id}>
        <div><strong><a href={url + '/' + d.id}>{d.ma}</a></strong>
          <p>{dinhDangGio(d.thoiDiem)} · {quanTri ? d.nguoiNhan + ' · ' : ''}{nhanDon[d.trangThai]}</p></div>
        <strong>{dinhDangTien(d.tongTien)}</strong>
      </div>)}
    </div>
    {don.duLieu && <PhanTrang trang={trang} coTrangSau={don.duLieu.length === 20} datTrang={datTrang} />}
  </>
}

const trangThaiKe: Partial<Record<TrangThaiDon, TrangThaiDon[]>> = {
  CHO_XAC_NHAN: ['DA_XAC_NHAN', 'DA_HUY'],
  DA_XAC_NHAN: ['DANG_CHUAN_BI'],
  DANG_CHUAN_BI: ['DANG_GIAO'],
  DANG_GIAO: ['DA_GIAO_THANH_CONG', 'GIAO_THAT_BAI'],
}

function ChuyenDon({ don, lam, dangGui, choDuyet }: { don: Don; dangGui: boolean; choDuyet: boolean
  lam: (t: TrangThaiDon, lyDo: string, daThuTien: boolean) => void }) {
  const cacTrangThai = (trangThaiKe[don.trangThai] ?? []).filter(t => !choDuyet || t !== 'DANG_GIAO')
  const [chon, datChon] = useState<TrangThaiDon>(cacTrangThai[0] ?? don.trangThai)
  if (!cacTrangThai.length) return choDuyet ? <p>Xử lý yêu cầu hủy trước khi giao hàng.</p> : null
  function gui(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const f = new FormData(e.currentTarget)
    if (window.confirm('Xác nhận cập nhật trạng thái đơn hàng?')) lam(chon, nhap(f, 'lyDo'), f.has('daThuTien'))
  }
  return <form className="bh-form" onSubmit={gui}>
    <label>Trạng thái tiếp theo<select value={chon} onChange={e => datChon(e.target.value as TrangThaiDon)}>
      {cacTrangThai.map(t => <option key={t} value={t}>{nhanDon[t]}</option>)}
    </select></label>
    <label>Lý do<textarea name="lyDo" maxLength={500} required={chon === 'DA_HUY' || chon === 'GIAO_THAT_BAI'} /></label>
    {chon === 'DA_GIAO_THANH_CONG' && <label className="bh-check"><input type="checkbox" name="daThuTien" required /> Đã thu tiền COD</label>}
    <button className="bh-chinh" disabled={dangGui}>Cập nhật đơn</button>
  </form>
}

function SuaDanhGia({ id, dong, cu, taiLai }: { id: number; dong: DongDon; cu?: DanhGia; taiLai: () => void }) {
  const tacVu = useThaoTac()
  return <form className="bh-dong bh-form" onSubmit={e => {
    e.preventDefault()
    const f = new FormData(e.currentTarget)
    void tacVu.lam(async () => {
      await ghi('/api/danh-gia/' + id + '/' + dong.sanPhamId, 'PUT', { soSao: Number(f.get('soSao')), noiDung: nhap(f, 'noiDung') })
      taiLai()
    }, 'Đã lưu đánh giá.')
  }}>
    <strong>{dong.ten}</strong>
    <label>Điểm đánh giá<select name="soSao" defaultValue={cu?.soSao ?? 5}>
      {[5, 4, 3, 2, 1].map(s => <option key={s} value={s}>{s} sao</option>)}</select></label>
    <label>Nội dung<textarea name="noiDung" required maxLength={2000} defaultValue={cu?.noiDung} /></label>
    <button disabled={tacVu.dangGui}>{cu ? 'Lưu chỉnh sửa' : 'Gửi đánh giá'}</button>
    <PhanHoi {...tacVu} />
  </form>
}

export function TrangChiTietDon({ id, quanTri = false }: { id: number; quanTri?: boolean }) {
  const api = '/api' + duongDan(quanTri) + '/' + id
  const ct = useDuLieu<ChiTietDon>(api)
  const daGiao = ct.duLieu?.don.trangThai === 'DA_GIAO_THANH_CONG'
  const dsDanhGia = useDuLieu<DanhGia[]>(!quanTri && daGiao ? '/api/danh-gia/' + id : null)
  const tacVu = useThaoTac()
  function lam(duoi: string, duLieu?: object) {
    void tacVu.lam(async () => { await ghi(api + duoi, 'POST', duLieu); ct.taiLai() })
  }
  if (!ct.duLieu) return <TrangThaiTai {...ct} />
  const { don, chiTiet, lichSu, yeuCauHuy } = ct.duLieu
  const choDuyet = yeuCauHuy.find(y => y.trangThai === 'CHO_DUYET')
  const coTheYeuCauHuy = don.trangThai === 'DA_XAC_NHAN' || don.trangThai === 'DANG_CHUAN_BI'
  return <>
    <a href={duongDan(quanTri)}>Về danh sách đơn hàng</a>
    <div className="bh-dau-trang"><h1>{don.ma}</h1><p>{nhanDon[don.trangThai]} · {don.daThuTien ? 'Đã thu tiền COD' : 'Chưa thu tiền COD'}</p></div>
    <div className="bh-hai-cot">
      <Khoi tieuDe="Sản phẩm">
        {chiTiet.map(d => <div className="bh-dong bh-hang bh-can" key={d.sanPhamId}>
          <span>{d.ten} × {d.soLuong}</span><span>{dinhDangTien(d.donGia)} / sản phẩm</span></div>)}
        <ChiPhi tien={don} />
      </Khoi>
      <Khoi tieuDe="Thông tin nhận hàng">
        <p>{don.nguoiNhan} · {don.soDienThoai}</p><p>{don.diaChi}</p>{don.ghiChu && <p>{don.ghiChu}</p>}
        {quanTri && <ChuyenDon key={don.trangThai + ':' + (choDuyet?.id ?? '')} don={don} choDuyet={!!choDuyet}
          dangGui={tacVu.dangGui} lam={(trangThai, lyDo, daThuTien) => lam('/trang-thai', { trangThai, lyDo, daThuTien })} />}
        {!quanTri && don.trangThai === 'CHO_XAC_NHAN' && <button disabled={tacVu.dangGui}
          onClick={() => { if (window.confirm('Hủy đơn hàng này?')) lam('/huy') }}>Hủy đơn</button>}
        {!quanTri && coTheYeuCauHuy && !choDuyet && <form className="bh-form" onSubmit={e => {
          e.preventDefault()
          const lyDo = nhap(new FormData(e.currentTarget), 'lyDo')
          if (window.confirm('Gửi yêu cầu hủy để Admin xem xét?')) lam('/yeu-cau-huy', { lyDo })
        }}><label>Lý do hủy<textarea name="lyDo" required maxLength={500} /></label>
          <button disabled={tacVu.dangGui}>Gửi yêu cầu hủy</button></form>}
        <PhanHoi {...tacVu} />
      </Khoi>
    </div>
    {yeuCauHuy.length > 0 && <Khoi tieuDe="Yêu cầu hủy">
      {yeuCauHuy.map(y => <div className="bh-dong" key={y.id}>
        <strong>{nhanHuy[y.trangThai]}</strong><p>{y.lyDo}</p>
        {y.lyDoXuLy && <p>Lý do xử lý: {y.lyDoXuLy}</p>}
        {quanTri && y.trangThai === 'CHO_DUYET' && <XuLyYeuCau y={y} dangGui={tacVu.dangGui}
          lam={(duyet, lyDo) => lam('/yeu-cau-huy/' + y.id, { duyet, lyDo })} />}
      </div>)}
    </Khoi>}
    <Khoi tieuDe="Lịch sử đơn hàng"><ol>
      {lichSu.map((s, i) => <li key={i} className="bh-dong"><strong>{nhanDon[s.trangThai]}</strong>
        {' · ' + dinhDangGio(s.thoiDiem)}{s.lyDo && <p>{s.lyDo}</p>}</li>)}
    </ol></Khoi>
    {!quanTri && daGiao && <Khoi tieuDe="Đánh giá sản phẩm">
      <TrangThaiTai {...dsDanhGia} />
      {dsDanhGia.duLieu && chiTiet.map(d => <SuaDanhGia key={d.sanPhamId} id={id} dong={d}
        cu={dsDanhGia.duLieu?.find(g => g.sanPhamId === d.sanPhamId)} taiLai={dsDanhGia.taiLai} />)}
    </Khoi>}
  </>
}

function XuLyYeuCau({ y, lam, dangGui }: { y: YeuCauHuy; dangGui: boolean; lam: (duyet: boolean, lyDo: string) => void }) {
  return <form className="bh-form" onSubmit={e => {
    e.preventDefault()
    const f = new FormData(e.currentTarget)
    const nut = (e.nativeEvent as SubmitEvent).submitter as HTMLButtonElement | null
    if (window.confirm('Xác nhận xử lý yêu cầu hủy?')) lam(nut?.value === 'duyet', nhap(f, 'lyDo'))
  }}>
    <label htmlFor={'ly-do-huy-' + y.id}>Lý do xử lý</label>
    <textarea id={'ly-do-huy-' + y.id} name="lyDo" required maxLength={500} />
    <div className="bh-hang"><button value="duyet" disabled={dangGui}>Duyệt hủy</button>
      <button value="tu-choi" disabled={dangGui}>Từ chối</button></div>
  </form>
}
