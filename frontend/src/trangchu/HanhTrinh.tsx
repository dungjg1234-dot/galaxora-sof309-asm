import { useDuLieu } from '../banhang/dungChung.tsx'
import { LoiApi } from '../services/apiClient.ts'
import type { HoSoKhamPha } from '../services/nhiemVuService.ts'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'

const dinhDangSo = new Intl.NumberFormat('vi-VN')

export default function HanhTrinh() {
  const hoSo = useDuLieu<HoSoKhamPha>('/api/nhiem-vu/ho-so')
  const duLieu = hoSo.duLieu
  const huyHieu = duLieu?.phanThuong.filter(p => p.loai === 'HUY_HIEU').slice(0, 4) ?? []
  const noiDung = useDuLieu<{ ma: string; ten: string }[]>(huyHieu.some(h => !h.tenHuyHieu) ? '/api/nhiem-vu/noi-dung' : null)

  return <section className="gx-hanh-trinh gx-kinh" aria-labelledby="gx-hanh-trinh-tieu-de">
    <h2 className="gx-tieu-de-khoi" id="gx-hanh-trinh-tieu-de"><BieuTuong ten="sao" /> Hành trình nhà thám hiểm</h2>
    {hoSo.dangTai && <p className="gx-thong-bao" role="status">Đang tải hồ sơ khám phá…</p>}
    {hoSo.loi && <div className="gx-thong-bao" role="alert">
      {hoSo.loi instanceof LoiApi && hoSo.loi.maTrangThai === 401 ? <>
        Phiên đã hết hạn. <a href="/tai-khoan/dang-nhap">Đăng nhập lại</a>
      </> : <>Không tải được hồ sơ khám phá. <button type="button" onClick={hoSo.taiLai}>Thử lại</button></>}
    </div>}
    {duLieu && <div className="gx-hanh-trinh-noi-dung">
      <span className="gx-hinh-tham-hiem" aria-hidden="true"><BieuTuong ten="nguoi" /></span>
      <div className="gx-xp">
        <p className="gx-cap"><span className="gx-an-thi-giac">Cấp </span><span aria-hidden="true">Lv.</span> <strong>{duLieu.capDo.cap}</strong></p>
        <progress className="gx-thanh-xp" aria-label="Tiến độ lên cấp tiếp theo" value={Math.max(0, duLieu.tongXp - duLieu.capDo.mocCapHienTai)}
          max={duLieu.capDo.mocCapTiepTheo - duLieu.capDo.mocCapHienTai} />
        <p className="gx-xp-moc">{dinhDangSo.format(duLieu.tongXp)} / {dinhDangSo.format(duLieu.capDo.mocCapTiepTheo)} XP</p>
      </div>
      <div className="gx-huy-hieu">
        <h3>Huy hiệu đạt được</h3>
        {huyHieu.length ? <ul>
          {huyHieu.map(h => <li key={h.ma}><BieuTuong ten="huyHieu" />
            <span>{h.tenHuyHieu ?? noiDung.duLieu?.find(d => d.ma === 'HUY_HIEU_' + h.ma)?.ten ?? h.ma}</span>
          </li>)}
        </ul> : <p className="gx-thong-bao" role="status">Chưa có huy hiệu.</p>}
        {noiDung.loi && <p className="gx-thong-bao" role="alert">Không tải được tên huy hiệu. <button type="button" onClick={noiDung.taiLai}>Thử lại</button></p>}
        <a className="gx-nut gx-nut-phu" href="/tai-khoan/phien">Xem chi tiết <BieuTuong ten="muiTen" /></a>
      </div>
    </div>}
  </section>
}
