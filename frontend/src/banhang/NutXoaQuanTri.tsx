import { ghi } from './api.ts'
import { PhanHoi, useThaoTac } from './dungChung.tsx'

export default function NutXoaQuanTri({ nhan, doiTuong, duongDan, sauXoa, disabled }: {
  nhan: string; doiTuong: string; duongDan: string; sauXoa: () => void; disabled?: boolean
}) {
  const tacVu = useThaoTac()
  return <div className="bh-xoa-quan-tri">
    <button type="button" disabled={disabled || tacVu.dangGui} onClick={() => {
      if (!window.confirm(`Bạn xác nhận xóa ${doiTuong}?`)) return
      void tacVu.lam(async () => {
        await ghi(duongDan, 'DELETE')
        sauXoa()
      }, 'Đã xóa.')
    }}>{tacVu.dangGui ? 'Đang xóa…' : nhan}</button>
    <PhanHoi {...tacVu} />
  </div>
}
