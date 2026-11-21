import { useEffect, useState, type ReactNode } from 'react'
import { goiApi, LoiApi } from '../services/apiClient.ts'
import { dinhDangTien, type TongTien } from './duLieu.ts'

export function useDuLieu<T>(duongDan: string | null) {
  const [ketQua, datKetQua] = useState<{ duongDan: string; duLieu?: T; loi?: Error } | null>(null)
  const [lan, datLan] = useState(0)
  useEffect(() => {
    if (!duongDan) return
    const boHuy = new AbortController()
    goiApi<T>(duongDan, { signal: boHuy.signal, cache: 'no-store' })
      .then(duLieu => datKetQua({ duongDan, duLieu }))
      .catch((loi: Error) => { if (!boHuy.signal.aborted) datKetQua({ duongDan, loi }) })
    return () => boHuy.abort()
  }, [duongDan, lan])
  const duLieu = ketQua?.duongDan === duongDan ? ketQua?.duLieu : undefined
  const loi = ketQua?.duongDan === duongDan ? ketQua?.loi : undefined
  return { duLieu, loi, dangTai: !!duongDan && duLieu === undefined && !loi, taiLai: () => datLan(i => i + 1) }
}

export function useThaoTac() {
  const [thongBao, datThongBao] = useState('')
  const [loi, datLoi] = useState('')
  const [dangGui, datDangGui] = useState(false)
  async function lam(thaoTac: () => Promise<void>, ketQua = 'Đã lưu.') {
    if (dangGui) return
    datThongBao('')
    datLoi('')
    datDangGui(true)
    try {
      await thaoTac()
      datThongBao(ketQua)
    } catch (e) {
      datLoi(e instanceof Error ? e.message : 'Không thực hiện được thao tác.')
    } finally {
      datDangGui(false)
    }
  }
  return { lam, dangGui, thongBao, loi }
}

export function TrangThaiTai({ dangTai, loi }: { dangTai: boolean; loi?: Error }) {
  if (dangTai) return <p role="status">Đang tải…</p>
  if (!loi) return null
  if (loi instanceof LoiApi && loi.maTrangThai === 401) {
    return <p className="bh-loi">Hãy <a href="/tai-khoan/dang-nhap">đăng nhập</a> để tiếp tục.</p>
  }
  return <p className="bh-loi" role="alert">{loi instanceof LoiApi && loi.maTrangThai === 403
    ? 'Tài khoản không có quyền xem trang này.' : 'Không tải được dữ liệu. Hãy thử tải lại trang.'}</p>
}

export function PhanHoi({ loi, thongBao }: { loi: string; thongBao: string }) {
  return <>{loi && <p className="bh-loi" role="alert">{loi}</p>}{thongBao && <p role="status">{thongBao}</p>}</>
}

export function PhanTrang({ trang, coTrangSau, datTrang }: {
  trang: number; coTrangSau: boolean; datTrang: (trang: number) => void
}) {
  if (trang === 0 && !coTrangSau) return null
  return <nav className="bh-hang" aria-label="Phân trang">
    <button disabled={trang === 0} onClick={() => datTrang(trang - 1)}>Trang trước</button>
    <span>Trang {trang + 1}</span>
    <button disabled={!coTrangSau} onClick={() => datTrang(trang + 1)}>Trang sau</button>
  </nav>
}

export function ChiPhi({ tien }: { tien: TongTien }) {
  return <dl className="bh-gia">
    <div><dt>Tạm tính</dt><dd>{dinhDangTien(tien.tamTinh)}</dd></div>
    <div><dt>Giảm giá</dt><dd>{dinhDangTien(tien.giamGia)}</dd></div>
    <div><dt>Phí giao hàng</dt><dd>{dinhDangTien(tien.phiVanChuyen)}</dd></div>
    <div><dt>Tổng tiền COD</dt><dd>{dinhDangTien(tien.tongTien)}</dd></div>
  </dl>
}

export function Khoi({ tieuDe, tieuDeChinh, children }: { tieuDe: string; tieuDeChinh?: boolean; children: ReactNode }) {
  const TieuDe = tieuDeChinh ? 'h1' : 'h2'
  return <section className="bh-khoi"><TieuDe>{tieuDe}</TieuDe>{children}</section>
}

export function nhap(form: FormData, ten: string) { return String(form.get(ten) ?? '').trim() }
export function soTuyChon(form: FormData, ten: string) {
  const giaTri = nhap(form, ten)
  return giaTri ? Number(giaTri) : null
}
