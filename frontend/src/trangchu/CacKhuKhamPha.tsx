import anhTraiDat from '../../../assets/3d/solar-system/earth/be-mat.webp?no-inline'
import anhThienHa from '../../../assets/3d/galaxies/goc-nhin.webp?no-inline'
import anhTinhVan from '../../../assets/3d/nebulae/goc-nhin.webp?no-inline'
import anhHoDen from '../../../assets/3d/black-holes/goc-nhin.webp?no-inline'
import { cacKhuVucVuTru } from '../khampha/khuVucVuTru.ts'
import { cacThienThe } from '../khampha/thienThe.ts'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'

const cacThe = [
  { ma: 'he-mat-troi', duongDan: '/kham-pha', ten: 'Hệ Mặt Trời', moTa: `${cacThienThe.length} thiên thể kỳ vĩ`, anh: anhTraiDat, rong: 640, cao: 320 },
  { ma: 'thien-ha', duongDan: '/kham-pha/thien-ha', ten: cacKhuVucVuTru['thien-ha'].ten, moTa: 'Khám phá vô tận', anh: anhThienHa, rong: 900, cao: 570 },
  { ma: 'tinh-van', duongDan: '/kham-pha/tinh-van', ten: cacKhuVucVuTru['tinh-van'].ten, moTa: 'Vẻ đẹp huyền bí', anh: anhTinhVan, rong: 900, cao: 570 },
  { ma: 'ho-den', duongDan: '/kham-pha/ho-den', ten: cacKhuVucVuTru['ho-den'].ten, moTa: 'Bí ẩn vô cùng', anh: anhHoDen, rong: 900, cao: 570 },
]

export default function CacKhuKhamPha() {
  return <section className="gx-khung gx-cac-khu" aria-labelledby="gx-cac-khu-tieu-de">
    <h2 className="gx-an-thi-giac" id="gx-cac-khu-tieu-de">Các khu khám phá</h2>
    {cacThe.map(({ ma, duongDan, ten, moTa, anh, rong, cao }) => <a className="gx-khu gx-kinh" key={ma} data-khu={ma} href={duongDan}>
      <div className="gx-khu-anh"><img src={anh} alt="" width={rong} height={cao} loading="lazy" decoding="async" /></div>
      <div className="gx-khu-chu"><h3>{ten}</h3><p>{moTa}</p></div>
      <span className="gx-vong-nut"><BieuTuong ten="muiTen" /></span>
    </a>)}
  </section>
}
