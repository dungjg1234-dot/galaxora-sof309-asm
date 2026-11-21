import { goiApi, goiApiKhongDuLieu } from './apiClient.ts'

const tienTo = '/api/tai-khoan'

export type ThongTinPhien = {
  nguoiDungId: number
  vaiTro: string[]
}

type ThongTinCsrf = { tenHeader: string; token: string }

type DuLieuDangKy = {
  hoTen: string
  email: string
  soDienThoai: string
  matKhau: string
}

async function taoYeuCauPost(duLieu?: object): Promise<RequestInit> {
  const csrf = await goiApi<ThongTinCsrf>(`${tienTo}/csrf`, { cache: 'no-store' })
  const headers = new Headers({ [csrf.tenHeader]: csrf.token })
  if (duLieu) headers.set('Content-Type', 'application/json')
  return {
    method: 'POST',
    headers,
    body: duLieu ? JSON.stringify(duLieu) : undefined,
  }
}

async function guiKhongDuLieuTraVe(duongDan: string, duLieu?: object): Promise<void> {
  await goiApiKhongDuLieu(`${tienTo}${duongDan}`, await taoYeuCauPost(duLieu))
}

export const taiKhoanService = {
  dangKy: (duLieu: DuLieuDangKy) => guiKhongDuLieuTraVe('/dang-ky', duLieu),
  async dangNhap(dinhDanh: string, matKhau: string): Promise<ThongTinPhien> {
    return goiApi<ThongTinPhien>(`${tienTo}/dang-nhap`, await taoYeuCauPost({ dinhDanh, matKhau }))
  },
  layPhien: (signal?: AbortSignal) => goiApi<ThongTinPhien>(`${tienTo}/phien`, { signal, cache: 'no-store' }),
  dangXuat: () => guiKhongDuLieuTraVe('/dang-xuat'),
}
