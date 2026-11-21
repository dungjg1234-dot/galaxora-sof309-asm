import { goiApi } from '../services/apiClient.ts'

type Csrf = { tenHeader: string; token: string }

export async function ghi<T>(duongDan: string, phuongThuc: 'POST' | 'PUT' | 'DELETE', duLieu?: object, signal?: AbortSignal): Promise<T> {
  const csrf = await goiApi<Csrf>('/api/tai-khoan/csrf', { cache: 'no-store', signal })
  const headers = new Headers({ [csrf.tenHeader]: csrf.token, Accept: 'application/json' })
  const form = duLieu instanceof FormData
  if (duLieu && !form) headers.set('Content-Type', 'application/json')
  const phanHoi = await fetch(duongDan, {
    method: phuongThuc,
    headers,
    signal,
    credentials: 'same-origin',
    body: form ? duLieu : duLieu ? JSON.stringify(duLieu) : undefined,
  })
  if (!phanHoi.ok) {
    const loi = await phanHoi.json().catch(() => null) as { detail?: string } | null
    throw new Error(loi?.detail ?? (phanHoi.status === 401 ? 'Hãy đăng nhập để tiếp tục.'
      : phanHoi.status === 403 ? 'Tài khoản không có quyền thực hiện thao tác này.'
      : phanHoi.status === 400 ? 'Kiểm tra dữ liệu đã nhập.' : 'Không thực hiện được yêu cầu.'))
  }
  return phanHoi.status === 204 ? undefined as T : await phanHoi.json() as T
}

export function thamSo(duLieu: Record<string, string | number | undefined>) {
  const ketQua = new URLSearchParams()
  Object.entries(duLieu).forEach(([ten, giaTri]) => {
    if (giaTri !== undefined && giaTri !== '') ketQua.set(ten, String(giaTri))
  })
  return '?' + ketQua.toString()
}
