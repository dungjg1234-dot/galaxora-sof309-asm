// Chạy seed thủ công bằng phiên ADMIN của localhost. Các ghi kho đi qua phiếu nhập có lịch sử.
import danhSachMau from './duLieuSanPhamMau.mjs'

const cuaHang = '/api/quan-tri/cua-hang'
const kho = '/api/quan-tri/kho'
const khoaPhieu = '23c96ff7-0ef4-5ebc-9006-d111acc99439'
const lyDo = 'GALAXORA · Khởi tạo kho cho bộ 39 sản phẩm mẫu'

async function goi(duongDan, method = 'GET', duLieu) {
  const headers = { Accept: 'application/json' }
  if (duLieu) {
    const csrf = await goi('/api/tai-khoan/csrf')
    headers[csrf.tenHeader] = csrf.token
    headers['Content-Type'] = 'application/json'
  }
  const r = await fetch(duongDan, { method, headers, cache: 'no-store', body: JSON.stringify(duLieu) })
  if (!r.ok) {
    const loi = await r.json().catch(() => null)
    throw new Error(`${duongDan} · HTTP ${r.status}${loi?.detail ? ' · ' + loi.detail : ''}`)
  }
  return r.status === 204 ? undefined : r.json()
}

async function docSanPhamMau() {
  const theoMa = new Map()
  for (let trang = 0; ; trang += 1) {
    const kq = await goi(`${cuaHang}/san-pham?tuKhoa=GX-MAU-&trang=${trang}&kichThuoc=100`)
    for (const s of kq.noiDung) theoMa.set(s.sku, s)
    if ((trang + 1) * kq.kichThuoc >= kq.tong) return theoMa
  }
}

export async function napSanPhamMau() {
  if (!['localhost', '127.0.0.1', '[::1]'].includes(location.hostname)) {
    throw new Error('Seed mẫu chỉ chạy trên localhost.')
  }
  const phien = await goi('/api/tai-khoan/phien')
  if (!phien.vaiTro.includes('ADMIN')) throw new Error('Hãy đăng nhập bằng tài khoản ADMIN để nạp mẫu.')

  const [phieu, nguongKho, theoMa] = await Promise.all([
    goi(kho + '/phieu-nhap'), goi(kho + '/nguong'), docSanPhamMau(),
  ])
  const daNap = phieu.some(p => p.lyDo === lyDo)
  if (!daNap) {
    const nguong = nguongKho.giaTri ?? 5
    if (nguong <= 0 || nguong >= 2147483647) {
      throw new Error('Ngưỡng hiện tại không cho phép kiểm thử đủ ba trạng thái tồn kho.')
    }
    const daCoTon = (await goi(kho + '/ton')).some(t =>
      danhSachMau.some(dong => dong.sku === t.sku) && (t.thucTe !== 0 || t.dangGiu !== 0))
    if (daCoTon) throw new Error('Một SKU mẫu đã có tồn kho trước khi nạp. Hãy đối chiếu Quản trị kho trước khi tiếp tục.')
    for (const dong of danhSachMau) {
      const anh = await fetch(dong.anh, { method: 'HEAD' })
      if (!anh.ok || !anh.headers.get('Content-Type')?.startsWith('image/')) throw new Error('Thiếu ảnh ' + dong.anh)
    }

    if (nguongKho.giaTri === null) await goi(kho + '/nguong', 'PUT', { giaTri: nguong })
    const danhMuc = new Map((await goi(cuaHang + '/danh-muc')).map(d => [d.ten, d.id]))
    const chiTiet = []
    for (const dong of danhSachMau) {
      if (!danhMuc.has(dong.danhMuc)) {
        const muc = await goi(cuaHang + '/danh-muc', 'POST', { ten: dong.danhMuc, hienThi: true })
        danhMuc.set(dong.danhMuc, muc.id)
      }
      const s = theoMa.get(dong.sku) ?? await goi(cuaHang + '/san-pham', 'POST', {
        danhMucId: danhMuc.get(dong.danhMuc), sku: dong.sku, ten: dong.ten,
        moTaNgan: dong.moTa, moTa: dong.moTa, gia: dong.gia, giaSoSanh: dong.giaSoSanh,
        hienThi: true, kinhDoanh: true, hinhAnh: [{ duongDan: dong.anh, moTa: dong.ten }],
      })
      if (dong.ton > 0) {
        const soLuong = dong.ton <= 5 ? Math.min(dong.ton, nguong) : Math.max(dong.ton, nguong + 1)
        chiTiet.push({ sanPhamId: s.id, soLuong })
      }
    }
    await goi(kho + '/phieu-nhap', 'POST', { khoaYeuCau: khoaPhieu, lyDo, chiTiet })
  }

  const [daLuu, ton, nguong] = await Promise.all([
    docSanPhamMau(), goi(kho + '/ton'), goi(kho + '/nguong'),
  ])
  const theoTon = new Map(ton.map(t => [t.sanPhamId, t]))
  return {
    daNap, nguong: nguong.giaTri,
    sanPham: danhSachMau.flatMap(dong => {
      const s = daLuu.get(dong.sku)
      const t = s && theoTon.get(s.id)
      return t ? [{ sku: s.sku, ten: s.ten, danhMuc: s.danhMuc, gia: s.gia, giaSoSanh: s.giaSoSanh,
        ton: t.khaDung, trangThai: t.canhBao }] : []
    }),
  }
}
