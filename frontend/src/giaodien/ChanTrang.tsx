import { BieuTuong, BieuTuongThuongHieu } from './BieuTuong.tsx'
import { cacLienKet, type MucDieuHuong } from './ThanhDauTrang.tsx'

const cacNhom = [
  { ten: 'Khám phá', lienKet: [
    ['Hệ Mặt Trời', '/kham-pha'], ['Thiên hà', '/kham-pha/thien-ha'],
    ['Tinh vân', '/kham-pha/tinh-van'], ['Hố đen', '/kham-pha/ho-den'],
  ] },
  { ten: 'Cửa hàng', lienKet: [
    ['Sản phẩm', '/cua-hang'], ['Giỏ hàng', '/gio-hang'], ['Đơn hàng', '/don-hang'],
  ] },
  { ten: 'Tài khoản', lienKet: [
    ['Tài khoản của bạn', '/tai-khoan/phien'],
  ] },
] as const

export default function ChanTrang({ muc, gon }: { muc?: MucDieuHuong; gon?: boolean }) {
  return <footer className={gon ? 'gx-chan gx-chan-gon' : 'gx-chan'}>
    <div className="gx-khung gx-chan-trong">
      <a className="gx-thuong-hieu" href="/"><BieuTuongThuongHieu /><span>GALAXORA</span></a>
      {gon ? <nav aria-label="Điều hướng chân trang">
        {cacLienKet.map(({ duongDan, ten }) => <a key={ten} href={duongDan}
          aria-current={duongDan === muc && ten !== 'Hành trình' ? 'page' : undefined}>{ten}</a>)}
      </nav> : <nav className="gx-chan-cac-nhom" aria-label="Điều hướng chân trang">
        {cacNhom.map(({ ten, lienKet }) => <div key={ten}>
          <h2>{ten}</h2>
          <ul>{lienKet.map(([nhan, duongDan]) => <li key={duongDan}>
            <a href={duongDan} aria-current={window.location.pathname === duongDan ? 'page' : undefined}>{nhan}</a>
          </li>)}</ul>
        </div>)}
      </nav>}
      <div className="gx-chan-hanh-dong">
        <a className="gx-len-dau gx-vong-nut" href="#gx-dau-trang" aria-label="Lên đầu trang"><BieuTuong ten="len" /></a>
      </div>
    </div>
  </footer>
}
