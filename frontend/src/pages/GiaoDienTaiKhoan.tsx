import { useCallback, useEffect, useRef, useState } from 'react'
import type { FormEvent, MouseEvent } from 'react'
import { LoiApi } from '../services/apiClient.ts'
import { taiKhoanService } from '../services/taiKhoanService.ts'
import type { ThongTinPhien } from '../services/taiKhoanService.ts'
import BieuMauTaiKhoan from './BieuMauTaiKhoan.tsx'
import MinhHoaHeMatTroi from './MinhHoaHeMatTroi.tsx'
import KhungDangNhap from './KhungDangNhap.tsx'
import TrangHoSo from './TrangTaiKhoan.tsx'
import { cacQuocGia, chuanHoaSoDienThoai } from './soDienThoai.ts'
import { duongDanTaiKhoan, noiQuayVeSauDangNhap, timTrangTaiKhoan } from './duongDanTaiKhoan.ts'
import type { TrangTaiKhoan } from './duongDanTaiKhoan.ts'
import './taiKhoan.css'
import KhungTrang from '../giaodien/KhungTrang.tsx'
import ThanhDauTrang from '../giaodien/ThanhDauTrang.tsx'
import { BieuTuongThuongHieu } from '../giaodien/BieuTuong.tsx'

type ThongBao = { loai: 'loi' | 'thongTin'; noiDung: string }

function moTrangSauDangNhap(phien: ThongTinPhien) {
  window.location.replace(phien.vaiTro.includes('ADMIN') ? '/quan-tri' : noiQuayVeSauDangNhap(window.location.search))
}

const noiDungTrang: Record<TrangTaiKhoan, { tieuDe: string; moTa: string }> = {
  dangNhap: { tieuDe: 'Đăng nhập', moTa: 'Dùng email hoặc số điện thoại đã đăng ký.' },
  dangKy: { tieuDe: 'Tạo tài khoản', moTa: 'Đăng nhập bằng email hoặc số điện thoại sau khi đăng ký.' },
  phien: { tieuDe: 'Tài khoản của bạn', moTa: 'Phiên đăng nhập GALAXORA' },
}

function loiTheoTrang(loi: unknown, trang: TrangTaiKhoan): string {
  if (!(loi instanceof LoiApi)) return 'Không kết nối được. Kiểm tra mạng rồi thử lại.'
  switch (loi.maTrangThai) {
    case 400:
      return 'Thông tin nhập chưa hợp lệ. Hãy kiểm tra lại.'
    case 401:
      return trang === 'dangNhap'
        ? 'Không đăng nhập được. Kiểm tra thông tin hoặc thử lại sau.'
        : 'Phiên đã hết hạn. Hãy đăng nhập lại.'
    case 403:
      return 'Yêu cầu đã hết hiệu lực. Vui lòng thử lại.'
    case 429:
      return 'Bạn đã thử quá nhiều lần. Vui lòng chờ rồi thử lại.'
    case 503:
      return 'Dịch vụ tài khoản hiện chưa sẵn sàng. Vui lòng thử lại sau.'
    default:
      return 'Yêu cầu chưa xử lý được. Vui lòng thử lại.'
  }
}

export default function GiaoDienTaiKhoan({ trangDau }: { trangDau: TrangTaiKhoan }) {
  const [trang, datTrang] = useState(trangDau)
  const [email, datEmail] = useState('')
  const [phien, datPhien] = useState<ThongTinPhien | null>(null)
  const [thongBao, datThongBao] = useState<ThongBao | null>(null)
  const [dangGui, datDangGui] = useState(false)
  const [lanKiemTra, datLanKiemTra] = useState(0)
  const [dangKiemTra, datDangKiemTra] = useState(trangDau === 'phien')
  const dangXuLy = useRef(false)
  const tieuDe = useRef<HTMLHeadingElement>(null)

  const chuyenTrang = useCallback((dich: TrangTaiKhoan, bao: ThongBao | null = null, thayThe = false) => {
    if (thayThe) window.history.replaceState(null, '', duongDanTaiKhoan[dich])
    else if (window.location.pathname !== duongDanTaiKhoan[dich]) {
      window.history.pushState(null, '', duongDanTaiKhoan[dich])
    }
    datThongBao(bao)
    datTrang(dich)
  }, [])

  useEffect(() => {
    const khiQuayLai = () => {
      const dich = timTrangTaiKhoan(window.location.pathname)
      if (dich) {
        datTrang(dich)
        datThongBao(null)
      } else {
        window.location.reload()
      }
    }
    window.addEventListener('popstate', khiQuayLai)
    return () => window.removeEventListener('popstate', khiQuayLai)
  }, [])

  useEffect(() => {
    document.title = `${noiDungTrang[trang].tieuDe} · GALAXORA`
    if (trang === 'dangNhap') window.scrollTo({ top: 0, behavior: 'instant' })
    const heading = trang === 'phien' ? document.getElementById('tieu-de-tai-khoan') : tieuDe.current
    heading?.focus({ preventScroll: true })
  }, [trang])

  useEffect(() => {
    if (trang !== 'phien' && trang !== 'dangNhap') return
    const boHuy = new AbortController()
    if (trang === 'phien') datDangKiemTra(true)
    taiKhoanService.layPhien(boHuy.signal)
      .then((duLieu) => {
        if (boHuy.signal.aborted) return
        datPhien(duLieu)
        if (trang === 'dangNhap' && !dangXuLy.current) moTrangSauDangNhap(duLieu)
      })
      .catch((loi: unknown) => {
        if (boHuy.signal.aborted) return
        datPhien(null)
        if (trang === 'phien' && loi instanceof LoiApi && loi.maTrangThai === 401) {
          chuyenTrang('dangNhap', { loai: 'thongTin', noiDung: 'Hãy đăng nhập để xem tài khoản.' }, true)
        } else if (trang === 'phien') {
          datThongBao({ loai: 'loi', noiDung: loiTheoTrang(loi, trang) })
        }
      })
      .finally(() => {
        if (!boHuy.signal.aborted) datDangKiemTra(false)
      })
    return () => boHuy.abort()
  }, [trang, chuyenTrang, lanKiemTra])

  function lienKet(dich: TrangTaiKhoan, nhan: string) {
    function khiBam(suKien: MouseEvent<HTMLAnchorElement>) {
      if (suKien.button !== 0 || suKien.ctrlKey || suKien.metaKey || suKien.shiftKey || suKien.altKey) return
      suKien.preventDefault()
      if (!dangXuLy.current) {
        if (trang === 'dangNhap') window.scrollTo({ top: 0, behavior: 'instant' })
        chuyenTrang(dich)
      }
    }
    return <a href={duongDanTaiKhoan[dich]} onClick={khiBam}>{nhan}</a>
  }

  async function xuLy(yeuCau: () => Promise<void>) {
    if (dangXuLy.current) return
    dangXuLy.current = true
    datDangGui(true)
    datThongBao(null)
    try {
      await yeuCau()
    } catch (loi) {
      datThongBao({ loai: 'loi', noiDung: loiTheoTrang(loi, trang) })
    } finally {
      dangXuLy.current = false
      datDangGui(false)
    }
  }

  function khiGui(suKien: FormEvent<HTMLFormElement>) {
    suKien.preventDefault()
    const duLieu = new FormData(suKien.currentTarget)
    const lay = (ten: string) => String(duLieu.get(ten) ?? '')
    const matKhau = lay('matKhau')
    const diaChi = lay('email').trim()
    if (duLieu.has('matKhau') && (!matKhau.trim() || new TextEncoder().encode(matKhau).length > 72)) {
      datThongBao({ loai: 'loi', noiDung: 'Nhập mật khẩu có nội dung, tối đa 72 byte UTF-8.' })
      return
    }
    const quocGia = cacQuocGia.find(nuoc => nuoc.ma === lay('quocGia'))?.ma ?? 'VN'
    const soDienThoai = trang === 'dangKy' ? chuanHoaSoDienThoai(lay('soDienThoai'), quocGia) : ''
    if (trang === 'dangKy' && matKhau !== lay('xacNhanMatKhau')) {
      datThongBao({ loai: 'loi', noiDung: 'Mật khẩu xác nhận chưa khớp.' })
      suKien.currentTarget.querySelector<HTMLInputElement>('#xac-nhan-mat-khau')?.focus()
      return
    }
    if (trang === 'dangKy' && !soDienThoai) {
      datThongBao({ loai: 'loi', noiDung: 'Kiểm tra lại số điện thoại và quốc gia đã chọn.' })
      suKien.currentTarget.querySelector<HTMLInputElement>('#so-dien-thoai')?.focus()
      return
    }
    void xuLy(async () => {
      switch (trang) {
        case 'dangKy':
          await taiKhoanService.dangKy({
            hoTen: lay('hoTen').trim(), email: diaChi, soDienThoai, matKhau,
          })
          datEmail(diaChi)
          chuyenTrang('dangNhap', {
            loai: 'thongTin',
            noiDung: 'Yêu cầu đăng ký đã được tiếp nhận. Hãy đăng nhập bằng thông tin của bạn.',
          })
          break
        case 'dangNhap': {
          const dinhDanh = lay('dinhDanh').trim()
          const soNoiDia = !dinhDanh.includes('@') && !dinhDanh.startsWith('+')
          const phienMoi = await taiKhoanService.dangNhap(
            (soNoiDia && chuanHoaSoDienThoai(dinhDanh, 'VN')) || dinhDanh, matKhau,
          )
          moTrangSauDangNhap(phienMoi)
          break
        }
      }
    })
  }

  function dangXuat() {
    void xuLy(async () => {
      await taiKhoanService.dangXuat()
      datPhien(null)
      chuyenTrang('dangNhap', { loai: 'thongTin', noiDung: 'Bạn đã đăng xuất.' }, true)
    })
  }

  const { tieuDe: tenTrang, moTa } = noiDungTrang[trang]
  const lopNoiDung = trang === 'phien' ? 'tai-khoan-nen tk-trang-rieng'
    : trang === 'dangNhap' ? 'tai-khoan-nen tk-dang-nhap-nen' : 'tai-khoan-nen'
  const tinhTrang = thongBao && (
    <p className={`tai-khoan-thong-bao tai-khoan-${thongBao.loai}`}
      role={thongBao.loai === 'loi' ? 'alert' : 'status'}>{thongBao.noiDung}</p>
  )

  return (
    <KhungTrang muc="/tai-khoan" lopNoiDung={lopNoiDung}
      thanhDau={<ThanhDauTrang muc="/tai-khoan" laKhach={trang !== 'phien' && !phien}
        vaiTro={phien?.vaiTro} coGio={phien?.vaiTro.includes('USER')} hienGioKhach={false} />}>
      {trang === 'phien' ? (
        <TrangHoSo phien={phien} dangGui={dangGui} dangXuat={dangXuat}>
          {tinhTrang}
          {!phien && (dangKiemTra ? <p role="status">Đang kiểm tra phiên…</p> :
            <button type="button" className="gx-nut gx-nut-phu"
              onClick={() => { datThongBao(null); datLanKiemTra(lanKiemTra + 1) }}>Kiểm tra lại phiên</button>)}
        </TrangHoSo>
      ) : trang === 'dangNhap' ? (
        <KhungDangNhap>
          <h1 ref={tieuDe} id="tieu-de-tai-khoan" tabIndex={-1}>{tenTrang}</h1>
          <p className="tai-khoan-mo-ta">{moTa}</p>
          {tinhTrang}
          <BieuMauTaiKhoan key={trang} trang={trang} email={email} dangGui={dangGui} khiGui={khiGui} />
          <p className="tk-dang-nhap-dang-ky">Chưa có tài khoản? {lienKet('dangKy', 'Đăng ký ngay')}</p>
        </KhungDangNhap>
      ) : <div className="tai-khoan-khung gx-kinh">
        <aside className="tai-khoan-thuong-hieu">
          <a className="tai-khoan-logo" href="/">
            <BieuTuongThuongHieu /> GALAXORA
          </a>
          <MinhHoaHeMatTroi />
          <div className="tk-lien-ket-minh-hoa">
            <a className="tai-khoan-he-thong" href="/">Trang chủ <span aria-hidden="true">↗</span></a>
            <a className="tai-khoan-he-thong" href="/cua-hang">Cửa hàng <span aria-hidden="true">↗</span></a>
          </div>
        </aside>
        <section className="tai-khoan-noi-dung" aria-labelledby="tieu-de-tai-khoan">
          <h1 ref={tieuDe} id="tieu-de-tai-khoan" tabIndex={-1}>{tenTrang}</h1>
          <p className="tai-khoan-mo-ta">{moTa}</p>
          {tinhTrang}
          <BieuMauTaiKhoan key={trang} trang={trang} email={email} dangGui={dangGui} khiGui={khiGui} />
          <nav className="tai-khoan-dieu-huong" aria-label="Tùy chọn tài khoản">
            {lienKet('dangNhap', 'Quay về đăng nhập')}
          </nav>
        </section>
      </div>}
    </KhungTrang>
  )
}
