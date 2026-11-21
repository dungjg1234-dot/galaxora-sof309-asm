# AGENTS.md — Quy tắc bắt buộc cho AI Coding Agent (GALAXORA)

> Nguồn: `plan.md` mục 1.1, 13.3 và 20. Nếu có mâu thuẫn, `plan.md` là căn cứ; hãy dừng phần bị ảnh hưởng và hỏi người dùng.

## 0. Quy trình bắt đầu MỖI phiên làm việc

1. Đọc `plan.md`, file này và `docs/development-roadmap.md`.
2. Đọc các file trong `docs/` liên quan đến việc sắp làm.
3. Nói lại cho người dùng: dự án đang ở giai đoạn nào, việc nào đã xong, việc nào chờ duyệt, việc tiếp theo là gì.
4. Nêu mục tiêu, file dự kiến tạo/sửa và tiêu chí hoàn thành của bước sắp làm. **Chờ người dùng xác nhận rồi mới làm.**

## 0.1. Quy trình kết thúc MỖI phiên làm việc

1. Cập nhật `docs/development-roadmap.md`: trạng thái giai đoạn, việc đã làm, mục chờ duyệt, giả định tạm, mục còn mở.
2. Báo cáo trung thực những gì đã làm và đã kiểm tra thực tế.
3. Bàn giao qua các tệp đã cập nhật trong workspace. Chỉ tạo tệp ZIP khi người dùng yêu cầu rõ ràng.

## 1. Nguyên tắc làm việc

1. Không tự ý thay đổi yêu cầu đã chốt, công nghệ đã được phê duyệt hoặc quy tắc nghiệp vụ.
2. Không tự thêm, bớt hoặc đổi tính năng; muốn thay đổi phải hỏi người dùng trước.
3. Không tự ý xóa, thay thế hoặc viết lại chức năng đang hoạt động; không viết lại cả module khi chỉ cần sửa một lỗi.
4. Không tự tạo dữ liệu giả để thay thế dữ liệu thật; không tạo endpoint giả hoặc nút bấm không có chức năng để trình diễn như tính năng thật.
5. Yêu cầu chưa rõ hoặc mâu thuẫn: ghi nhận và hỏi xác nhận trước khi triển khai.
6. Làm theo từng giai đoạn nhỏ; hoàn thành, kiểm thử và được người dùng nghiệm thu rồi mới chuyển tiếp.
7. Không thay đổi UI/UX ngoài phạm vi yêu cầu được giao.
8. Không đánh dấu hoàn thành nếu còn lỗi build, lỗi test hoặc chức năng chưa hoạt động. Nếu chưa chạy được test, nói rõ lý do; không tuyên bố đã kiểm thử thành công.

## 2. Các mục chưa chốt

Các mục ở `plan.md` mục 19.2 **không được tự suy đoán**. Có thể tiếp tục thiết kế phần không phụ thuộc chúng, nhưng phải ghi rõ là giả định (trong roadmap) và không khóa cứng giá trị chưa được duyệt (nên là cấu hình do Admin thiết lập).

## 3. Công nghệ

Stack đề xuất (xem `plan.md` mục 13): Java + Spring Boot 3, Spring Security, Spring Data JPA, Maven, React + TypeScript + Vite, Tailwind CSS, Three.js + React Three Fiber + Drei, GSAP, MySQL 8, Flyway.

Trạng thái phê duyệt stack xem trong `docs/development-roadmap.md`. Không đổi stack, database hoặc naming convention nếu chưa có quyết định của người dùng.

Kiến trúc: Modular Monolith, không dùng microservices. Frontend không truy cập trực tiếp database. API phải xác thực, phân quyền và validate ở backend.

## 4. Naming

- Tên bảng, cột, identifier: tiếng Việt **không dấu**.
- Database: `snake_case`, chữ thường (ví dụ `nguoi_dung`, `don_hang`).
- Java class: PascalCase (ví dụ `NguoiDung`, `SanPhamController`). Biến/phương thức: camelCase.
- Tên hiển thị UI, tài liệu, comment: tiếng Việt **có dấu**.
- Không trộn Anh–Việt tùy tiện trong một identifier; giữ nguyên tên chuẩn bắt buộc của framework/API/thư viện.

## 5. Quy tắc nghiệp vụ và backend

- Frontend **không** được quyết định: giá, quyền, XP, tồn kho, kết quả duyệt hủy. Backend tính lại và kiểm tra tất cả.
- Quyền kiểm tra ở backend; ẩn nút trên frontend không phải là bảo mật.
- Các thao tác ảnh hưởng đồng thời đến đơn hàng, kho, XP, voucher: dùng transaction, có cơ chế chống thực hiện lặp.
- Tiền dùng `DECIMAL`, không dùng `FLOAT`.
- Dùng migration có kiểm soát (Flyway); không sửa schema production thủ công không ghi nhận.
- Không đưa secret, mật khẩu, API key, thông tin môi trường vào Git.

## 6. Tài liệu phải cập nhật cùng thay đổi

- Thay đổi database → cập nhật ERD / data dictionary (`docs/database.md`).
- Thay đổi API → cập nhật `docs/api-specification.md` và kiểm tra frontend tương thích.
- Thay đổi nghiệp vụ kho/đơn hàng/XP/voucher → bổ sung hoặc cập nhật test.

## 7. 3D và asset

- Kiểm tra license của asset trước khi tích hợp; ghi metadata vào `docs/3d-assets.md`.
- Ưu tiên `.glb`/`.gltf`. Không tải tất cả model lớn ngay khi mở trang; tối ưu tài nguyên 3D.
- Hiệu ứng giao diện không được làm giảm khả năng truy cập, độ ổn định, hiệu năng; hỗ trợ `prefers-reduced-motion` và fallback khi không có WebGL.

## 8. Vị trí thư mục

| Thư mục | Nội dung |
|---|---|
| `docs/` | Tài liệu dự án |
| `docs/usecase/` | Ảnh sơ đồ use case |
| `backend/`, `frontend/` | Mã nguồn |
| `database/migrations/`, `database/seed-data/` | Migration, dữ liệu khởi tạo |
| `assets/images/`, `assets/textures/` | Ảnh, texture dùng chung |
| `assets/3d-models/` | Model 3D của sản phẩm cửa hàng |
| `assets/3d/` | Tài nguyên 3D khu vực khám phá vũ trụ |
