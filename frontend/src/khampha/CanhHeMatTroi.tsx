import { useEffect, useRef } from 'react'
import { lapCanhHeMatTroi, type GocNhin, type TrangThaiMoHinh } from './lapCanhHeMatTroi.ts'
import { cacThienThe, type MaThienThe } from './thienThe.ts'

type Props = {
  goc: GocNhin
  tocDo: number
  chuyenDongNhe: boolean
  chon: (ma: MaThienThe) => void
  baoLoi: () => void
  baoMoHinh: (ma: MaThienThe, trangThai: TrangThaiMoHinh) => void
}

export default function CanhHeMatTroi({ goc, tocDo, chuyenDongNhe, chon, baoLoi, baoMoHinh }: Props) {
  const canvas = useRef<HTMLCanvasElement>(null)
  const nhan = useRef({} as Record<MaThienThe, HTMLSpanElement | null>)
  const canh = useRef<ReturnType<typeof lapCanhHeMatTroi>>(null)

  useEffect(() => {
    if (!canvas.current) return
    try {
      canh.current = lapCanhHeMatTroi(canvas.current, nhan.current, { chon, baoLoi, baoMoHinh })
    } catch {
      canh.current = null
    }
    if (!canh.current) {
      baoLoi()
      return
    }
    return () => {
      canh.current?.dong()
      canh.current = null
    }
  }, [chon, baoLoi, baoMoHinh])

  useEffect(() => { canh.current?.datChuyenDongNhe(chuyenDongNhe) }, [chuyenDongNhe])
  useEffect(() => { canh.current?.datGoc(goc) }, [goc])
  useEffect(() => { canh.current?.datTocDo(tocDo) }, [tocDo])

  return <>
    <canvas ref={canvas} className="kp-canvas" tabIndex={0} aria-label="Mô phỏng Hệ Mặt Trời 3D"
      aria-describedby="kp-dieu-huong" />
    <div className="kp-nhan-canh" aria-hidden="true">
      {cacThienThe.map(t => <span key={t.ma} ref={o => { nhan.current[t.ma] = o }} hidden>{t.ten}</span>)}
    </div>
  </>
}
