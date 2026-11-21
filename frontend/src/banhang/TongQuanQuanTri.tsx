import { useEffect, useRef } from 'react'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'
import anhPhiHanhGia from '../assets/figure-phi-hanh-gia.webp'
import { TrangThaiTai, useDuLieu } from './dungChung.tsx'
import { dinhDangTien } from './duLieu.ts'

type TongQuan = {
  donCanXuLy: number
  yeuCauHuy: number
  tonKhoThap: number
  doanhThu: number
  nhiemVuHoanThanh: number
}

export type ThongBaoQuanTri = { noiDung: string; duongDan: string }
const dinhDangSo = new Intl.NumberFormat('vi-VN')

export default function TongQuanQuanTri({ thongBao }: {
  thongBao: ReturnType<typeof useDuLieu<ThongBaoQuanTri[]>>
}) {
  const tongQuan = useDuLieu<TongQuan>('/api/quan-tri/tong-quan')
  const duLieu = tongQuan.duLieu
  const vungThongBao = useRef<HTMLElement>(null)

  useEffect(() => {
    if (window.location.hash === '#thong-bao-quan-tri') {
      vungThongBao.current?.focus()
    }
  }, [])

  return <>
    <section className="qt-chao">
      <img className="qt-phi-hanh-gia" src={anhPhiHanhGia} alt="" />
      <div className="qt-chao-noi-dung">
        <h1>Tổng quan quản trị</h1>
        <p>Theo dõi cửa hàng và hoạt động khám phá GALAXORA.</p>
      </div>
    </section>
    <TrangThaiTai {...tongQuan} />
    {duLieu && <dl className="qt-chi-so" aria-label="Chỉ số tổng quan">
      <div className="qt-the-so">
        <BieuTuong ten="gio" />
        <dt>Đơn cần xử lý</dt>
        <dd><a href="/quan-tri/don-hang" aria-label={`${duLieu.donCanXuLy} đơn cần xử lý`}>
          {dinhDangSo.format(duLieu.donCanXuLy)}<BieuTuong ten="muiTen" />
        </a></dd>
      </div>
      <div className="qt-the-so">
        <BieuTuong ten="tep" />
        <dt>Yêu cầu hủy đang chờ</dt>
        <dd><a href="/quan-tri/don-hang" aria-label={`${duLieu.yeuCauHuy} yêu cầu hủy đang chờ`}>
          {dinhDangSo.format(duLieu.yeuCauHuy)}<BieuTuong ten="muiTen" />
        </a></dd>
      </div>
      <div className="qt-the-so">
        <BieuTuong ten="kho" />
        <dt>Sản phẩm sắp hết hoặc hết hàng</dt>
        <dd><a href="/quan-tri/kho" aria-label={`${duLieu.tonKhoThap} sản phẩm sắp hết hoặc hết hàng`}>
          {dinhDangSo.format(duLieu.tonKhoThap)}<BieuTuong ten="muiTen" />
        </a></dd>
      </div>
      <div className="qt-the-so">
        <BieuTuong ten="doanhThu" />
        <dt>Doanh thu đơn đã giao</dt>
        <dd>{dinhDangTien(duLieu.doanhThu)}</dd>
      </div>
    </dl>}
    <div className="qt-bang-luoi">
      <section className="bh-khoi qt-thong-bao" id="thong-bao-quan-tri" aria-labelledby="tieu-de-thong-bao"
        ref={vungThongBao} tabIndex={-1}>
        <h2 id="tieu-de-thong-bao"><BieuTuong ten="chuong" />Thông báo quản trị</h2>
        <TrangThaiTai {...thongBao} />
        {thongBao.duLieu && (thongBao.duLieu.length ? <ul>{thongBao.duLieu.map(dong =>
          <li key={dong.noiDung}><a href={dong.duongDan}>
            <span>{dong.noiDung}</span><BieuTuong ten="muiTen" />
          </a></li>)}</ul> : <p>Không có việc đang chờ.</p>)}
      </section>
      {duLieu && <section className="bh-khoi qt-nhiem-vu">
        <h2><BieuTuong ten="cup" />Nhiệm vụ đã hoàn thành</h2>
        <p className="qt-nhiem-vu-so">{dinhDangSo.format(duLieu.nhiemVuHoanThanh)}</p>
        <a href="/quan-tri/noi-dung">Quản lý nhiệm vụ <BieuTuong ten="muiTen" /></a>
      </section>}
    </div>
  </>
}
