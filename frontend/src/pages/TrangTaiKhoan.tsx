import type { ReactNode } from 'react'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'
import type { ThongTinPhien } from '../services/taiKhoanService.ts'
import DoiMatKhau from './DoiMatKhau.tsx'
import HoSoKhamPha from './HoSoKhamPha.tsx'
import SoDiaChi from './SoDiaChi.tsx'

export default function TrangTaiKhoan({ phien, dangGui, dangXuat, children }: {
  phien: ThongTinPhien | null
  dangGui: boolean
  dangXuat: () => void
  children: ReactNode
}) {
  return <div className="tk-ho-so gx-khung">
    <header className="tk-ho-so-dau gx-kinh">
      <div>
        <h1 tabIndex={-1} id="tieu-de-tai-khoan">Tài khoản của bạn</h1>
        {phien?.vaiTro.includes('USER') && <a className="gx-nut gx-nut-chinh" href="/kham-pha">
          <BieuTuong ten="tenLua" /> Tiếp tục khám phá
        </a>}
      </div>
      {phien && <dl>
        <div><dt>Mã người dùng</dt><dd>#{phien.nguoiDungId}</dd></div>
        <div><dt>Vai trò</dt><dd>{phien.vaiTro.map(vaiTro => vaiTro === 'ADMIN' ? 'Quản trị viên' : 'Người dùng').join(', ')}</dd></div>
      </dl>}
    </header>
    {children}
    {phien && <div className="tk-ho-so-luoi">
      {phien.vaiTro.includes('USER') && <HoSoKhamPha key={phien.nguoiDungId} />}
      <section className="tk-bao-mat gx-kinh" aria-labelledby="tk-bao-mat-tieu-de">
        <h2 id="tk-bao-mat-tieu-de">Bảo mật tài khoản</h2>
        <DoiMatKhau key={phien.nguoiDungId} />
        <button type="button" className="gx-nut gx-nut-phu" disabled={dangGui} onClick={dangXuat}>
          {dangGui ? 'Đang đăng xuất…' : 'Đăng xuất'}
        </button>
      </section>
      {phien.vaiTro.includes('USER') && <SoDiaChi key={phien.nguoiDungId} />}
    </div>}
  </div>
}
