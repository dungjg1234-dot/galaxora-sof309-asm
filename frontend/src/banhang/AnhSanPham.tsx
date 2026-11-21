import { useState } from 'react'
import { BieuTuong } from '../giaodien/BieuTuong.tsx'
import { anhKichThuocNho } from './anhSanPham.ts'
import './anhSanPham.css'

export default function AnhSanPham({ anh, ten, lon = false, uuTien = false }: {
  anh: string | null; ten: string; lon?: boolean; uuTien?: boolean
}) {
  const [anhLoi, datAnhLoi] = useState<string | null>(null)
  const src = anh && (lon ? anh : anhKichThuocNho(anh))
  return <span className={`gx-anh-san-pham${lon ? ' gx-anh-san-pham-lon' : ''}`}>
    {src && src !== anhLoi
      ? <img src={src} alt={ten} loading={uuTien ? 'eager' : 'lazy'} decoding="async"
        referrerPolicy="no-referrer" onError={() => datAnhLoi(src)} />
      : <BieuTuong ten="sao" />}
  </span>
}
