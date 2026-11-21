// Hàm gọi API dùng chung. Dùng cùng origin (proxy khi dev) nên cookie session tự được gửi kèm.
export class LoiApi extends Error {
  readonly maTrangThai: number

  constructor(maTrangThai: number, thongBao: string) {
    super(thongBao)
    this.name = 'LoiApi'
    this.maTrangThai = maTrangThai
  }
}

async function guiYeuCau(duongDan: string, tuyChon: RequestInit): Promise<Response> {
  const headers = new Headers(tuyChon.headers)
  if (!headers.has('Accept')) headers.set('Accept', 'application/json')

  const phanHoi = await fetch(duongDan, {
    ...tuyChon,
    credentials: 'same-origin',
    headers,
  })

  if (!phanHoi.ok) {
    throw new LoiApi(phanHoi.status, `Yêu cầu thất bại (mã ${phanHoi.status})`)
  }
  return phanHoi
}

export async function goiApi<T>(duongDan: string, tuyChon: RequestInit = {}): Promise<T> {
  const phanHoi = await guiYeuCau(duongDan, tuyChon)
  return (await phanHoi.json()) as T
}

export async function goiApiKhongDuLieu(duongDan: string, tuyChon: RequestInit): Promise<void> {
  await guiYeuCau(duongDan, tuyChon)
}
