import { useId, type ReactNode } from 'react'

const netVe = {
  nha: <path d="m3 11 9-8 9 8M5 10v10h5v-6h4v6h5V10" />,
  tenLua: <><path d="M8 15a17 17 0 0 1 7-10c2-1 4-2 7-2 0 3-1 5-2 7a17 17 0 0 1-10 7l-2-2ZM8 15l-4-1 2-5 6-1m-2 9 1 4 5-2 1-6M7 17c-2 0-4 2-4 4 2 0 4-1 5-3" /><circle cx="16.5" cy="8.5" r="2" /></>,
  gio: <><path d="M2 4h3l3 12h11l3-9H6m3 5h11" /><circle cx="9" cy="20" r="1" /><circle cx="18" cy="20" r="1" /></>,
  cup: <path d="M8 3h8v6a4 4 0 0 1-8 0V3Zm0 2H4v2a4 4 0 0 0 4 4m8-6h4v2a4 4 0 0 1-4 4m-4 2v5m-4 3h8m-7-3h6v3" />,
  nguoi: <><circle cx="12" cy="7" r="3.2" fill="var(--gx-icon-ruot)" stroke="none" /><path d="M3.5 20v-2a8.5 6 0 0 1 17 0v2Z" fill="var(--gx-icon-ruot)" stroke="none" /></>,
  dangKy: <><circle cx="9" cy="7" r="3.3" /><path d="M3 21v-2a6 5 0 0 1 12 0v2H3Zm16-9v7m-3.5-3.5h7" /></>,
  sao: <path d="M12 1c1.4 7.2 3.8 9.6 11 11-7.2 1.4-9.6 3.8-11 11C10.6 15.8 8.2 13.4 1 12 8.2 10.6 10.6 8.2 12 1Z" fill="var(--gx-icon-ruot)" stroke="none" />,
  muiTen: <path d="M4 12h15m-6-6 6 6-6 6" />,
  len: <path d="M12 20V4m-6 6 6-6 6 6" />,
  menu: <path d="M3 6h18M3 12h18M3 18h18" />,
  hop: <path d="m12 2 9 5v10l-9 5-9-5V7l9-5Zm0 10 9-5m-9 5L3 7m9 5v10" />,
  kho: <path d="m3 10 9-7 9 7v11H3V10Zm4 11v-9h10v9m-10-5h10" />,
  ve: <path d="M3 6h18v4a2 2 0 0 0 0 4v4H3v-4a2 2 0 0 0 0-4V6Zm9 .5v3m0 2v1m0 2v3" />,
  tep: <path d="M5 3h10l4 4v14H5V3Zm10 0v5h4M8 12h8m-8 4h8" />,
  chuong: <path d="M6 9a6 6 0 1 1 12 0v5l2 3H4l2-3V9Zm4 11a2 2 0 0 0 4 0" />,
  thoat: <path d="M10 4H4v16h6m3-13 5 5-5 5m-5-5h13" />,
  doanhThu: <><ellipse cx="12" cy="6" rx="8" ry="3" /><path d="M4 6v6c0 4 16 4 16 0V6M4 12v6c0 4 16 4 16 0v-6" /></>,
  thu: <><rect x="3" y="5" width="18" height="14" rx="2" /><path d="m3 7 9 6 9-6" /></>,
  khoa: <><rect x="5" y="10" width="14" height="11" rx="2" /><path d="M8 10V7a4 4 0 1 1 8 0v3m-4 5v2" /></>,
  hienThi: <><path d="M2 12s3.5-6.5 10-6.5S22 12 22 12s-3.5 6.5-10 6.5S2 12 2 12Z" /><circle cx="12" cy="12" r="2.5" /></>,
  anThi: <><path d="m3 3 18 18M10.6 5.6 12 5.5C18.5 5.5 22 12 22 12a17 17 0 0 1-3 3.7M6 7.5A17 17 0 0 0 2 12s3.5 6.5 10 6.5a11 11 0 0 0 4-1" /></>,
  huyHieu: <><path d="m12 2 8.7 5v10L12 22l-8.7-5V7L12 2Z" /><path d="m12 7 1.4 3 3.3.4-2.4 2.3.6 3.3-2.9-1.6-2.9 1.6.6-3.3-2.4-2.3 3.3-.4L12 7Z" /></>,
} satisfies Record<string, ReactNode>

export function BieuTuong({ ten }: { ten: keyof typeof netVe }) {
  const id = useId()
  const hinhHuyHieu = ten === 'huyHieu' || ten === 'cup'
  const mat = `url(#${id}-mat)`
  const vien = `url(#${id}-vien)`
  return <svg className="gx-bieu-tuong gx-bieu-tuong-mau" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
    <defs>
      <radialGradient id={id + '-mat'} cx="35%" cy="24%" r="73%">
        <stop stopColor="var(--gx-icon-quang)" stopOpacity=".65" />
        <stop offset="1" stopColor="var(--gx-icon-mat)" />
      </radialGradient>
      <linearGradient id={id + '-vien'} x2="100%" y2="100%">
        <stop stopColor="var(--gx-icon-cyan)" />
        <stop offset="1" stopColor="var(--gx-icon-tim)" />
      </linearGradient>
    </defs>
    {hinhHuyHieu ? <path className="gx-icon-nen" d="m12 .9 9.6 5.55v11.1L12 23.1l-9.6-5.55V6.45Z" fill={mat} stroke="var(--gx-icon-vien)" /> :
      <circle className="gx-icon-nen" cx="12" cy="12" r="11" fill={mat} stroke="var(--gx-icon-vien)" />}
    <g className="gx-icon-net" transform="translate(3.4 3.4) scale(.7167)" fill="none"
      stroke={vien} strokeWidth="1.9" strokeLinecap="round" strokeLinejoin="round">{netVe[ten]}</g>
    <circle className="gx-icon-dom" cx="19" cy="5" r="1.15" fill="var(--gx-icon-cyan)" />
  </svg>
}

export function BieuTuongThuongHieu() {
  const ma = useId()
  return <svg className="gx-logo" viewBox="0 0 80 54" aria-hidden="true" focusable="false">
    <defs>
      <radialGradient id={ma + '-cau'} cx="72%" cy="32%" r="73%">
        <stop stopColor="var(--gx-logo-sang)" /><stop offset=".35" stopColor="var(--gx-logo-xanh)" />
        <stop offset=".71" stopColor="var(--gx-logo-tim)" /><stop offset="1" stopColor="var(--gx-logo-toi)" />
      </radialGradient>
      <linearGradient id={ma + '-vanh'} x2="1" y2=".7" gradientUnits="objectBoundingBox">
        <stop stopColor="var(--gx-vanh-logo-sang)" /><stop offset=".5" stopColor="var(--gx-vanh-logo-xanh)" /><stop offset="1" stopColor="var(--gx-vanh-logo-tim)" />
      </linearGradient>
    </defs>
    <ellipse cx="40" cy="27" rx="37" ry="10" transform="rotate(-28 40 27)" fill="none" stroke={'url(#' + ma + '-vanh)'} strokeWidth="2.5" />
    <circle cx="40" cy="27" r="21" fill={'url(#' + ma + '-cau)'} stroke={'url(#' + ma + '-vanh)'} strokeWidth="1.2" />
    <path d="M8 42c6 5 23 1 39-7 16-8 26-17 28-22" fill="none" stroke={'url(#' + ma + '-vanh)'} strokeWidth="3" strokeLinecap="round" />
    <path d="M56 12c.7 3 2 4.3 5 5-3 .7-4.3 2-5 5-.7-3-2-4.3-5-5 3-.7 4.3-2 5-5Z" fill="var(--gx-sao-logo)" />
  </svg>
}

const bieuTuongMangXaHoi = [
  { ten: 'YouTube', net: 'M2 7a3 3 0 0 1 2.5-2.8 60 60 0 0 1 15 0A3 3 0 0 1 22 7a32 32 0 0 1 0 10 3 3 0 0 1-2.5 2.8 60 60 0 0 1-15 0A3 3 0 0 1 2 17 32 32 0 0 1 2 7Zm8 1v8l6-4-6-4Z' },
  { ten: 'Discord', net: 'M19.7 5a19 19 0 0 0-4.5-1.4l-.5 1.1a17 17 0 0 0-5.4 0l-.5-1.1A18 18 0 0 0 4.3 5C1.5 9.1.7 13.1 1.1 17.1a19 19 0 0 0 5.5 2.8l1.2-1.9-1.7-.8.4-.3a15 15 0 0 0 11 0l.4.3-1.7.8 1.2 1.9a19 19 0 0 0 5.5-2.8C23.4 12.5 22 8.5 19.7 5ZM8.5 14.8a1.8 2 0 1 1 0-4 1.8 2 0 0 1 0 4Zm7 0a1.8 2 0 1 1 0-4 1.8 2 0 0 1 0 4Z' },
  { ten: 'Facebook', net: 'M12 1.5c-5.8 0-10.5 4.7-10.5 10.5 0 5.2 3.8 9.6 8.9 10.4V15H7.8v-3h2.6V9.8c0-2.6 1.5-4 3.9-4 1.1 0 2.2.2 2.2.2v2.5h-1.2c-1.3 0-1.7.8-1.7 1.6V12h2.9l-.5 3h-2.4v7.4a10.5 10.5 0 0 0-1.6-20.9Z' },
  { ten: 'TikTok', net: 'M14 2h3.3a5 5 0 0 0 4.7 4.6V10a8 8 0 0 1-4.7-1.5v7.2a6.2 6.2 0 1 1-6.2-6.2l1 .1v3.4l-1-.2a2.9 2.9 0 1 0 2.9 2.9V2Z' },
]

export function BieuTuongMangXaHoi() {
  return <div className="gx-mang-xa-hoi" aria-hidden="true">
    {bieuTuongMangXaHoi.map(({ ten, net }) => <svg key={ten} className="gx-bieu-tuong" viewBox="0 0 24 24" fill="currentColor" focusable="false">
      <path d={net} fillRule="evenodd" />
    </svg>)}
  </div>
}
