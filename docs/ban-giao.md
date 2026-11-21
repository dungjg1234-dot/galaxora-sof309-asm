# Bàn giao GALAXORA

## Nghiệm thu trên máy hiện tại

1. Chuẩn bị JDK 21, Maven, MySQL 8 và Node.js theo [README](../README.md). Sao lưu cơ sở dữ liệu trước khi nâng cấp. Chạy backend từ thư mục `backend/` để Flyway nhận đúng thư mục migration.
2. Với cơ sở dữ liệu mới, đăng ký tài khoản USER và kiểm tra đăng nhập ngay. Kiểm tra đăng nhập bằng email và số điện thoại, rồi đổi mật khẩu bằng mật khẩu hiện tại. Khi cần cấp quyền ADMIN, thực hiện theo [README](../README.md). Admin cấu hình phí COD, ngưỡng kho và voucher hành trình, sau đó nhập danh mục, sản phẩm và tồn kho thực tế.
3. Chạy frontend, mở trang chủ bằng Chrome và chọn Khám phá 3D hoặc Cửa hàng. Dùng tài khoản USER để kiểm tra ghi nhận khám phá, hồ sơ, giỏ hàng và checkout COD. Dùng tài khoản ADMIN để kiểm tra dashboard, đơn hàng, cập nhật nội dung và nhật ký. Trang kiểm tra kết nối ở `/he-thong`.
4. Kiểm tra ở kích thước desktop, tablet, điện thoại. Thử chế độ 2D và quay lại cảnh 3D trên trình duyệt có WebGL 2.

Bàn giao từng phần qua các tệp đã cập nhật trong workspace và báo cáo kiểm tra. Chỉ tạo ZIP khi người dùng yêu cầu rõ ràng.

### Quản lý nhiệm vụ

Ở `/quan-tri/noi-dung`, Admin tạo nhiệm vụ, chọn một điều kiện và khai
báo phần thưởng. Nếu có voucher, mở nhiệm vụ vừa tạo để nhập thời hạn,
giá trị và lượt cấp. Có thể bật/tắt nhiệm vụ hoặc đổi tên, mô tả sau khi
tạo. Điều kiện và loại phần thưởng gắn với nhiệm vụ lúc tạo. Tài khoản
USER mở bảng **Hành trình** ở `/kham-pha` để theo dõi và thử nhận
voucher đang chờ. Huy hiệu và mã voucher đã cấp xuất hiện trong hồ sơ.

### Đo FPS ngày 14/11/2026

Máy Windows người dùng chọn, GPU Intel UHD Graphics (D3D11), Chrome
headless 157.0.8103.52, canvas 1.262 × 704. Mỗi cảnh được đo 7 giây sau
khi tải. Hệ Mặt Trời chạy mô phỏng toàn cảnh. Ba khu vực còn lại xoay
bằng phím mũi tên. Số khung tính theo lượt trình duyệt có lệnh vẽ WebGL.

| Cảnh | FPS trung bình | Khoảng khung p95 |
|---|---:|---:|
| Hệ Mặt Trời | 144,3 | 7,1 ms |
| Thiên hà | 58,9 | 21,0 ms |
| Tinh vân | 144,0 | 7,1 ms |
| Hố đen | 65,4 | 20,9 ms |

Thiên hà thấp hơn mục tiêu tham khảo 60 FPS trên máy phổ thông. Kết
quả áp dụng cho cấu hình và kích thước đo trên.

## Sao lưu và khôi phục MySQL

Sao lưu khi hệ thống không chạy migration hay thay đổi cấu trúc bảng. Dùng tài khoản được cấp quyền đọc cần thiết. Tệp SQL có dữ liệu cá nhân và cần lưu ở nơi giới hạn truy cập.

```powershell
mysqldump.exe --host=127.0.0.1 --user=backup_user --password --single-transaction --no-tablespaces --result-file=galaxora-backup.sql galaxora
mysql.exe --host=127.0.0.1 --user=restore_user --password galaxora_khoi_phuc -e "SOURCE galaxora-backup.sql"
```

Tạo schema `galaxora_khoi_phuc` trống với `utf8mb4` trước khi chạy lệnh thứ hai. Đối chiếu số dòng ở các bảng đơn hàng, tồn kho, lịch sử XP, phần thưởng và lịch sử Flyway. Trỏ bản chạy thử tới schema vừa khôi phục để kiểm tra các luồng đọc. Nếu có tệp mô hình sản phẩm đã tải lên, sao lưu thư mục kho tệp cùng phiên bản database tương ứng.

## Đưa lên hosting

1. Build JAR với `mvn package` và frontend với `npm ci`, `npm run build`.
2. Đặt JAR, thư mục migration và kho tệp tải lên trên ổ lưu trữ bền vững. Chạy JAR với biến môi trường database. Giữ cấu trúc thư mục trong README hoặc đặt `SPRING_FLYWAY_LOCATIONS` trỏ tới thư mục migration thật.
3. Phục vụ `frontend/dist` qua HTTPS. Chuyển `/api/` tới Spring Boot cùng origin, trả `index.html` cho đường dẫn giao diện và phục vụ các tệp GLB như tệp tĩnh. Đặt `GALAXORA_COOKIE_SECURE=true`, URL đặt lại mật khẩu theo tên miền thật và cấu hình sao lưu định kỳ.
4. Dùng cơ sở dữ liệu đã khôi phục ở môi trường thử để kiểm tra migration và luồng chính trước khi chuyển lưu lượng.
