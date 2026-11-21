import { useState } from 'react'
import { ghi } from '../banhang/api.ts'

export default function DoiMatKhau() {
  const [gui, datGui] = useState(false)
  const [loi, datLoi] = useState('')
  return <details className="tai-khoan-kham-pha">
    <summary>Đổi mật khẩu</summary>
    <form onSubmit={async e => {
      e.preventDefault()
      const f = new FormData(e.currentTarget)
      if (f.get('matKhauMoi') !== f.get('nhapLai')) {
        datLoi('Hai ô mật khẩu mới chưa khớp.')
        return
      }
      datGui(true)
      datLoi('')
      try {
        await ghi('/api/tai-khoan/doi-mat-khau', 'POST', { matKhau: f.get('matKhau'), matKhauMoi: f.get('matKhauMoi') })
        window.location.assign('/tai-khoan/dang-nhap')
      } catch (e) {
        datLoi(e instanceof Error ? e.message : 'Chưa đổi được mật khẩu.')
      } finally { datGui(false) }
    }}>
      <label>Mật khẩu hiện tại<input type="password" name="matKhau" autoComplete="current-password" required /></label>
      <label>Mật khẩu mới<input type="password" name="matKhauMoi" autoComplete="new-password" required /></label>
      <label>Nhập lại mật khẩu mới<input type="password" name="nhapLai" autoComplete="new-password" required /></label>
      <button className="tai-khoan-nut" disabled={gui}>Đổi mật khẩu và đăng nhập lại</button>
      {loi && <p role="alert">{loi}</p>}
    </form>
  </details>
}
