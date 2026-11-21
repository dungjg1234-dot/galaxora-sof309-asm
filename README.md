# GALAXORA

Website khám phá vũ trụ 3D, cửa hàng và gamification. Mô tả đầy đủ trong [`plan.md`](plan.md); quy tắc làm việc trong [`AGENTS.md`](AGENTS.md); tiến độ trong [`docs/development-roadmap.md`](docs/development-roadmap.md).

> Giai đoạn 3–7 đã nghiệm thu. Xem trạng thái phần còn lại trong roadmap và [hướng dẫn bàn giao, sao lưu và hosting](docs/ban-giao.md).

## Yêu cầu môi trường

- JDK 21
- Maven 3.9+ (hoặc dùng Maven tích hợp trong IntelliJ IDEA / NetBeans)
- MySQL 8 với database tên `galaxora` (tạo sẵn, bộ ký tự `utf8mb4`)
- Node.js 22+ và npm

## Chạy backend

Chạy từ thư mục `backend/` (đường dẫn migration Flyway tính theo thư mục này).

| Biến môi trường | Ý nghĩa | Mặc định |
|---|---|---|
| `GALAXORA_DB_PASSWORD` | Mật khẩu MySQL (**bắt buộc**, không lưu trong Git) | không có |
| `GALAXORA_DB_USER` | Tài khoản MySQL | `root` |
| `GALAXORA_DB_URL` | Chuỗi JDBC | `jdbc:mysql://localhost:3306/galaxora?...` |
| `GALAXORA_COOKIE_SECURE` | `true` khi chạy HTTPS | `false` |
| `SPRING_PROFILES_ACTIVE` | `dev` để bật log chi tiết | không có |

```bash
cd backend
mvn spring-boot:run
```

Kiểm tra: `GET http://localhost:8080/api/he-thong/suc-khoe` trả về `{"trangThai":"HOAT_DONG"}`.

Chạy test: `mvn test`.

## Chạy frontend

```bash
cd frontend
npm install
npm run dev
```

Mở địa chỉ Vite in ra (mặc định http://localhost:5173). Mọi yêu cầu `/api` được chuyển tới backend `http://localhost:8080` (đổi bằng biến `GALAXORA_BACKEND_URL`).

Build production: `npm run build`.

## Tài khoản

Khi đăng ký, chọn quốc gia và nhập số điện thoại cùng xác nhận mật khẩu. Đăng nhập bằng email hoặc số điện thoại và mật khẩu. USER vào Trang chủ, còn ADMIN vào khu vực riêng tại `/quan-tri`. Trong tài khoản, người dùng có thể đổi mật khẩu bằng mật khẩu hiện tại. Website đã bỏ chức năng gửi email. Khôi phục mật khẩu đã quên và tự đổi thông tin đăng nhập đang chờ phương án xác minh mới.

## Dùng Cửa hàng, Kho và Đơn hàng

- Mở `/cua-hang` để xem danh mục, tìm sản phẩm và mở chi tiết. Đăng nhập bằng tài khoản `USER` để thêm vào giỏ, đặt COD, theo dõi đơn và đánh giá đơn đã giao.
- Trong **Tài khoản của bạn**, USER có thể thêm, sửa và xóa địa chỉ nhận hàng. Giỏ hàng có lựa chọn điền từ sổ địa chỉ. Đơn giữ thông tin giao nhận tại lúc đặt.
- Mở `/quan-tri/cua-hang` bằng phiên `ADMIN` để tạo danh mục và sản phẩm, nhập giá bán VND và chọn ảnh từ bộ ảnh GALAXORA hoặc nhập URL HTTPS. Ảnh đầu tiên là ảnh bìa. Sản phẩm chưa gắn ảnh sẽ dùng ảnh trong bộ nếu SKU hoặc tên khớp. Mô hình GLB/glTF 2.0 cần đóng gói tài nguyên trong một tệp tối đa 8 MiB, khai báo giấy phép và được duyệt hiển thị.
- Mở `/quan-tri/kho` để nhập hàng theo phiếu, xuất/điều chỉnh và xem lịch sử. Đặt ngưỡng cảnh báo chung tại đây. Đặt phí COD cố định toàn quốc ở `/quan-tri/don-hang` trước khi mở checkout.
- Để nghiệm thu với 39 sản phẩm mẫu, xem [cách chạy seed cục bộ](database/seed-data/README.md). Khởi động lại backend sau khi cập nhật mã để Flyway áp dụng V8 và mở các thao tác xóa trong Quản trị.
- Xử lý trạng thái giao và yêu cầu hủy ở `/quan-tri/don-hang`. Dùng `/quan-tri/voucher` để tạo, khóa hoặc xem lịch sử voucher.

Đăng ký công khai chỉ cấp `USER`. Người quản trị cơ sở dữ liệu có thể cấp quyền `ADMIN` cho tài khoản đã được chọn bằng cách thêm bản ghi tương ứng trong `nguoi_dung_vai_tro` cho đúng ID người dùng và vai trò `ADMIN`. Tài khoản cần đăng nhập lại để nạp quyền vào phiên.

Khi phục vụ bản build React, cấu hình máy chủ trả `index.html` cho các đường dẫn giao diện `/he-thong`, `/tai-khoan/*`, `/cua-hang/*`, `/gio-hang`, `/don-hang/*`, `/quan-tri/*`, `/kham-pha` và `/kham-pha/*`. Chuyển `/api/*` tới Spring Boot trên cùng origin.

## Khám phá vũ trụ 3D

Mở `/kham-pha` sau khi chạy frontend. Chọn thiên thể trên cảnh Hệ Mặt Trời hoặc trong danh sách để xem mô hình chi tiết và số liệu NASA. Dùng thanh khu vực để chuyển sang Thiên hà, Tinh vân hoặc Hố đen. Các cảnh hỗ trợ WebGL 2 và có chế độ xem 2D dự phòng. [Đặc tả trải nghiệm](docs/3d-visual-spec.md) · [Nguồn và giấy phép mô hình](docs/3d-assets.md)

Cảnh chiếm toàn bộ khung trình duyệt. Nút **Toàn màn hình** ẩn thanh công cụ trên trình duyệt hỗ trợ. Ở Hệ Mặt Trời, nút **Cảnh gốc** mở mô hình toàn cảnh của tác giả. Mỗi mô hình gốc tải khi mở cảnh hoặc chọn thiên thể.
