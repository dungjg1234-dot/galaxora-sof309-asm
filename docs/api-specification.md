# API tài khoản — GALAXORA

Các đường dẫn dưới đây dùng tiền tố `/api/tai-khoan`. Gửi JSON với `Content-Type: application/json` khi endpoint có thân yêu cầu. API dùng cùng origin với frontend.

## Phiên và CSRF

Trước yêu cầu POST, PUT hoặc DELETE, gọi `GET /api/tai-khoan/csrf` và giữ cookie phiên mà máy chủ trả về. Đọc `tenHeader` và `token` trong JSON để gửi token theo đúng header. Lấy token mới sau khi đăng nhập, đăng xuất hoặc khi phiên hết hạn. Thiếu token hoặc token sai trả `403`.

Đăng nhập thành công đổi ID phiên và trả cookie phiên `HttpOnly`, `SameSite=Lax`. Bật `GALAXORA_COOKIE_SECURE=true` khi chạy HTTPS. Phiên hết sau 30 phút không hoạt động. Phiên lưu trong bộ nhớ Tomcat trên một máy chủ và kết thúc khi máy chủ khởi động lại. Đổi mật khẩu hủy phiên hiện tại. Các phiên khác dùng mật khẩu cũ mất hiệu lực khi gọi API lần tiếp theo.

## Endpoint

| Phương thức và đường dẫn | JSON gửi | Kết quả |
|---|---|---|
| `GET /csrf` | — | `200` với `tenHeader` và `token`. |
| `POST /dang-ky` | `hoTen`, `email`, `soDienThoai`, `matKhau` | `202` cho yêu cầu hợp lệ, kể cả trùng email hoặc số điện thoại. Tài khoản mới nhận vai trò `USER` và đăng nhập được ngay. |
| `POST /dang-nhap` | `dinhDanh`, `matKhau` | `200` với `nguoiDungId` và mảng `vaiTro` khi đúng mật khẩu và tài khoản đang mở khóa. `401` cho lỗi xác thực hoặc vượt giới hạn tài khoản. `429` khi vượt giới hạn IP. |
| `GET /phien` | — | `200` với `nguoiDungId` và `vaiTro`, hoặc `401` khi không có phiên hợp lệ. Cùng DTO với đăng nhập. |
| `POST /dang-xuat` | — | `204`. Hủy phiên hiện tại và xóa cookie phiên. |
| `POST /doi-mat-khau` | `matKhau`, `matKhauMoi` | Yêu cầu phiên hợp lệ. `204` khi đổi thành công và kết thúc phiên. `400` nếu mật khẩu hiện tại sai hoặc mật khẩu mới không hợp lệ. |

Dữ liệu sai định dạng trả `400`. Email là một định danh đăng nhập. Website không gửi email. Tự khôi phục mật khẩu và đổi thông tin liên hệ đang chờ quy trình xác minh mới.

## Dữ liệu và giới hạn

- Email lưu theo dạng người dùng đã nhập. Khóa tra cứu được chuyển sang chữ thường bằng `Locale.ROOT`. Số điện thoại phải có dấu `+` và mã quốc gia. Backend bỏ dấu cách, ngoặc tròn và dấu gạch ngang rồi kiểm tra dạng E.164.
- Mật khẩu bắt buộc có nội dung và giới hạn 72 byte UTF-8 của BCrypt. Backend giữ nguyên nội dung mật khẩu khi băm và đối chiếu.
- Sau 5 lần đăng nhập không thành công trong cửa sổ 15 phút, backend từ chối đăng nhập theo tài khoản. Email và số điện thoại của cùng tài khoản dùng chung bộ đếm. Đăng nhập thành công đặt lại bộ đếm của tài khoản. Giới hạn kỹ thuật theo IP là 30 yêu cầu đăng nhập hợp lệ về cấu trúc trong 15 phút. Bộ đếm dùng IP kết nối tới backend và lưu trong bộ nhớ. Khi triển khai qua reverse proxy, cần chốt cách lấy IP thực từ proxy tin cậy.
- Mỗi yêu cầu có phiên kiểm lại tập vai trò hiện hành. Thay đổi vai trò, khóa tài khoản hoặc đổi mật khẩu khiến phiên cũ mất hiệu lực.

## Sổ địa chỉ

Các endpoint dưới đây yêu cầu USER và chỉ thao tác trên địa chỉ của phiên. Thông tin nhận hàng vẫn được gửi bằng các trường checkout hiện hành và lưu thành snapshot của đơn.

| Endpoint | Dữ liệu và kết quả |
|---|---|
| `GET /api/tai-khoan/dia-chi` | `200` với danh sách `id`, `nguoiNhan`, `soDienThoai`, `diaChi`. |
| `POST /api/tai-khoan/dia-chi` | Nhận `nguoiNhan`, `soDienThoai`, `diaChi`. `204` khi tạo. |
| `PUT /api/tai-khoan/dia-chi/{id}` | Cùng các trường như POST. `204` khi lưu, `404` khi địa chỉ không thuộc phiên hoặc đã bị xóa. |
| `DELETE /api/tai-khoan/dia-chi/{id}` | `204` khi xóa, `404` khi địa chỉ không thuộc phiên hoặc đã bị xóa. |

## Giao diện xác thực

Từ trang kiểm tra hệ thống ở `/`, chọn **Đến trang đăng nhập**. Các màn hình tại `/tai-khoan/` gọi API qua proxy Vite khi phát triển. Khi phục vụ bản build, cấu hình máy chủ để các đường dẫn giao diện dưới `/tai-khoan/` trả về `index.html` và chuyển riêng `/api/` tới Spring Boot trên cùng origin.

## Cửa hàng

Mọi yêu cầu thay đổi dữ liệu dùng CSRF và cookie phiên. Các đường dẫn dưới `/api/quan-tri/` yêu cầu `ADMIN`. Yêu thích, giỏ, đơn của khách và viết đánh giá yêu cầu `USER`. Lỗi nghiệp vụ trả `400`, `404` hoặc `409` với trường `detail`. Trường hợp thiếu xác thực hoặc quyền trả `401` hoặc `403`.

| Endpoint | Dữ liệu |
|---|---|
| `GET /api/cua-hang/danh-muc` | Danh mục đang hiển thị. |
| `GET /api/cua-hang/san-pham` | `tuKhoa`, `danhMucId`, `sapXep` (`moi`, `gia-tang`, `gia-giam`, `ten`), `trang` từ 0, `kichThuoc` tối đa 100. Trả danh sách, tổng và thông tin trang. |
| `GET /api/cua-hang/san-pham/{id}` | Thông tin sản phẩm, ảnh và sản phẩm liên quan. Đối tượng sản phẩm có `soLuongKhaDung`, bằng tồn thực tế trừ tồn đang giữ. |
| `GET /api/cua-hang/san-pham/{id}/mo-hinh[/noi-dung]` | Metadata hoặc tệp 3D đã duyệt của sản phẩm công khai. |
| `GET /api/cua-hang/san-pham/{id}/danh-gia` | Đánh giá đang hiển thị. |
| `GET /api/cua-hang/yeu-thich`, `PUT /api/cua-hang/yeu-thich/{id}` | Đọc danh sách hoặc đặt `yeuThich` thành true/false. |
| `GET /api/danh-gia/{donHangId}`, `PUT /api/danh-gia/{donHangId}/{sanPhamId}` | Đọc đánh giá của mình hoặc lưu `soSao` (1–5), `noiDung` cho đơn đã giao. |
| `GET/POST /api/quan-tri/cua-hang/danh-muc`, `PUT …/danh-muc/{id}` | Danh sách Admin hoặc lưu `ten`, `hienThi`. |
| `GET/POST /api/quan-tri/cua-hang/san-pham`, `GET/PUT …/san-pham/{id}` | Danh sách phân trang, đọc chi tiết hoặc lưu `danhMucId`, `sku`, `ten`, `moTaNgan`, `moTa`, `gia`, `giaSoSanh`, `hienThi`, `kinhDoanh`, `hinhAnh` (mảng `duongDan`, `moTa`). Đường dẫn ảnh hỗ trợ URL HTTPS và ảnh tĩnh cùng tên miền theo mẫu `/anh-san-pham/{nhom}/{ma}.webp`. Ảnh đầu tiên là ảnh bìa. |
| `GET/POST/PUT /api/quan-tri/cua-hang/san-pham/{id}/mo-hinh` | Đọc metadata, tải multipart với `tep`, `giayPhep`, `nguon`, `tacGia`, `ghiCong` hoặc cập nhật `daDuyet`. |
| `GET /api/quan-tri/cua-hang/san-pham/{id}/danh-gia`, `PUT /api/quan-tri/cua-hang/danh-gia/{donHangId}/{sanPhamId}/hien-thi` | Đọc đánh giá kể cả đã ẩn hoặc cập nhật `hienThi`. |

## Xóa trong Quản trị

Các yêu cầu `DELETE` trong Quản trị yêu cầu ADMIN và CSRF, trả `204` khi hoàn tất và ghi nhật ký trong cùng giao dịch.

| Đường dẫn dưới `/api/quan-tri` | Quy tắc xóa |
|---|---|
| `/cua-hang/danh-muc/{id}` | Trả `409` nếu còn sản phẩm chưa xóa. Danh mục chỉ còn sản phẩm đã lưu trữ được ẩn khỏi danh sách, đồng thời giữ khóa ngoại. |
| `/cua-hang/san-pham/{id}` · `/cua-hang/san-pham/{id}/mo-hinh` | Xóa sản phẩm khỏi danh sách và ngừng bán, giữ lịch sử kho và đơn. Xóa mô hình sẽ gỡ tệp 3D đã tải lên. |
| `/voucher/{id}` | Ngừng áp dụng voucher, giữ lịch sử sử dụng. |
| `/nhiem-vu/{id}` · `/nhiem-vu/{id}/voucher` · `/voucher-hanh-trinh` | Xóa nhiệm vụ khỏi danh sách quản trị và dừng ghi nhận mới. Xóa mẫu voucher dừng cấp từ mẫu đó và giữ số lượt đã cấp. |
| `/su-kien/{id}` · `/noi-dung/{loai}/{ma}` | Xóa sự kiện hoặc gỡ bản nội dung tùy chỉnh để giao diện dùng nội dung mặc định. |

## Kho

Tiền tố `/api/quan-tri/kho`. `GET /ton` trả tồn thực tế, đang giữ, khả dụng và cảnh báo. `GET /ton/{id}/lich-su` đọc biến động. `GET/PUT /nguong` đọc hoặc đặt `giaTri` không âm. `GET /phieu-nhap` và `GET /phieu-nhap/{id}` đọc phiếu. `POST /phieu-nhap` nhận `khoaYeuCau` (UUID), `lyDo`, `chiTiet` (mảng `sanPhamId`, `soLuong`), trả `id`. `POST /ton/{id}/dieu-chinh` nhận `khoaYeuCau`, `thayDoi`, `xuatKho`, `lyDo`. Dùng lại UUID khi gửi lại cùng yêu cầu.

## Giỏ hàng và COD

| Endpoint | Dữ liệu |
|---|---|
| `GET /api/gio-hang` | Dòng sản phẩm, tồn khả dụng và tạm tính. Mỗi dòng có `anh` là đường dẫn ảnh bìa, hoặc `null` nếu chưa gắn ảnh. |
| `POST/PUT /api/gio-hang/{id}` | `POST` cộng thêm `soLuong`. `PUT` đặt số lượng mới, dùng 0 để xóa. |
| `GET /api/gio-hang/tinh-tien` | `maVoucher` tùy chọn. Trả `tamTinh`, `giamGia`, `phiVanChuyen`, `tongTien` từ backend. |
| `POST /api/don-hang` | `khoaYeuCau` UUID, `nguoiNhan`, `soDienThoai`, `diaChi`, `ghiChu` và `maVoucher` tùy chọn, `tongTienDaXacNhan`. Trả `id` cũ nếu gửi lại cùng khóa. Từ chối nếu tổng backend vừa tính khác tổng đã xác nhận. |
| `GET /api/don-hang[/{id}]` | Danh sách phân trang hoặc chi tiết đơn của người mua, gồm sản phẩm, lịch sử và yêu cầu hủy. |
| `POST /api/don-hang/{id}/huy`, `POST …/{id}/yeu-cau-huy` | Tự hủy trước xác nhận hoặc gửi `lyDo` từ Đã xác nhận đến hết Đang chuẩn bị. |
| `GET /api/quan-tri/don-hang[/{id}]` | Danh sách phân trang hoặc chi tiết cho Admin. |
| `GET/PUT /api/quan-tri/don-hang/phi-van-chuyen` | Đọc hoặc đặt `giaTri` VND cho một mức phí COD toàn quốc. |
| `POST /api/quan-tri/don-hang/{id}/trang-thai` | `trangThai`, `lyDo` khi hủy/giao thất bại và `daThuTien=true` khi xác nhận giao thành công. |
| `POST /api/quan-tri/don-hang/{id}/yeu-cau-huy/{yeuCauId}` | Quyết định `duyet` và `lyDo`. |
| `GET/POST /api/quan-tri/voucher`, `PUT …/voucher/{id}`, `GET …/voucher/{id}/lich-su` | Danh sách, lưu cấu hình hoặc đọc lịch sử. Dữ liệu ghi gồm `ma`, `loai` (`PHAN_TRAM`, `SO_TIEN`), `giaTri`, `giamToiDa`, `donToiThieu`, `batDau`, `ketThuc` (UTC), `luotToiDa`, `kichHoat`, `chuSoHuuId` tùy chọn. |

## Hồ sơ khám phá

`GET /api/nhiem-vu/ho-so` yêu cầu phiên có vai trò USER. Trả về `tongXp`, `capDo` gồm cấp và hai mốc XP, `lichSuXp` và `phanThuong` của chính tài khoản. Tài khoản mới có 0 XP, cấp 1 và hai danh sách rỗng. Mã voucher chỉ được trả về khi voucher thuộc tài khoản đang đăng nhập.

| Endpoint | Hành vi |
|---|---|
| `GET /api/nhiem-vu/tien-do` | Hồ sơ, ba chương, cờ mở/hoàn thành và quyền nhận voucher đang chờ. |
| `POST /api/nhiem-vu/kham-pha/{ma}/mo-bang` | Trả `ve` và `choMiliGiay` khi mở bảng của thiên thể thuộc hành trình. Vé gắn với thiên thể và phiên. |
| `POST /api/nhiem-vu/kham-pha/{ma}` | Nhận `ve`. Ghi nhận sau khoảng chờ nếu vé còn hạn và chưa dùng. Backend tính XP, huy hiệu và điều kiện hành trình. |
| `POST /api/nhiem-vu/dau-hieu` | Mở tín hiệu. Trả `ve`, `cauHoi`, `soLanConLai`. Một câu hỏi có hiệu lực 5 phút với tối đa 3 lần trả lời. |
| `POST /api/nhiem-vu/vat-the-an` | Nhận `ve` và số nguyên `traLoi`. Server chấm đáp án rồi mới ghi nhận. Vé hết hạn, đã dùng hoặc trả lời sai trả `409`. |
| `POST /api/nhiem-vu/voucher` | Thử nhận voucher đang chờ. Quyền vẫn còn nếu cấu hình chưa hợp lệ. |
| `GET /api/nhiem-vu/thong-bao` | Thông báo đơn, yêu cầu hủy, thưởng, voucher và sự kiện. |
| `GET /api/kham-pha/noi-dung`, `GET /api/nhiem-vu/noi-dung` | Nội dung Admin đã lưu. Nội dung khám phá công khai, nội dung nhiệm vụ dành cho USER. |
| `GET /api/quan-tri/tong-quan`, `GET /api/quan-tri/thong-bao` | Số liệu vận hành và thông báo Admin. Cảnh báo chạm giới hạn đăng nhập dùng bộ đếm hiện có, hiển thị trong 15 phút kể từ lượt bị chặn gần nhất và không chứa địa chỉ IP hay định danh. |
| `GET /api/quan-tri/nhat-ky` | Lọc theo `tu`, `den`, `thaoTac`, `adminId`, `trang`. `thaoTac` lọc theo nội dung thao tác. Ngày tính theo UTC. Bản ghi mới mô tả cụ thể thao tác và được lưu cùng giao dịch nghiệp vụ. |
| `GET /api/quan-tri/tai-khoan`, `PUT /api/quan-tri/tai-khoan/{id}/khoa` | Tìm tài khoản USER và khóa/mở bằng trường `khoa`. |
| `GET /api/quan-tri/noi-dung/{loai}`, `PUT /api/quan-tri/noi-dung/{loai}/{ma}` | Đọc hoặc lưu tên và mô tả cho `kham-pha` hoặc `nhiem-vu`. |
| `GET/POST /api/quan-tri/su-kien`, `PUT /api/quan-tri/su-kien/{id}` | Đọc, tạo và sửa sự kiện khám phá. |
| `GET/PUT /api/quan-tri/voucher-hanh-trinh` | Đọc hoặc cấu hình loại giảm, giá trị, điều kiện đơn, thời hạn, lượt cấp và kích hoạt. |
| `GET /api/quan-tri/nhiem-vu` | Liệt kê cấu hình nhiệm vụ và voucher cho ADMIN. |
| `POST /api/quan-tri/nhiem-vu` | ADMIN tạo nhiệm vụ với `ten`, `moTa`, `loaiDieuKien`, `maDieuKien`, `xp`, `tenHuyHieu`, `coVoucher`, `kichHoat`. Trả 201 và `id`. |
| `PUT /api/quan-tri/nhiem-vu/{id}` | ADMIN lưu tên, mô tả và trạng thái kích hoạt. Trả 204. |
| `PUT /api/quan-tri/nhiem-vu/{id}/voucher` | ADMIN cập nhật mẫu voucher theo cùng hợp đồng với voucher hành trình. Trả 204. |
| `POST /api/nhiem-vu/doi-chieu` | USER đối chiếu hoạt động đã ghi nhận và thử cấp voucher đang chờ. Trả cấu trúc tiến độ như GET. |

Loại điều kiện gồm `THIEN_THE` (mã của một trong mười thiên thể ghi
nhận trong hành trình), `CHUONG` (chuỗi `1`, `2`, `3`), hoặc
`DAU_HIEU_AN` (`maDieuKien` rỗng/null). Tiến độ trả thêm `nhiemVu`;
nhiệm vụ dấu hiệu ẩn chỉ hiện sau khi hoàn thành. Các lần đối chiếu sau
có thể cấp tiếp voucher đang chờ khi hợp lệ. Các lượt hoàn thành vẫn
được ghi nhận sau khi nhiệm vụ bị tắt.
