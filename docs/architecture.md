# Kiến trúc xác thực tài khoản — GALAXORA

> Bước 3.2. Cập nhật phạm vi xác thực ngày 19/11/2026.

## 1. Cơ sở thiết kế

GALAXORA dùng Spring Security và phiên phía máy chủ qua cookie. Backend xác thực và kiểm tra vai trò `USER` / `ADMIN`. Khách truy cập có thể xem nội dung công khai. Tài khoản tự đăng ký có vai trò `USER`. Mật khẩu được băm bằng BCrypt. Tham chiếu [plan.md](../plan.md) mục 2.4, 4 và 15, cùng [yêu cầu tài khoản](requirements.md#41-tài-khoản-và-hồ-sơ).

## 2. Luồng xác thực

| Luồng | Xử lý dự kiến |
|---|---|
| Đăng ký | Backend kiểm tra họ tên, email, số điện thoại và mật khẩu. Email và số điện thoại phải duy nhất. Tài khoản mới có vai trò USER và đăng nhập được ngay. |
| Đăng nhập | Người dùng nhập email hoặc số điện thoại cùng mật khẩu. Backend kiểm tra mật khẩu, trạng thái khóa và giới hạn tần suất. Nếu xác thực thành công, backend đổi ID phiên và áp dụng quyền theo vai trò lưu ở máy chủ. Frontend mở `/quan-tri` cho phiên có quyền ADMIN hoặc Trang chủ cho USER. Thông báo lỗi nhạy cảm dùng nội dung chung. |
| Đăng xuất | Frontend gửi yêu cầu thay đổi trạng thái kèm CSRF token. Backend hủy phiên và xóa cookie phiên. |
| Đổi mật khẩu | Người dùng đang đăng nhập cung cấp mật khẩu hiện tại và mật khẩu mới. Backend kiểm tra mật khẩu hiện tại, lưu bản băm BCrypt và hủy phiên đang dùng. Các phiên khác mất hiệu lực khi gọi API lần tiếp theo. |

Tài liệu kỹ thuật về [quản lý phiên Spring Security 6.5](https://docs.spring.io/spring-security/reference/6.5/servlet/authentication/session-management.html).

## 3. Quyết định thiết kế

| Hạng mục | Thiết kế | Trạng thái |
|---|---|---|
| Nơi lưu và thời hạn session | Dùng bộ nhớ của Tomcat nhúng cho cấu hình một máy chủ. Phiên hết sau 30 phút không hoạt động và mất khi máy chủ khởi động lại. Cần đánh giá lại nơi lưu phiên khi triển khai nhiều máy chủ hoặc cần giữ phiên qua lần khởi động lại. | Đã chọn theo ủy quyền của người dùng |
| Cookie và CSRF | Cookie phiên `HttpOnly`, `SameSite=Lax`, `Secure` trên HTTPS. Frontend và API cùng origin. Frontend lấy CSRF token qua API, giữ trong bộ nhớ và gửi qua header do Spring Security chỉ định cho các yêu cầu thay đổi trạng thái, kể cả đăng nhập và đăng xuất. Tải token mới sau khi đăng nhập, đăng xuất hoặc khi phiên hết hạn. | Cấu hình đi kèm phương án phiên đã chọn |
| Kênh gửi email | GALAXORA đã bỏ kênh gửi email. Khôi phục mật khẩu đã quên và tự đổi thông tin liên hệ chờ quy trình xác minh mới. | Người dùng duyệt ngày 19/11/2026 |

Tài liệu kỹ thuật về [CSRF trong Spring Security 6.5](https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html).

## 4. Điều kiện trước khi triển khai

- Cần chốt riêng cách xác minh để khôi phục mật khẩu hoặc đổi email, số điện thoại (TK-05, TK-06).
- Dùng [data dictionary của phân hệ tài khoản](database.md) trước khi viết migration Flyway. Vẽ ERD sau khi hoàn thiện website theo [plan.md](../plan.md#141-nguyên-tắc).
