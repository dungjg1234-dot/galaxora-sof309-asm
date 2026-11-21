import { lazy, Suspense, useCallback, useEffect, useRef, useState, type CSSProperties } from 'react'
import { bangSoLieuNasa, soLieuNasa } from './soLieuNasa.ts'
import { anhBeMat, ghiCong, ghiCongBanDo, giayPhep } from './taiNguyenHeMatTroi.ts'
import { cacThienThe, theoMa, type MaThienThe, type ThienThe } from './thienThe.ts'
import type { GocNhin, TrangThaiMoHinh } from './lapCanhHeMatTroi.ts'
import TienDoKhamPha from './TienDoKhamPha.tsx'
import { useDuLieu } from '../banhang/dungChung.tsx'

const CanhHeMatTroi = lazy(() => import('./CanhHeMatTroi.tsx'))
const CanhGoc = lazy(() => import('./CanhKhuVucVuTru.tsx'))
const dinhDang = new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 2 })

function HinhThienThe({ thienThe }: { thienThe: ThienThe }) {
  const anh = anhBeMat(thienThe.ma)
  return <span className={'kp-hanh-tinh' + (thienThe.ma === 'saturn' ? ' kp-co-vanh' : '')}
    aria-hidden="true" style={{ '--kp-mau': thienThe.mau, '--kp-map': anh ? 'url("' + anh + '")' : 'none' } as CSSProperties} />
}

function DockThienThe({ ma, chon }: { ma: MaThienThe | null; chon: (ma: MaThienThe) => void }) {
  const danhSach = useRef<HTMLDivElement>(null)
  const [canh, datCanh] = useState({ dau: true, cuoi: true })
  const capNhatCanh = useCallback(() => {
    const day = danhSach.current
    if (day) datCanh({ dau: day.scrollLeft <= 1, cuoi: day.scrollLeft + day.clientWidth >= day.scrollWidth - 1 })
  }, [])

  useEffect(() => {
    const day = danhSach.current
    if (!day) return
    const theoDoi = new ResizeObserver(capNhatCanh)
    theoDoi.observe(day)
    capNhatCanh()
    return () => theoDoi.disconnect()
  }, [capNhatCanh])

  function cuon(huong: -1 | 1) {
    const day = danhSach.current
    if (!day) return
    const giamChuyenDong = window.matchMedia('(prefers-reduced-motion: reduce)').matches
    day.scrollBy({ left: huong * day.clientWidth * .8, behavior: giamChuyenDong ? 'instant' : 'smooth' })
  }

  return <nav className="kp-dock" aria-label="Chọn thiên thể">
    <button className="kp-dock-mui-ten" type="button" aria-label="Cuộn về đầu danh sách"
      disabled={canh.dau} onClick={() => cuon(-1)}>‹</button>
    <div className="kp-danh-sach" ref={danhSach} onScroll={capNhatCanh}>
      {cacThienThe.map(t => <button key={t.ma} type="button" aria-pressed={ma === t.ma} onClick={() => chon(t.ma)}>
        <HinhThienThe thienThe={t} /><span>{t.ten}</span>
      </button>)}
    </div>
    <button className="kp-dock-mui-ten" type="button" aria-label="Cuộn về cuối danh sách"
      disabled={canh.cuoi} onClick={() => cuon(1)}>›</button>
  </nav>
}

function SoDo({ daChon }: { daChon: ThienThe | null }) {
  return <div className="kp-so-do">
    <p className="kp-thong-bao" role="status">Đang dùng chế độ 2D. Bạn vẫn có thể chọn thiên thể và xem thông tin.</p>
    {daChon ? <div className="kp-2d-chon"><HinhThienThe thienThe={daChon} /><strong>{daChon.ten}</strong></div> :
      <div className="kp-2d-day" aria-label="Các thiên thể trong mô phỏng">
        {cacThienThe.map(t => <span key={t.ma}><HinhThienThe thienThe={t} />{t.ten}</span>)}
      </div>}
  </div>
}

function ThongTin({ thienThe, noiDung }: { thienThe: ThienThe | null; noiDung?: { ten: string; moTa: string } }) {
  if (!thienThe) return <aside className="kp-thong-tin">
    <div className="kp-hinh-tong-quan"><HinhThienThe thienThe={theoMa.saturn} /></div>
    <h2>Chọn một thiên thể</h2>
    <p>Chọn trên cảnh 3D hoặc trong danh sách để di chuyển camera và xem số liệu từ NASA.</p>
  </aside>
  const duLieu = soLieuNasa[thienThe.ma]
  const nguonModel = ghiCong[thienThe.ma]
  const quanhTraiDat = thienThe.ma === 'moon'
  const khoangCach = duLieu.khoangCachTrieuKm
  return <aside className="kp-thong-tin" aria-labelledby="kp-ten-thien-the">
    <div className="kp-thong-tin-dau">
      <div><p className="kp-phan-loai">{thienThe.loai}</p><h2 id="kp-ten-thien-the">{noiDung?.ten ?? thienThe.ten}</h2></div>
    </div>
    <div className="kp-hinh-tong-quan"><HinhThienThe thienThe={thienThe} /></div>
    <p>{duLieu.ghiChu}</p>
    {noiDung && <p>{noiDung.moTa}</p>}
    <dl className="kp-so-lieu">
      <div><dt>Đường kính</dt><dd>≈ {dinhDang.format(duLieu.duongKinhKm)} km</dd></div>
      {khoangCach !== undefined && <div>
        <dt>{quanhTraiDat ? 'Cách Trái Đất' : 'Cách Mặt Trời'}</dt>
        <dd>≈ {dinhDang.format(quanhTraiDat ? khoangCach * 1000000 : khoangCach)} {quanhTraiDat ? 'km' : 'triệu km'}</dd>
      </div>}
      {duLieu.quyDaoNgay !== undefined && <div><dt>Chu kỳ quỹ đạo</dt><dd>≈ {dinhDang.format(duLieu.quyDaoNgay)} ngày</dd></div>}
      <div><dt>Chu kỳ tự quay</dt><dd>≈ {dinhDang.format(duLieu.tuQuayGio)} giờ{duLieu.tuQuayNguoc ? ' · ngược' : ''}</dd></div>
    </dl>
    <div className="kp-nguon">
      <a href={duLieu.nguon ?? bangSoLieuNasa} target="_blank" rel="noreferrer">Số liệu NASA ↗</a>
      {nguonModel && <p>
        Mô hình <a href={nguonModel.source} target="_blank" rel="noreferrer">{nguonModel.title}</a> · {nguonModel.author.split(' (')[0]}
        {' · '}<a href={giayPhep} target="_blank" rel="noreferrer">CC BY 4.0</a>.
      </p>}
    </div>
  </aside>
}

export default function TrangKhamPha() {
  const [goc, datGoc] = useState<GocNhin>({ ma: null, tuTren: false })
  const [chuyenDongNhe, datChuyenDongNhe] = useState(() => window.matchMedia('(prefers-reduced-motion: reduce)').matches)
  const [tamDung, datTamDung] = useState(chuyenDongNhe)
  const [tocDo, datTocDo] = useState(1)
  const [loiWebGl, datLoiWebGl] = useState(false)
  const [xemCanhGoc, datXemCanhGoc] = useState(false)
  const [trangThaiCanhGoc, datTrangThaiCanhGoc] = useState<'tai' | 'san-sang' | 'loi'>('tai')
  const [ketQua, datKetQua] = useState<{ ma: MaThienThe; trangThai: TrangThaiMoHinh } | null>(null)
  const bangThongTin = useRef<HTMLDetailsElement>(null)
  const [bangMo, datBangMo] = useState(() => window.matchMedia('(min-width: 900px) and (min-height: 680px)').matches)
  const chon = useCallback((ma: MaThienThe) => {
    datGoc({ ma, tuTren: false })
    if (bangThongTin.current) bangThongTin.current.open = true
  }, [])
  const baoLoi = useCallback(() => datLoiWebGl(true), [])
  const baoMoHinh = useCallback((ma: MaThienThe, trangThai: TrangThaiMoHinh) => datKetQua({ ma, trangThai }), [])
  const canhGocSanSang = useCallback(() => datTrangThaiCanhGoc('san-sang'), [])
  const canhGocLoi = useCallback(() => datTrangThaiCanhGoc('loi'), [])
  const daChon = goc.ma ? theoMa[goc.ma] : null
  const thienTheThongTin = xemCanhGoc ? null : daChon ?? theoMa.sun
  const noiDung = useDuLieu<{ ma: string; ten: string; moTa: string }[]>('/api/kham-pha/noi-dung')
  const trangThaiChiTiet = !loiWebGl && ketQua?.ma === daChon?.ma ? ketQua?.trangThai : undefined

  useEffect(() => {
    document.title = 'Hệ Mặt Trời | GALAXORA'
    const media = window.matchMedia('(prefers-reduced-motion: reduce)')
    function doi(e: MediaQueryListEvent) {
      datChuyenDongNhe(e.matches)
      if (e.matches) datTamDung(true)
    }
    media.addEventListener('change', doi)
    return () => media.removeEventListener('change', doi)
  }, [])

  return <div className="kp-he-mat-troi">
      <div className="kp-tieu-de">
        <p className="kp-nhan-khu-vuc">Khám phá vũ trụ</p>
        <h1>Hệ Mặt Trời</h1>
      </div>
      <div className="kp-luoi">
        <section className="kp-quan-sat" aria-label="Cảnh quan sát Hệ Mặt Trời">
          {xemCanhGoc ? <>
            {trangThaiCanhGoc === 'loi' ? <div className="kp-san"><SoDo daChon={null} /></div> :
              <Suspense fallback={<div className="kp-san" aria-hidden="true" />}>
                <CanhGoc tocDo={tamDung ? 0 : tocDo} baoSanSang={canhGocSanSang} baoLoi={canhGocLoi} />
              </Suspense>}
            {trangThaiCanhGoc === 'tai' && <p className="kp-dang-tai" role="status">Đang tải cảnh Hệ Mặt Trời gốc…</p>}
          </> : <div className="kp-san">
            {loiWebGl ? <SoDo daChon={daChon} /> : <Suspense fallback={<p className="kp-dang-tai" role="status">Đang tải cảnh 3D…</p>}>
              <CanhHeMatTroi goc={goc} tocDo={tamDung ? 0 : tocDo} chuyenDongNhe={chuyenDongNhe}
                chon={chon} baoLoi={baoLoi} baoMoHinh={baoMoHinh} />
            </Suspense>}
          </div>}
          <div className="kp-thanh-dieu-khien kp-dieu-khien-mo-phong">
            {!xemCanhGoc && <button type="button" aria-pressed={!goc.ma && !goc.tuTren}
              onClick={() => datGoc({ ma: null, tuTren: false })}>Toàn cảnh</button>}
            {!loiWebGl && <button type="button" aria-pressed={xemCanhGoc} onClick={() => {
              datTrangThaiCanhGoc('tai')
              datXemCanhGoc(!xemCanhGoc)
            }}>{xemCanhGoc ? 'Về mô phỏng' : 'Cảnh gốc'}</button>}
            {!loiWebGl && <>
              {!xemCanhGoc && <button type="button" aria-pressed={!goc.ma && goc.tuTren}
                onClick={() => datGoc({ ma: null, tuTren: true })}>Nhìn từ trên</button>}
              <button type="button" onClick={() => datTamDung(!tamDung)}>{tamDung ? 'Tiếp tục' : 'Tạm dừng'}</button>
              <label>Tốc độ <select value={tocDo} onChange={e => datTocDo(Number(e.target.value))}>
                <option value={0.25}>0,25×</option><option value={1}>1×</option>
                <option value={2}>2×</option><option value={4}>4×</option>
              </select></label>
            </>}
          </div>
          {!loiWebGl && !xemCanhGoc && <p id="kp-dieu-huong" className="kp-goi-y">Kéo để xoay, cuộn để thu phóng. Dùng chuột phải hoặc hai ngón tay để di chuyển.</p>}
          {!xemCanhGoc && trangThaiChiTiet === 'dang-tai' && <p className="kp-trang-thai-canh" role="status">Đang tải mô hình gốc…</p>}
          {!xemCanhGoc && trangThaiChiTiet === 'loi' && <p className="kp-trang-thai-canh" role="status">Chưa tải được mô hình gốc. Đang hiển thị mô phỏng.</p>}
        </section>
        <details className="kp-ho-so" ref={bangThongTin} open={bangMo} onToggle={e => datBangMo(e.currentTarget.open)}>
          <summary>Thông tin thiên thể</summary>
          <ThongTin thienThe={thienTheThongTin} noiDung={noiDung.duLieu?.find(d => d.ma === thienTheThongTin?.ma)} />
        </details>
      </div>
      <TienDoKhamPha ma={xemCanhGoc ? undefined : daChon?.ma} bangMo={bangMo} />
      {!xemCanhGoc && <DockThienThe ma={goc.ma} chon={chon} />}
      <details className="kp-ghi-cong">
        <summary>Nguồn mô hình và bản đồ</summary>
        <p>Cảnh mô phỏng sử dụng tỷ lệ và tốc độ đã điều chỉnh.
          {' '}<a href={giayPhep} target="_blank" rel="noreferrer">Giấy phép CC BY 4.0</a>.</p>
        <ul>
          {cacThienThe.map(t => {
            const taiNguyen = ghiCong[t.ma]
            return <li key={t.ma}><a href={taiNguyen.source} target="_blank" rel="noreferrer">{taiNguyen.title}</a>
              {' · ' + taiNguyen.author.split(' (')[0]}</li>
          })}
          <li>Cảnh gốc và bản đồ toàn cảnh của Sao Mộc, Sao Thổ, Sao Thiên Vương và Sao Hải Vương từ{' '}
            <a href={ghiCongBanDo.source} target="_blank" rel="noreferrer">{ghiCongBanDo.title}</a>
            {' · ' + ghiCongBanDo.author}</li>
        </ul>
      </details>
  </div>
}
