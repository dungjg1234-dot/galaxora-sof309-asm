# Development Roadmap — GALAXORA

> Cập nhật lần cuối: 19/11/2026
> **AI Coding Agent: đọc file này đầu tiên mỗi phiên làm việc, và cập nhật lại cuối phiên.**

## 1. Trạng thái hiện tại

**Assignment Kiểm thử nâng cao (Java) — Y1: Hoàn thành** (19/11/2026, theo xác nhận của người dùng). Backend đã đóng gói thành công, chạy trên cổng `8080` và kết nối MySQL với schema `galaxora` phiên bản `8`. Frontend đã build thành công và chạy trên cổng `5173`. Trang chủ và API sức khỏe qua frontend đều trả HTTP `200`, với trạng thái API `HOAT_DONG`. Lượt xác nhận này gồm build, khởi động và kiểm tra kết nối frontend–backend. Kết quả kiểm thử từng luồng giao diện chưa được ghi nhận lại trong lượt này.

**Giai đoạn 3–7 đã nghiệm thu. Giai đoạn 8–9 đã bổ sung nhiệm vụ cấu hình được và qua kiểm thử tích hợp, chờ nghiệm thu. Giai đoạn 10 đã kiểm tra khôi phục MySQL và đo FPS trên máy Windows hiện tại, chờ nghiệm thu cục bộ.**
Ngày 11/11/2026 người dùng duyệt `plan.md` 1.3 và `requirements.md` 0.3, rồi quyết định **hoàn thiện website trước, vẽ ERD sau**.

Ngày 18/11/2026, người dùng bỏ yêu cầu xác minh email khi đăng nhập và yêu cầu bàn giao từng phần qua workspace. Chỉ tạo ZIP theo yêu cầu rõ ràng.

Ngày 19/11/2026, người dùng duyệt bỏ toàn bộ chức năng gửi email. Bản `plan.md` 1.4 thay thế các quyết định trước về Brevo, xác minh và đặt lại mật khẩu qua email. Các mục ghi triển khai hoặc kiểm thử email trước đó là lịch sử.

Đợt hai của kiểm toán đã chuyển toàn bộ thao tác ghi của Admin sang nhật ký trong giao dịch và giảm số khung dựng khi cảnh 3D đứng yên hoặc khuất. Chờ nghiệm thu giao diện và đo FPS trên thiết bị mục tiêu. Hàng bán thực tế vẫn cần dữ liệu giá và tồn kho do người dùng cung cấp. Đợt ba giảm dung lượng tải của 15 mô hình khám phá từ 581,68 xuống 368,23 MiB bằng nén không mất dữ liệu. Đợt bốn bổ sung độ phân giải thích ứng cho các cảnh Khám phá.

Đợt UI Trang chủ ngày 17/11 đã triển khai theo Q1–Q13, qua build và được Dũng xác nhận làm chuẩn đồng bộ. Kiểm tra với backend và dữ liệu thật vẫn là mục cần thực hiện. Cảnh hero dùng tài nguyên sẵn có nên còn khác ảnh mẫu.

## 2. Bảng giai đoạn

Trạng thái: `Chưa làm` · `Đang làm` · `Chờ duyệt` · `Đã duyệt`

| # | Giai đoạn | Kết quả cần có | Trạng thái |
|---|---|---|---|
| 1 | Phân tích | Bộ yêu cầu được duyệt | Đã duyệt `plan.md` 1.3 và `requirements.md` 0.3 (11/11/2026); mục khác còn "Chờ duyệt" ở mục 3 |
| 2 | Thiết kế | ERD, data dictionary, kiến trúc, design system | ERD vật lý có trang tổng quát V1–V5 và trang bổ sung V6. Bộ 4 Activity Diagram đã vẽ, chờ nghiệm thu sơ đồ |
| 3 | Nền tảng | Đăng ký / đăng nhập / phân quyền | Đã duyệt ngày 12/11/2026 (các bước 3.1–3.5) |
| 4 | Cửa hàng | Cửa hàng hoạt động | Đã duyệt ngày 12/11/2026. Kiểm thử tạo sản phẩm, tìm kiếm và WebGL trên Chrome đạt |
| 5 | Kho | Quản lý kho đúng nghiệp vụ | Đã duyệt ngày 12/11/2026. Kiểm thử nhập kho và đối chiếu số tồn trên Chrome đạt |
| 6 | Đơn hàng | Luồng mua hàng hoàn chỉnh | Đã duyệt ngày 12/11/2026. Kiểm thử đặt COD, giao thành công, duyệt hủy và giao thất bại trên Chrome đạt |
| 7 | 3D | Khu vực 3D có tương tác | Đã nghiệm thu chung cả bốn khu vực ngày 12/11/2026 |
| 8 | Gamification | Nhiệm vụ, XP, huy hiệu, voucher | Đã triển khai luồng khởi đầu và tiến độ nhiệm vụ cấu hình được. Kiểm thử thưởng một lần và quyền nhận voucher đạt, chờ nghiệm thu |
| 9 | Quản trị | Admin vận hành được hệ thống | Đã bổ sung quản lý nhiệm vụ, huy hiệu và voucher theo phạm vi đã chốt, chờ nghiệm thu |
| 10 | Hoàn thiện | Bản nghiệm thu | Đã cập nhật tài liệu, thử khôi phục MySQL, đo FPS và kiểm thử bản đã bỏ gửi email. Chờ nghiệm thu cục bộ |

## 3. Chi tiết Giai đoạn 1 (các mục còn chờ duyệt)

| Hạng mục | Trạng thái |
|---|---|
| Cấu trúc thư mục dự án | Chờ duyệt |
| `AGENTS.md` | Chờ duyệt |
| `docs/development-roadmap.md` | Chờ duyệt |
| `plan.md` bản 1.3 (bản 1.1: Pluto, Mặt Trăng, zoom, khách truy cập; bản 1.2: 9 quyết định ngày 10/11; bản 1.3: quyết định ngày 11/11, bỏ lịch sử xem gần đây, sửa ví dụ nhiệm vụ) | Đã duyệt (11/11/2026) |
| Ảnh use case trong `docs/usecase/` (5 ảnh) | Chờ duyệt (đã nhận, đã nhúng vào `requirements.md` mục 3) |
| `docs/requirements.md` (bản 0.3) | Đã duyệt (11/11/2026) |
| `README.md` | Chờ duyệt (tạo ở bước 3.1, hướng dẫn chạy backend/frontend) |

`architecture.md` ghi thiết kế xác thực. `database.md` ghi đặc tả dữ liệu, sơ đồ quan hệ chính và cách kiểm thử migration. `api-specification.md` ghi hợp đồng API. `3d-visual-spec.md` và `3d-assets.md` mô tả các khu vực Khám phá. `design-system.md` ghi quy tắc giao diện đang dùng.

### Chi tiết Giai đoạn 3 (đã nghiệm thu)

| Bước | Nội dung | Trạng thái |
|---|---|---|
| 3.1 | Khung backend (Spring Boot 3.5.13, Java 21, Maven) và frontend (Vite, React, TypeScript, Tailwind) | Kiểm tra đạt. Đã nghiệm thu ngày 12/11/2026 |
| 3.2 | Thiết kế xác thực tài khoản | Đã chốt phiên, quy tắc mã và Brevo SMTP theo [architecture.md](architecture.md) |
| 3.3 | Đặc tả dữ liệu tài khoản trước migration | Người dùng đã nghiệm thu [database.md](database.md) |
| 3.4 | Đợt triển khai migration Flyway, đặc tả API và backend xác thực | Migration và backend đã qua kiểm thử tích hợp. Đã nghiệm thu ngày 12/11/2026 |
| 3.5 | Đợt giao diện xác thực và kiểm tra Brevo | Đã nghiệm thu ngày 12/11/2026. Đã kiểm tra giao diện, xác minh bằng mã Brevo, đặt lại qua link Brevo và đăng nhập bằng mật khẩu mới |

**Đã kiểm tra thực tế ngày 11/11/2026:**

- `mvn test` biên dịch backend bằng JDK 21 và chạy 2 bài test MVC. Kết quả 2 đạt, 0 lỗi, 0 bỏ qua.
- `npm run build` kiểm tra kiểu TypeScript và build production thành công.
- `mvn spring-boot:run` khởi động Tomcat nhúng 10.1.53 trên cổng `8080`. Nhật ký Hikari và Flyway xác nhận kết nối MySQL 8.4.13 với database `galaxora`. Thời điểm kiểm tra 3.1 chưa có migration SQL.
- Trang Vite trên cổng `5173` trả HTTP 200. `GET /api/he-thong/suc-khoe` trả HTTP 200 và `{"trangThai":"HOAT_DONG"}` cả khi gọi trực tiếp backend lẫn khi đi qua proxy Vite. Lần này kiểm tra qua HTTP.

**Kết quả kiểm tra đợt 3.4 ngày 12/11/2026:**

- `mvn package` thành công. Sáu bài test đạt, gồm bốn bài tích hợp trên MySQL 8.4.13. V1 áp dụng một lần, lần chạy kế tiếp áp dụng 0 migration. Đã kiểm tra khóa ngoại, UNIQUE và CHECK.
- Đã kiểm tra đăng ký, mã hết hạn, giới hạn nhập sai, gửi lại mã, đăng nhập bằng email hoặc số điện thoại, phân quyền, CSRF, đăng xuất và hai yêu cầu đồng thời dùng cùng token đặt lại mật khẩu.
- Chạy JAR trên Tomcat nhúng với MySQL kiểm thử. HTTP trả `200` cho health và CSRF, `401` khi đọc phiên chưa đăng nhập, `403` khi thiếu CSRF, `503` khi đăng ký thiếu cấu hình gửi email. `npm run build` của frontend thành công.
- Email kiểm thử đi qua bộ nhận thư trong test. Máy chưa có thông tin SMTP, địa chỉ gửi, khóa HMAC hoặc URL đặt lại mật khẩu. Chưa gửi email thật. Đã dọn schema và tài khoản MySQL kiểm thử tạm.

**Tiêu chí hoàn thành Giai đoạn 1:** người dùng duyệt `requirements.md`; mọi mục chưa chốt được ghi rõ là giả định.

**Kết quả kiểm tra đợt 3.5:**

- `npm run build` thành công với các màn hình xác thực. Trang kiểm tra hệ thống vẫn mở được từ `/`.
- Chrome headless đi qua Vite, backend Spring Boot và schema MySQL kiểm thử. Đã kiểm tra đăng ký, chờ 60 giây rồi gửi lại mã, từ chối mã cũ, xác minh, đăng nhập bằng email và số điện thoại, tải lại phiên, đăng xuất và đặt lại mật khẩu. Đã kiểm tra mở link thiếu token, xóa fragment chứa token khỏi thanh địa chỉ và bố cục ở chiều rộng 320 px.
- Backend gửi thư tới máy chủ SMTP kiểm thử cục bộ qua STARTTLS. Đã nhận hai mã và một link đặt lại trong kiểm thử.
- Sau khi người dùng xác minh và cho phép tạo khóa, đã tạo khóa SMTP riêng `GALAXORA Local` hết hạn 12/12/2026. Khóa được mã hóa bằng DPAPI cho tài khoản Windows và script mở môi trường nạp từ LocalAppData. Khóa HMAC và URL đặt lại cục bộ cũng đã được cấu hình.
- Gmail đã nhận thư xác minh và thư đặt lại mật khẩu do backend gửi qua Brevo. Máy chủ thử đầu tiên dừng trước khi xác minh được mã thật. Thư đặt lại được kiểm tra bằng tài khoản kiểm thử đã xác minh trong schema tạm riêng. Link trong Gmail mở đúng form và frontend bỏ token khỏi thanh địa chỉ. Việc nhập mã thành công và đổi mật khẩu đã được kiểm tra riêng với SMTP cục bộ.
- Lượt kiểm tra bổ sung đã được người dùng xác nhận. `npm run build` và `mvn -o package` thành công. Lượt chạy riêng `mvn -o test` trên schema MySQL tạm đạt 6/6 bài, gồm 4 bài tích hợp. Schema của bộ test đã được dọn. Ứng dụng chạy từ JAR đã áp dụng Flyway V1 trên một schema tạm riêng khác và API kiểm tra sức khỏe trả `HOAT_DONG` qua Vite. Máy Windows kết nối được tới cổng SMTP 587 của Brevo.
- Đã đăng ký tài khoản qua API đi qua Vite, dùng hộp thư Gmail đã chỉ định. Người dùng nhập mã xác minh trên trang thử. MySQL ghi nhận tài khoản đã xác minh và mã đã dùng. API trả `200` khi đăng nhập bằng tài khoản vừa xác minh và `204` khi đăng xuất. API quên mật khẩu trả `202` và MySQL ghi nhận token đặt lại đã phát hành.
- Đã yêu cầu gửi lại một lần khi chưa tìm thấy thư đặt lại. API trả `202` và MySQL ghi nhận token mới thay token trước. Nhật ký Brevo ghi cả hai thư đặt lại ở trạng thái `Delivered` lúc 12:55 và 13:00. Hai thư nằm trong cùng hội thoại Gmail, và Gmail xếp thư 13:00 vào Spam. Đã mở link trong thư 13:00 trên máy tính chạy GALAXORA. Người dùng lưu mật khẩu mới và xác nhận đăng nhập lại thành công. Đã đối chiếu MySQL ghi token đặt lại đã dùng và trình duyệt hiển thị trang **Tài khoản của bạn**. Đã dừng ứng dụng thử và dọn schema cùng tài khoản MySQL tạm.

### Chi tiết Giai đoạn 4–6 (đã nghiệm thu)

- Cửa hàng có danh mục và sản phẩm do Admin quản lý, tìm kiếm, sắp xếp, phân trang, xem nhanh, chi tiết, yêu thích, đánh giá và trình xem 3D tải theo yêu cầu.
- Kho có phiếu nhập, xuất và điều chỉnh có lý do, cảnh báo theo ngưỡng chung, lịch sử, tồn thực tế, đang giữ và khả dụng.
- Đơn hàng có giỏ, báo giá và checkout COD, voucher do Admin quản lý, lịch sử trạng thái và yêu cầu hủy. Hàng được giữ khi Admin xác nhận. Admin cần xử lý yêu cầu hủy đang chờ trước khi chuyển sang Đang giao. Duyệt hủy sẽ giải phóng lượng hàng đã giữ.
- `mvn -o package` đạt 9/9 bài trên MySQL 8.4.13, gồm kiểm thử hai đơn cùng xác nhận khi kho chỉ đủ cho một đơn. Đã kiểm thử giao thành công, giao thất bại, hủy có duyệt, lưu lịch sử khi sửa đánh giá và chống tạo trùng phiếu nhập hoặc đơn.
- `npm run build` thành công. Vite cảnh báo riêng gói trình xem 3D tải theo yêu cầu lớn hơn 500 kB. Đã kiểm tra HTTP qua Vite với JAR và schema MySQL riêng. Đăng nhập, phân quyền, tạo sản phẩm, nhập hàng, đặt và giao COD, đánh giá, tải lên và phục vụ glTF sau duyệt đều đạt.
- Lượt kiểm thử ngày 12/11/2026 chạy Chrome headless với Playwright và schema MySQL riêng. Admin tạo danh mục, sản phẩm, nhập 8 sản phẩm, cấu hình phí COD, tải lên và duyệt một glTF thử. Khách tìm theo SKU, mở xem nhanh, mở mô hình WebGL và xoay được mô hình.
- Qua giao diện, đơn COD đầu giữ 2 sau khi Admin xác nhận và giảm tồn thực tế từ 8 xuống 6 khi giao thành công. Đơn thứ hai tiếp tục giữ 2 khi yêu cầu hủy đang chờ, rồi nhả đủ 2 khi Admin duyệt. Đơn giao thất bại nhả đúng 1 và tồn thực tế vẫn là 6. Không ghi nhận lỗi JavaScript trong lượt đạt.
- WebGL được kiểm tra bằng mô hình tam giác thử và bộ dựng hình phần mềm của Chrome. Mô hình thương mại của sản phẩm cần do người dùng cung cấp và xác nhận giấy phép. Đã dừng máy chủ thử và dọn schema tạm.

### Chi tiết Giai đoạn 7

- Đã kiểm kê 15 gói glTF gốc người dùng cung cấp. Các gói có tệp giấy phép CC BY 4.0, thông tin tác giả và đủ tài nguyên được tham chiếu. [Danh mục tài nguyên](3d-assets.md)
- Đợt Hệ Mặt Trời có trang công khai `/kham-pha`, mô phỏng quỹ đạo, camera xoay, thu phóng và bám thiên thể, điều chỉnh tốc độ, bảng số liệu NASA và chế độ 2D. Bản tổng quan tải khoảng 228 KiB ảnh bề mặt. 11 GLB chi tiết từ mô hình gốc có tổng dung lượng khoảng 4,51 MiB và được tải từng tệp theo lựa chọn. [Đặc tả trải nghiệm](3d-visual-spec.md)
- `npm run build` đạt. Đã kiểm tra bản build qua Vite Preview trên Chrome headless với WebGL dựng hình phần mềm. Cả 11 GLB tải thành công khi chọn, không phát sinh yêu cầu GLB lúc mở toàn cảnh. Đã kiểm tra chọn trực tiếp trên cảnh, đổi góc nhìn, tốc độ, tạm dừng, giảm chuyển động và giao diện 2D ở 390 px khi WebGL 2 không khả dụng. Vite báo gói Three.js tải theo yêu cầu lớn hơn 500 kB. Chưa đo FPS trên thiết bị đích.

- Người dùng đã xác nhận nghiệm thu đợt Hệ Mặt Trời ngày 12/11/2026.
- Đã tích hợp Thiên hà xoay/thu phóng, Tinh vân 360° và Hố đen xoay/thu phóng với mô hình gốc đã tối ưu. Ba trang có dữ liệu cơ bản từ NASA, ghi công CC BY 4.0 và ảnh 2D dự phòng chụp từ cảnh. [Đặc tả ba khu vực](3d-visual-spec.md)
- `npm run build` đạt. Bản build được kiểm tra trên Chrome headless với WebGL dựng hình phần mềm. Đã kiểm tra từng khu vực chỉ tải một GLB, xoay bằng kéo và bàn phím, thu phóng ở Thiên hà/Hố đen, đặt lại góc nhìn, liên kết NASA và giấy phép. Cả ba ảnh 2D hiển thị ở 390 px khi tắt WebGL, và Hố đen chuyển sang 2D khi mất ngữ cảnh WebGL. Frontend ở cổng 5173 trả HTTP 200 cho cả bốn đường dẫn. Cảnh báo Vite về gói Three.js tải theo yêu cầu lớn hơn 500 kB vẫn còn. Chưa đo FPS trên thiết bị đích.
- Kiểm thử hồi quy Hệ Mặt Trời đạt cho cả 11 mô hình, thao tác chọn trên cảnh, dữ liệu NASA, góc nhìn, tốc độ, tạm dừng và chế độ 2D ở 390 px.

**Đợt chỉnh theo góp ý nghiệm thu:** Bốn khu vực phủ khung trình duyệt, có nút toàn màn hình, chuyển cảnh giữ nguyên chế độ toàn màn hình và bảng thông tin thu gọn. Đóng 15 gói gốc thành GLB tự chứa. Toàn cảnh mô phỏng vẫn tải nhanh bằng ảnh bề mặt, mục Cảnh gốc mở toàn bộ mô hình Hệ Mặt Trời của tác giả, mỗi cận cảnh tải gói gốc của thiên thể. Sao Thổ khoảng 143 MiB và chỉ tải khi chọn. [Đặc tả bản chỉnh](3d-visual-spec.md)

- Đã đối chiếu byte hình học và từng ảnh, cùng cấu trúc node, vật liệu và hoạt ảnh của cả 15 gói. Trong cùng trình dựng Chrome, so ảnh ở cùng góc nhìn cho Thiên hà, Tinh vân, Hố đen, toàn cảnh Hệ Mặt Trời gốc, Mặt Trời, Trái Đất, Sao Mộc và Sao Thổ. Mức sai khác trung bình theo kênh màu dưới 0,1 trên thang 255.
- Build cuối đạt. Chrome đã tải đủ 11 mô hình chi tiết gốc. Đã kiểm tra vào/thoát toàn màn hình, giữ toàn màn hình qua chuyển khu vực, nút Quay lại/Tiến, chuyển cảnh có hiệu ứng và tắt hiệu ứng theo chế độ giảm chuyển động. Đã kiểm tra bốn khu vực ở chế độ 2D tại 390 px. Cổng 5173 trả HTTP 200 cho cả bốn đường dẫn. Đã sửa cấu hình phục vụ GLB của Vite dev và xác nhận ở cổng 5173 rằng Cảnh gốc, Sao Thổ, Thiên hà, Tinh vân và Hố đen mở được. Mô hình Hố đen nằm trọn trong khung WebGL 390 px. Cảnh báo Vite về gói Three.js và mục đo FPS trên thiết bị đích vẫn còn.

**Đợt chỉnh đồ họa:** Người dùng duyệt tập trung vào chất lượng hiển thị cho cả 15 mô hình, lấy ảnh Sao Thổ trên Sketchfab làm tham chiếu. Dùng phản xạ môi trường mềm, lọc texture theo khả năng GPU, chỉnh phơi sáng Sao Thổ và giảm lóe Trái Đất. Chỉnh hiển thị điểm Thiên hà, vùng sáng Tinh vân và màu Hố đen, chụp lại ba ảnh 2D dự phòng. Các góc nhìn và điều khiển theo đặc tả đã duyệt.

- Build đạt. Chrome tải và chụp đủ 15 mô hình trước/sau ở 1.200 × 760 px. Sau khi tinh chỉnh Trái Đất và Sao Thổ, đã chụp lại hai mẫu trực tiếp trên cổng 5173. Ảnh cận Enceladus dùng góc camera thử để đối chiếu bề mặt và vành với mẫu Sketchfab. Thiết lập ánh sáng và góc máy của trang Sketchfab chưa có dữ liệu để đối chiếu chính xác. Chưa đo FPS trên thiết bị đích.
- Kiểm thử hồi quy trên cổng 5173 đạt cho góc nhìn, tốc độ và tạm dừng Hệ Mặt Trời. Thiên hà, Tinh vân và Hố đen giữ toàn màn hình khi chuyển khu vực, xoay bằng bàn phím và đặt lại góc nhìn. Thu phóng Thiên hà và Hố đen đạt. Cả bốn khu vực hiển thị dự phòng 2D ở 390 px.

**Đợt đối chiếu Thiên hà và Hố đen:** Người dùng gửi hai cặp ảnh và duyệt chỉnh riêng hai cảnh. Đã đối chiếu byte hình học và ảnh texture với ZIP nguồn, rồi chỉnh kích thước điểm sao, quầng theo mật độ cụm và màu phát sáng của đĩa bồi tụ trong trình dựng. Hai ảnh 2D được chụp lại. Có hai bảng ảnh gồm ảnh Sketchfab do người dùng gửi và cặp ảnh GALAXORA chụp trước/sau cùng góc. Bố cục, độ phóng và cấu hình hiển thị của ảnh Sketchfab có khác biệt với khung GALAXORA. Chưa đo FPS trên thiết bị thật.

- Build đạt. Chrome trên cổng 5173 tải hai GLB, xoay, thu phóng, đặt lại camera và vào/thoát toàn màn hình. Hai cảnh đạt kiểm tra ở 390 px với WebGL, WebGL thiếu phần mở rộng bộ đệm HDR giả lập và ảnh 2D khi tắt WebGL. Cảnh báo kích thước gói Three.js của Vite vẫn còn.

- Ngày 12/11/2026, người dùng xác nhận nghiệm thu phần chỉnh Thiên hà và Hố đen.

- Ngày 12/11/2026, người dùng xác nhận nghiệm thu chung cả bốn khu vực và chuyển sang Giai đoạn 8. Tài liệu ở mục 3 và việc cập nhật sơ đồ use case (D1, D2, D13, D14) vẫn chờ người dùng.

### Chi tiết Giai đoạn 8

- Đã triển khai ba chương, ghi lượt khám phá khi mở bảng thông tin, cộng XP và cấp huy hiệu một lần. Vật thể ẩn dùng vé theo phiên. Quyền nhận voucher còn nếu cấu hình thiếu, hết hạn hoặc hết lượt. Voucher cá nhân dùng cơ chế kiểm tra hiện có của checkout. [Đặc tả dữ liệu](database.md) · [Đặc tả API](api-specification.md)

### Chi tiết Giai đoạn 9–10

- Trang quản trị đọc năm chỉ số đã chốt. Đã thêm tìm kiếm và khóa/mở khóa USER, sửa tên và mô tả của nội dung khám phá và nhiệm vụ, quản lý sự kiện, cấu hình voucher hành trình, thông báo và nhật ký lọc theo ngày, thao tác và Admin. Thông báo vận hành dựa trên trạng thái hiện có. Thông báo người dùng đọc lịch sử đơn, yêu cầu hủy, phần thưởng, voucher sắp hết hạn và sự kiện.
- TK-06 xác minh qua email theo lựa chọn đã chốt. Bổ sung đổi mật khẩu bằng mật khẩu hiện tại. Đổi liên hệ hoặc mật khẩu yêu cầu đăng nhập lại. Kiểm thử email sử dụng bộ nhận thư trong test.
- Cập nhật sơ đồ use case D1/D2/D13/D14, sơ đồ quan hệ chính của V1–V5, [tài liệu giao diện](design-system.md) và [hướng dẫn bàn giao](ban-giao.md). Kiểm thử dùng MySQL 8.4.13 riêng. Chrome kiểm tra ở 390, 768 và 1.280 px, gồm trang chủ và liên kết sang cửa hàng. Đã chạy hồi quy tạo sản phẩm, WebGL, nhập kho, checkout COD, giao thành công, giao thất bại và duyệt hủy. Luồng Gamification đạt 250 XP/cấp 3 và cấp voucher khi cấu hình có hiệu lực.
- Admin thấy cảnh báo trong 15 phút sau khi có lượt đăng nhập bị chặn bởi bộ giới hạn hiện có theo tài khoản hoặc IP. Cảnh báo là tín hiệu trong bộ nhớ của một máy chủ và không hiển thị dữ liệu định danh.
- Nhật ký quản trị ghi sau khi API thay đổi trả thành công. Nếu lưu nhật ký thất bại, máy chủ ghi lỗi, còn giao dịch nghiệp vụ đã hoàn tất.
- Lượt Chrome cuối hoàn tất trên bản đóng gói. Bản sao từ lượt này khôi phục khớp 12 bản ghi XP và 4 bản ghi phần thưởng giữa hai schema thử.
- Admin tạo nhiệm vụ theo điều kiện khám phá một thiên thể trong bộ hành trình, hoàn thành một chương hoặc tìm dấu hiệu ẩn. Có thể cấu hình XP, huy hiệu và voucher. Admin sửa tên và mô tả của ba chương theo cấu trúc đã duyệt. Đã kiểm thử phân quyền, cộng XP, nhận huy hiệu, quyền voucher đang chờ và hai yêu cầu nhận voucher đồng thời.

## 4. Quyết định đã ghi nhận trong quá trình làm

| Ngày | Nội dung |
|---|---|
| 06/11/2026 | Người dùng đồng ý bắt đầu Giai đoạn 1 theo stack đề xuất trong `plan.md` mục 13. Việc phê duyệt chính thức stack đã được xác nhận ngày 10/11/2026 (xem các dòng cuối bảng). |
| 06/11/2026 | Thư mục `docs/usecase/` được thêm theo yêu cầu người dùng để chứa ảnh use case (plan gốc chưa có). |
| 10/11/2026 | Người dùng yêu cầu bổ sung **Pluto** vào Hệ Mặt Trời (ghi là hành tinh lùn). Đã ghi vào `plan.md` 1.1 (mục 9.3, 9.8, 19.1). |
| 10/11/2026 | Người dùng yêu cầu thêm chính thức **Mặt Trăng** và việc **click thiên thể thì zoom vào mô hình 3D chi tiết** (áp dụng cho mọi thiên thể trong Hệ Mặt Trời). Đã ghi vào `plan.md` 1.1 (mục 9.3, 9.8, 19.1). |
| 10/11/2026 | Người dùng quyết định **khách truy cập xem được toàn bộ nội dung công khai** như website bán hàng thông thường. Đã ghi vào `plan.md` 1.1 (mục 2.4, 19.1). |
| 10/11/2026 | Người dùng xác nhận bước viết `requirements.md` và yêu cầu nhúng ảnh use case vào đúng mục. |
| 10/11/2026 | Đã nhận thông tin về 15 file zip mô hình 3D từ Sketchfab, giấy phép CC BY 4.0, khoảng 383 MB nén. |
| 06/11/2026 | `plan.md` có hai cách đặt tên thư mục 3D (mục 9.8: `assets/3d/...`; mục 16: `assets/3d-models/`). Tạm dùng cả hai: `assets/3d-models/` cho model sản phẩm cửa hàng, `assets/3d/` cho khu vực khám phá vũ trụ. **Chờ người dùng xác nhận.** |
| 10/11/2026 | **Chốt stack:** duyệt theo `plan.md` mục 13. Thêm công nghệ trong lúc làm được phép nhưng phải đề xuất và được người dùng duyệt trước. |
| 10/11/2026 | **Chốt xác thực:** session phía server qua cookie. |
| 10/11/2026 | **Chốt giỏ hàng:** mỗi người dùng một giỏ hoạt động. |
| 10/11/2026 | **Chốt giao thất bại:** giải phóng ngay hàng đang giữ, đơn kết thúc, không giao lại. |
| 10/11/2026 | **Chốt voucher khi hủy:** không hoàn lại. |
| 10/11/2026 | **Chốt đánh giá:** được sửa tự do; một đánh giá cho mỗi người dùng, sản phẩm và đơn (D9). |
| 10/11/2026 | **Chốt ngưỡng tồn kho:** một ngưỡng chung, một giá trị toàn hệ thống do Admin cấu hình (D8). |
| 10/11/2026 | **Chốt thiếu hàng khi xác nhận:** chặn xác nhận, báo lỗi; đơn giữ `CHO_XAC_NHAN` (D10). |
| 10/11/2026 | **Chốt Admin hủy đơn:** Admin được hủy đơn `CHO_XAC_NHAN`, kèm lý do và audit log. |
| 11/11/2026 | **Quyết định xác minh email (D3) của giai đoạn 3.** Cập nhật ngày 18/11/2026: tài khoản được đăng nhập ngay sau khi đăng ký. Xác minh email theo yêu cầu dùng cho khôi phục mật khẩu và đổi liên hệ. |
| 11/11/2026 | **Chốt D3–D7 (phần plan đã yêu cầu nhưng sơ đồ chưa có):** đăng xuất, thanh toán COD, giao thất bại/xuất bán, thông báo Admin, nhiệm vụ bí mật, quản lý huy hiệu/phần thưởng, Dashboard tổng quan vẫn làm theo plan; không vẽ thêm vào sơ đồ use case. |
| 11/11/2026 | **Chốt D6:** xem nhanh và sản phẩm liên quan thuộc phạm vi; **bỏ** lịch sử xem gần đây (bỏ bảng `san_pham_da_xem` khỏi danh sách bảng dự kiến). |
| 11/11/2026 | **Chốt D11:** ví dụ nhiệm vụ sửa thành "Khám phá đủ 8 hành tinh, Pluto và Mặt Trăng" (plan 10.3); vẫn chỉ là ví dụ minh họa. |
| 11/11/2026 | Người dùng xác nhận: duyệt `plan.md` 1.3 và `requirements.md` 0.3; bắt đầu code dù chưa có `database.md`/`architecture.md`; **hoàn thiện website trước, vẽ ERD sau**; dùng Java 21, package gốc `com.galaxora`, database `galaxora`. |
| 11/11/2026 | **Hoãn D12:** nội dung chọn thiên thể ở Tinh vân/Thiên hà sẽ chốt khi viết `3d-assets.md`. |
| 12/11/2026 | Người dùng giao quyền chọn thiết kế phiên. Chọn bộ nhớ Tomcat cho cấu hình một máy chủ, phiên hết sau 30 phút không hoạt động, kèm cookie và CSRF như mô tả trong `architecture.md`. Người dùng duyệt quy tắc mã xác minh và link đặt lại mật khẩu. |
| 12/11/2026 | Người dùng duyệt Brevo SMTP để gửi email xác minh tài khoản và đặt lại mật khẩu. |
| 12/11/2026 | Người dùng xác nhận dùng data dictionary cho phân hệ tài khoản trước migration Flyway và giữ lịch vẽ ERD sau khi hoàn thiện website. |
| 12/11/2026 | Người dùng duyệt gộp migration, API và backend xác thực thành một đợt. Tra cứu email không phân biệt hoa thường, số điện thoại lưu theo E.164, chặn đăng nhập sau 5 lần thất bại trong 15 phút theo tài khoản và áp dụng thêm giới hạn theo IP. |
| 12/11/2026 | Người dùng yêu cầu triển khai Cửa hàng, Kho và Đơn hàng trong cùng một đợt. Phí COD cố định áp dụng toàn quốc. Khách có thể gửi yêu cầu hủy đến hết trạng thái Đang chuẩn bị. Sửa đánh giá lưu bản trước và giữ trạng thái hiển thị. |
| 12/11/2026 | Người dùng duyệt triển khai Giai đoạn 7–10 theo thứ tự và nghiệm thu từng giai đoạn. **Giai đoạn 7:** Thiên hà xoay/thu phóng toàn cảnh, Tinh vân xem nền 360°, Hố đen xoay/thu phóng mô hình. Ba khu vực có kiến thức cơ bản ưu tiên NASA, có nguồn. D12 đã chốt. |
| 12/11/2026 | **Giai đoạn 8:** 10 XP một lần cho mỗi thiên thể trong 8 hành tinh, Pluto và Mặt Trăng. Khám phá đủ 10 thiên thể nhận huy hiệu khám phá. Hoàn thành các chương Hệ Mặt Trời nhận 100 XP, huy hiệu hành trình và một voucher do Admin cấu hình. Tìm vật thể ẩn nhận 50 XP và huy hiệu bí mật. Backend xác minh và cấp mỗi phần thưởng một lần cho mỗi tài khoản. Bắt đầu cấp 1, tổng XP lên cấp 2–5 lần lượt là 100, 250, 450 và 700. Chi phí mỗi cấp tiếp theo tăng 50 XP so với chặng trước. |
| 12/11/2026 | **Giai đoạn 9:** Dashboard có đơn cần xử lý, yêu cầu hủy, tồn kho thấp, doanh thu đơn đã giao và nhiệm vụ đã hoàn thành. Thay đổi nội dung khám phá và nhiệm vụ có hiệu lực khi Admin lưu. Có hộp thông báo trong website và nhật ký lọc theo thời gian, thao tác, Admin. |
| 12/11/2026 | **Giai đoạn 10:** Nghiệm thu trên máy hiện tại. Kiểm thử Chrome ở kích thước máy tính, máy tính bảng và điện thoại. Bàn giao hướng dẫn đưa lên hosting, hướng dẫn sao lưu MySQL và kiểm tra khôi phục trên cơ sở dữ liệu thử. |
| 12/11/2026 | Người dùng yêu cầu chỉnh Giai đoạn 7 và duyệt một đợt cho cả bốn khu vực. Cảnh phủ khung trình duyệt, có nút toàn màn hình, khung chuyển khu vực có hiệu ứng và bảng thông tin thu gọn. Đối chiếu mô hình với các gói gốc, gồm 11 thiên thể Hệ Mặt Trời. |
| 12/11/2026 | Người dùng duyệt đợt chỉnh đồ họa cho 15 mô hình và dùng ảnh Sao Thổ trên Sketchfab làm mẫu. Phạm vi gồm ánh sáng, vật liệu khi dựng, màu và độ rõ bề mặt. Tiếp tục dùng góc nhìn và cách điều khiển đã duyệt. |
| 13/11/2026 | Người dùng duyệt làm Giai đoạn 8–10 trong một đợt. Chốt ba chương, ghi khám phá khi mở bảng, tìm dấu hiệu ẩn, giữ quyền nhận voucher, đổi liên hệ qua mã email và cập nhật sơ đồ use case. |
| 14/11/2026 | Người dùng xác nhận ba điều kiện tạo nhiệm vụ: khám phá thiên thể, hoàn thành chương, tìm dấu hiệu ẩn. Admin sửa tên và mô tả ba chương, giữ thứ tự và danh sách thiên thể. Phần thưởng gồm XP, huy hiệu và voucher theo cơ chế hiện có. |

## 5. Các mục còn mở (plan.md mục 19.2)

Các mục đã chốt ngày 10/11/2026 (stack, xác thực phiên, giỏ hàng, giao thất bại, voucher khi hủy, đánh giá, ngưỡng tồn kho, thiếu hàng khi xác nhận) đã chuyển sang mục 4. Các mục dưới đây **chưa chốt**; không tự suy đoán.

| Hạng mục | Cần chốt | Mức ảnh hưởng | Trạng thái |
|---|---|---|---|
| Nơi lưu session khi mở rộng | Đã chọn bộ nhớ cho một máy chủ. Đánh giá lại nếu cần nhiều máy chủ hoặc giữ phiên khi khởi động lại | Kiến trúc | Chỉ cần xem lại khi thay đổi yêu cầu triển khai |
| Phí vận chuyển | Giá cố định áp dụng toàn quốc. Admin nhập mức phí trước khi mở checkout | Cấu hình Admin | Phạm vi đã chốt. Giá trị để Admin cấu hình |
| Voucher hành trình | Mức giảm, thời hạn, quota và điều kiện đơn do Admin nhập | Nội dung | Có màn hình cấu hình. Quyền nhận được giữ khi chưa cấp được |
| ERD và data dictionary | [ERD chỉnh sửa](galaxora-erd.drawio) · [Bản xem V1–V5](galaxora-erd.svg) · [Bản xem V6](galaxora-erd-v6.svg) · [Đặc tả dữ liệu](database.md) | Database | Đã đối chiếu bản V1–V5. Trang V6 bổ sung 3 bảng, 22 cột và 3 khóa ngoại. Chờ nghiệm thu ERD |
| Khôi phục tài khoản và đổi thông tin liên hệ | Chọn cách xác minh không dùng email | Nghiệp vụ / Xác thực | Chờ quyết định. Người dùng vẫn đổi được mật khẩu khi đăng nhập và biết mật khẩu hiện tại |
| Nghiệm thu quản lý nhiệm vụ | Admin cấu hình nhiệm vụ và USER theo dõi tiến độ, nhận thưởng trên trình duyệt | Gamification / Quản trị | Phạm vi đã chốt và có kiểm thử tích hợp. Chờ nghiệm thu |
| Hiệu năng 3D | FPS trên máy Windows hiện tại | Hiệu năng | Đã đo cả bốn cảnh. Thiên hà đạt 58,9 FPS so với mục tiêu tham khảo 60. [Kết quả](ban-giao.md#đo-fps-ngày-14112026) |
| Tương thích Flyway với MySQL | Flyway 11.7.2 báo cảnh báo tương thích trên MySQL 8.4.13. Migration V1 đã chạy thành công và chạy lại không áp dụng trùng trên schema kiểm thử | Database | Theo dõi trước triển khai |
| Phiên bản Spring Boot | `plan.md` ghi "Spring Boot 3". Đang dùng 3.5.13 (bản 3.x mới nhất tìm thấy, 26/03/2026). Dòng 3.5 là dòng 3.x cuối; nên kiểm tra thời hạn hỗ trợ. Chuyển sang 4.x là đổi stack, cần người dùng duyệt | Kiến trúc | Chờ người dùng xác nhận |
| Công cụ chưa có trong `plan.md` 13.1 | Linter frontend (template Vite mới dùng oxlint, ESLint là lựa chọn khác), định tuyến frontend (ví dụ React Router), Maven Wrapper. Chưa thêm; cần đề xuất và được duyệt | Công nghệ | Chờ người dùng quyết định |
| Triển khai | Hosting, tên miền và cấu hình production | Hạ tầng | Giai đoạn 10 nghiệm thu cục bộ, có hướng dẫn hosting và kiểm tra khôi phục MySQL |
| Tagline | Có dùng tagline tham khảo không | Nội dung | Chưa chốt |

## 6. Giả định tạm đang dùng

| # | Giả định | Nguồn / trạng thái |
|---|---|---|
| 1 | Giỏ hàng, yêu thích, đặt hàng, đánh giá, nhiệm vụ và XP yêu cầu đăng nhập (khách chỉ xem). | Là mặc định tôi đề xuất, người dùng xác nhận bước mà không chỉnh; đã ghi ở `plan.md` 2.4. Cần xác nhận lại nếu muốn khách thêm giỏ hàng trước khi đăng nhập. |
| 2 | Zoom mô hình chi tiết áp dụng cả cho Mặt Trời (cùng Mặt Trăng, Pluto và 8 hành tinh). | Mặc định đề xuất, người dùng xác nhận bước. |
| 3 | Người dùng giữ 15 ZIP gốc. Workspace có các GLB tự chứa từ 15 gói và ảnh bề mặt nhẹ dùng riêng trong mô phỏng Hệ Mặt Trời. Camera và ánh sáng do GALAXORA dựng. | Số đo, cách đóng gói và ghi công nằm trong `3d-assets.md`. |
| 4 | Giá trị cụ thể của ngưỡng tồn kho do Admin đặt khi vận hành; chưa chọn giá trị mặc định nào và không khóa cứng trong code. | Hệ quả của quyết định 10/11 (một ngưỡng chung); cần hỏi nếu muốn có giá trị khởi tạo. |
| 5 | Phiên trong bộ nhớ, hết sau 30 phút không hoạt động, cookie `HttpOnly`, `SameSite=Lax` và bật `Secure` trên HTTPS bằng cấu hình môi trường. | Đã áp dụng ở backend. |
| 6 | Backend cấp token CSRF theo phiên qua API. | Frontend lấy token mới trước mỗi POST và giữ cookie cùng origin. Đã kiểm tra qua trình duyệt. |
| 7 | Migration Flyway đọc từ `../database/migrations` (chạy backend từ `backend/`); cách đóng gói khi triển khai chưa quyết. | Theo `AGENTS.md` mục 8; đổi khi chốt triển khai. |
| 8 | Endpoint `GET /api/he-thong/suc-khoe` chỉ cho biết backend chạy, không kiểm tra database. Mật khẩu DB bắt buộc đọc từ biến môi trường, không có mặc định. | Tạm; đủ cho khung. |
| 9 | Kiểm tra 3.1 dùng MySQL riêng cho GALAXORA tại `127.0.0.1:3306`, database `galaxora` với `utf8mb4`, JDK 21 và Maven 3.9.9 đi kèm NetBeans. | Cấu hình phát triển cục bộ trên máy người dùng. Biến môi trường được nạp bằng script trong `%LOCALAPPDATA%\GALAXORA\dev`. |
| 10 | Thiết kế phiên trong bộ nhớ dựa trên cấu hình một máy chủ. | Xem lại khi chốt mô hình triển khai nếu cần nhiều máy chủ hoặc giữ phiên qua khởi động lại. |
| 11 | Đặc tả 3.3 dùng thời gian UTC, khóa chính `BIGINT` và một bản ghi mã/token hiện hành cho mỗi tài khoản và mục đích. | Đã nghiệm thu trong `database.md`. |
| 12 | Chọn 30 lượt đăng nhập trong 15 phút cho một IP kết nối tới backend. Bộ đếm nằm trong bộ nhớ của một máy chủ. | Cần cấu hình IP thực từ reverse proxy tin cậy khi triển khai qua proxy. |
| 13 | Lượt kiểm tra Brevo bổ sung dùng cấu hình Gmail/Brevo cục bộ hiện có và schema MySQL tạm riêng. | Chỉ dùng cho kiểm thử 3.5. Cấu hình tên miền gửi thư khi triển khai vẫn còn mở. |
| 14 | Mỗi sản phẩm có một mô hình GLB/glTF tự chứa, tối đa 8 MiB. Ảnh sản phẩm dùng URL HTTPS. | Áp dụng trong Cửa hàng. Kiểm thử với tệp glTF nhỏ. Chưa tích hợp tài nguyên sản phẩm thương mại. |
| 15 | Đơn có yêu cầu hủy đang chờ cần Admin xử lý trước khi chuyển sang Đang giao. | Bảo đảm yêu cầu hủy hợp lệ được xử lý trước khi hết phạm vi cho phép hủy. |
| 16 | Nhiệm vụ đã tạo giữ điều kiện và loại phần thưởng để lịch sử cấp thưởng có nghĩa nhất quán. Admin chỉnh nội dung, bật/tắt nhiệm vụ và cập nhật mẫu voucher. Backend đối chiếu cả lượt khám phá đã ghi trước đó. | Quy ước triển khai trong đợt 14/11, chờ nghiệm thu. |
| 17 | Cấu hình Brevo từng dùng ở mục 13 cần kiểm tra lại trước khi gửi thử. Phiên 16/11 không tìm thấy thư mục cấu hình tại vị trí LocalAppData đã ghi. Hai hộp thư thử do người dùng chỉ định. | Cần nạp cấu hình SMTP/HMAC cục bộ cho lượt gửi thật. |

## 7. Nhật ký phiên làm việc

| Ngày | Đã làm |
|---|---|
| 06/11/2026 | Đọc `plan.md`; đề xuất Giai đoạn 1; tạo cấu trúc thư mục; tạo `AGENTS.md` và roadmap. |
| 10/11/2026 | Đọc zip, `AGENTS.md`, `plan.md`, roadmap; đối chiếu 5 sơ đồ use case và 15 zip mô hình (chỉ liệt kê, đọc license và cấu trúc `scene.gltf`, không giải nén vào dự án). Sửa `plan.md` lên 1.1 (header, 2.4, 9.3, 9.8 thêm `moon/` và `pluto/`, 19.1 thêm hai dòng quyết định). Chép 5 ảnh vào `docs/usecase/`, cập nhật README của thư mục này. Tạo `docs/requirements.md` bản nháp 0.1 (nhúng ảnh ở mục 3.1–3.5). Ghi chú: `plan.md` trong zip nhận được có mốc sửa file 09/11 trong khi tiêu đề là bản 1.0 ngày 05/11; không có bản cũ để so, nên không rõ đã đổi gì. Chưa chạy build/test vì chưa có source code. |
| 10/11/2026 (phiên 2) | Đọc lại toàn bộ tài liệu, đề xuất bước "chốt mục mở ảnh hưởng kiến trúc"; người dùng đồng ý và trả lời 8 câu hỏi (9 quyết định). Sửa `plan.md` lên 1.2 (mục 5.4, 6.4, 6.5, 7.3, 7.4, 8.3, 13, 14.3, 19.1, 19.2, 21), `requirements.md` lên 0.2 (thêm GH-11, PCN-08, PCN-09, D13, D14, mục 8), cập nhật roadmap. Chưa có source code nên không có build/test; đã kiểm tra bằng cách: script thay thế xác nhận mỗi đoạn cần sửa khớp đúng một vị trí, so diff với bản trước, kiểm tra bảng markdown không lệch cột, đọc đối chiếu quyết định giữa `plan.md` 19.1, `requirements.md` mục 8 và roadmap mục 4. |
| 11/11/2026 (phiên 3) | Đọc lại `AGENTS.md`, `plan.md`, roadmap, `requirements.md`, README `docs/usecase/`; đề xuất bước "chốt D3–D7, D11, D12", người dùng đồng ý và trả lời 5 câu hỏi (một câu đổi ý: giữ xác minh email; D12 hoãn). Kết quả: `plan.md` lên 1.3 (bỏ lịch sử xem gần đây ở mục 3.1, 5.2, 5.5 và bảng dữ liệu; sửa ví dụ nhiệm vụ mục 10.3; thêm dòng ở 19.1), `requirements.md` lên 0.3 (CH-04, D3–D7, D11, D12, mục 8.2, mục 9), cập nhật roadmap. Chưa có source code nên không có build/test; đã kiểm tra bằng cách: diff từng file với zip gốc, grep toàn bộ `.md` để chắc không còn chỗ nào coi lịch sử xem là tính năng, đối chiếu quyết định giữa `plan.md`, `requirements.md` và roadmap. Chưa kiểm tra bằng cách mở lại sơ đồ ảnh vì không có thay đổi nào ở ảnh. |
| 11/11/2026 (phiên 4) | Người dùng yêu cầu bắt đầu code; sau trao đổi, chọn "hoàn thiện website rồi vẽ ERD sau" và xác nhận Java 21, `com.galaxora`, `galaxora`. Làm bước 3.1: tạo `backend/` (pom.xml, ứng dụng Spring Boot, `CauHinhBaoMat`, `HeThongController`, `application.yml`, `application-dev.yml`, 1 lớp test), `frontend/` (Vite, React, TypeScript, Tailwind, trang kiểm tra kết nối, service gọi API, proxy `/api`), `README.md`, `.gitignore`. Báo cáo trung thực: frontend build thành công và proxy dev thử đạt; backend **chưa biên dịch/chạy/test được** trong môi trường của tôi (không có Maven, Maven Central bị chặn, không có MySQL), chỉ kiểm tra cú pháp. Không thêm tính năng nghiệp vụ; không thêm công nghệ ngoài `plan.md` 13.1 (gỡ oxlint của template Vite, không thêm router). |
| 11/11/2026 (phiên 5) | Chuẩn bị môi trường Windows theo yêu cầu người dùng. Sau khi được xác nhận bắt đầu kiểm tra bước 3.1, chạy `mvn test` (2 đạt) và `npm run build` (đạt), xác minh Spring Boot khởi động với MySQL thật và API trả `HOAT_DONG` qua Vite. Dừng hai tiến trình ứng dụng dùng để kiểm tra. Cập nhật roadmap và chuẩn bị ZIP bàn giao. Chờ người dùng nghiệm thu. |
| 11/11/2026 (phiên 6) | Người dùng xác nhận bắt đầu bước 3.2. Soạn bản nháp `architecture.md` về đăng ký, xác minh email, đăng nhập, đăng xuất và đặt lại mật khẩu. Đối chiếu với plan, TK-01 đến TK-07, PCN-09 và cấu hình Spring Security hiện có. Đã gửi các lựa chọn về phiên, email và mã xác minh. Chờ người dùng duyệt. Phiên này chỉ thay đổi tài liệu. |
| 12/11/2026 (phiên 7) | Ghi phương án phiên trong bộ nhớ theo quyền lựa chọn người dùng giao. Ghi quy tắc mã và link đã được người dùng duyệt. Đề xuất Brevo SMTP theo tài liệu nhà cung cấp. Đối chiếu trạng thái giữa kiến trúc và roadmap. Chờ duyệt dịch vụ email. Phiên này chỉ cập nhật tài liệu. |
| 12/11/2026 (phiên 8) | Ghi quyết định duyệt Brevo SMTP trong kiến trúc và roadmap. Chốt bước 3.2 ở phạm vi thiết kế. Đối chiếu trạng thái hai tài liệu và đóng gói ZIP bàn giao. Chưa cấu hình SMTP hoặc gửi email thử. |
| 12/11/2026 (phiên 9) | Người dùng xác nhận làm bước 3.3. Viết data dictionary cho người dùng, vai trò, quan hệ người dùng–vai trò, mã xác minh và token đặt lại mật khẩu. Đồng bộ quy tắc data dictionary và lịch vẽ ERD trong plan, kiến trúc và roadmap. Đối chiếu ràng buộc với TK-01 đến TK-08 và thiết kế xác thực đã duyệt. Phiên này chỉ thay đổi tài liệu. |
| 12/11/2026 (phiên 10) | Người dùng nghiệm thu đặc tả bước 3.3. Ghi trạng thái vào đặc tả và roadmap, đề xuất bước 3.4 về migration Flyway. |
| 12/11/2026 (phiên 11) | Triển khai migration V1, API tài khoản và adapter Brevo SMTP. Ghi chuẩn hóa định danh và giới hạn đăng nhập. Chạy `mvn package` với 6 bài test đạt, kiểm tra HTTP trên Tomcat và chạy build frontend. Đối chiếu endpoint với đặc tả API. Kiểm thử MySQL dùng schema tạm đã dọn. Chờ nghiệm thu đợt và cấu hình Brevo để kiểm tra gửi thật. |
| 12/11/2026 (phiên 12) | Người dùng duyệt đợt giao diện xác thực và kiểm tra Brevo. Triển khai màn hình xác thực bằng React, kết nối API và cập nhật đường dẫn link đặt lại. Chạy build và kiểm tra luồng trên Chrome với backend, MySQL và SMTP STARTTLS cục bộ. Xác minh kết nối TCP 587 tới Brevo. Chờ cấu hình gửi email thật. |
| 12/11/2026 (phiên 13) | Người dùng xác minh tài khoản Brevo, duyệt tạo khóa SMTP riêng và chỉ định Gmail đã xác minh để gửi và nhận thử. Tạo và lưu khóa bằng DPAPI, xác minh Gmail nhận hai thư GALAXORA, mở link đặt lại trên trình duyệt. Ghi giới hạn kiểm thử mã thật và tách dữ liệu kiểm thử khỏi schema chính. |
| 12/11/2026 (phiên 14) | Người dùng duyệt lượt kiểm tra Brevo bổ sung cho 3.5 và yêu cầu cập nhật trực tiếp file. Đã build frontend/backend, chạy 6 bài test đạt, khởi động ứng dụng với schema tạm, kiểm tra migration, proxy Vite và kết nối SMTP. Đã gửi đường dẫn kiểm thử trên Chrome cho người dùng. Chưa ghi nhận đăng ký trong schema thử tại thời điểm cập nhật, nên kết quả trọn luồng Brevo vẫn chờ kiểm tra. |
| 12/11/2026 (phiên 15) | Hoàn tất kiểm tra 3.5 trên cùng schema tạm. Đăng ký qua API, xác minh bằng mã Brevo, đăng nhập và đăng xuất qua API, rồi kiểm tra luồng đặt lại qua thư Brevo. Gmail xếp thư mới nhất vào Spam. Đã đối chiếu người dùng đặt mật khẩu mới, MySQL ghi token đã dùng và trình duyệt mở trang tài khoản sau khi đăng nhập lại. Dừng ứng dụng và dọn schema, tài khoản MySQL thử. Chờ nghiệm thu đợt 3.5. |
| 12/11/2026 (phiên 16) | Người dùng xác nhận nghiệm thu đợt 3.5. Đã cập nhật trạng thái trong roadmap dựa trên kết quả kiểm tra ở phiên 15. |
| 12/11/2026 (phiên 17) | Người dùng xác nhận nghiệm thu các mục còn chờ của Giai đoạn 3, gồm bước 3.1 và đợt 3.4. Đã cập nhật trạng thái Giai đoạn 3 dựa trên kết quả kiểm thử đã ghi. |
| 12/11/2026 (phiên 18) | Triển khai giao diện và backend Giai đoạn 4–6 cùng Flyway V2–V3. Đồng bộ plan, yêu cầu, data dictionary, API và hướng dẫn chạy. Kiểm thử MySQL 9/9, build frontend và kiểm thử HTTP trên JAR qua Vite đạt. Đã dừng máy chủ thử và dọn các schema tạm. Chờ kiểm tra trình duyệt và nghiệm thu. |
| 12/11/2026 (phiên 19) | Kiểm thử trọn luồng Giai đoạn 4–6 bằng Chrome với tài khoản Admin, khách hàng và dữ liệu MySQL thử. Đạt các trường hợp tạo sản phẩm, nhập kho, WebGL, đặt COD, giao thành công, duyệt hủy và giao thất bại. Đã dọn schema thử. Chờ nghiệm thu. |
| 12/11/2026 (phiên 20) | Người dùng xác nhận nghiệm thu Giai đoạn 4–6. Đã ghi nhận trạng thái Cửa hàng, Kho và Đơn hàng dựa trên kết quả kiểm thử ở phiên 18–19. |
| 12/11/2026 (phiên 21) | Bắt đầu Giai đoạn 7. Người dùng chọn NASA và cung cấp 15 ZIP mô hình gốc. Kiểm kê, ghi công và tối ưu tài nguyên Hệ Mặt Trời. Triển khai /kham-pha, kiểm thử build và Chrome headless trên bản build. Chờ nghiệm thu đợt Hệ Mặt Trời. |
| 12/11/2026 (phiên 22) | Khởi động lại frontend cục bộ sau lỗi kết nối và xác nhận `/kham-pha` trả HTTP 200. Người dùng xác nhận nghiệm thu đợt Hệ Mặt Trời. Cập nhật roadmap dựa trên kết quả đã ghi ở phiên 21 và đối chiếu thư mục tài nguyên. D12 vẫn mở. |
| 12/11/2026 (phiên 23) | Ghi quyết định duyệt triển khai Giai đoạn 7–10. Chốt D12, tối ưu và tích hợp ba mô hình, thêm ảnh 2D và thông tin NASA, cập nhật đặc tả 3D và README. Build đạt, kiểm tra Chrome cho ba khu vực và hồi quy Hệ Mặt Trời đạt. Xác nhận bốn đường dẫn cục bộ trả HTTP 200. Chờ nghiệm thu Giai đoạn 7. |
| 12/11/2026 (phiên 24) | Thực hiện đợt chỉnh Giai đoạn 7 theo góp ý. Cập nhật giao diện toàn màn hình và khung chuyển khu vực, đóng 15 mô hình gốc thành GLB, hỗ trợ vật liệu specular/glossiness và thay ảnh 2D dự phòng. Kiểm tra build, đối chiếu dữ liệu và ảnh trong Chrome. Chờ nghiệm thu bản chỉnh. |
| 12/11/2026 (phiên 25) | Chỉnh chất lượng hiển thị của 15 mô hình, chụp ảnh trước/sau trong Chrome và chụp cận Enceladus bằng camera thử. Cập nhật ba ảnh 2D dự phòng và tài liệu 3D. Chờ nghiệm thu đợt chỉnh đồ họa. |
| 12/11/2026 (phiên 26) | Đối chiếu ảnh Sketchfab của Thiên hà và Hố đen với ZIP nguồn. Dựng quầng theo cụm điểm sao, hiệu chỉnh màu phát sáng của đĩa và chụp lại hai ảnh dự phòng. Chụp trước/sau trên Chrome để người dùng đối chiếu. Chờ nghiệm thu. |
| 12/11/2026 (phiên 27) | Ghi nhận người dùng nghiệm thu phần chỉnh Thiên hà và Hố đen. Đối chiếu trạng thái trong roadmap. Chờ xác nhận nghiệm thu chung Giai đoạn 7. |
| 12/11/2026 (phiên 28) | Người dùng nghiệm thu chung Giai đoạn 7 và chuyển sang Gamification. Lưu nền tảng hồ sơ XP/cấp độ và ba câu hỏi nghiệp vụ cho phiên sau. Người dùng yêu cầu dừng phiên để tắt máy. |
| 13/11/2026 (phiên 29) | Rà soát plan, roadmap, tài liệu liên quan và tệp mã nguồn hiện có. Xác nhận còn Giai đoạn 8–10 để hoàn thành website theo phạm vi bàn giao cục bộ. Giai đoạn 8 đã có nền tảng hồ sơ, còn ba quyết định nghiệp vụ. Phiên rà soát không chạy build, test hoặc migration. |
| 13/11/2026 (phiên 30) | Người dùng yêu cầu thực hiện Giai đoạn 8–10 trong cùng một đợt. Gửi một lượt câu hỏi về chương, ghi nhận khám phá, nhiệm vụ bí mật, voucher khi chưa cấp được, xác minh đổi thông tin liên hệ và sơ đồ use case. Chờ câu trả lời trước khi triển khai. |
| 13/11/2026 (phiên 31) | Áp dụng sáu quyết định người dùng đã xác nhận. Triển khai nhiệm vụ, XP, huy hiệu, quyền nhận voucher, dashboard, nội dung, sự kiện, thông báo, nhật ký, đổi liên hệ và đổi mật khẩu. Bổ sung trang chủ, Flyway V5, đặc tả API, dữ liệu, sơ đồ D1/D2/D13/D14 và hướng dẫn bàn giao. Maven đóng gói đạt với 13/13 bài test trên MySQL 8.4.13, gồm chống cộng XP trùng khi chạy đồng thời và xác minh đổi liên hệ qua bộ nhận thư kiểm thử. Frontend build đạt, còn cảnh báo gói Three.js lớn. Chrome kiểm tra luồng Hệ Mặt Trời 2D, 250 XP/cấp 3, dấu hiệu bằng bàn phím, nhận voucher, quản trị và các luồng cửa hàng/kho/đơn hàng. Khôi phục schema thử, đối chiếu 11/11 bản ghi XP và 3/3 bản ghi thưởng trong lượt kiểm thử backend cuối. Khởi động localhost bằng schema chính mới, Flyway áp dụng V1–V5 và các trang chính trả HTTP 200. Dữ liệu kiểm thử nằm ở MySQL riêng. Chờ nghiệm thu phần đã triển khai. Còn phạm vi quản lý nhiệm vụ mở rộng, gửi mã đổi liên hệ qua Brevo thật và FPS trên thiết bị đích. |
| 13/11/2026 (phiên 32) | Người dùng xác nhận vẽ ERD vật lý theo Flyway V1–V5. Đã tạo [tệp draw.io](galaxora-erd.drawio) và [bản xem SVG](galaxora-erd.svg). Đã kiểm tra cấu trúc hai tệp, đối chiếu 32 bảng, 202 cột, khóa chính, 38 khóa ngoại và bội số Crow’s Foot với SQL, rồi kiểm tra hiển thị SVG trên Chrome. Chờ nghiệm thu ERD. Các bảng dự kiến chỉ có trong plan.md mục 14.2 và cấu hình nhiệm vụ mở rộng tiếp tục theo phạm vi đã ghi. |
| 13/11/2026 (phiên 33) | Rà lại ERD theo bài viết ACE Academy và hướng dẫn Crow’s Foot của draw.io. Chuyển tệp draw.io và SVG sang nền trắng nét đen, giản lược tiêu đề và khung nhóm. Bổ sung 15 biểu thức CHECK cấp bảng trong chú thích khi di chuột. Kiểm tra lại 32 bảng, 202 cột và 38 khóa ngoại theo Flyway V1–V5, xem SVG trên Chrome. Chờ nghiệm thu ERD. |
| 14/11/2026 (phiên 34) | Người dùng chốt 4 luồng Activity Diagram. Đã vẽ Đăng nhập, Mua sản phẩm và đặt hàng COD, Hủy đơn hàng, Khám phá Hệ Mặt Trời và nhận thưởng hành trình. Đối chiếu với yêu cầu, API và logic hiện tại. [Tệp draw.io 4 trang](galaxora-activity.drawio), [bản xem trước](activity-diagram/). Chờ nghiệm thu bộ sơ đồ. |
| 14/11/2026 (phiên 35) | Theo yêu cầu báo cáo, mỗi Activity Diagram dùng một nút kết thúc hoạt động. Đã nối nhánh hủy xác nhận COD và các nhánh báo lỗi khi hủy đơn vào nút hợp luồng cuối của từng trang. Kiểm tra tệp draw.io và SVG đều có đúng một nút kết thúc trên mỗi luồng. Chờ nghiệm thu. |
| 14/11/2026 (phiên 36) | Người dùng xác nhận tiếp tục Giai đoạn 8–10 và duyệt phạm vi quản trị nhiệm vụ. Triển khai Flyway V6, API, trang quản trị và tiến độ USER. Cập nhật yêu cầu, ERD, hợp đồng API và hướng dẫn bàn giao. Frontend build đạt, còn cảnh báo kích thước gói Three.js. Maven package đạt 14/14 trên MySQL 8.4.13 riêng. Flyway áp dụng V1–V6 và lần sau không chạy lại. Khôi phục schema thử khớp 6 migration, 13 dòng XP, 5 phần thưởng, 4 đơn và 2 phiếu nhập. Chrome kiểm tra Admin tạo nhiệm vụ, USER khám phá Sao Thủy, nhận 25 XP và thấy huy hiệu trong hồ sơ. Đo FPS trên máy Windows người dùng chọn. Hai yêu cầu đổi email qua Brevo trả HTTP 202 trên schema thử. Người dùng chưa thấy thư lượt đầu. Mã lượt cuối hết hạn trước khi xác nhận nhận thư. Đã dừng phiên ứng dụng và dọn schema Brevo thử. Chờ nghiệm thu cục bộ. |
| 16/11/2026 (phiên 37) | Người dùng duyệt kiểm tra TK-06 qua Brevo thật và chỉ định hai hộp thư thử. Maven đóng gói thành công, 3 bài đạt và 11 bài MySQL được bỏ qua ở lượt đóng gói. Lượt riêng áp dụng Flyway V1–V6 trên MySQL 8.4.13 tạm, chạy bài TK-06 với bộ nhận thư kiểm thử đạt 1/1, rồi dừng MySQL và dọn dữ liệu thử. Frontend build đạt, còn cảnh báo gói Three.js trên 500 kB. Kết nối TCP 587 tới Brevo thành công. Chưa tìm thấy cấu hình SMTP/HMAC cục bộ theo vị trí đã ghi, chưa gửi thư thật trong phiên. Giữ TK-06 qua Brevo thật và nghiệm thu cục bộ ở trạng thái chờ. Thay đổi trong dự án chỉ ở roadmap. |
| 16/11/2026 (phiên 38) | Người dùng duyệt tài liệu tiếp quản. Tạo `READMECHITIET.md` và ba phụ lục UI, API, CSDL dưới `docs/`. Đối chiếu đường dẫn, 27 route, 89 endpoint, 35 bảng, 224 cột và 41 khóa ngoại với mã nguồn. Ghi mục cần xác nhận về triển khai, nghiệm thu, email thật, thiết bị đích, cấu hình vận hành và quy tắc thứ tự chương. Scope là tài liệu, không đổi mã ứng dụng hoặc nghiệp vụ. `npm run build` đạt, còn cảnh báo kích thước gói Three.js. `mvn -o package` chạy 14/14 bài trên MySQL 8.4.13 tạm, đã dọn dữ liệu thử. Chuẩn bị ZIP riêng `Dũngchitiet.zip` gồm mã nguồn, tài liệu và asset cần cho build. Trạng thái nghiệm thu và TK-06 qua Brevo thật tiếp tục theo mục 5. |

### 17/11/2026 · Đợt giao diện Trang chủ

- Dũng chốt Q1–Q13 và duyệt Giai đoạn 1. Triển khai header, hero, chào mừng cho khách, bốn khu Khám phá, Hành trình USER, Sản phẩm mới và footer. Thao tác thêm một đơn vị dùng API giỏ hiện có. `ghi()` nhận thêm `AbortSignal` tùy chọn. Giữ nguyên cấu hình Vite vì đợt này dùng ảnh trong `assets/3d/`.
- Chạy `npm run build` sau các bước triển khai. Bản cuối qua `tsc -b` và Vite. Tệp JS chính 312,01 kB, CSS chính 40,00 kB. Gói Three.js dùng chung cho khu vực 3D vẫn là 659,47 kB và Vite giữ cảnh báo trên 500 kB. Vite cũng có thông báo thời gian plugin trong lượt cuối.
- Chrome headless 157 trên `localhost:5173` đã chụp tại 1672×941, 1920×1080, 1280×720, 768×1024 và 390×844. Đã kiểm tra tràn ngang, Tab, Enter, Escape, nút lên đầu, giảm chuyển động, console và mở hồi quy `/kham-pha`, `/cua-hang`, `/tai-khoan/dang-nhap`, `/he-thong`. Network ở dev và bản build không tải Three.js hoặc GLB khi mở `/`.
- Backend localhost:8080 không kết nối và môi trường chưa có `GALAXORA_DB_PASSWORD`. Chrome kiểm tra lỗi proxy thực tế. Các lượt khách, USER, ADMIN, dữ liệu rỗng, lỗi, tồn kho, thêm giỏ và số lượng sau khi thêm dùng phản hồi mô phỏng trong trình duyệt. Mô phỏng nằm ngoài mã nguồn và không ghi vào MySQL. Cần lặp lại với phiên và dữ liệu thật để nghiệm thu.
- Mốc dọc ở 1672×941 là header 70 px, danh mục y=427, đầu hai panel y=541 và footer y=876. Hành trình cao hơn mẫu khoảng 12 px khi có tên huy hiệu, thẻ sản phẩm cao hơn khoảng 17 px trong ảnh kiểm có tình trạng hết hàng/ngừng bán. Ảnh hero hiện thiếu trạm vũ trụ, cảnh đêm và ảnh phi hành gia của mẫu. Dải Liên hệ, kính lúp, tagline và bản quyền bỏ theo quyết định của Dũng. Bốn icon mạng xã hội là trang trí.

### 17/11/2026 · Đồng bộ giao diện · Đợt 1

- Dũng xác nhận Trang chủ làm chuẩn, đồng ý Q-A–Q-K và duyệt bắt đầu Đợt 1. Tạo `KhungTrang`, chuyển bộ đầu trang, chân trang và SVG vào `frontend/src/giaodien/`. Tách token và CSS thành phần dùng chung, áp dụng cho Trang chủ. Các trang con vẫn theo lịch triển khai từng đợt.
- `npm run build` đạt ở bản mốc, sau khi chuyển component và sau khi tách CSS. JS chính từ 312,01 lên 312,36 kB. CSS chính từ 40,00 lên 45,94 kB. Gói Three.js chứa `OrbitControls` giữ 659,47 kB và tiếp tục có cảnh báo trên 500 kB. Rà màu trong phạm vi Đợt 1, các giá trị hex và RGB tập trung ở `bienThietKe.css`.
- Chrome 157 headless chụp trước/sau ở 390×844, 768×1024, 1280×800, 1672×941 và 1920×1080, gồm ảnh menu bàn phím ở 390 px. Đối chiếu bố cục, liên kết đầu/chân trang, số lần đọc API, thanh XP và hiển thị sản phẩm theo vai trò. Kiểm tra Tab, Enter, Escape, viền focus, nút lên đầu và giảm chuyển động. Trang chủ không tải gói dựng cảnh hoặc GLB. Kiểm tra nền, chữ và khung hiện tại ở Đăng nhập, Cửa hàng, Kiểm tra hệ thống và Khám phá.
- Ngày 18/11, kiểm tra lại bằng Chrome 157 headless với kết xuất phần mềm. 12/16 cặp ảnh khớp từng pixel. Bốn cặp còn lại lệch từ 1 đến 123 pixel, tối đa 2/255 trên mỗi kênh màu. Kích thước, kiểu tính toán, liên kết, focus và các lượt đọc API mô phỏng đều khớp giữa hai bản.
- Kiểm tra cổng `8080` nhận lỗi từ chối kết nối. Chrome dùng phản hồi mô phỏng cho ảnh khách, USER và ADMIN. Kiểm thử phiên, XP, sản phẩm và giỏ với dữ liệu thật vẫn cần lượt chạy backend.
- Dũng nghiệm thu Đợt 1 ngày 18/11/2026. Kế hoạch đồng bộ giao diện còn sáu đợt, từ Đợt 2 đến Đợt 7. Đợt tiếp theo là Tài khoản, 404 và Kiểm tra hệ thống.

### 18/11/2026 · Đồng bộ giao diện · Đợt 2–7

- Dũng duyệt gộp sáu đợt còn lại để nghiệm thu một lần. Áp dụng token, kiểu biểu mẫu, nút, thẻ, bảng và khung chung cho Tài khoản, 404, Kiểm tra hệ thống, Cửa hàng, Giỏ hàng, Đơn và Quản trị. Dùng biến thể đầu và chân trang gọn ở Quản trị và Kiểm tra hệ thống. Menu Tài khoản giữ các liên kết theo vai trò. Logo của khung bán hàng dẫn về Trang chủ, còn mục Cửa hàng giữ đích `/cua-hang`.
- Khám phá dùng đầu trang gọn bên trong vùng toàn màn hình. Các bảng thông tin, Hành trình và ghi công lấy vị trí theo chiều cao đầu trang. Thu nhỏ chiều cao bảng trên màn hẹp để giữ khoảng trống cho điều khiển và danh sách thiên thể. Dùng cùng tài nguyên và bộ dựng cảnh hiện có.
- `npm run build` đạt. CSS chính 48,06 kB, JS chính 313,52 kB, CSS tải riêng của Khám phá 10,59 kB. Gói `OrbitControls` giữ 659,47 kB và Vite giữ cảnh báo kích thước có từ bản mốc. Rà màu cho thấy các giá trị hex và RGB của CSS trong phạm vi nằm trong `bienThietKe.css`.
- Chrome headless đối chiếu 70 cặp ảnh màn hình và hai cặp Xem nhanh, gồm 31 trường hợp mỗi kích thước 390×844, 1280×800 và bốn khu Khám phá ở 768×1024, 1672×941. Kiểm tra Tab/Enter/Escape và trả focus ở menu, mở/đóng Xem nhanh, hiện/ẩn mật khẩu, bỏ fragment token khi đặt lại mật khẩu, bảng kho trong khung, các panel Khám phá mở đồng thời và Quay lại giữa các khu. Liên kết trong vùng nội dung và các lượt gọi API ngoài Khám phá khớp bản mốc trong dữ liệu kiểm tra.
- Chrome với WebGL kết xuất bằng SwiftShader tải GLB ở cả bốn khu. Kiểm tra chọn Trái Đất, vào và thoát toàn màn hình, chuyển sang Cảnh gốc rồi về mô phỏng, và đặt lại góc nhìn ở ba khu còn lại. Trình xem sản phẩm mở, đặt lại góc nhìn và đóng được với GLB lấy từ tài nguyên dự án qua phản hồi mô phỏng.
- Endpoint sức khỏe trên cổng 8080 từ chối kết nối. Phiên, sản phẩm, đơn hàng và XP trong ảnh được mô phỏng trong Chrome. Các luồng cần tài khoản, đơn hàng và dữ liệu backend thật vẫn cần một lượt kiểm thử riêng.
- Chờ Dũng nghiệm thu chung Đợt 2–7.

### 18/11/2026 · Đăng nhập và bàn giao từng phần

- Đăng ký không gọi SMTP. Người dùng vào màn hình đăng nhập ngay sau khi đăng ký. Tài khoản mới và tài khoản cũ chưa xác minh dùng được phiên đăng nhập. Người dùng yêu cầu xác minh email từ trang tài khoản khi cần khôi phục mật khẩu hoặc đổi liên hệ.
- Đồng bộ quy tắc nghiệp vụ, đặc tả API, hướng dẫn chạy, sơ đồ hoạt động đăng nhập và quy tắc bàn giao. Cập nhật trực tiếp tệp trong workspace, chỉ đóng gói ZIP khi người dùng yêu cầu rõ ràng.
- Maven chạy trên JDK 21 với MySQL tạm, 14/14 bài đạt, gồm kiểm tra đăng nhập và đọc phiên trước khi xác minh, xác minh theo yêu cầu và khôi phục mật khẩu. `npm run build` đạt; Vite vẫn báo gói Three.js trên 500 kB.
- Đã chạy lại `spring-boot:run` trong NetBeans. API sức khỏe trực tiếp và qua Vite, trang đăng nhập và trang đăng ký đều trả HTTP 200 trên máy hiện tại.

### 18/11/2026 · Cập nhật giao diện tài khoản và Khám phá

- Biểu mẫu đăng ký thêm danh sách quốc gia, chuẩn hóa số E.164, so khớp hai ô mật khẩu và mở trang đăng nhập sau khi API tiếp nhận. Đăng nhập thành công mở Trang chủ. Trang hồ sơ dùng khung riêng, hiển thị dữ liệu hồ sơ và bảo mật từ các API hiện có.
- Bổ sung minh họa Hệ Mặt Trời từ ảnh bề mặt của dự án và nền tinh vân ImageGen. Chân trang đầy đủ chia nhóm Khám phá, Cửa hàng và Tài khoản. Bỏ liên kết xác minh email khỏi trang hồ sơ.
- Cập nhật khung bốn khu Khám phá bằng lớp trang trí ngoài canvas, panel thông tin, thanh điều khiển và dock 11 thiên thể. Dữ liệu số liệu đến từ danh mục NASA và nội dung quản trị hiện có. Giữ lớp kết xuất 3D và ghi công nguồn.
- Build TypeScript và Vite đạt, còn cảnh báo kích thước gói OrbitControls. Đã thử bốn khu trong Chrome WebGL ở bố cục desktop và mobile, cùng chế độ dự phòng 2D trên mobile. Kiểm tra toàn màn hình, chuyển sang Cảnh gốc rồi về mô phỏng, cuộn dock và giảm chuyển động. Luồng đăng ký, đăng nhập và hồ sơ dùng phản hồi mô phỏng trong trình duyệt để kiểm tra giao diện. API sức khỏe và API nội dung Khám phá trên backend cục bộ trả HTTP 200.

### 18/11/2026 · Làm lại trang Đăng nhập theo ảnh mẫu

- Đổi bố cục riêng của trang Đăng nhập sang nền vũ trụ toàn chiều rộng. Figure lấy từ ảnh sản phẩm phi hành gia Chibi ngồi trên đế Mặt Trăng. Khung kính chứa biểu mẫu email hoặc số điện thoại, mật khẩu, hiện hoặc ẩn mật khẩu, quên mật khẩu và đăng ký. Trên màn hình hẹp, minh họa nằm phía trên form. Vòng sáng tôn trọng cài đặt giảm chuyển động. [Nguồn ảnh và prompt](readmechitiet-ui.md#52-trang-công-khai-và-tài-khoản)
- Build đã đạt. Chrome kiểm tra bố cục ở chiều rộng 320, 390, 820, 1366 và 1500 px. Kiểm tra chuyển trang và lỗi xác thực bằng phản hồi API mô phỏng. Vite trả HTTP 200 cho trang Đăng nhập. Backend cổng 8080 từ chối kết nối trong lượt xác minh này.

### 18/11/2026 · Rà soát bố cục và làm mới giao diện toàn trang

- Nới khoảng dòng tiêu đề tiếng Việt, tách vòng quỹ đạo khỏi chữ ở Trang chủ, tăng vùng bấm và khoảng đệm, giữ bảng và dải thiên thể cuộn trong khung. Điều chỉnh bảng Nguồn mô hình và bản đồ theo chiều cao tiêu đề Khám phá.
- Bổ sung nền nhiều lớp, biểu tượng SVG nhiều lớp, vệt sáng, viền quay và hiệu ứng vào vùng nhìn. Quản trị dùng hiệu ứng tiết chế. Khám phá đặt lớp trang trí ở ngoài canvas. Giao diện tôn trọng cài đặt giảm chuyển động.
- Chrome chụp 116 cặp ảnh trước và sau trên 29 đường dẫn tại 390, 768, 1280 và 1672 px. Kiểm tra mở các bảng Khám phá, thao tác bằng bàn phím, hiện và ẩn mật khẩu, phản hồi khi rê hoặc nhấn và chế độ giảm chuyển động. Build TypeScript và Vite đạt, còn cảnh báo kích thước gói OrbitControls.
- Trong lượt kiểm tra, kết nối trực tiếp tới cổng 8080 bị từ chối và proxy Vite trả 502. Các ảnh dùng phản hồi trực tiếp từ ứng dụng. Cần xác minh thêm bố cục sản phẩm, đơn hàng, hồ sơ, Hành trình theo phiên và các bảng Quản trị khi có API và dữ liệu thật.

### 18/11/2026 · Dashboard và khu vực Admin riêng

- Người dùng duyệt gộp chuyển hướng theo vai trò, khung Admin độc lập, dashboard và kiểm thử. Phiên có ADMIN mở `/quan-tri` sau khi đăng nhập hoặc khi mở trang đăng nhập lúc đã có phiên. Phiên USER mở Trang chủ.
- Tách khung Admin khỏi luồng bán hàng và tải mã Admin theo đường dẫn. Tám mục quản trị dùng thanh bên trên desktop và menu thu gọn trên mobile. Giữ các biểu mẫu, API và quyền phía server của module hiện có. Dùng ảnh tinh vân và figure có sẵn cho banner.
- Tổng quan lấy năm chỉ số từ `/api/quan-tri/tong-quan` và danh sách từ `/api/quan-tri/thong-bao`. Doanh thu là tổng của đơn đã giao. Ảnh tham khảo chỉ định hướng bố cục và phối màu.
- Build TypeScript và Vite đạt. Chrome headless với API mô phỏng kiểm tra chuyển hướng USER và ADMIN, chặn khách và USER vào Admin, tám trang quản trị, chi tiết đơn, thông báo, đăng xuất, trang cửa hàng và đơn hàng USER. Bố cục không tràn ngang ở 320, 390, 820 và 1440 px. Cảnh báo kích thước gói OrbitControls vẫn xuất hiện.
- Khởi động backend bằng Maven với JDK 21 và MySQL cục bộ. Flyway xác minh sáu migration. API sức khỏe trả HTTP 200 trực tiếp và qua Vite, còn API quản trị trả 401 khi chưa đăng nhập. Sau khi người dùng đăng nhập, trình duyệt mở Tổng quan Admin và hiển thị dữ liệu thật. Đã thấy màn hình Voucher trong cùng phiên. Chờ nghiệm thu giao diện.

### 18/11/2026 · Bộ ảnh sản phẩm Trang chủ

- Đưa khu Sản phẩm lên ngay sau phần mở đầu. Nút Xem sản phẩm dẫn đến `/#san-pham`. Khu này có bốn ảnh nổi bật, bộ lọc nhóm, tìm theo tên có hoặc không dấu, tải thêm từng lượt và hộp thoại xem ảnh nguyên khung.
- Nguồn gồm 120 JPG trong thư mục người dùng cung cấp `Ảnh sản phẩm bán hàng cho website golaxora`, có cả `Sản phẩm thêm`. Tên hiển thị lấy từ tên tệp. Bốn nhóm có 30 ảnh mỗi nhóm. Một số tệp chứa ảnh minh họa, chẳng hạn ảnh mang tên áo thun James Webb hiển thị tinh vân Carina. Các ảnh có vai trò tham khảo. Danh sách mới để mua hàng vẫn đọc từ API cửa hàng.
- Tạo hai bản WebP cho mỗi ảnh với cạnh tối đa 480 px và 1280 px. Tổng dung lượng giảm từ 178,75 MB xuống 14,95 MB. Đã xác minh đủ 120 tên nguồn cùng 240 tệp WebP trong mã nguồn và bản build.
- `npm run build` đạt, còn cảnh báo kích thước gói OrbitControls hiện có. Trình duyệt kiểm tra ở 320, 390 và 1366 px, tải đủ 120 thẻ, kiểm tra bốn bộ lọc, tìm không dấu, trạng thái không có kết quả, phím mũi tên, Escape và trả focus. API sức khỏe trả `HOAT_DONG` và danh sách sản phẩm trả rỗng, nên chưa kiểm tra thêm giỏ bằng sản phẩm thực. Bàn giao trong workspace, chờ nghiệm thu giao diện.

### 19/11/2026 · Đợt đầu xử lý kết quả kiểm toán

- Kiểm tra lại vai trò hiện hành khi dùng phiên. Giới hạn phát hành link đặt lại mật khẩu tối đa một lần mỗi 60 giây theo tài khoản và 10 yêu cầu mỗi 15 phút theo IP. Giữ phản hồi chung. Hồ sơ hiển thị trạng thái email và có lối xác minh. Màn Quên mật khẩu cũng có liên kết này.
- Dùng vé ngắn hạn gắn với phiên và thiên thể để ghi nhận mở bảng. Khoảng chờ cục bộ ban đầu là 900 ms. Dấu hiệu bí mật mở câu hỏi do backend chấm, mỗi câu có ba lần thử trong năm phút. Cơ chế này xác minh vé, thời gian và câu trả lời. Việc người dùng thực sự nhìn bảng trên trình duyệt vẫn cần thống nhất mức bảo đảm nếu muốn chốt đầy đủ tiêu chí chống gian lận.
- Flyway V7 bổ sung sổ địa chỉ USER. Hồ sơ có thêm, sửa, xóa. Checkout cho điền địa chỉ đã lưu và vẫn chụp thông tin giao nhận vào đơn. Khóa ô voucher khi đang tính báo giá. Trang chi tiết sản phẩm có thông báo 404 và lối về cửa hàng.
- Ghi nhật ký chuyển trạng thái đơn và xử lý hủy trong transaction nghiệp vụ. Các phân hệ Admin khác tiếp tục dùng cơ chế ghi hiện có.
- `npm run build` đạt, còn cảnh báo kích thước OrbitControls. `mvn -o package` đạt 19/19, 0 bỏ qua trên MySQL 8.4.13 riêng. Flyway áp dụng V1–V7. Trình duyệt trên bản thử tách biệt xác nhận đăng ký và đăng nhập, thêm/sửa địa chỉ và tải lại, chọn địa chỉ tại checkout, báo giá COD, trang 404, mở bảng Trái Đất và giải tín hiệu bằng bàn phím đến 60 XP. Thử thư dùng bộ nhận trong kiểm thử. Chưa chạy Brevo thật hoặc áp dụng V7 vào database đang dùng.
- Đợt sửa sau còn dữ liệu hàng bán thật, audit ở các phân hệ Admin khác, tối ưu 3D, và các quyết định về stack, thứ tự khóa chương, loại nhiệm vụ và phần thưởng mở rộng.

### 19/11/2026 · Bỏ toàn bộ chức năng gửi email

- Gỡ Brevo, cấu hình SMTP, thư viện Spring Mail, API và màn hình xác minh, gửi lại mã, khôi phục mật khẩu và đổi thông tin liên hệ. Đăng ký, đăng nhập bằng email hoặc số điện thoại, đổi mật khẩu bằng mật khẩu hiện tại và thông báo trong website tiếp tục hoạt động.
- Cập nhật `plan.md` 1.4 và đặc tả. Tự khôi phục mật khẩu đã quên và đổi thông tin đăng nhập chờ quy trình xác minh mới. Giữ migration Flyway V1–V7 và các bảng, cột lịch sử. Ứng dụng hiện không đọc hoặc ghi dữ liệu xác minh cũ.
- Frontend build đạt, còn cảnh báo kích thước gói OrbitControls. Maven build sạch với MySQL 8.4.13 tạm đạt 19/19, gồm đăng nhập, đổi mật khẩu và xác nhận 7 API cũ đã gỡ. Đã kiểm tra JAR không chứa lớp hoặc thư viện gửi email. Môi trường đang vận hành chưa được cập nhật.

### 19/11/2026 · Đợt hai xử lý kết quả kiểm toán

- Thực hiện mục 11.5 của plan. Ghi nhật ký quản trị ngay trong giao dịch của danh mục, sản phẩm, mô hình, đánh giá, kho, tài khoản, voucher, nội dung, sự kiện, nhiệm vụ và cấu hình. Giữ ghi nhận chuyển trạng thái đơn và xử lý hủy trong giao dịch đã có. Nhật ký dùng mô tả thao tác, mã Admin, đường dẫn và giờ UTC. Phiếu nhập hoặc điều chỉnh kho gửi lại với cùng khóa yêu cầu được ghi một lần.
- Thực hiện phần lập lịch dựng hình của mục 9.7. Các cảnh 3D ngừng lập lịch khung khi tab ẩn hoặc canvas ngoài vùng nhìn, gộp các lần vẽ tĩnh theo khung trình duyệt và dựng lại khi quay về. Tốc độ mô phỏng tiếp tục từ thời điểm đã dừng.
- Maven đóng gói thành công, 20/20 bài đạt trên MySQL 8.4.13 tạm, 0 bỏ qua. Kiểm thử buộc bản ghi nhật ký lỗi xác nhận cả tồn kho và lịch sử biến động được hoàn tác. Frontend build đạt, vẫn có cảnh báo gói OrbitControls 659 kB. Kiểm thử bộ lập lịch bằng Node xác nhận dừng, vẽ lại và dọn đăng ký sự kiện. Chrome bản thử chưa kiểm chứng được vì Computer Use dừng khi không xác định được URL. Chưa đo lại FPS trên thiết bị mục tiêu.
- Bàn giao trong workspace. Còn dữ liệu hàng bán thật, giảm dung lượng mô hình 3D và các quyết định nghiệp vụ đã ghi ở mục 5.

### 19/11/2026 · Đợt ba xử lý kết quả kiểm toán

- Thực hiện phần tối ưu kích thước mô hình trong plan mục 9.7. Nén chín GLB
  giảm từ 15% trở lên và phục vụ sáu GLB còn lại trực tiếp. Bản build chỉ
  xuất một biến thể cho mỗi mô hình. Các GLB nguồn được giữ để bảo trì.
- Tổng tải giảm 36,7%, từ 581,68 xuống 368,23 MiB. Sao Thổ giảm từ 143,04
  xuống 80,00 MiB, Sao Mộc từ 102,59 xuống 55,45 MiB và Sao Thiên Vương từ
  83,15 xuống 34,04 MiB. Trình tải giải nén trước khi gửi dữ liệu cho cùng
  trình dựng. [Bảng dung lượng và cách phục vụ](3d-assets.md#gói-tải-tối-ưu)
- Frontend build thành công. Bản build có đúng 15 mô hình. Đã đối chiếu
  SHA-256 cho chín tệp sau giải nén và xác nhận mỗi tệp trùng GLB nguồn.
  HTTP qua Vite kiểm tra đủ 15/15 đường tải, GLB 2.0, giải nén dự phòng,
  gzip qua HTTP và hủy yêu cầu. Cảnh báo gói JavaScript 3D vẫn xuất hiện. Chưa nghiệm thu bằng trình
  duyệt hoặc đo FPS trên thiết bị mục tiêu trong đợt này.

### 19/11/2026 · Đợt bốn xử lý kết quả kiểm toán

- Thực hiện phần độ phân giải thích ứng của plan mục 9.7. Hệ Mặt Trời,
  Cảnh gốc, Thiên hà, Tinh vân và Hố đen dùng giới hạn 2,3 triệu điểm
  ảnh và 4.096 điểm ảnh mỗi cạnh. Bộ điều chỉnh hạ từ 100% qua 85%, 70%
  và 58% khi cảnh vẽ chậm liên tiếp, rồi tăng lại từng mức khi ổn định.
- Lịch dựng bỏ các khoảng nghỉ giữa lần đo và đặt lại mốc khi tab ẩn
  hoặc canvas ra khỏi vùng nhìn. Bộ đệm HDR và điểm sáng Thiên hà lấy
  kích thước sau khi điều chỉnh. Camera, tọa độ bấm và bố cục dùng kích
  thước CSS. [Đặc tả độ phân giải](3d-visual-spec.md#độ-phân-giải-thích-ứng)
- Mặc định kỹ thuật dùng ngưỡng chậm từ 24 ms trong khoảng 550 ms
  mẫu hợp lệ, và nhanh tối đa 19 ms trong ba giây trước khi tăng cấp.
  Kiểm thử Node xác nhận giới hạn bộ đệm, hạ/tăng độ phân giải, lọc
  khoảng nghỉ và khởi động lại lịch dựng. Frontend build thành công.
  Chrome headless với nhịp khung hình mô phỏng xác nhận bộ đệm Hệ Mặt
  Trời giảm từ 1.650 × 1.089 xuống 956 × 631, rồi tăng lên 1.154 × 762.
  Đã kiểm tra giới hạn với canvas CSS 2.350 × 1.200, tạm dừng và đổi góc
  camera, phóng to Thiên hà và chuyển sang 2D khi mất ngữ cảnh WebGL.
  Cảnh báo kích thước OrbitControls vẫn xuất hiện. Chưa đo FPS trên
  thiết bị mục tiêu.

- Theo yêu cầu thay bộ ảnh sản phẩm, đã chuyển thư mục `frontend/public/anh-san-pham` vào Thùng rác và tạm ẩn bộ ảnh tham khảo trên Trang chủ. Danh sách sản phẩm từ API cửa hàng vẫn hiển thị theo dữ liệu hiện có. Frontend build thành công, bản build không còn thư mục ảnh cũ. Chờ bộ ảnh mới.

### 19/11/2026 · Thay ảnh và đồng bộ luồng mua hàng

- Tích hợp 39 ảnh mới vào Trang chủ, Cửa hàng, trang chi tiết và giỏ hàng. Khung ảnh trên thẻ có tỉ lệ 1:1, giữ trọn ảnh, có quầng sáng xanh tím và hỗ trợ giảm chuyển động. Mỗi ảnh có bản WebP nhỏ cho danh sách và bản lớn cho trang chi tiết. [Nguồn ảnh](3d-assets.md#bộ-ảnh-cửa-hàng)
- Quản trị có thể chọn ảnh trong bộ và đặt ảnh bìa. Sản phẩm thiếu ảnh dùng ảnh khớp chính xác theo SKU hoặc tên. Bộ ảnh tham khảo xuất hiện khi danh mục công khai trống hoặc khi không tải được danh mục sản phẩm.
- Thẻ sản phẩm có giá VND, giá so sánh khi có, nút xem chi tiết và thêm vào giỏ theo quyền mua. Đăng nhập từ sản phẩm đưa khách trở lại trang chi tiết. Giỏ hiển thị ảnh, số lượng và tiền tính từ backend.
- Frontend build thành công. Chrome xác nhận tải đủ 39 ảnh, tìm ảnh, mở ảnh lớn bằng bàn phím, khung tại 390 px và cài đặt giảm chuyển động. Maven chạy 21 bài kiểm thử thành công với MySQL 8.4 tạm, gồm bài mới đối chiếu ảnh bìa giữa trang chi tiết và giỏ.
- Chrome kết nối Spring Boot và MySQL tạm đã kiểm tra chọn ảnh trong Quản trị, mở xem nhanh và chi tiết, đổi ảnh chi tiết, đăng nhập rồi trở về sản phẩm, thêm hàng ở Trang chủ và Cửa hàng, cập nhật số lượng, xóa hàng và tính tổng COD. Đã kiểm tra bố cục tại 390 px và 1.440 px.
- Chờ giá bán và tồn kho thực tế để đăng các mặt hàng mới. Bàn giao cục bộ qua workspace.

### 19/11/2026 · Bộ dữ liệu mẫu, thẻ dùng chung và xóa trong Quản trị

- Người dùng duyệt bộ seed 39 sản phẩm và ngưỡng thử 5 nếu cấu hình chung đang để trống. Seed độc lập, chạy với phiên ADMIN và ghi tồn qua phiếu nhập có nhật ký. Hai lượt trên MySQL 8.4 nghiệm thu cho 39 ảnh, một phiếu và phân bố 32 còn hàng, 4 sắp hết, 3 hết hàng.
- Trang chủ dùng một danh sách từ DB và chia sẻ thẻ, xem nhanh với Cửa hàng. Chi tiết có breadcrumb, ảnh lớn và số lượng giới hạn theo tồn khả dụng. Hành trình nhà thám hiểm phủ bề rộng khung Trang chủ.
- V8 bổ sung xóa danh mục, sản phẩm, voucher, nhiệm vụ và cấu hình voucher theo chính sách giữ lịch sử. Bổ sung thao tác xóa mô hình 3D, sự kiện và nội dung tùy chỉnh. Phiếu kho đã ghi sổ dùng quy trình điều chỉnh có lý do.
- Frontend build thành công, Maven đạt 22 bài trên MySQL nghiệm thu. Chrome kiểm tra luồng Trang chủ, Cửa hàng, chi tiết, giỏ hàng và xóa danh mục. [Chi tiết tệp, cách kiểm tra và bảng 39 sản phẩm](doi-chieu-san-pham-mau.md)
- Ở lần đo cuối, API của `localhost:5173` còn 0 sản phẩm công khai. Trang seed đã sẵn sàng. Cần chạy từ phiên ADMIN đang mở và khởi động lại backend để dùng V8 trong ứng dụng cục bộ.
