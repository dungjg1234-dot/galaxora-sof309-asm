import { useId, type ReactNode } from 'react'
import anhTraiDat from '../../../assets/3d/solar-system/earth/be-mat.webp?no-inline'
import anhMatTrang from '../../../assets/3d/solar-system/moon/be-mat.webp?no-inline'
import anhTinhVan from '../../../assets/3d/nebulae/goc-nhin.webp?no-inline'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'

function VongQuyDao() {
  const ma = useId()
  return <svg className="gx-quy-dao" viewBox="0 0 590 174" fill="none" aria-hidden="true" focusable="false">
    <defs>
      <linearGradient id={ma} x2="1" y2="0" gradientUnits="objectBoundingBox">
        <stop stopColor="var(--gx-quy-dao-tim)" /><stop offset=".43" stopColor="var(--gx-quy-dao-mo)" stopOpacity="0" />
        <stop offset=".7" stopColor="var(--gx-quy-dao-mo)" stopOpacity="0" /><stop offset="1" stopColor="var(--gx-quy-dao-cyan)" />
      </linearGradient>
    </defs>
    <path d="M76 30C30 45 8 69 9 94c1 43 86 63 210 65 144 3 321-45 356-98 37-57-103-60-229-45"
      stroke={'url(#' + ma + ')'} strokeWidth="3.3" strokeLinecap="round" />
    <path className="gx-sao-quy-dao" d="M568 46c2 9 5 12 14 14-9 2-12 5-14 14-2-9-5-12-14-14 9-2 12-5 14-14Z" fill="var(--gx-sao-quy-dao)" />
    <circle cx="26" cy="118" r="2.6" fill="var(--gx-cham-quy-dao)" />
  </svg>
}

export default function MoDau({ theChaoMung }: { theChaoMung: ReactNode }) {
  return <section className="gx-hero" aria-labelledby="gx-tieu-de">
    <div className="gx-canh-hero" aria-hidden="true">
      <img className="gx-tinh-van-hero" src={anhTinhVan} alt="" width="900" height="570" decoding="async" />
      <div className="gx-trai-dat"><img src={anhTraiDat} alt="" width="640" height="320" fetchPriority="high" decoding="async" /></div>
      <div className="gx-mat-trang gx-mat-trang-xa"><img src={anhMatTrang} alt="" width="640" height="320" decoding="async" /></div>
      <div className="gx-mat-trang gx-mat-trang-gan"><img src={anhMatTrang} alt="" width="640" height="320" decoding="async" /></div>
      <div className="gx-da-khong-gian" />
      <VongQuyDao />
    </div>
    <div className="gx-khung gx-hero-trong">
      <div className="gx-loi-moi">
        <p className="gx-nhan-hero"><BieuTuong ten="sao" /> Khám phá vũ trụ 3D</p>
        <div className="gx-tieu-de-khung">
          <h1 id="gx-tieu-de"><span>GALAXORA</span>{' '}<span>Chạm đến những</span>{' '}<span>vì sao xa xôi</span></h1>
        </div>
        <p className="gx-mo-ta-hero">Khám phá Hệ Mặt Trời, tìm hiểu các thiên hà kỳ bí và sở hữu những sản phẩm vũ trụ độc đáo.</p>
        <div className="gx-hanh-dong">
          <a className="gx-nut gx-nut-chinh" href="#san-pham"><BieuTuong ten="gio" />Xem sản phẩm <BieuTuong ten="muiTen" /></a>
          <a className="gx-nut gx-nut-phu" href="/kham-pha"><BieuTuong ten="tenLua" />Bắt đầu khám phá</a>
        </div>
      </div>
      {theChaoMung}
    </div>
  </section>
}
