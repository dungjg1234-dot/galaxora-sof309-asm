import { useEffect, useState } from 'react'
import { LoiApi } from '../services/apiClient.ts'
import { taiKhoanService, type ThongTinPhien } from '../services/taiKhoanService.ts'

type TrangThaiPhien = { trangThai: 'dang-tai' | 'khach' | 'loi' }
  | { trangThai: 'da-dang-nhap'; phien: ThongTinPhien }

export function usePhienTrangChu() {
  const [ketQua, datKetQua] = useState<TrangThaiPhien>({ trangThai: 'dang-tai' })
  const [lan, datLan] = useState(0)

  useEffect(() => {
    const boHuy = new AbortController()
    const signal = boHuy.signal
    void taiKhoanService.layPhien(signal).then(phien => {
      if (!signal.aborted) datKetQua({ trangThai: 'da-dang-nhap', phien })
    }).catch((loi: unknown) => {
      if (!signal.aborted) datKetQua({ trangThai: loi instanceof LoiApi && loi.maTrangThai === 401 ? 'khach' : 'loi' })
    })
    return () => boHuy.abort()
  }, [lan])

  return {
    ...ketQua,
    taiLai: () => { datKetQua({ trangThai: 'dang-tai' }); datLan(i => i + 1) },
  }
}
