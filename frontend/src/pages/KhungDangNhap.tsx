import type { ReactNode } from 'react'
import { BieuTuongThuongHieu } from '../giaodien/BieuTuong.tsx'
import anhPhiHanhGia from '../assets/figure-phi-hanh-gia.webp'
import './dangNhap.css'

export default function KhungDangNhap({ children }: { children: ReactNode }) {
  return <div className="tk-dang-nhap">
    <div className="tk-dang-nhap-minh-hoa" aria-hidden="true">
      <div className="tk-dang-nhap-thuong-hieu">
        <span>GALAXORA</span>
        <i />
      </div>
      <div className="tk-dang-nhap-figure">
        <div className="tk-dang-nhap-quy-dao" />
        <img src={anhPhiHanhGia} alt="" decoding="async" fetchPriority="high" />
        <div className="tk-dang-nhap-quy-dao tk-dang-nhap-quy-dao-truoc" />
        <span className="tk-dang-nhap-tia-sang" />
      </div>
    </div>
    <section className="tk-dang-nhap-the" aria-labelledby="tieu-de-tai-khoan">
      <div className="tk-dang-nhap-bieu-tuong"><BieuTuongThuongHieu /></div>
      {children}
    </section>
  </div>
}
