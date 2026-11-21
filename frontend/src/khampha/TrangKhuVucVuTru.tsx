import { lazy, Suspense, useCallback, useEffect, useState } from 'react'
import { cacKhuVucVuTru, type MaKhuVucVuTru } from './khuVucVuTru.ts'
import { giayPhep } from './taiNguyenHeMatTroi.ts'
import { useDuLieu } from '../banhang/dungChung.tsx'

const CanhKhuVucVuTru = lazy(() => import('./CanhKhuVucVuTru.tsx'))
const anhDuPhong = import.meta.glob<string>('../../../assets/3d/*/goc-nhin.webp', {
  eager: true, query: '?url&no-inline', import: 'default',
})

export default function TrangKhuVucVuTru({ khuVuc }: { khuVuc: MaKhuVucVuTru }) {
  const duLieu = cacKhuVucVuTru[khuVuc]
  const noiDung = useDuLieu<{ ma: string; ten: string; moTa: string }[]>('/api/kham-pha/noi-dung')
    .duLieu?.find(d => d.ma === khuVuc)
  const [trangThai, datTrangThai] = useState<'tai' | 'san-sang' | 'loi'>('tai')
  const [bangMo, datBangMo] = useState(() => window.matchMedia('(min-width: 900px) and (min-height: 680px)').matches)
  const baoSanSang = useCallback(() => datTrangThai('san-sang'), [])
  const baoLoi = useCallback(() => datTrangThai('loi'), [])
  const anh = anhDuPhong['../../../assets/3d/' + duLieu.thuMuc + '/goc-nhin.webp']
  useEffect(() => { document.title = duLieu.ten + ' | GALAXORA' }, [duLieu.ten])

  return <>
    <div className="kp-tieu-de">
      <p className="kp-nhan-khu-vuc">Khám phá vũ trụ</p>
      <h1>{noiDung?.ten ?? duLieu.ten}</h1>
    </div>
    <div className="kp-luoi">
      <section className="kp-quan-sat kp-quan-sat-vu-tru" aria-label={'Quan sát ' + duLieu.ten}>
        {trangThai === 'loi' ? <>
          <div className="kp-san">
            {anh && <img className="kp-anh-du-phong" src={anh} alt={duLieu.chuThich} />}
          </div>
          <p role="status" className="kp-goi-y">Đang dùng chế độ 2D. Bạn vẫn có thể đọc bảng thông tin.</p>
        </> : <>
          <Suspense fallback={<div className="kp-san" aria-hidden="true" />}>
            <CanhKhuVucVuTru khuVuc={khuVuc} baoSanSang={baoSanSang} baoLoi={baoLoi} />
          </Suspense>
          {trangThai === 'tai' && <p className="kp-dang-tai" role="status">Đang tải cảnh 3D…</p>}
        </>}
      </section>
      <details className="kp-ho-so" open={bangMo} onToggle={e => datBangMo(e.currentTarget.open)}>
        <summary>Thông tin khu vực</summary>
        <aside className="kp-thong-tin" aria-labelledby="kp-thong-tin-vu-tru">
          <h2 id="kp-thong-tin-vu-tru">Tìm hiểu {duLieu.ten.toLocaleLowerCase('vi-VN')}</h2>
          {anh && <img className="kp-anh-thong-tin" src={anh} alt="" decoding="async" />}
          <p>{duLieu.tomTat}</p>
          {noiDung && <p>{noiDung.moTa}</p>}
          <dl className="kp-so-lieu kp-kien-thuc">
            {duLieu.diemChinh.map(([nhan, noiDung]) => <div key={nhan}><dt>{nhan}</dt><dd>{noiDung}</dd></div>)}
          </dl>
          <div className="kp-nguon">
            <a href={duLieu.nasa} target="_blank" rel="noreferrer">Đọc thêm tại NASA ↗</a>
            <p>Mô hình <a href={duLieu.nguonMoHinh} target="_blank" rel="noreferrer">{duLieu.tacPham}</a>
              {' · ' + duLieu.tacGia + ' · '}<a href={giayPhep} target="_blank" rel="noreferrer">CC BY 4.0</a>.</p>
          </div>
        </aside>
      </details>
    </div>
  </>
}
