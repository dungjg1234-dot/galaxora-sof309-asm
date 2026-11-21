import { lazy, Suspense, useEffect, useId, useRef, useState, type FormEvent } from 'react'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'
import { duongDanDangNhap } from '../pages/duongDanTaiKhoan.ts'
import AnhSanPham from './AnhSanPham.tsx'
import BoAnhSanPham from '../trangchu/BoAnhSanPham.tsx'
import { timAnhSanPham } from './anhSanPham.ts'
import { ghi, thamSo } from './api.ts'
import { dinhDangTien, nhanKho, type ChiTietSanPham, type DanhGia, type DanhMuc, type Gio,
  type PhanTrang as Trang, type SanPham } from './duLieu.ts'
import { Khoi, nhap, PhanHoi, PhanTrang, TrangThaiTai, useDuLieu, useThaoTac } from './dungChung.tsx'
import { LoiApi } from '../services/apiClient.ts'

const XemMoHinh = lazy(() => import('./XemMoHinh.tsx'))
const api = '/api/cua-hang'

function Gia({ s }: { s: SanPham }) {
  return <p className="bh-gia-sp"><strong>{dinhDangTien(s.gia)}</strong>
    {s.giaSoSanh !== null && s.giaSoSanh > s.gia && <del>{dinhDangTien(s.giaSoSanh)}</del>}</p>
}

export type QuyenMua = { coQuyen: boolean; laKhach: boolean; taiLaiGio: () => void; gio?: Gio }

function ThemGio({ s, coQuyen, laKhach, taiLaiGio, gio, gon = false }: QuyenMua & { s: SanPham; gon?: boolean }) {
  const tacVu = useThaoTac()
  const daCo = gio?.chiTiet.find(dong => dong.sanPhamId === s.id)?.soLuong ?? 0
  const toiDa = s.soLuongKhaDung == null ? undefined : Math.max(0, s.soLuongKhaDung - daCo)
  const hetHang = !s.kinhDoanh || s.trangThaiKho === 'HET_HANG'
  if (hetHang || !coQuyen || toiDa === 0) {
    if (!hetHang && laKhach) return <a className="bh-nut" href={duongDanDangNhap('/cua-hang/san-pham/' + s.id)}
      aria-label="Thêm vào giỏ · Đăng nhập để mua">Thêm vào giỏ</a>
    return <div className="bh-them-gio">
      <button type="button" className="bh-chinh" disabled>Thêm vào giỏ</button>
      {!hetHang && coQuyen && toiDa === 0 && <p className="bh-nho">Giỏ đã có đủ số lượng khả dụng.</p>}
    </div>
  }
  function them(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const soLuong = gon ? 1 : Number(new FormData(e.currentTarget).get('soLuong'))
    void tacVu.lam(async () => {
      await ghi('/api/gio-hang/' + s.id, 'POST', { soLuong })
      taiLaiGio()
    }, 'Đã thêm vào giỏ hàng.')
  }
  return <div className="bh-them-gio"><form onSubmit={them}>
    {!gon && <label>Số lượng <input className="bh-so" type="number" name="soLuong" min="1" max={toiDa} step="1" defaultValue="1" required /></label>}
    <button className="bh-chinh" disabled={tacVu.dangGui}><BieuTuong ten="gio" />
      {tacVu.dangGui ? 'Đang thêm…' : 'Thêm vào giỏ'}</button>
  </form><PhanHoi {...tacVu} />
    {tacVu.thongBao && !gon && <a href="/gio-hang">Xem giỏ hàng</a>}
  </div>
}

function TheSanPham({ s, xemNhanh, quyen }: { s: SanPham; xemNhanh?: (s: SanPham) => void; quyen: QuyenMua }) {
  const duongDan = '/cua-hang/san-pham/' + s.id
  return <article className="bh-the-sp">
    <a className="bh-anh" href={duongDan} aria-label={'Xem chi tiết ' + s.ten}>
      <AnhSanPham anh={timAnhSanPham(s)} ten="" />
    </a>
    <div className="bh-the-noi-dung">
      <p className="bh-nho">{s.danhMuc}</p>
      <h2><a href={duongDan} title={s.ten}>{s.ten}</a></h2>
      <Gia s={s} />
      <p className="bh-nhan-kho" data-kho={s.kinhDoanh ? s.trangThaiKho : 'NGUNG_BAN'}>{s.kinhDoanh ? nhanKho[s.trangThaiKho] : 'Ngừng bán'}</p>
      <div className="bh-hanh-dong-sp">
        <a className="bh-nut" href={duongDan} aria-label={`Xem chi tiết ${s.ten}`}>Xem chi tiết</a>
        <ThemGio s={s} {...quyen} gon />
        {xemNhanh && <button type="button" className="bh-xem-nhanh" onClick={() => xemNhanh(s)}>Xem nhanh</button>}
      </div>
    </div>
  </article>
}

export function DanhSachSanPham({ yeuThich = false, ...quyen }: QuyenMua & { yeuThich?: boolean }) {
  const [tim, datTim] = useState(() => {
    const danhMucId = new URLSearchParams(window.location.search).get('danhMucId') ?? ''
    return { tuKhoa: '', danhMucId: /^[1-9]\d*$/.test(danhMucId) ? danhMucId : '', sapXep: 'moi' }
  })
  const [trang, datTrang] = useState(0)
  const muc = useDuLieu<DanhMuc[]>(yeuThich ? null : api + '/danh-muc')
  const sp = useDuLieu<Trang<SanPham>>(yeuThich ? null : api + '/san-pham' + thamSo({ ...tim, trang }))
  const dsYeuThich = useDuLieu<SanPham[]>(yeuThich ? api + '/yeu-thich' : null)
  const duLieu = yeuThich ? dsYeuThich.duLieu : sp.duLieu?.noiDung
  return <>
    <div className="bh-dau-trang">
      <h1>{yeuThich ? 'Sản phẩm yêu thích' : 'Cửa hàng GALAXORA'}</h1>
    </div>
    {!yeuThich && <form className="bh-loc" onSubmit={e => {
      e.preventDefault()
      const f = new FormData(e.currentTarget)
      datTim({ tuKhoa: nhap(f, 'tuKhoa'), danhMucId: nhap(f, 'danhMucId'), sapXep: nhap(f, 'sapXep') })
      datTrang(0)
    }}>
      <label>Tìm sản phẩm<input type="search" name="tuKhoa" maxLength={160} placeholder="Tên hoặc SKU" /></label>
      <label>Danh mục<select name="danhMucId" defaultValue={tim.danhMucId} key={muc.duLieu ? "da-tai" : "dang-tai"}><option value="">Tất cả danh mục</option>
        {muc.duLieu?.map(d => <option key={d.id} value={d.id}>{d.ten}</option>)}</select></label>
      <label>Sắp xếp<select name="sapXep"><option value="moi">Mới nhất</option><option value="gia-tang">Giá tăng dần</option>
        <option value="gia-giam">Giá giảm dần</option><option value="ten">Tên sản phẩm</option></select></label>
      <button className="bh-chinh">Tìm kiếm</button>
    </form>}
    <TrangThaiTai {...(yeuThich ? dsYeuThich : sp)} />
    {muc.loi && <p className="bh-loi" role="alert">Không tải được danh mục.</p>}
    {duLieu?.length === 0 && <p className="bh-trong">{yeuThich ? 'Danh sách yêu thích đang trống.' : 'Không tìm thấy sản phẩm phù hợp.'}</p>}
    {duLieu && duLieu.length > 0 && <LuoiSanPham danhSach={duLieu} quyen={quyen} />}
    {sp.duLieu && <PhanTrang trang={trang} coTrangSau={(trang + 1) * sp.duLieu.kichThuoc < sp.duLieu.tong} datTrang={datTrang} />}
  </>
}

export function LuoiSanPham({ danhSach, quyen }: { danhSach: SanPham[]; quyen: QuyenMua }) {
  const [nhanh, datNhanh] = useState<SanPham | null>(null)
  const dialog = useRef<HTMLDialogElement>(null)
  const tieuDe = useId()
  useEffect(() => {
    if (nhanh && !dialog.current?.open) dialog.current?.showModal()
  }, [nhanh])
  return <>
    <div className="bh-luoi-sp">{danhSach.map(s => <TheSanPham key={s.id} s={s} xemNhanh={datNhanh} quyen={quyen} />)}</div>
    <dialog ref={dialog} className="bh-dialog" onClose={() => datNhanh(null)} aria-labelledby={tieuDe}>
      {nhanh && <div>
        <div className="bh-hang bh-can"><h2 id={tieuDe}>{nhanh.ten}</h2>
          <button onClick={() => dialog.current?.close()}>Đóng</button></div>
        <AnhSanPham anh={timAnhSanPham(nhanh)} ten={nhanh.ten} lon />
        <p>{nhanh.moTaNgan}</p><Gia s={nhanh} /><ThemGio s={nhanh} {...quyen} />
        <a href={'/cua-hang/san-pham/' + nhanh.id}>Xem chi tiết</a>
      </div>}
    </dialog>
  </>
}

export function ChiTietCuaHang({ id, ...quyen }: QuyenMua & { id: number }) {
  const duLieu = useDuLieu<ChiTietSanPham>(api + '/san-pham/' + id)
  const danhGia = useDuLieu<DanhGia[]>(api + '/san-pham/' + id + '/danh-gia')
  const yeuThich = useDuLieu<SanPham[]>(quyen.coQuyen ? api + '/yeu-thich' : null)
  const tacVu = useThaoTac()
  const [mo3d, datMo3d] = useState(false)
  const daYeuThich = yeuThich.duLieu?.some(s => s.id === id) ?? false
  const ct = duLieu.duLieu
  if (duLieu.loi instanceof LoiApi && duLieu.loi.maTrangThai === 404) {
    return <section className="bh-trong gx-tien-ich-noi-dung">
      <h1>Không tìm thấy sản phẩm</h1>
      <p>Sản phẩm có thể đã được ẩn hoặc đường dẫn đã thay đổi.</p>
      <a className="bh-nut" href="/cua-hang">Về cửa hàng</a>
    </section>
  }
  if (!ct) return <TrangThaiTai {...duLieu} />
  const s = ct.sanPham
  return <>
    <nav className="bh-duong-dan-sp" aria-label="Đường dẫn sản phẩm">
      <a href="/">Trang chủ</a><span aria-hidden="true">/</span>
      <a href="/cua-hang">Cửa hàng</a><span aria-hidden="true">/</span>
      <a href={`/cua-hang?danhMucId=${s.danhMucId}`}>{s.danhMuc}</a><span aria-hidden="true">/</span>
      <span aria-current="page">{s.ten}</span>
    </nav>
    <div className="bh-chi-tiet-sp">
      <div>
        <BoAnhChiTiet s={s} hinhAnh={ct.hinhAnh} />
      </div>
      <div className="bh-khoi bh-thong-tin-sp"><p className="bh-nho">{s.danhMuc} · {s.sku}</p><h1>{s.ten}</h1>
        <Gia s={s} /><p>{s.kinhDoanh ? nhanKho[s.trangThaiKho] : 'Ngừng bán'}</p>
        {s.moTaNgan && <p className="bh-mo-ta">{s.moTaNgan}</p>}
        <ThemGio s={s} {...quyen} />
        <div className="bh-hang">
          {quyen.coQuyen && <button disabled={tacVu.dangGui || !yeuThich.duLieu} onClick={() => void tacVu.lam(async () => {
            await ghi(api + '/yeu-thich/' + id, 'PUT', { yeuThich: !daYeuThich }); yeuThich.taiLai()
          })}>{daYeuThich ? 'Bỏ yêu thích' : 'Thêm yêu thích'}</button>}
          {s.coMoHinh && <button onClick={() => datMo3d(!mo3d)} aria-expanded={mo3d}>{mo3d ? 'Đóng mô hình 3D' : 'Xem mô hình 3D'}</button>}
        </div>
        <PhanHoi {...tacVu} />
      </div>
    </div>
    {s.moTa && s.moTa !== s.moTaNgan && <Khoi tieuDe="Mô tả sản phẩm"><p className="bh-mo-ta">{s.moTa}</p></Khoi>}
    {mo3d && <Suspense fallback={<p role="status">Đang tải trình xem 3D…</p>}><XemMoHinh id={id} /></Suspense>}
    <Khoi tieuDe="Đánh giá sản phẩm"><TrangThaiTai {...danhGia} />
      {danhGia.duLieu?.length === 0 && <p>Chưa có đánh giá.</p>}
      {danhGia.duLieu?.map((d, i) => <article key={i} className="bh-dong">
        <strong>{d.tacGia} · {d.soSao}/5 sao</strong><p className="bh-mo-ta">{d.noiDung}</p>
      </article>)}
    </Khoi>
    {ct.lienQuan.length > 0 && <section><h2>Sản phẩm liên quan</h2><LuoiSanPham danhSach={ct.lienQuan} quyen={quyen} /></section>}
  </>
}

function BoAnhChiTiet({ s, hinhAnh }: { s: SanPham; hinhAnh: ChiTietSanPham['hinhAnh'] }) {
  const [chon, datChon] = useState<string | null>(null)
  const anhBia = timAnhSanPham(s)
  const danhSach = new Map(anhBia ? [[anhBia, { duongDan: anhBia, moTa: s.ten }]] : [])
  for (const anh of hinhAnh) {
    if (timAnhSanPham({ ...s, anh: anh.duongDan }) === anh.duongDan) danhSach.set(anh.duongDan, anh)
  }
  const anhChon = (chon ? danhSach.get(chon) : undefined) ?? danhSach.values().next().value
  return <>
    <BoAnhSanPham anh={anhChon} danhSach={[...danhSach.values()]} ten={s.ten} />
    {danhSach.size > 1 && <div className="bh-anh-phu" role="group" aria-label="Ảnh sản phẩm">
      {[...danhSach.values()].map((anh, i) => <button type="button" key={anh.duongDan}
        aria-pressed={anhChon?.duongDan === anh.duongDan} aria-label={`Xem ảnh ${i + 1}`}
        onClick={() => datChon(anh.duongDan)}><AnhSanPham anh={anh.duongDan} ten="" /></button>)}
    </div>}
  </>
}
