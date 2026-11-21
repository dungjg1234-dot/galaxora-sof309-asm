import { useEffect, useState } from 'react'
import { cacThienThe } from '../khampha/thienThe.ts'
import { layHoSoKhamPha, type HoSoKhamPha as DuLieuHoSo } from '../services/nhiemVuService.ts'
import { useDuLieu } from '../banhang/dungChung.tsx'

const tenNguon: Record<string, string> = {
  KHAM_PHA: 'Khám phá thiên thể',
  HANH_TRINH: 'Hành trình Hệ Mặt Trời',
  VAT_THE_AN: 'Nhiệm vụ bí mật',
  NHIEM_VU: 'Hoàn thành nhiệm vụ',
}

function moTaXp(nguon: string, ma: string) {
  if (nguon === 'KHAM_PHA') {
    const ten = cacThienThe.find(t => t.ma === ma)?.ten
    if (ten) return 'Khám phá ' + ten
  }
  return tenNguon[nguon] ?? 'Ghi nhận XP'
}

export default function HoSoKhamPha() {
  const [duLieu, datDuLieu] = useState<DuLieuHoSo | null>(null)
  const [loi, datLoi] = useState(false)
  const [lanTai, datLanTai] = useState(0)
  const tb = useDuLieu<{ noiDung: string; duongDan: string }[]>('/api/nhiem-vu/thong-bao')
  const noiDung = useDuLieu<{ ma: string; ten: string; moTa: string }[]>('/api/nhiem-vu/noi-dung')

  useEffect(() => {
    const huy = new AbortController()
    layHoSoKhamPha(huy.signal)
      .then(datDuLieu)
      .catch(() => { if (!huy.signal.aborted) datLoi(true) })
    return () => huy.abort()
  }, [lanTai])

  return <section className="tai-khoan-kham-pha" aria-labelledby="tai-khoan-kham-pha-tieu-de">
    <h2 id="tai-khoan-kham-pha-tieu-de">Hồ sơ khám phá</h2>
    {loi ? <>
      <p role="alert">Chưa tải được hồ sơ khám phá.</p>
      <button type="button" onClick={() => { datLoi(false); datLanTai(lanTai + 1) }}>Thử lại</button>
    </> : duLieu ? <>
      <p><strong className="gx-cap">Cấp {duLieu.capDo.cap}</strong> · {duLieu.tongXp} XP</p>
      <progress className="gx-thanh-xp" aria-label="Tiến độ lên cấp tiếp theo"
        value={Math.max(0, duLieu.tongXp - duLieu.capDo.mocCapHienTai)}
        max={duLieu.capDo.mocCapTiepTheo - duLieu.capDo.mocCapHienTai} />
      <p>{duLieu.capDo.mocCapTiepTheo - duLieu.tongXp} XP để lên cấp {duLieu.capDo.cap + 1}</p>
      <details>
        <summary>Lịch sử XP</summary>
        {duLieu.lichSuXp.length ? <ul>{duLieu.lichSuXp.map(xp => <li key={xp.nguon + ':' + xp.maNoiDung}>
          {moTaXp(xp.nguon, xp.maNoiDung)} · {xp.soXp > 0 ? '+' : ''}{xp.soXp} XP
        </li>)}</ul> : <p>Chưa có XP được ghi nhận.</p>}
      </details>
      <details>
        <summary>Phần thưởng</summary>
        {duLieu.phanThuong.length ? <ul>{duLieu.phanThuong.map(p => {
          const huyHieu = noiDung.duLieu?.find(d => d.ma === 'HUY_HIEU_' + p.ma)
          return <li key={p.ma}>
            {p.loai === 'VOUCHER' ? <>Voucher {p.maVoucher && <code>{p.maVoucher}</code>}</> :
              p.tenHuyHieu ?? huyHieu?.ten ?? 'Huy hiệu · ' + (tenNguon[p.ma] ?? p.ma)}
            {huyHieu && <p>{huyHieu.moTa}</p>}
          </li>
        })}</ul> : <p>Chưa có phần thưởng.</p>}
      </details>
    </> : <p role="status">Đang tải hồ sơ khám phá…</p>}
    <a href="/kham-pha">Mở Hệ Mặt Trời <span aria-hidden="true">↗</span></a>
    <details>
      <summary>Thông báo</summary>
      {tb.dangTai && <p role="status">Đang tải thông báo…</p>}
      {tb.loi && <p role="alert">Chưa tải được thông báo.</p>}
      {tb.duLieu && (tb.duLieu.length ? <ul>{tb.duLieu.map((t,i) =>
        <li key={t.duongDan + i}><a href={t.duongDan}>{t.noiDung}</a></li>)}</ul> : <p>Chưa có thông báo.</p>)}
    </details>
  </section>
}
