import { useEffect, useState, type FormEvent } from 'react'
import { ghi } from '../banhang/api.ts'
import { taiKhoanService } from '../services/taiKhoanService.ts'
import { theoMa, type MaThienThe } from './thienThe.ts'
import type { HoSoKhamPha } from '../services/nhiemVuService.ts'
import { useDuLieu } from '../banhang/dungChung.tsx'

type TienDo = {
  hoSo: HoSoKhamPha
  cacChuong: { so: number; thienThe: MaThienThe[]; mo: boolean; hoanThanh: boolean }[]
  voucherDangCho: boolean
  nhiemVu: {
    id: number; ten: string; moTa: string; loaiDieuKien: 'THIEN_THE' | 'CHUONG' | 'DAU_HIEU_AN'
    maDieuKien: MaThienThe | null; xp: number; tenHuyHieu: string | null; coVoucher: boolean
    hoanThanh: boolean; voucherDangCho: boolean
  }[]
}

type TinHieu = { ve: string; cauHoi: string; soLanConLai: number }

export default function TienDoKhamPha({ ma, bangMo }: { ma?: MaThienThe; bangMo: boolean }) {
  const [coQuyen, datCoQuyen] = useState(false)
  const [tienDo, datTienDo] = useState<TienDo>()
  const [tinHieu, datTinHieu] = useState<TinHieu>()
  const [loi, datLoi] = useState('')
  const [gui, datGui] = useState(false)
  const noiDung = useDuLieu<{ ma: string; ten: string; moTa: string }[]>(coQuyen ? '/api/nhiem-vu/noi-dung' : null)
  const daTai = !!tienDo
  const daGhiThienThe = tienDo?.hoSo.lichSuXp.some(x => x.nguon === 'KHAM_PHA' && x.maNoiDung === ma)
  const daTim = tienDo?.hoSo.phanThuong.some(p => p.ma === 'VAT_THE_AN')

  useEffect(() => {
    const huy = new AbortController()
    taiKhoanService.layPhien(huy.signal).then(p => {
      if (!huy.signal.aborted) datCoQuyen(p.vaiTro.includes('USER'))
    }).catch(() => {})
    return () => huy.abort()
  }, [])

  useEffect(() => {
    if (!coQuyen) return
    let hienTai = true
    ghi<TienDo>('/api/nhiem-vu/doi-chieu', 'POST')
      .then(d => { if (hienTai) datTienDo(cu => cu ?? d) })
      .catch(() => { if (hienTai) datLoi('Chưa tải được tiến độ khám phá.') })
    return () => { hienTai = false }
  }, [coQuyen])

  useEffect(() => {
    if (!coQuyen || !daTai || !bangMo || !ma || ma === 'sun' || daGhiThienThe) return
    const huy = new AbortController()
    const duongDan = '/api/nhiem-vu/kham-pha/' + ma
    let henGio: number | undefined
    function baoLoi() {
      if (!huy.signal.aborted) datLoi('Chưa ghi nhận được lượt khám phá. Hãy mở lại bảng thông tin.')
    }
    ghi<{ ve: string; choMiliGiay: number }>(duongDan + '/mo-bang', 'POST', undefined, huy.signal)
      .then(({ ve, choMiliGiay }) => {
        if (huy.signal.aborted) return
        henGio = window.setTimeout(() => {
          ghi<TienDo>(duongDan, 'POST', { ve }, huy.signal)
            .then(d => { if (!huy.signal.aborted) datTienDo(d) })
            .catch(baoLoi)
        }, choMiliGiay)
      })
      .catch(baoLoi)
    return () => { huy.abort(); window.clearTimeout(henGio) }
  }, [coQuyen, daTai, bangMo, ma, daGhiThienThe])

  async function moTinHieu() {
    datGui(true)
    datLoi('')
    try {
      datTinHieu(await ghi<TinHieu>('/api/nhiem-vu/dau-hieu', 'POST'))
    } catch (e) {
      datTinHieu(undefined)
      datLoi(e instanceof Error ? e.message : 'Chưa đọc được tín hiệu.')
    } finally { datGui(false) }
  }

  async function nhan(duongDan: string, duLieu?: object) {
    datGui(true)
    datLoi('')
    try {
      const d = await ghi<TienDo>('/api/nhiem-vu/' + duongDan, 'POST', duLieu)
      datTienDo(d)
      if (duongDan === 'vat-the-an') {
        datTinHieu(undefined)
        noiDung.taiLai()
      }
      if (d.voucherDangCho && duongDan === 'voucher') datLoi('Voucher chưa có lượt cấp hợp lệ. Quyền nhận vẫn được giữ.')
      if (duongDan === 'doi-chieu' && d.nhiemVu.some(n => n.voucherDangCho)) {
        datLoi('Một số voucher nhiệm vụ chưa cấp được. Quyền nhận vẫn được giữ.')
      }
    } catch (e) {
      datLoi(e instanceof Error ? e.message : 'Chưa nhận được phần thưởng.')
    } finally { datGui(false) }
  }

  function traLoiTinHieu(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (!tinHieu) return
    const traLoi = Number(new FormData(e.currentTarget).get('traLoi'))
    void nhan('vat-the-an', { ve: tinHieu.ve, traLoi })
  }

  if (!coQuyen) return null
  const daKhamPha = new Set(tienDo?.hoSo.lichSuXp.filter(x => x.nguon === 'KHAM_PHA').map(x => x.maNoiDung))
  return <details className="kp-ho-so kp-tien-do">
    <summary>Hành trình{tienDo && ' · ' + tienDo.hoSo.tongXp + ' XP'}</summary>
    <div className="kp-thong-tin">
      <h2>{noiDung.duLieu?.find(d => d.ma === 'HANH_TRINH')?.ten ?? 'Hành trình Hệ Mặt Trời'}</h2>
      {noiDung.duLieu?.filter(d => ['KHAM_PHA','HANH_TRINH','VAT_THE_AN'].includes(d.ma)).map(d => <p key={d.ma}>{d.moTa}</p>)}
      {tienDo?.cacChuong.map(c => {
        const chuong = noiDung.duLieu?.find(d => d.ma === 'CHUONG_' + c.so)
        return <section key={c.so}>
        <strong>{chuong?.ten ?? 'Chương ' + c.so} · {c.hoanThanh ? 'Hoàn thành' : c.mo ? 'Đang mở' : 'Đang khóa'}</strong>
        {chuong && <p>{chuong.moTa}</p>}
        <ul>{c.thienThe.map(m => <li key={m}>{theoMa[m].ten}{daKhamPha.has(m) ? ' · Đã khám phá' : ''}</li>)}</ul>
      </section>})}
      {tienDo?.voucherDangCho && <button disabled={gui} onClick={() => void nhan('voucher')}>Nhận voucher hành trình</button>}
      {tienDo && tienDo.nhiemVu.length > 0 && <section>
        <h3>Nhiệm vụ</h3>
        <ul>{tienDo.nhiemVu.map(n => <li key={n.id}>
          <strong>{n.ten} · {n.hoanThanh ? 'Hoàn thành' : 'Đang mở'}</strong>
          <p>{n.moTa}</p>
          {n.loaiDieuKien === 'THIEN_THE' && n.maDieuKien && <p>Khám phá {theoMa[n.maDieuKien].ten}</p>}
          {n.loaiDieuKien === 'CHUONG' && <p>Hoàn thành chương {n.maDieuKien}</p>}
          <p>{[n.xp > 0 && n.xp + ' XP', n.tenHuyHieu, n.coVoucher && 'Voucher'].filter(Boolean).join(' · ')}</p>
          {n.voucherDangCho && <p>Voucher đang chờ cấp.</p>}
        </li>)}</ul>
        {tienDo.nhiemVu.some(n => n.voucherDangCho) &&
          <button disabled={gui} onClick={() => void nhan('doi-chieu')}>Thử nhận voucher nhiệm vụ</button>}
      </section>}
      {daTai && !daTim && !tinHieu && <button className="kp-dau-hieu" type="button" aria-label="Kiểm tra tín hiệu nhỏ"
        disabled={gui} onClick={() => void moTinHieu()}>✧</button>}
      {tinHieu && !daTim && <form className="kp-cau-hoi-tin-hieu" onSubmit={traLoiTinHieu}>
        <label>{tinHieu.cauHoi}<input name="traLoi" type="number" min={1} step={1} inputMode="numeric" required /></label>
        <div>
          <button disabled={gui}>Xác nhận</button>
          <button type="button" disabled={gui} onClick={() => void moTinHieu()}>Mở lại tín hiệu</button>
        </div>
      </form>}
      {daTim && <p>Đã tìm thấy vật thể ẩn.</p>}
      {loi && <p role="status">{loi}</p>}
      <a href="/tai-khoan/phien">Xem huy hiệu và voucher trong hồ sơ</a>
    </div>
  </details>
}
