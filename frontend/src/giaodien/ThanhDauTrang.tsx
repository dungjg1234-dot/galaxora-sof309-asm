import { useId, useRef, useState, type ReactNode } from 'react'
import { BieuTuong, BieuTuongThuongHieu } from './BieuTuong.tsx'

export const cacLienKet = [
  { duongDan: '/', ten: 'Trang chủ', bieuTuong: 'nha' },
  { duongDan: '/kham-pha', ten: 'Khám phá', bieuTuong: 'tenLua' },
  { duongDan: '/cua-hang', ten: 'Cửa hàng', bieuTuong: 'gio' },
  { duongDan: '/kham-pha', ten: 'Hành trình', bieuTuong: 'cup' },
] as const
export type MucDieuHuong = '/' | '/kham-pha' | '/cua-hang' | '/tai-khoan' | '/quan-tri'

export default function ThanhDauTrang({ laKhach = false, coGio = false, soGio, vaiTro, muc,
  kieu, tuyChon, hienGioKhach = true }: {
  laKhach?: boolean; coGio?: boolean; soGio?: number; vaiTro?: readonly string[]; muc?: MucDieuHuong
  kieu?: 'gon' | 'kham-pha'; tuyChon?: ReactNode; hienGioKhach?: boolean
}) {
  const [moMenu, datMoMenu] = useState(false)
  const [moTaiKhoan, datMoTaiKhoan] = useState(false)
  const nutMenu = useRef<HTMLButtonElement>(null)
  const nutTaiKhoan = useRef<HTMLButtonElement>(null)
  const maMenu = useId()
  const maTaiKhoan = useId()
  const laKhuKhamPha = kieu === 'kham-pha'

  return <header className={kieu ? `gx-dau gx-dau-${kieu}` : 'gx-dau'} onKeyDown={e => {
    if (e.key !== 'Escape') return
    if (moTaiKhoan) {
      datMoTaiKhoan(false)
      nutTaiKhoan.current?.focus()
    } else if (moMenu) {
      datMoMenu(false)
      nutMenu.current?.focus()
    }
  }}>
    <div className="gx-khung gx-dau-trong">
      <a className="gx-thuong-hieu" href="/"><BieuTuongThuongHieu /><span>GALAXORA</span></a>
      <button className="gx-menu" type="button" ref={nutMenu} aria-expanded={moMenu} aria-controls={maMenu}
        aria-label="Điều hướng chính" onClick={() => { datMoMenu(!moMenu); datMoTaiKhoan(false) }}><BieuTuong ten="menu" /></button>
      <nav id={maMenu} className="gx-dieu-huong" aria-label="Điều hướng chính" data-mo={moMenu}>
        {cacLienKet.filter(({ ten }) => !laKhuKhamPha || ten === 'Khám phá' || ten === 'Cửa hàng')
          .map(({ duongDan, ten, bieuTuong }) => <a key={ten} href={duongDan}
          aria-current={duongDan === muc && ten !== 'Hành trình' ? 'page' : undefined} onClick={() => datMoMenu(false)}>
          <BieuTuong ten={bieuTuong} />{ten}
        </a>)}
      </nav>
      <div className="gx-tai-khoan-dau">
        {vaiTro ? <div className="gx-tai-khoan-menu" onBlur={e => {
          if (e.relatedTarget && !e.currentTarget.contains(e.relatedTarget)) datMoTaiKhoan(false)
        }}>
          <button className="gx-tai-khoan" type="button" ref={nutTaiKhoan}
            aria-expanded={moTaiKhoan} aria-controls={maTaiKhoan}
            onClick={() => { datMoTaiKhoan(!moTaiKhoan); datMoMenu(false) }}>
            <BieuTuong ten="nguoi" />Tài khoản
          </button>
          <nav id={maTaiKhoan} className="gx-tai-khoan-lien-ket" aria-label="Tài khoản" hidden={!moTaiKhoan}>
            <a href="/tai-khoan/phien" aria-current={muc === '/tai-khoan' ? 'location' : undefined}>Hồ sơ</a>
            {vaiTro.includes('USER') && <><a href="/cua-hang/yeu-thich">Yêu thích</a><a href="/don-hang">Đơn hàng</a></>}
            {vaiTro.includes('ADMIN') && <a href="/quan-tri" aria-current={muc === '/quan-tri' ? 'location' : undefined}>Quản trị</a>}
          </nav>
        </div> : <a className="gx-tai-khoan" href={laKhach || laKhuKhamPha ? '/tai-khoan/dang-nhap' : '/tai-khoan/phien'}
          aria-current={muc === '/tai-khoan' ? 'location' : undefined}>
          <BieuTuong ten="nguoi" />{laKhach ? 'Đăng nhập' : 'Tài khoản'}
        </a>}
        {(coGio || (laKhach && hienGioKhach)) && <a className="gx-gio-dau" href={coGio ? '/gio-hang' : '/tai-khoan/dang-nhap'}
          aria-label={soGio !== undefined ? `Giỏ hàng, ${soGio} sản phẩm` : laKhach ? 'Đăng nhập để mở giỏ hàng' : 'Giỏ hàng'}>
          <BieuTuong ten="gio" />{soGio !== undefined && <span aria-hidden="true">{soGio}</span>}
        </a>}
        {tuyChon}
      </div>
    </div>
  </header>
}
