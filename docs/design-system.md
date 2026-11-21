# Hệ thống giao diện GALAXORA

## Nền tảng

Trang chủ là mẫu chuẩn cho nền navy, chữ sáng, điểm nhấn cyan và tím, thẻ kính và nút gradient. Toàn bộ website dùng `system-ui` để hiển thị tiếng Việt. Các giá trị `--gx-*` được khai báo ở [bienThietKe.css](../frontend/src/giaodien/bienThietKe.css) và nạp cùng [thanhPhan.css](../frontend/src/giaodien/thanhPhan.css) tại `main.tsx`.

| Thành phần | Quy tắc |
|---|---|
| Nền và chữ | Navy `#01081d` đến `#0e112f`, chữ `#f4f7ff`, chữ phụ `#c7d3ed`. Trang con dùng lớp sao CSS. Ảnh hero dùng ở Trang chủ. |
| Thẻ và sản phẩm | Thẻ kính nền navy, bo mặc định `1rem`, viền xanh tím. Thẻ sản phẩm có viền, nền và bán kính riêng lấy từ thẻ Trang chủ. Blur cục bộ ưu tiên các panel nhỏ. |
| Nút và trạng thái | Nút chính dùng gradient `#65dcff → #979af9 → #c783f5` và chữ navy. Nút phụ tối có viền sáng. Lỗi, cảnh báo kho và trạng thái đơn giữ nhãn bằng chữ. |
| Biểu mẫu, bảng và hộp thoại | Ô nhập và tùy chọn có nền tối, vòng focus cyan, kiểu autofill trên Chrome. Bảng có đầu hàng tối, đường kẻ mảnh, hover và cuộn trong khung. Xem nhanh dùng `dialog` gốc. |
| XP | `progress` gốc có nhãn truy cập, dùng nền `#242d4e` và gradient `#38baff → #74ecff`. |

## Hiệu ứng và màu dùng chung

- Khung trang dùng nền nhiều lớp gồm gradient, tinh vân, sao và viền tối. Khung, biểu tượng SVG, ánh sáng nút và chữ dùng token màu từ [bienThietKe.css](../frontend/src/giaodien/bienThietKe.css). Các hiệu ứng dùng chung nằm trong [hieuUng.css](../frontend/src/giaodien/hieuUng.css).
- Viền quay, vệt sáng, nhịp sáng của nút chính và chuyển động nhẹ của thẻ dùng `transform` và `opacity`. Nội dung hiện sẵn trước hiệu ứng cuộn. Các lớp trang trí bỏ qua sự kiện con trỏ. Khi bật giảm chuyển động, CSS giữ màu và tắt hiệu ứng động.
- Quản trị dùng nền tinh vân, bề mặt kính tối và điểm sáng xanh tím ở banner, thẻ chỉ số và mục đang chọn. Bảng và biểu mẫu dùng bề mặt tối để dễ đọc. Khám phá dùng màu chung cho lớp điều khiển bên ngoài canvas và giữ riêng hiệu ứng của cảnh.

## Khung và điều hướng

- [KhungTrang](../frontend/src/giaodien/KhungTrang.tsx) dựng một `main`, nhận đầu trang và hỗ trợ chân trang tùy chọn. [ThanhDauTrang](../frontend/src/giaodien/ThanhDauTrang.tsx) và [ChanTrang](../frontend/src/giaodien/ChanTrang.tsx) dùng chung logo và danh sách liên kết. Logo về Trang chủ. Hành trình dẫn vào Khám phá Hệ Mặt Trời.
- Khi trang có phiên đăng nhập, menu Tài khoản hiện Hồ sơ, Yêu thích và Đơn hàng với USER, hoặc Quản trị với ADMIN. Giỏ hàng giữ vị trí riêng. Trang chưa đọc phiên dùng liên kết Tài khoản hoặc Đăng nhập theo ngữ cảnh. Menu sử dụng nút với `aria-expanded`, liên kết gốc, phím Tab và Escape trả focus.
- Quản trị có thanh bên và đầu trang riêng với thông báo, tài khoản đang hoạt động và đăng xuất. Trên màn hình hẹp, menu mở từ đầu trang và các thẻ tự xuống hàng. Kiểm tra hệ thống dùng đầu và chân trang gọn. Khám phá đặt đầu trang gọn trong vùng toàn màn hình, giữ thanh khu vực phía đáy để dành không gian cho cảnh. Các trang còn lại dùng chân trang chuẩn, với nút Lên đầu trang.

## Responsive và truy cập

- Các mốc của khung chung khoảng 600, 900 và 1280 px. Bố cục hiện có của Tài khoản, Bán hàng và Khám phá lần lượt gập tại `48rem`, `720px` và `700px`. Khi đổi chiều cao đầu trang Khám phá, các bảng lấy vị trí theo biến chiều cao đầu trang.
- Dùng liên kết khi đổi trang và nút cho thao tác tại chỗ. Giữ một tiêu đề chính cho từng màn hình nội dung, vòng `:focus-visible`, `lang="vi"` và thông báo trạng thái có chữ.
- Tiêu đề tiếng Việt dùng `--gx-dong-tieu-de` bằng 1,2. Tiêu đề gradient có đệm dọc để giữ dấu. Vùng bấm của điều khiển dùng `--gx-vung-bam` bằng 44 px. Cuộn ngang của bảng và danh sách thiên thể nằm trong khung tương ứng.
- `prefers-reduced-motion` giảm hoặc tắt các hiệu ứng trang trí và chuyển cảnh. Khám phá giữ lựa chọn 2D dự phòng và điều khiển toàn màn hình. Nội dung và giấy phép tài nguyên nằm trong [đặc tả 3D](3d-visual-spec.md).
