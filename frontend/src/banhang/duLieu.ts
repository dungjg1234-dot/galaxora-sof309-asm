export type PhanTrang<T> = { noiDung: T[]; tong: number; trang: number; kichThuoc: number }
export type DanhMuc = { id: number; ten: string; hienThi: boolean }
export type Anh = { duongDan: string; moTa: string }
export type SanPham = {
  id: number; danhMucId: number; danhMuc: string; sku: string; ten: string
  moTaNgan: string; moTa: string; gia: number; giaSoSanh: number | null
  hienThi: boolean; kinhDoanh: boolean; anh: string | null; coMoHinh: boolean; trangThaiKho: string; soLuongKhaDung: number
}
export type ChiTietSanPham = { sanPham: SanPham; hinhAnh: Anh[]; lienQuan: SanPham[] }
export type DanhGia = {
  donHangId: number; sanPhamId: number; tacGia: string; soSao: number; noiDung: string; hienThi: boolean; capNhatLuc: string
}
export type DongGio = {
  sanPhamId: number; sku: string; ten: string; gia: number; soLuong: number; khaDung: number; dangBan: boolean; anh: string | null
}
export type Gio = { chiTiet: DongGio[]; tamTinh: number }
export type DiaChi = { id: number; nguoiNhan: string; soDienThoai: string; diaChi: string }
export type TongTien = { tamTinh: number; giamGia: number; phiVanChuyen: number; tongTien: number }
export type TrangThaiDon = 'CHO_XAC_NHAN' | 'DA_XAC_NHAN' | 'DANG_CHUAN_BI' | 'DANG_GIAO' | 'DA_GIAO_THANH_CONG' | 'DA_HUY' | 'GIAO_THAT_BAI'
export type Don = TongTien & {
  id: number; ma: string; nguoiDungId: number; nguoiNhan: string; soDienThoai: string; diaChi: string; ghiChu: string
  phuongThuc: 'COD'; trangThai: TrangThaiDon; daThuTien: boolean; thoiDiem: string
}
export type DongDon = { sanPhamId: number; sku: string; ten: string; donGia: number; soLuong: number }
export type YeuCauHuy = { id: number; trangThai: string; lyDo: string; lyDoXuLy: string | null; thoiDiem: string }
export type ChiTietDon = {
  don: Don; chiTiet: DongDon[]; lichSu: { trangThai: TrangThaiDon; lyDo: string; thoiDiem: string }[]; yeuCauHuy: YeuCauHuy[]
}
export type TonKho = {
  sanPhamId: number; sku: string; ten: string; thucTe: number; dangGiu: number; khaDung: number; canhBao: string
}
export type GiaoDich = {
  id: number; loai: string; thayDoiThucTe: number; thayDoiDangGiu: number; thucTeSau: number
  dangGiuSau: number; lyDo: string; thoiDiem: string; phieuNhapId: number | null; donHangId: number | null
}
export type Phieu = { id: number; lyDo: string; thoiDiem: string }
export type DongNhap = { sanPhamId: number; soLuong: number }
export type ChiTietPhieu = { phieu: Phieu; chiTiet: DongNhap[] }
export type MoHinh = {
  tenTep: string; dinhDang: string; dungLuong: number; giayPhep: string; nguon: string; tacGia: string; ghiCong: string; daDuyet: boolean
}
export type Voucher = {
  id: number; ma: string; loai: 'PHAN_TRAM' | 'SO_TIEN'; giaTri: number; giamToiDa: number | null
  donToiThieu: number | null; batDau: string; ketThuc: string; luotToiDa: number; luotDaDung: number
  kichHoat: boolean; chuSoHuuId: number | null
}
export const nhanDon: Record<TrangThaiDon, string> = {
  CHO_XAC_NHAN: 'Chờ xác nhận', DA_XAC_NHAN: 'Đã xác nhận', DANG_CHUAN_BI: 'Đang chuẩn bị',
  DANG_GIAO: 'Đang giao', DA_GIAO_THANH_CONG: 'Đã giao thành công', DA_HUY: 'Đã hủy', GIAO_THAT_BAI: 'Giao thất bại',
}
export const nhanKho: Record<string, string> = { HET_HANG: 'Hết hàng', SAP_HET: 'Sắp hết', CON_HANG: 'Còn hàng' }
export const nhanHuy: Record<string, string> = { CHO_DUYET: 'Chờ duyệt', DA_DUYET: 'Đã duyệt', TU_CHOI: 'Từ chối' }
export const nhanBienDong: Record<string, string> = {
  NHAP: 'Nhập kho', XUAT: 'Xuất kho', DIEU_CHINH: 'Điều chỉnh', GIU: 'Giữ hàng', GIAI_PHONG: 'Giải phóng',
  XUAT_BAN: 'Xuất bán',
}
const tien = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 2 })
export const dinhDangTien = (gia: number) => tien.format(gia)
export const dinhDangGio = (iso: string) => new Date(iso + 'Z').toLocaleString('vi-VN')
