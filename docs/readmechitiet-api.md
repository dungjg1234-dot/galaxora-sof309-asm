# Danh mục API GALAXORA

[Về README chi tiết](../READMECHITIET.md#8-api)

## 8.1. Quy ước

Phân quyền kiểm tra trong [CauHinhBaoMat.java](../backend/src/main/java/com/galaxora/cauhinh/CauHinhBaoMat.java). `Công khai` cho phép khách. `Phiên` yêu cầu đăng nhập. `USER` và `ADMIN` yêu cầu vai trò tương ứng. Mọi POST/PUT yêu cầu header CSRF kể cả luồng đăng ký/đăng nhập. Lấy tên header và token từ GET `/api/tai-khoan/csrf`. Các yêu cầu dùng cookie phiên cùng origin.

Các cột lỗi dưới đây tóm tắt trường hợp nghiệp vụ, binding và xác thực gần nhất với endpoint. `401` áp dụng khi thiếu phiên trên đường dẫn bảo vệ. `403` áp dụng khi sai quyền hoặc CSRF. Với bán hàng, các lỗi nghiệp vụ được ánh xạ sang `ProblemDetail` có `status`, `title` và `detail` theo [PhanHoiBanHang.java](../backend/src/main/java/com/galaxora/banhang/PhanHoiBanHang.java). Ký hiệu `—` nghĩa là không có ngoại lệ nghiệp vụ riêng liệt kê tại dòng đó. Ví dụ phản hồi là dữ liệu minh họa, dấu `…` báo còn trường trong DTO. Tên kiểu liên kết tới định nghĩa DTO ở dưới.

## 8.2. Endpoint

**Cập nhật 19/11/2026.** Sổ địa chỉ và cơ chế vé nhiệm vụ dùng [hợp đồng API hiện hành](api-specification.md). Vé xác nhận khám phá đi qua `POST /api/nhiem-vu/kham-pha/{ma}/mo-bang` trước khi ghi lượt. Nhiệm vụ bí mật cần nộp câu trả lời cùng vé.

| Phương thức | Đường dẫn | Mô tả | Tham số | Body | Phản hồi mẫu | Mã lỗi | Xác thực | Mã nguồn |
|---|---|---|---|---|---|---|---|---|
| GET | `/api/cua-hang/danh-muc` | Danh mục công khai | — | — | 200 `[]` hoặc danh sách [DichVuCuaHang.DanhMuc](#dichvucuahangdanhmuc) | — | Công khai | [CuaHangController.java:40](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L40) |
| GET | `/api/quan-tri/cua-hang/danh-muc` | Tất cả danh mục cho Admin | — | — | 200 `[]` hoặc danh sách [DichVuCuaHang.DanhMuc](#dichvucuahangdanhmuc) | — | ADMIN | [CuaHangController.java:43](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L43) |
| POST | `/api/quan-tri/cua-hang/danh-muc` | Tạo danh mục | — | [GhiDanhMuc](#cuahangcontrollerghidanhmuc) | 200 [MaDinhDanh](#cuahangcontrollermadinhdanh) `{"id":1}` | 400, 409 | ADMIN | [CuaHangController.java:46](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L46) |
| PUT | `/api/quan-tri/cua-hang/danh-muc/{id}` | Cập nhật danh mục | `id` (path) | [GhiDanhMuc](#cuahangcontrollerghidanhmuc) | 200 [MaDinhDanh](#cuahangcontrollermadinhdanh) `{"id":1}` | 400, 404, 409 | ADMIN | [CuaHangController.java:51](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L51) |
| GET | `/api/cua-hang/san-pham` | Tìm/lọc/sắp xếp và phân trang sản phẩm công khai | `tuKhoa=""` (query tùy chọn), `danhMucId` (query tùy chọn), `sapXep=moi` (query tùy chọn), `trang=0` (query tùy chọn), `kichThuoc=12` (query tùy chọn) | — | 200 [DichVuCuaHang.Trang](#dichvucuahangtrang) `{"noiDung":[],"tong":0}` … | 400 | Công khai | [CuaHangController.java:56](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L56) |
| GET | `/api/quan-tri/cua-hang/san-pham` | Tìm và phân trang sản phẩm cho Admin | `tuKhoa=""` (query tùy chọn), `trang=0` (query tùy chọn), `kichThuoc=24` (query tùy chọn) | — | 200 [DichVuCuaHang.Trang](#dichvucuahangtrang) `{"noiDung":[],"tong":0}` … | 400 | ADMIN | [CuaHangController.java:64](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L64) |
| GET | `/api/cua-hang/san-pham/{id}` | Chi tiết sản phẩm, ảnh và sản phẩm liên quan | `id` (path) | — | 200 [DichVuCuaHang.ChiTiet](#dichvucuahangchitiet) `{"sanPham":{"id":1,"sku":"SKU-MAU"},"hinhAnh":[]}` … | 404 | Công khai | [CuaHangController.java:71](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L71) |
| GET | `/api/quan-tri/cua-hang/san-pham/{id}` | Chi tiết sản phẩm trong quản trị | `id` (path) | — | 200 [DichVuCuaHang.ChiTiet](#dichvucuahangchitiet) `{"sanPham":{"id":1,"sku":"SKU-MAU"},"hinhAnh":[]}` … | 404 | ADMIN | [CuaHangController.java:74](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L74) |
| POST | `/api/quan-tri/cua-hang/san-pham` | Tạo sản phẩm và dòng tồn kho | — | [GhiSanPham](#cuahangcontrollerghisanpham) | 200 [MaDinhDanh](#cuahangcontrollermadinhdanh) `{"id":1}` | 400, 404, 409 | ADMIN | [CuaHangController.java:77](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L77) |
| PUT | `/api/quan-tri/cua-hang/san-pham/{id}` | Sửa sản phẩm, thay tập URL ảnh | `id` (path) | [GhiSanPham](#cuahangcontrollerghisanpham) | 200 [MaDinhDanh](#cuahangcontrollermadinhdanh) `{"id":1}` | 400, 404, 409 | ADMIN | [CuaHangController.java:82](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L82) |
| GET | `/api/cua-hang/yeu-thich` | Đọc sản phẩm yêu thích của phiên | — | — | 200 `[]` hoặc danh sách [DichVuCuaHang.SanPham](#dichvucuahangsanpham) | — | USER | [CuaHangController.java:87](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L87) |
| PUT | `/api/cua-hang/yeu-thich/{id}` | Đặt hoặc bỏ yêu thích | `id` (path) | [GhiYeuThich](#cuahangcontrollerghiyeuthich) | 204 rỗng | 400, 404 | USER | [CuaHangController.java:92](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L92) |
| POST | `/api/quan-tri/cua-hang/san-pham/{id}/mo-hinh` | Tải GLB/glTF và metadata về giấy phép | `id` (path), `tep` (multipart), `giayPhep` (multipart), `nguon` (multipart), `tacGia` (multipart), `ghiCong` (multipart) | `multipart/form-data` | 200 [KhoMoHinhSanPham.ThongTin](#khomohinhsanphamthongtin) `{"tenTep":"mo-hinh-mau.glb","dinhDang":"glb"}` … | 400, 404, 413 | ADMIN | [CuaHangController.java:99](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L99) |
| GET | `/api/quan-tri/cua-hang/san-pham/{id}/mo-hinh` | Xem metadata mô hình kể cả chưa duyệt | `id` (path) | — | 200 [KhoMoHinhSanPham.ThongTin](#khomohinhsanphamthongtin) `{"tenTep":"mo-hinh-mau.glb","dinhDang":"glb"}` … | 404 | ADMIN | [CuaHangController.java:108](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L108) |
| PUT | `/api/quan-tri/cua-hang/san-pham/{id}/mo-hinh` | Đặt trạng thái duyệt mô hình | `id` (path) | [DuyetMoHinh](#cuahangcontrollerduyetmohinh) | 200 [KhoMoHinhSanPham.ThongTin](#khomohinhsanphamthongtin) `{"tenTep":"mo-hinh-mau.glb","dinhDang":"glb"}` … | 400, 404 | ADMIN | [CuaHangController.java:111](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L111) |
| GET | `/api/cua-hang/san-pham/{id}/mo-hinh` | Xem metadata mô hình công khai đã duyệt | `id` (path) | — | 200 [KhoMoHinhSanPham.ThongTin](#khomohinhsanphamthongtin) `{"tenTep":"mo-hinh-mau.glb","dinhDang":"glb"}` … | 404 | Công khai | [CuaHangController.java:116](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L116) |
| GET | `/api/cua-hang/san-pham/{id}/mo-hinh/noi-dung` | Phục vụ nội dung GLB/glTF đã duyệt | `id` (path) | — | 200 byte `model/gltf-binary` hoặc `model/gltf+json` | 404 | Công khai | [CuaHangController.java:119](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java#L119) |
| GET | `/api/cua-hang/san-pham/{id}/danh-gia` | Đánh giá đang hiển thị của sản phẩm | `id` (path) | — | 200 `[]` hoặc danh sách [DanhGia](#danhgiacontrollerdanhgia) | — | Công khai | [DanhGiaController.java:32](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java#L32) |
| GET | `/api/quan-tri/cua-hang/san-pham/{id}/danh-gia` | Danh sách đánh giá cho quản trị | `id` (path) | — | 200 `[]` hoặc danh sách [DanhGia](#danhgiacontrollerdanhgia) | — | ADMIN | [DanhGiaController.java:38](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java#L38) |
| GET | `/api/danh-gia/{donHangId}` | Đánh giá của chính người dùng cho đơn | `donHangId` (path) | — | 200 `[]` hoặc danh sách [DanhGia](#danhgiacontrollerdanhgia) | — | USER | [DanhGiaController.java:44](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java#L44) |
| PUT | `/api/danh-gia/{donHangId}/{sanPhamId}` | Tạo/sửa đánh giá của người mua | `donHangId` (path), `sanPhamId` (path) | [GhiDanhGia](#danhgiacontrollerghidanhgia) | 204 rỗng | 400, 409 | USER | [DanhGiaController.java:53](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java#L53) |
| PUT | `/api/quan-tri/cua-hang/danh-gia/{donHangId}/{sanPhamId}/hien-thi` | Ẩn hoặc hiện đánh giá | `donHangId` (path), `sanPhamId` (path) | [HienThi](#danhgiacontrollerhienthi) | 204 rỗng | 400, 404 | ADMIN | [DanhGiaController.java:83](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java#L83) |
| GET | `/api/gio-hang` | Đọc giỏ và tạm tính | — | — | 200 [DichVuGioHang.Gio](#dichvugiohanggio) `{"chiTiet":[],"tamTinh":0}` | — | USER | [DonHangController.java:45](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L45) |
| PUT | `/api/gio-hang/{id}` | Đổi số lượng hoặc xóa dòng khi bằng 0 | `id` (path) | [SoLuong](#donhangcontrollersoluong) | 204 rỗng | 400, 404, 409 | USER | [DonHangController.java:48](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L48) |
| POST | `/api/gio-hang/{id}` | Cộng số lượng sản phẩm vào giỏ | `id` (path) | [ThemSoLuong](#donhangcontrollerthemsoluong) | 204 rỗng | 400, 404, 409 | USER | [DonHangController.java:55](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L55) |
| GET | `/api/gio-hang/tinh-tien` | Tính báo giá, phí COD và voucher | `maVoucher` (query tùy chọn) | — | 200 [DichVuDonHang.TongTien](#dichvudonhangtongtien) `{"tamTinh":0,"giamGia":0}` … | 400, 409 | USER | [DonHangController.java:62](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L62) |
| POST | `/api/don-hang` | Đặt đơn COD theo báo giá và khóa yêu cầu | — | [DatDon](#donhangcontrollerdatdon) | 200 [MaDinhDanh](#donhangcontrollermadinhdanh) `{"id":1}` | 400, 409 | USER | [DonHangController.java:68](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L68) |
| GET | `/api/don-hang` | Phân trang đơn của người dùng | `trang=0` (query tùy chọn), `kichThuoc=20` (query tùy chọn) | — | 200 `[]` hoặc danh sách [DichVuDonHang.Don](#dichvudonhangdon) | 400 | USER | [DonHangController.java:73](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L73) |
| GET | `/api/don-hang/{id}` | Đọc chi tiết đơn thuộc về phiên | `id` (path) | — | 200 [DichVuDonHang.ChiTiet](#dichvudonhangchitiet) `{"don":{"id":1,"trangThai":"CHO_XAC_NHAN"},"chiTiet":[{"sanPhamId":1,"soLuong":1}]}` … | 404 | USER | [DonHangController.java:79](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L79) |
| POST | `/api/don-hang/{id}/huy` | Khách tự hủy đơn chờ xác nhận | `id` (path) | — | 204 rỗng | 404, 409 | USER | [DonHangController.java:84](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L84) |
| POST | `/api/don-hang/{id}/yeu-cau-huy` | Gửi lý do xin hủy đơn | `id` (path) | [LyDo](#donhangcontrollerlydo) | 204 rỗng | 400, 404, 409 | USER | [DonHangController.java:90](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L90) |
| GET | `/api/quan-tri/don-hang` | Phân trang đơn cho quản trị | `trang=0` (query tùy chọn), `kichThuoc=20` (query tùy chọn) | — | 200 `[]` hoặc danh sách [DichVuDonHang.Don](#dichvudonhangdon) | 400 | ADMIN | [DonHangController.java:97](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L97) |
| GET | `/api/quan-tri/don-hang/{id}` | Đọc chi tiết đơn cho quản trị | `id` (path) | — | 200 [DichVuDonHang.ChiTiet](#dichvudonhangchitiet) `{"don":{"id":1,"trangThai":"CHO_XAC_NHAN"},"chiTiet":[{"sanPhamId":1,"soLuong":1}]}` … | 404 | ADMIN | [DonHangController.java:103](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L103) |
| POST | `/api/quan-tri/don-hang/{id}/trang-thai` | Chuyển trạng thái đơn và đối chiếu thu COD | `id` (path) | [ChuyenTrangThai](#donhangcontrollerchuyentrangthai) | 204 rỗng | 400, 404, 409 | ADMIN | [DonHangController.java:106](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L106) |
| POST | `/api/quan-tri/don-hang/{id}/yeu-cau-huy/{yeuCauId}` | Duyệt hoặc từ chối yêu cầu hủy | `id` (path), `yeuCauId` (path) | [XuLyHuy](#donhangcontrollerxulyhuy) | 204 rỗng | 400, 404, 409 | ADMIN | [DonHangController.java:113](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L113) |
| GET | `/api/quan-tri/don-hang/phi-van-chuyen` | Đọc cấu hình phí COD | — | — | 200 [Phi](#donhangcontrollerphi) `{"giaTri":0}` | — | ADMIN | [DonHangController.java:120](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L120) |
| PUT | `/api/quan-tri/don-hang/phi-van-chuyen` | Đặt phí giao COD | — | [Phi](#donhangcontrollerphi) | 204 rỗng | 400 | ADMIN | [DonHangController.java:123](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L123) |
| GET | `/api/quan-tri/voucher` | Liệt kê voucher | — | — | 200 `[]` hoặc danh sách [DichVuVoucher.Voucher](#dichvuvouchervoucher) | — | ADMIN | [DonHangController.java:130](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L130) |
| POST | `/api/quan-tri/voucher` | Tạo voucher | — | [GhiVoucher](#donhangcontrollerghivoucher) | 204 rỗng | 400, 404, 409 | ADMIN | [DonHangController.java:133](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L133) |
| PUT | `/api/quan-tri/voucher/{id}` | Sửa voucher | `id` (path) | [GhiVoucher](#donhangcontrollerghivoucher) | 204 rỗng | 400, 404, 409 | ADMIN | [DonHangController.java:139](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L139) |
| GET | `/api/quan-tri/voucher/{id}/lich-su` | Lịch sử sử dụng voucher | `id` (path) | — | 200 `[]` hoặc danh sách [DichVuVoucher.LanDung](#dichvuvoucherlandung) | — | ADMIN | [DonHangController.java:145](../backend/src/main/java/com/galaxora/donhang/DonHangController.java#L145) |
| GET | `/api/he-thong/suc-khoe` | Báo Spring Boot đang phản hồi | — | — | 200 `{"trangThai":"HOAT_DONG"}` | — | Công khai | [HeThongController.java:16](../backend/src/main/java/com/galaxora/hethong/HeThongController.java#L16) |
| GET | `/api/quan-tri/kho/ton` | Đọc số tồn thực tế, đang giữ, khả dụng và cảnh báo | — | — | 200 `[]` hoặc danh sách [DichVuKho.TonKho](#dichvukhotonkho) | — | ADMIN | [KhoController.java:28](../backend/src/main/java/com/galaxora/kho/KhoController.java#L28) |
| GET | `/api/quan-tri/kho/ton/{id}/lich-su` | Đọc biến động kho theo sản phẩm | `id` (path) | — | 200 `[]` hoặc danh sách [DichVuKho.GiaoDich](#dichvukhogiaodich) | — | ADMIN | [KhoController.java:31](../backend/src/main/java/com/galaxora/kho/KhoController.java#L31) |
| GET | `/api/quan-tri/kho/nguong` | Đọc ngưỡng tồn chung | — | — | 200 [Nguong](#khocontrollernguong) `{"giaTri":0}` | — | ADMIN | [KhoController.java:34](../backend/src/main/java/com/galaxora/kho/KhoController.java#L34) |
| PUT | `/api/quan-tri/kho/nguong` | Lưu ngưỡng tồn chung | — | [Nguong](#khocontrollernguong) | 204 rỗng | 400 | ADMIN | [KhoController.java:37](../backend/src/main/java/com/galaxora/kho/KhoController.java#L37) |
| GET | `/api/quan-tri/kho/phieu-nhap` | Liệt kê phiếu nhập | — | — | 200 `[]` hoặc danh sách [DichVuKho.Phieu](#dichvukhophieu) | — | ADMIN | [KhoController.java:44](../backend/src/main/java/com/galaxora/kho/KhoController.java#L44) |
| GET | `/api/quan-tri/kho/phieu-nhap/{id}` | Đọc phiếu nhập và các dòng | `id` (path) | — | 200 [DichVuKho.ChiTietPhieu](#dichvukhochitietphieu) `{"phieu":{"id":1},"chiTiet":[{"sanPhamId":1,"soLuong":1}]}` | 404 | ADMIN | [KhoController.java:47](../backend/src/main/java/com/galaxora/kho/KhoController.java#L47) |
| POST | `/api/quan-tri/kho/phieu-nhap` | Nhập kho bằng phiếu có khóa yêu cầu | — | [NhapKho](#khocontrollernhapkho) | 200 [MaDinhDanh](#khocontrollermadinhdanh) `{"id":1}` | 400, 404, 409 | ADMIN | [KhoController.java:50](../backend/src/main/java/com/galaxora/kho/KhoController.java#L50) |
| POST | `/api/quan-tri/kho/ton/{id}/dieu-chinh` | Điều chỉnh hoặc xuất kho | `id` (path) | [DieuChinh](#khocontrollerdieuchinh) | 204 rỗng | 400, 404, 409 | ADMIN | [KhoController.java:55](../backend/src/main/java/com/galaxora/kho/KhoController.java#L55) |
| GET | `/api/nhiem-vu/ho-so` | Đọc hồ sơ, cấp, XP và phần thưởng | — | — | 200 [DichVuHoSoKhamPha.HoSo](#dichvuhosokhamphahoso) `{"tongXp":0,"capDo":{"cap":1,"mocCapHienTai":0,"mocCapTiepTheo":100}}` … | — | USER | [HoSoKhamPhaController.java:30](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L30) |
| GET | `/api/nhiem-vu/tien-do` | Đọc tiến độ hành trình và nhiệm vụ | — | — | 200 [DichVuNhiemVu.TienDo](#dichvunhiemvutiendo) `{"hoSo":{"tongXp":0},"cacChuong":[{"so":1,"mo":true,"hoanThanh":false}]}` … | — | USER | [HoSoKhamPhaController.java:35](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L35) |
| POST | `/api/nhiem-vu/kham-pha/{ma}/mo-bang` | Cấp vé mở bảng gắn với thiên thể và phiên | `ma` (path) | — | 200 với `ve`, `choMiliGiay` | 404 | USER | [HoSoKhamPhaController.java](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java) |
| POST | `/api/nhiem-vu/kham-pha/{ma}` | Ghi khám phá sau khoảng chờ và tiêu thụ vé hợp lệ | `ma` (path) | `ve` (UUID) | 200 [DichVuNhiemVu.TienDo](#dichvunhiemvutiendo) | 400, 409 | USER | [HoSoKhamPhaController.java](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java) |
| POST | `/api/nhiem-vu/dau-hieu` | Mở câu hỏi tín hiệu trong phiên | — | — | 200 với `ve`, `cauHoi`, `soLanConLai` | 409 hết lượt thử | USER | [HoSoKhamPhaController.java](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java) |
| POST | `/api/nhiem-vu/vat-the-an` | Chấm câu trả lời và ghi lần tìm dấu hiệu nếu hợp lệ | — | `ve` (UUID), `traLoi` (số nguyên dương) | 200 [DichVuNhiemVu.TienDo](#dichvunhiemvutiendo) | 400, 409 | USER | [HoSoKhamPhaController.java](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java) |
| POST | `/api/nhiem-vu/voucher` | Thử nhận voucher hành trình đang chờ | — | — | 200 [DichVuNhiemVu.TienDo](#dichvunhiemvutiendo) `{"hoSo":{"tongXp":0},"cacChuong":[{"so":1,"mo":true,"hoanThanh":false}]}` … | 409 | USER | [HoSoKhamPhaController.java:68](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L68) |
| GET | `/api/quan-tri/voucher-hanh-trinh` | Đọc mẫu voucher hành trình | — | — | 200 `[]` hoặc danh sách [DichVuNhiemVu.CauHinh](#dichvunhiemvucauhinh) | — | ADMIN | [HoSoKhamPhaController.java:73](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L73) |
| PUT | `/api/quan-tri/voucher-hanh-trinh` | Lưu mẫu voucher hành trình | — | [DichVuNhiemVu.MauVoucher](#dichvunhiemvumauvoucher) | 204 rỗng | 400 | ADMIN | [HoSoKhamPhaController.java:78](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L78) |
| POST | `/api/nhiem-vu/doi-chieu` | Đối chiếu nhiệm vụ và thử cấp voucher đang chờ | — | — | 200 [DichVuNhiemVu.TienDo](#dichvunhiemvutiendo) `{"hoSo":{"tongXp":0},"cacChuong":[{"so":1,"mo":true,"hoanThanh":false}]}` … | — | USER | [HoSoKhamPhaController.java:84](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L84) |
| GET | `/api/quan-tri/nhiem-vu` | Liệt kê cấu hình nhiệm vụ | — | — | 200 `[]` hoặc danh sách [QuanLyNhiemVu.NhiemVu](#quanlynhiemvunhiemvu) | — | ADMIN | [HoSoKhamPhaController.java:89](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L89) |
| POST | `/api/quan-tri/nhiem-vu` | Tạo nhiệm vụ | — | [QuanLyNhiemVu.Tao](#quanlynhiemvutao) | 201 [NhiemVuMoi](#hosokhamphacontrollernhiemvumoi) `{"id":1}` | 400 | ADMIN | [HoSoKhamPhaController.java:96](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L96) |
| PUT | `/api/quan-tri/nhiem-vu/{id}` | Sửa tên, mô tả và trạng thái nhiệm vụ | `id` (path) | [QuanLyNhiemVu.Sua](#quanlynhiemvusua) | 204 rỗng | 400, 404 | ADMIN | [HoSoKhamPhaController.java:101](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L101) |
| PUT | `/api/quan-tri/nhiem-vu/{id}/voucher` | Lưu mẫu voucher của nhiệm vụ | `id` (path) | [DichVuNhiemVu.MauVoucher](#dichvunhiemvumauvoucher) | 204 rỗng | 400, 404 | ADMIN | [HoSoKhamPhaController.java:107](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java#L107) |
| GET | `/api/quan-tri/tong-quan` | Đọc chỉ số dashboard | — | — | 200 [TongQuan](#quantricontrollertongquan) `{"donCanXuLy":0,"yeuCauHuy":0}` … | — | ADMIN | [QuanTriController.java:49](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L49) |
| GET | `/api/quan-tri/thong-bao` | Đọc cảnh báo vận hành | — | — | 200 `[]` hoặc danh sách [ThongBao](#quantricontrollerthongbao) | — | ADMIN | [QuanTriController.java:64](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L64) |
| GET | `/api/quan-tri/su-kien` | Liệt kê sự kiện | — | — | 200 `[]` hoặc danh sách [SuKien](#quantricontrollersukien) | — | ADMIN | [QuanTriController.java:79](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L79) |
| POST | `/api/quan-tri/su-kien` | Tạo sự kiện | — | [LuuSuKien](#quantricontrollerluusukien) | 204 rỗng | 400 | ADMIN | [QuanTriController.java:87](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L87) |
| PUT | `/api/quan-tri/su-kien/{id}` | Cập nhật sự kiện | `id` (path) | [LuuSuKien](#quantricontrollerluusukien) | 204 rỗng | 400, 404 | ADMIN | [QuanTriController.java:95](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L95) |
| GET | `/api/quan-tri/nhat-ky` | Lọc nhật ký Admin | `tu` (query tùy chọn), `den` (query tùy chọn), `thaoTac` (query tùy chọn), `adminId` (query tùy chọn), `trang=0` (query tùy chọn) | — | 200 `[]` hoặc danh sách [DongNhatKy](#quantricontrollerdongnhatky) | 400 | ADMIN | [QuanTriController.java:108](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L108) |
| GET | `/api/quan-tri/tai-khoan` | Tìm tài khoản USER | `tuKhoa=""` (query tùy chọn), `trang=0` (query tùy chọn) | — | 200 `[]` hoặc danh sách [TaiKhoan](#quantricontrollertaikhoan) | 400 | ADMIN | [QuanTriController.java:125](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L125) |
| PUT | `/api/quan-tri/tai-khoan/{id}/khoa` | Khóa hoặc mở khóa tài khoản USER | `id` (path) | [DatKhoa](#quantricontrollerdatkhoa) | 204 rỗng | 400, 404, 409 | ADMIN | [QuanTriController.java:137](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L137) |
| GET | `/api/kham-pha/noi-dung` | Đọc ghi đè nội dung khám phá công khai | — | — | 200 `[]` hoặc danh sách [NoiDung](#quantricontrollernoidung) | — | Công khai | [QuanTriController.java:152](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L152) |
| GET | `/api/nhiem-vu/noi-dung` | Đọc nội dung hành trình được phép thấy | — | — | 200 `[]` hoặc danh sách [NoiDung](#quantricontrollernoidung) | — | USER | [QuanTriController.java:155](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L155) |
| GET | `/api/quan-tri/noi-dung/{loai}` | Đọc danh sách nội dung theo loại | `loai` (path) | — | 200 `[]` hoặc danh sách [NoiDung](#quantricontrollernoidung) | 404 | ADMIN | [QuanTriController.java:164](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L164) |
| PUT | `/api/quan-tri/noi-dung/{loai}/{ma}` | Lưu ghi đè tên và mô tả theo mã | `loai` (path), `ma` (path) | [SuaNoiDung](#quantricontrollersuanoidung) | 204 rỗng | 400, 404 | ADMIN | [QuanTriController.java:167](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java#L167) |
| GET | `/api/nhiem-vu/thong-bao` | Đọc thông báo của phiên | — | — | 200 `[]` hoặc danh sách [ThongBao](#thongbaonguoidungthongbao) | — | USER | [ThongBaoNguoiDung.java:17](../backend/src/main/java/com/galaxora/quantri/ThongBaoNguoiDung.java#L17) |
| GET | `/api/tai-khoan/csrf` | Cấp tên header và token CSRF | — | — | 200 [ThongTinCsrf](#taikhoancontrollerthongtincsrf) `{"tenHeader":"X-CSRF-TOKEN","token":"<csrf>"}` | — | Công khai | [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java) |
| POST | `/api/tai-khoan/dang-ky` | Đăng ký tài khoản USER có thể đăng nhập ngay | — | [DangKy](#taikhoancontrollerdangky) | 202 rỗng | 400 | Công khai | [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java) |
| POST | `/api/tai-khoan/dang-nhap` | Đăng nhập bằng email hoặc số điện thoại | — | [DangNhap](#taikhoancontrollerdangnhap) | 200 [ThongTinPhien](#taikhoancontrollerthongtinphien) `{"nguoiDungId":1,"vaiTro":["USER"]}` | 400, 401, 429 | Công khai | [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java) |
| GET | `/api/tai-khoan/phien` | Đọc tài khoản và vai trò hiện hành | — | — | 200 [ThongTinPhien](#taikhoancontrollerthongtinphien) `{"nguoiDungId":1,"vaiTro":["USER"]}` | — | Phiên | [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java) |
| POST | `/api/tai-khoan/doi-mat-khau` | Đổi mật khẩu và hủy phiên hiện tại | — | [DoiMatKhau](#taikhoancontrollerdoimatkhau) | 204 rỗng | 400 | Phiên | [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java) |
| POST | `/api/tai-khoan/dang-xuat` | Hủy phiên và xóa `JSESSIONID` | — | — | 204 rỗng | 403 khi thiếu CSRF | Phiên để đăng xuất tài khoản | [CauHinhBaoMat.java](../backend/src/main/java/com/galaxora/cauhinh/CauHinhBaoMat.java) |

Sản phẩm công khai hỗ trợ `sapXep=moi`, `gia-tang`, `gia-giam` và `ten`. `trang` bắt đầu từ 0, `kichThuoc` có khoảng 1 đến 100 nơi controller khai báo tham số này. Danh sách nhật ký và tài khoản Admin phân trang 50 mục trong SQL. Các giới hạn request body ở DTO bên dưới. Xem thêm [mô tả luồng](../READMECHITIET.md#6-chức-năng).

## 8.3. DTO yêu cầu và phản hồi

Các kiểu bên dưới dùng tên trường Java/JSON. Annotation được giữ để thấy giới hạn, với ràng buộc nghiệp vụ bổ sung tại service. Một số endpoint có cả quy tắc HTTP và khóa ngoại, nên dữ liệu hợp kiểu vẫn có thể bị từ chối.

### CapDoKhamPha.CapDoKhamPha

Nguồn [CapDoKhamPha.java](../backend/src/main/java/com/galaxora/nhiemvu/CapDoKhamPha.java).

- `long cap`
- `long mocCapHienTai`
- `long mocCapTiepTheo`

### CuaHangController.Anh

Nguồn [CuaHangController.java](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java).

- `@NotBlank @Size(max = 2048) String duongDan`
- `@NotNull @Size(max = 250) String moTa`

### CuaHangController.DuyetMoHinh

Nguồn [CuaHangController.java](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java).

- `@NotNull Boolean daDuyet`

### CuaHangController.GhiDanhMuc

Nguồn [CuaHangController.java](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java).

- `@NotBlank @Size(max = 160) String ten`
- `@NotNull Boolean hienThi`

### CuaHangController.GhiSanPham

Nguồn [CuaHangController.java](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java).

- `@Positive long danhMucId`
- `@NotBlank @Size(max = 64) String sku`
- `@NotBlank @Size(max = 200) String ten`
- `@NotNull @Size(max = 500) String moTaNgan`
- `@NotNull @Size(max = 16000) String moTa`
- `@NotNull @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal gia`
- `@PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giaSoSanh`
- `@NotNull Boolean hienThi`
- `@NotNull Boolean kinhDoanh`
- `@NotNull @Size(max = 12) List<@NotNull @Valid Anh> hinhAnh`

### CuaHangController.GhiYeuThich

Nguồn [CuaHangController.java](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java).

- `@NotNull Boolean yeuThich`

### CuaHangController.MaDinhDanh

Nguồn [CuaHangController.java](../backend/src/main/java/com/galaxora/cuahang/CuaHangController.java).

- `long id`

### DanhGiaController.DanhGia

Nguồn [DanhGiaController.java](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java).

- `Long donHangId`
- `Long sanPhamId`
- `String tacGia`
- `int soSao`
- `String noiDung`
- `boolean hienThi`
- `LocalDateTime capNhatLuc`

### DanhGiaController.GhiDanhGia

Nguồn [DanhGiaController.java](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java).

- `@Min(1) @Max(5) int soSao`
- `@NotBlank @Size(max = 2000) String noiDung`

### DanhGiaController.HienThi

Nguồn [DanhGiaController.java](../backend/src/main/java/com/galaxora/cuahang/DanhGiaController.java).

- `@NotNull Boolean hienThi`

### DichVuCuaHang.Anh

Nguồn [DichVuCuaHang.java](../backend/src/main/java/com/galaxora/cuahang/DichVuCuaHang.java).

- `String duongDan`
- `String moTa`

### DichVuCuaHang.ChiTiet

Nguồn [DichVuCuaHang.java](../backend/src/main/java/com/galaxora/cuahang/DichVuCuaHang.java).

- `SanPham sanPham`
- `List<Anh> hinhAnh`
- `List<SanPham> lienQuan`

### DichVuCuaHang.DanhMuc

Nguồn [DichVuCuaHang.java](../backend/src/main/java/com/galaxora/cuahang/DichVuCuaHang.java).

- `long id`
- `String ten`
- `boolean hienThi`

### DichVuCuaHang.SanPham

Nguồn [DichVuCuaHang.java](../backend/src/main/java/com/galaxora/cuahang/DichVuCuaHang.java).

- `long id`
- `long danhMucId`
- `String danhMuc`
- `String sku`
- `String ten`
- `String moTaNgan`
- `String moTa`
- `BigDecimal gia`
- `BigDecimal giaSoSanh`
- `boolean hienThi`
- `boolean kinhDoanh`
- `String anh`
- `boolean coMoHinh`
- `String trangThaiKho`

### DichVuCuaHang.Trang

Nguồn [DichVuCuaHang.java](../backend/src/main/java/com/galaxora/cuahang/DichVuCuaHang.java).

- `List<SanPham> noiDung`
- `long tong`
- `int trang`
- `int kichThuoc`

### DichVuDonHang.ChiTiet

Nguồn [DichVuDonHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuDonHang.java).

- `Don don`
- `List<Dong> chiTiet`
- `List<LichSu> lichSu`
- `List<YeuCauHuy> yeuCauHuy`

### DichVuDonHang.Don

Nguồn [DichVuDonHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuDonHang.java).

- `long id`
- `String ma`
- `long nguoiDungId`
- `String nguoiNhan`
- `String soDienThoai`
- `String diaChi`
- `String ghiChu`
- `BigDecimal tamTinh`
- `BigDecimal giamGia`
- `BigDecimal phiVanChuyen`
- `BigDecimal tongTien`
- `String phuongThuc`
- `TrangThai trangThai`
- `boolean daThuTien`
- `LocalDateTime thoiDiem`

### DichVuDonHang.Dong

Nguồn [DichVuDonHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuDonHang.java).

- `long sanPhamId`
- `String sku`
- `String ten`
- `BigDecimal donGia`
- `int soLuong`

### DichVuDonHang.LichSu

Nguồn [DichVuDonHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuDonHang.java).

- `TrangThai trangThai`
- `String lyDo`
- `LocalDateTime thoiDiem`

### DichVuDonHang.TongTien

Nguồn [DichVuDonHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuDonHang.java).

- `BigDecimal tamTinh`
- `BigDecimal giamGia`
- `BigDecimal phiVanChuyen`
- `BigDecimal tongTien`

### DichVuDonHang.YeuCauHuy

Nguồn [DichVuDonHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuDonHang.java).

- `long id`
- `String trangThai`
- `String lyDo`
- `String lyDoXuLy`
- `LocalDateTime xuLyLuc`
- `LocalDateTime thoiDiem`

### DichVuGioHang.Dong

Nguồn [DichVuGioHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuGioHang.java).

- `long sanPhamId`
- `String sku`
- `String ten`
- `BigDecimal gia`
- `int soLuong`
- `int khaDung`
- `boolean dangBan`

### DichVuGioHang.Gio

Nguồn [DichVuGioHang.java](../backend/src/main/java/com/galaxora/donhang/DichVuGioHang.java).

- `List<Dong> chiTiet`
- `BigDecimal tamTinh`

### DichVuHoSoKhamPha.GhiNhanXp

Nguồn [DichVuHoSoKhamPha.java](../backend/src/main/java/com/galaxora/nhiemvu/DichVuHoSoKhamPha.java).

- `String nguon`
- `String maNoiDung`
- `int soXp`
- `LocalDateTime ghiNhanLuc`

### DichVuHoSoKhamPha.HoSo

Nguồn [DichVuHoSoKhamPha.java](../backend/src/main/java/com/galaxora/nhiemvu/DichVuHoSoKhamPha.java).

- `long tongXp`
- `CapDoKhamPha capDo`
- `List<GhiNhanXp> lichSuXp`
- `List<PhanThuong> phanThuong`

### DichVuHoSoKhamPha.PhanThuong

Nguồn [DichVuHoSoKhamPha.java](../backend/src/main/java/com/galaxora/nhiemvu/DichVuHoSoKhamPha.java).

- `String ma`
- `String loai`
- `String maVoucher`
- `LocalDateTime nhanLuc`
- `String tenHuyHieu`

### DichVuKho.ChiTietPhieu

Nguồn [DichVuKho.java](../backend/src/main/java/com/galaxora/kho/DichVuKho.java).

- `Phieu phieu`
- `List<KhoController.DongNhap> chiTiet`

### DichVuKho.GiaoDich

Nguồn [DichVuKho.java](../backend/src/main/java/com/galaxora/kho/DichVuKho.java).

- `long id`
- `String loai`
- `int thayDoiThucTe`
- `int thayDoiDangGiu`
- `int thucTeSau`
- `int dangGiuSau`
- `String lyDo`
- `Long phieuNhapId`
- `Long donHangId`
- `LocalDateTime thoiDiem`

### DichVuKho.Phieu

Nguồn [DichVuKho.java](../backend/src/main/java/com/galaxora/kho/DichVuKho.java).

- `long id`
- `String lyDo`
- `LocalDateTime thoiDiem`

### DichVuKho.TonKho

Nguồn [DichVuKho.java](../backend/src/main/java/com/galaxora/kho/DichVuKho.java).

- `long sanPhamId`
- `String sku`
- `String ten`
- `int thucTe`
- `int dangGiu`
- `int khaDung`
- `String canhBao`

### DichVuNhiemVu.CauHinh

Nguồn [DichVuNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/DichVuNhiemVu.java).

- `MauVoucher mau`
- `int luotDaCap`

### DichVuNhiemVu.Chuong

Nguồn [DichVuNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/DichVuNhiemVu.java).

- `int so`
- `List<String> thienThe`
- `boolean mo`
- `boolean hoanThanh`

### DichVuNhiemVu.MauVoucher

Nguồn [DichVuNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/DichVuNhiemVu.java).

- `@NotBlank @Pattern(regexp = "PHAN_TRAM\|SO_TIEN") String loai`
- `@NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal giaTri`
- `@PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giamToiDa`
- `@PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal donToiThieu`
- `@NotNull LocalDateTime batDau`
- `@NotNull LocalDateTime ketThuc`
- `@Positive int luotToiDa`
- `boolean kichHoat`

### DichVuNhiemVu.TienDo

Nguồn [DichVuNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/DichVuNhiemVu.java).

- `DichVuHoSoKhamPha.HoSo hoSo`
- `List<Chuong> cacChuong`
- `boolean voucherDangCho`
- `List<QuanLyNhiemVu.TienDoNhiemVu> nhiemVu`

### DichVuVoucher.LanDung

Nguồn [DichVuVoucher.java](../backend/src/main/java/com/galaxora/donhang/DichVuVoucher.java).

- `long donHangId`
- `String ma`
- `BigDecimal giamGia`
- `LocalDateTime thoiDiem`

### DichVuVoucher.Voucher

Nguồn [DichVuVoucher.java](../backend/src/main/java/com/galaxora/donhang/DichVuVoucher.java).

- `long id`
- `String ma`
- `String loai`
- `BigDecimal giaTri`
- `BigDecimal giamToiDa`
- `BigDecimal donToiThieu`
- `LocalDateTime batDau`
- `LocalDateTime ketThuc`
- `int luotToiDa`
- `int luotDaDung`
- `boolean kichHoat`
- `Long chuSoHuuId`

### DonHangController.ChuyenTrangThai

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@NotNull DichVuDonHang.TrangThai trangThai`
- `boolean daThuTien`
- `@Size(max = 500) String lyDo`

### DonHangController.DatDon

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@NotNull @Pattern(regexp = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}") String khoaYeuCau`
- `@NotBlank @Size(max = 160) String nguoiNhan`
- `@NotBlank @Pattern(regexp = "\\+?[0-9]{8,15}") String soDienThoai`
- `@NotBlank @Size(max = 500) String diaChi`
- `@Size(max = 500) String ghiChu`
- `@Size(max = 40) String maVoucher`
- `@NotNull @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal tongTienDaXacNhan`

### DonHangController.GhiVoucher

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@NotBlank @Pattern(regexp = "[a-zA-Z0-9_-]{1,40}") String ma`
- `@NotNull @Pattern(regexp = "PHAN_TRAM\|SO_TIEN") String loai`
- `@NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal giaTri`
- `@PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giamToiDa`
- `@PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal donToiThieu`
- `@NotNull LocalDateTime batDau`
- `@NotNull LocalDateTime ketThuc`
- `@Positive int luotToiDa`
- `@NotNull Boolean kichHoat`
- `@Positive Long chuSoHuuId`

### DonHangController.LyDo

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@NotBlank @Size(max = 500) String lyDo`

### DonHangController.MaDinhDanh

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `long id`

### DonHangController.Phi

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giaTri`

### DonHangController.SoLuong

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@NotNull @PositiveOrZero Integer soLuong`

### DonHangController.ThemSoLuong

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@Positive int soLuong`

### DonHangController.XuLyHuy

Nguồn [DonHangController.java](../backend/src/main/java/com/galaxora/donhang/DonHangController.java).

- `@NotNull Boolean duyet`
- `@NotBlank @Size(max = 500) String lyDo`

### HoSoKhamPhaController.XacNhanDocBang và TraLoiTinHieu

Nguồn [HoSoKhamPhaController.java](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java).

- `@NotNull UUID ve` dùng ở cả hai DTO.
- `@NotNull @Positive Integer traLoi` chỉ có khi trả lời tín hiệu.

### HoSoKhamPhaController.NhiemVuMoi

Nguồn [HoSoKhamPhaController.java](../backend/src/main/java/com/galaxora/nhiemvu/HoSoKhamPhaController.java).

- `long id`

### KhoController.DieuChinh

Nguồn [KhoController.java](../backend/src/main/java/com/galaxora/kho/KhoController.java).

- `@NotNull @Pattern(regexp = DINH_DANG_KHOA) String khoaYeuCau`
- `int thayDoi`
- `boolean xuatKho`
- `@NotBlank @Size(max = 500) String lyDo`

### KhoController.DongNhap

Nguồn [KhoController.java](../backend/src/main/java/com/galaxora/kho/KhoController.java).

- `@Positive long sanPhamId`
- `@Positive int soLuong`

### KhoController.MaDinhDanh

Nguồn [KhoController.java](../backend/src/main/java/com/galaxora/kho/KhoController.java).

- `long id`

### KhoController.Nguong

Nguồn [KhoController.java](../backend/src/main/java/com/galaxora/kho/KhoController.java).

- `@PositiveOrZero Integer giaTri`

### KhoController.NhapKho

Nguồn [KhoController.java](../backend/src/main/java/com/galaxora/kho/KhoController.java).

- `@NotNull @Pattern(regexp = DINH_DANG_KHOA) String khoaYeuCau`
- `@NotBlank @Size(max = 500) String lyDo`
- `@NotEmpty @Size(max = 100) List<@NotNull @Valid DongNhap> chiTiet`

### KhoMoHinhSanPham.ThongTin

Nguồn [KhoMoHinhSanPham.java](../backend/src/main/java/com/galaxora/cuahang/KhoMoHinhSanPham.java).

- `String tenTep`
- `String dinhDang`
- `long dungLuong`
- `String giayPhep`
- `String nguon`
- `String tacGia`
- `String ghiCong`
- `boolean daDuyet`

### QuanLyNhiemVu.NhiemVu

Nguồn [QuanLyNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/QuanLyNhiemVu.java).

- `long id`
- `Tao duLieu`
- `DichVuNhiemVu.CauHinh voucher`

### QuanLyNhiemVu.Sua

Nguồn [QuanLyNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/QuanLyNhiemVu.java).

- `@NotBlank @Size(max=160) String ten`
- `@NotBlank @Size(max=1000) String moTa`
- `@NotNull Boolean kichHoat`

### QuanLyNhiemVu.Tao

Nguồn [QuanLyNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/QuanLyNhiemVu.java).

- `@NotBlank @Size(max=160) String ten`
- `@NotBlank @Size(max=1000) String moTa`
- `@NotNull LoaiDieuKien loaiDieuKien`
- `@Size(max=48) String maDieuKien`
- `@PositiveOrZero int xp`
- `@Size(max=160) String tenHuyHieu`
- `boolean coVoucher`
- `boolean kichHoat`

### QuanLyNhiemVu.TienDoNhiemVu

Nguồn [QuanLyNhiemVu.java](../backend/src/main/java/com/galaxora/nhiemvu/QuanLyNhiemVu.java).

- `long id`
- `String ten`
- `String moTa`
- `LoaiDieuKien loaiDieuKien`
- `String maDieuKien`
- `int xp`
- `String tenHuyHieu`
- `boolean coVoucher`
- `boolean hoanThanh`
- `boolean voucherDangCho`

### QuanTriController.DatKhoa

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `@NotNull Boolean khoa`

### QuanTriController.DongNhatKy

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `long id`
- `long adminId`
- `String thaoTac`
- `String duongDan`
- `LocalDateTime thoiDiem`

### QuanTriController.LuuSuKien

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `@NotBlank @Size(max=160) String ten`
- `@NotBlank @Size(max=1000) String moTa`
- `@NotNull LocalDateTime batDau`
- `@NotNull LocalDateTime ketThuc`
- `@NotNull Boolean hienThi`

### QuanTriController.NoiDung

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `String ma`
- `String ten`
- `String moTa`

### QuanTriController.SuaNoiDung

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `@NotBlank @Size(max=160) String ten`
- `@NotBlank @Size(max=1000) String moTa`

### QuanTriController.SuKien

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `long id`
- `String ten`
- `String moTa`
- `LocalDateTime batDau`
- `LocalDateTime ketThuc`
- `boolean hienThi`

### QuanTriController.TaiKhoan

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `long id`
- `String hoTen`
- `String email`
- `boolean dangKhoa`

### QuanTriController.ThongBao

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `String noiDung`
- `String duongDan`

### QuanTriController.TongQuan

Nguồn [QuanTriController.java](../backend/src/main/java/com/galaxora/quantri/QuanTriController.java).

- `long donCanXuLy`
- `long yeuCauHuy`
- `long tonKhoThap`
- `BigDecimal doanhThu`
- `long nhiemVuHoanThanh`

### TaiKhoanController.DangKy

Nguồn [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java).

- `@NotBlank @Size(max = 255) String hoTen`
- `@NotBlank @Email @Size(max = 254) String email`
- `@NotBlank @Size(max = 32) String soDienThoai`
- `@NotBlank @Size(max = 72) String matKhau`

### TaiKhoanController.DangNhap

Nguồn [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java).

- `@NotBlank @Size(max = 254) String dinhDanh`
- `@NotBlank @Size(max = 72) String matKhau`

### TaiKhoanController.DoiMatKhau

Nguồn [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java).

- `@NotBlank @Size(max = 72) String matKhau`
- `@NotBlank @Size(max = 72) String matKhauMoi`

### TaiKhoanController.ThongTinCsrf

Nguồn [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java).

- `String tenHeader`
- `String token`

### TaiKhoanController.ThongTinPhien

Nguồn [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java).

- `long nguoiDungId`
- `List<String> vaiTro`

### ThongBaoNguoiDung.ThongBao

Nguồn [ThongBaoNguoiDung.java](../backend/src/main/java/com/galaxora/quantri/ThongBaoNguoiDung.java).

- `String noiDung`
- `String duongDan`

## 8.4. Dạng phản hồi thực

```json
{ "nguoiDungId": 1, "vaiTro": ["USER"] }
```

Ví dụ dạng phản hồi phiên. `GET /api/tai-khoan/phien` và `POST /api/tai-khoan/dang-nhap` trả cùng DTO. Xem [TaiKhoanController.java](../backend/src/main/java/com/galaxora/taikhoan/TaiKhoanController.java).

```json
{ "noiDung": [], "tong": 0, "trang": 0, "kichThuoc": 12 }
```

Ví dụ trang sản phẩm rỗng với bộ tham số mặc định. Xem [DichVuCuaHang.java](../backend/src/main/java/com/galaxora/cuahang/DichVuCuaHang.java).
