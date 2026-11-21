# Đối chiếu 39 sản phẩm mẫu GALAXORA

## Các bước bàn giao

| Bước | Tệp chính đã đổi | Cách kiểm tra |
|---|---|---|
| 1. Nạp mẫu | `database/seed-data/duLieuSanPhamMau.mjs`, `napSanPhamMau.mjs`, `nap-san-pham-mau.html` và `README.md` cùng thư mục | Trong phiên ADMIN, mở trang nạp mẫu. Chạy hai lần và đối chiếu số sản phẩm, trạng thái kho và phiếu nhập. |
| 2. Thẻ sản phẩm và cửa hàng | `SanPhamTrangChu.tsx`, `sanPhamTrangChu.css`, `CuaHang.tsx`, `banHang.css`, `anhSanPham.css`, `duLieu.ts`, `TrangBanHang.tsx` | Mở `/#san-pham` và `/cua-hang`. Thử xem nhanh, vào chi tiết, tìm theo tên, lọc danh mục, sắp xếp giá, chuyển trang và xem nút vô hiệu trên mặt hàng hết tồn. |
| 3. Chi tiết và giỏ | `CuaHang.tsx`, `BoAnhSanPham.tsx`, `DichVuCuaHang.java` | Mở chi tiết, bấm ảnh để xem trọn hình, đóng bằng Escape. Đăng nhập USER, thử chọn số lượng vượt tồn và xác nhận giỏ cập nhật sau khi thêm. |
| 4. Hành trình và responsive | `TrangChu.tsx`, `trangChu.css` | Đăng nhập USER, về Trang chủ và đối chiếu bề rộng Hành trình với khung Trang chủ. Thu cửa sổ hoặc dùng chế độ điện thoại 390 px. |
| 5. Xóa trong Quản trị | `V8__xoa_du_lieu_quan_tri.sql`, `NutXoaQuanTri.tsx`, `QuanTriCuaHang.tsx`, `QuanTriVoucher.tsx`, `QuanTriHeThong.tsx`. Backend cập nhật các lớp `CuaHangController`, `DichVuCuaHang`, `KhoMoHinhSanPham`, `DonHangController`, `DichVuVoucher`, `QuanTriController`, `HoSoKhamPhaController`, `QuanLyNhiemVu` và `DichVuNhiemVu`. Kiểm thử bổ sung trong `BanHangMySqlTest.java`. | Sau khi backend áp dụng V8, mở `/quan-tri/cua-hang`, tạo một danh mục trống và xóa. Danh mục còn sản phẩm có thông báo chuyển hoặc xóa sản phẩm trước. Mở Nhật ký hoạt động để đối chiếu. Các màn hình voucher, nhiệm vụ, sự kiện và nội dung có nút xóa cạnh thao tác quản trị hiện có. |

README gốc, `.gitignore`, đặc tả API, data dictionary và roadmap được cập nhật cùng đợt. Phiếu kho đã ghi sổ được giữ để đối chiếu. Nếu cần sửa tồn, dùng chức năng điều chỉnh kho có lý do.

## Kết quả kiểm tra

Frontend build thành công. Maven chạy 22 bài trên MySQL 8.4, gồm kiểm tra phân quyền xóa, điều kiện danh mục còn sản phẩm, tồn khả dụng, sản phẩm đã xóa và lịch sử kho. Chrome kiểm tra thực tế thẻ dùng chung, breadcrumb, lightbox, số lượng giỏ, bộ lọc, phân trang, Hành trình và xóa danh mục. Kiểm thử API cũng xác nhận xóa được mô hình 3D sau khi tải lên, voucher, mẫu voucher, nhiệm vụ, sự kiện và nội dung.

Lần kiểm tra cuối tại `localhost:5173` trả 0 sản phẩm công khai. Trang seed tạm đã sẵn sàng để chạy từ phiên ADMIN đang mở. Khởi động lại backend để áp dụng V8 trước khi kiểm tra các nút xóa trên ứng dụng đang dùng.

## Bảng sản phẩm mẫu

Bộ seed đã được nạp hai lần qua API quản trị trên MySQL nghiệm thu riêng. Hai lần trả cùng 39 sản phẩm, 39 đường dẫn ảnh và một phiếu nhập. Với ngưỡng chung 5, API trả 32 sản phẩm còn hàng, 4 sắp hết và 3 hết hàng.

Các mức giá dùng để nghiệm thu và đã được làm tròn theo VND. Mức giá tham khảo theo nhóm, chẳng hạn áo in họa tiết và đèn trang trí chủ đề phi hành gia. [Danh mục thời trang GO!](https://go-vietnam.vn/cdn/files/c/2026-28-01-2026-12-45-04/c607-25-03-2026-12-26-24/go-chomoigu-gialuonbatbai-bac.pdf) · [Đèn Cosmonaut của Quà Trực Tuyến](https://quatructuyen.vn/den-trang-tri-phi-hanh-gia-cosmonaut)

**Giá (VND)** là giá gốc khi có giảm. **Giá giảm (VND)** là giá bán áp dụng. Dấu gạch ngang nghĩa là sản phẩm chỉ có một giá bán. Tồn là số lượng khả dụng sau khi nạp trên DB nghiệm thu.

| Sản phẩm | Danh mục | Giá (VND) | Giá giảm (VND) | Tồn | Trạng thái |
|---|---|---:|---:|---:|---|
| Balo vỏ giáp Iron Man tích hợp đèn Arc Reactor | Phụ kiện công nghệ | 890.000 | 690.000 | 20 | Còn hàng |
| Bộ trang phục phi hành gia UXSA EVA | Thời trang | 1.490.000 | — | 8 | Còn hàng |
| Giày bốt cao cổ phi hành gia trắng viền LED | Phụ kiện công nghệ | 1.090.000 | 890.000 | 2 | Sắp hết |
| Giày sneaker Cyberpunk LED màu đen xanh neon | Phụ kiện công nghệ | 590.000 | — | 21 | Còn hàng |
| Kính mắt Cyberpunk phát quang LED mạch điện tử | Phụ kiện công nghệ | 329.000 | 249.000 | 17 | Còn hàng |
| Mô hình Figure Songoku chiến đấu rách áo | Mô hình sưu tầm | 329.000 | — | 24 | Còn hàng |
| Mô hình gỗ 3D tàu con thoi và tháp phóng NASA | Mô hình sưu tầm | 389.000 | — | 12 | Còn hàng |
| Mô hình Robot Chibi Android trắng tương lai | Mô hình sưu tầm | 249.000 | 189.000 | 28 | Còn hàng |
| Mô hình Robot chiến giáp Mecha Heavy Armor trắng đen | Mô hình sưu tầm | 459.000 | — | 14 | Còn hàng |
| Mô hình Robot Rover thám hiểm mini viễn tưởng | Mô hình sưu tầm | 289.000 | — | 20 | Còn hàng |
| Mô hình súng ngắn viễn tưởng Sci-Fi Blaster | Phụ kiện công nghệ | 169.000 | — | 0 | Hết hàng |
| Mô hình tiểu cảnh tên lửa không gian cất cánh | Mô hình sưu tầm | 429.000 | 349.000 | 3 | Sắp hết |
| Mô hình tàu con thoi Columbia NASA cỡ lớn | Mô hình sưu tầm | 1.250.000 | — | 9 | Còn hàng |
| Mô hình tàu con thoi NASA Discovery có bệ đỡ | Mô hình sưu tầm | 1.190.000 | 990.000 | 11 | Còn hàng |
| Mặt nạ công nghệ Cyberpunk LED neon | Phụ kiện công nghệ | 289.000 | — | 18 | Còn hàng |
| Poster tranh nghệ thuật Son Goku ngân hà tinh vân | Đồ trang trí | 129.000 | — | 34 | Còn hàng |
| Quần boxer nam họa tiết hoạt hình vũ trụ | Thời trang | 89.000 | — | 30 | Còn hàng |
| Quần boxer nam phi hành gia cưỡi cá voi không gian | Thời trang | 99.000 | — | 25 | Còn hàng |
| Set thời trang Techwear nữ tương lai xanh trắng | Thời trang | 489.000 | — | 16 | Còn hàng |
| Set trang phục Techwear nữ Cyberpunk đen dạ quang | Thời trang | 649.000 | 529.000 | 4 | Sắp hết |
| Set đồ nữ Cosmic Galaxy hoodie croptop xanh đậm | Thời trang | 359.000 | — | 18 | Còn hàng |
| Set đồ nữ Goku Chibi Cân Đẩu Vân màu trắng | Thời trang | 299.000 | — | 22 | Còn hàng |
| Set đồ nữ Goku Dragon Ball áo croptop đen cam | Thời trang | 399.000 | 329.000 | 16 | Còn hàng |
| Set đồ nữ Vegeta Dragon Ball áo dài tay xanh navy | Thời trang | 349.000 | — | 14 | Còn hàng |
| Set đồ nữ Vegeta Prince of Saiyans quần đỏ | Thời trang | 329.000 | — | 15 | Còn hàng |
| Set đồ nữ Zoro One Piece croptop và quần ống rộng | Thời trang | 359.000 | — | 12 | Còn hàng |
| Tai nghe gaming Cyberpunk LED viễn tưởng | Phụ kiện công nghệ | 599.000 | 479.000 | 25 | Còn hàng |
| Tranh nghệ thuật 3D cắt giấy Hệ Mặt Trời | Đồ trang trí | 269.000 | — | 18 | Còn hàng |
| Tượng decor phi hành gia Chibi ngồi mặt trăng | Mô hình sưu tầm | 169.000 | — | 29 | Còn hàng |
| Áo khoác da Techwear trắng viền đèn LED Cyberpunk | Thời trang | 799.000 | 679.000 | 9 | Còn hàng |
| Áo sweater nỉ đen họa tiết Hệ Mặt Trời | Thời trang | 299.000 | — | 20 | Còn hàng |
| Áo sweater nỉ đen phi hành gia đu trăng khuyết | Thời trang | 279.000 | — | 13 | Còn hàng |
| Áo thun streetwear vũ trụ Nhật Bản hai mặt | Thời trang | 259.000 | — | 0 | Hết hàng |
| Áo thun đen Astronaut Interstellar Travel | Thời trang | 239.000 | 189.000 | 26 | Còn hàng |
| Áo thun đen phi hành gia ngồi tiểu hành tinh | Thời trang | 159.000 | — | 32 | Còn hàng |
| Áo thun đen đồng hồ ngân hà Time Never Waits | Thời trang | 179.000 | — | 23 | Còn hàng |
| Đèn ngủ Hologram mèo phát sáng Sci-Fi | Đồ trang trí | 439.000 | 349.000 | 1 | Sắp hết |
| Đạo cụ đoản đao Cyberpunk đèn LED đỏ | Phụ kiện công nghệ | 189.000 | — | 0 | Hết hàng |
| Đồng hồ đeo tay thông minh Hologram 3D viễn tưởng | Phụ kiện công nghệ | 449.000 | — | 10 | Còn hàng |
