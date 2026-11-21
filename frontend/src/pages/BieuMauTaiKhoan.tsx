import { useState } from 'react'
import type { FormEvent } from 'react'
import type { TrangTaiKhoan } from './duongDanTaiKhoan.ts'
import { cacQuocGia } from './soDienThoai.ts'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'

type ThuocTinh = {
  trang: Exclude<TrangTaiKhoan, 'phien'>
  email: string
  dangGui: boolean
  khiGui: (suKien: FormEvent<HTMLFormElement>) => void
}

const nhanNut = {
  dangNhap: 'Đăng nhập',
  dangKy: 'Tạo tài khoản',
}

export default function BieuMauTaiKhoan({ trang, email, dangGui, khiGui }: ThuocTinh) {
  const [hienMatKhau, datHienMatKhau] = useState(false)
  const [matKhauKhongKhop, datMatKhauKhongKhop] = useState(false)

  return (
    <form className="tai-khoan-form" onSubmit={khiGui} aria-busy={dangGui} onInput={trang === 'dangKy' ? e => {
      const xacNhan = e.currentTarget.querySelector<HTMLInputElement>('#xac-nhan-mat-khau')
      const matKhau = e.currentTarget.querySelector<HTMLInputElement>('#mat-khau')
      datMatKhauKhongKhop(Boolean(xacNhan?.value && xacNhan.value !== matKhau?.value))
    } : undefined}>
      {trang === 'dangKy' && (
        <div className="tai-khoan-truong">
          <label htmlFor="ho-ten">Họ và tên</label>
          <input id="ho-ten" name="hoTen" autoComplete="name" maxLength={255} required />
        </div>
      )}
      {trang === 'dangKy' && (
        <div className="tai-khoan-truong">
          <label htmlFor="email">Email</label>
          <input id="email" name="email" type="email" autoComplete="email"
            defaultValue={email} maxLength={254} required />
        </div>
      )}
      {trang === 'dangKy' && (
        <div className="tk-so-dien-thoai">
          <div className="tai-khoan-truong">
            <label htmlFor="quoc-gia">Quốc gia / vùng</label>
            <select id="quoc-gia" name="quocGia" defaultValue="VN">
              {cacQuocGia.map(({ ma, ten, maGoi }) => <option key={ma} value={ma}>{maGoi} · {ten}</option>)}
            </select>
          </div>
          <div className="tai-khoan-truong">
            <label htmlFor="so-dien-thoai">Số điện thoại</label>
            <input id="so-dien-thoai" name="soDienThoai" type="tel" autoComplete="tel-national" maxLength={32} required />
          </div>
        </div>
      )}
      {trang === 'dangNhap' && (
        <div className="tai-khoan-truong">
          <label htmlFor="dinh-danh">Email hoặc số điện thoại</label>
          <div className="tk-dang-nhap-truong">
            <BieuTuong ten="thu" />
            <input id="dinh-danh" name="dinhDanh" autoComplete="username" autoCapitalize="none" spellCheck={false}
              placeholder="Email hoặc số điện thoại" defaultValue={email} maxLength={254} required />
          </div>
        </div>
      )}
      <div className="tai-khoan-truong">
        <label htmlFor="mat-khau">Mật khẩu</label>
        <div className="tai-khoan-mat-khau">
          {trang === 'dangNhap' && <BieuTuong ten="khoa" />}
          <input id="mat-khau" name="matKhau" type={hienMatKhau ? 'text' : 'password'}
            autoComplete={trang === 'dangNhap' ? 'current-password' : 'new-password'}
            placeholder={trang === 'dangNhap' ? 'Mật khẩu' : undefined} maxLength={72} required />
          <button type="button" aria-pressed={hienMatKhau} aria-controls="mat-khau"
            aria-label={hienMatKhau ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
            onClick={() => datHienMatKhau(!hienMatKhau)}>
            {trang === 'dangNhap' ? <BieuTuong ten={hienMatKhau ? 'hienThi' : 'anThi'} />
              : hienMatKhau ? 'Ẩn' : 'Hiện'}
          </button>
        </div>
      </div>
      {trang === 'dangKy' && (
        <div className="tai-khoan-truong">
          <label htmlFor="xac-nhan-mat-khau">Xác nhận mật khẩu</label>
          <input id="xac-nhan-mat-khau" name="xacNhanMatKhau" type={hienMatKhau ? 'text' : 'password'}
            autoComplete="new-password" maxLength={72} aria-invalid={matKhauKhongKhop}
            aria-describedby="tk-kiem-tra-mat-khau" required />
          <small id="tk-kiem-tra-mat-khau" role="status">
            {matKhauKhongKhop && 'Mật khẩu xác nhận chưa khớp.'}
          </small>
        </div>
      )}
      <button type="submit" className="tai-khoan-nut" disabled={dangGui}>
        {trang === 'dangNhap' ? <>
          <BieuTuong ten="tenLua" />
          <span>{dangGui ? 'Đang xử lý…' : nhanNut.dangNhap}</span>
          <span className="tk-dang-nhap-mui-ten"><BieuTuong ten="muiTen" /></span>
        </> : dangGui ? 'Đang xử lý…' : nhanNut[trang]}
      </button>
    </form>
  )
}
