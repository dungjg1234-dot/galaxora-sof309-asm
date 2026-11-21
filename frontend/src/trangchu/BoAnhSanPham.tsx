import { useId, useRef, useState } from 'react'
import AnhSanPham from '../banhang/AnhSanPham.tsx'
import type { Anh } from '../banhang/duLieu.ts'

export default function BoAnhSanPham({ anh, danhSach, ten }: { anh?: Anh; danhSach: Anh[]; ten: string }) {
  const [dangXem, datDangXem] = useState(0)
  const hopThoai = useRef<HTMLDialogElement>(null)
  const nutMo = useRef<HTMLButtonElement>(null)
  const tieuDe = useId()
  const anhLon = danhSach[dangXem]
  if (!anh) return <div className="bh-anh-chi-tiet"><AnhSanPham anh={null} ten={ten} lon /></div>
  function doiAnh(buoc: number) {
    datDangXem(viTri => (viTri + buoc + danhSach.length) % danhSach.length)
  }

  return <>
    <button ref={nutMo} type="button" className="bh-anh-chi-tiet bh-mo-anh" aria-haspopup="dialog" aria-label="Mở ảnh sản phẩm"
      onClick={() => { datDangXem(Math.max(0, danhSach.findIndex(a => a.duongDan === anh.duongDan))); hopThoai.current?.showModal() }}>
      <AnhSanPham anh={anh.duongDan} ten={anh.moTa || ten} lon uuTien />
    </button>
    <dialog ref={hopThoai} className="bh-dialog bh-anh-hop" aria-labelledby={tieuDe}
      onClose={() => nutMo.current?.focus({ preventScroll: true })}
      onClick={e => { if (e.target === e.currentTarget) hopThoai.current?.close() }}
      onKeyDown={e => {
        if (danhSach.length < 2 || (e.key !== 'ArrowLeft' && e.key !== 'ArrowRight')) return
        e.preventDefault()
        doiAnh(e.key === 'ArrowLeft' ? -1 : 1)
      }}>
      <div>
        <header className="bh-hang bh-can"><h2 id={tieuDe}>{ten}</h2>
          <button type="button" onClick={() => hopThoai.current?.close()}>Đóng ảnh</button></header>
        {anhLon && <img src={anhLon.duongDan} alt={anhLon.moTa || ten} referrerPolicy="no-referrer" />}
        {danhSach.length > 1 && <div className="bh-hang bh-can">
          <button type="button" onClick={() => doiAnh(-1)}>Ảnh trước</button>
          <span role="status">{dangXem + 1} / {danhSach.length}</span>
          <button type="button" onClick={() => doiAnh(1)}>Ảnh sau</button>
        </div>}
      </div>
    </dialog>
  </>
}
