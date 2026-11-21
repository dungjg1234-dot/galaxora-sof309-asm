# GALAXORA --- Project Plan

> **Trạng thái tài liệu:** Bản kế hoạch tổng hợp yêu cầu đã trao đổi\
> **Phiên bản:** 1.4\
> **Ngày cập nhật:** 19/11/2026\
> **Mô hình kinh doanh:** Một cửa hàng duy nhất, do Admin quản lý\
> **Backend:** Java, Spring Boot 3, Spring Security, Spring Data JPA / Hibernate\
> **Frontend:** React + TypeScript + Vite + Tailwind CSS\
> **Database:** MySQL 8.0+\
> **3D & Animation Engine:** Three.js, React Three Fiber (R3F), Drei, GSAP, GLSL Shader\
> **3D Model Support:** `.glb` / `.gltf` (Camera Hybrid, tương tác và khám phá không gian 3D)\
> **Authentication & Security:** Spring Security + BCrypt (Phân quyền ADMIN / USER)\
> **Design System Reference:** Premium Futuristic + Cinematic Realism (`docs/design-system.md`, `docs/3d-visual-spec.md`, `docs/3d-assets.md`)\

---

## 1. Mục tiêu tài liệu

Tài liệu này tổng hợp các yêu cầu, quyết định và định hướng đã thống
nhất cho dự án GALAXORA. Đây là tài liệu nền tảng để nhóm phát triển và
AI Coding Agent hiểu đúng phạm vi trước khi thiết kế cơ sở dữ liệu, kiến
trúc, giao diện và triển khai chức năng.

### 1.1. Nguyên tắc làm việc

1.  Đọc `plan.md` và toàn bộ tài liệu trong `docs/` trước khi thay đổi
    mã nguồn.
2.  Không tự ý thay đổi yêu cầu đã chốt, công nghệ đã được phê duyệt
    hoặc quy tắc nghiệp vụ.
3.  Không tự ý xóa, thay thế hoặc viết lại chức năng đang hoạt động.
4.  Không tự tạo dữ liệu giả để thay thế dữ liệu thật trong môi trường
    triển khai.
5.  Khi yêu cầu chưa rõ hoặc có mâu thuẫn, phải ghi nhận và hỏi xác nhận
    trước khi triển khai.
6.  Chia dự án thành từng giai đoạn nhỏ; hoàn thành, kiểm thử và nghiệm
    thu từng giai đoạn trước khi chuyển tiếp.
7.  Ưu tiên bảo mật, tính nhất quán dữ liệu, khả năng bảo trì, hiệu năng
    và trải nghiệm người dùng.
8.  Các thao tác ảnh hưởng đồng thời đến đơn hàng, kho, XP hoặc voucher
    phải được xử lý an toàn ở backend và có cơ chế chống thực hiện lặp.
9.  Không coi hiệu ứng giao diện là lý do để làm giảm khả năng truy cập,
    độ ổn định hoặc hiệu năng.
10. Không được đánh dấu tính năng hoàn thành nếu chưa có kiểm thử phù
    hợp.
11. Bàn giao từng phần qua các tệp đã cập nhật trong workspace. Chỉ tạo
    ZIP khi người dùng yêu cầu rõ ràng.

---

## 2. Tổng quan sản phẩm

### 2.1. Tên và ý tưởng

- **Tên dự án:** GALAXORA.
- **Tagline tham khảo:** "Explore Beyond. Discover the Universe."
  (chưa xác nhận là tagline chính thức).
- **Ý tưởng:** Website kết hợp trải nghiệm khám phá vũ trụ 3D, cửa
  hàng sản phẩm chủ đề vũ trụ và hệ thống nhiệm vụ/gamification.
- **Định hướng:** Một website có trải nghiệm khám phá trực quan, điện
  ảnh và tương tác, đồng thời có đầy đủ quy trình thương mại điện tử
  cơ bản và khu vực quản trị.

### 2.2. Mục tiêu chính

1.  Cho phép người dùng khám phá Hệ Mặt Trời, thiên hà, tinh vân và hố
    đen bằng giao diện 3D.
2.  Cung cấp cửa hàng bán các sản phẩm liên quan đến vũ trụ.
3.  Cho phép xem mô hình 3D của những sản phẩm có tài nguyên phù hợp.
4.  Tạo động lực khám phá thông qua nhiệm vụ, XP, cấp độ, huy hiệu và
    voucher.
5.  Cung cấp trang quản trị để Admin quản lý toàn bộ hoạt động cửa hàng
    và nội dung.
6.  Đảm bảo dữ liệu đơn hàng, kho hàng, tài khoản và phần thưởng nhất
    quán.

### 2.3. Mô hình vận hành

- Một cửa hàng duy nhất.
- Không xây dựng mô hình marketplace hoặc nhiều nhà bán hàng.
- Admin quản lý sản phẩm, danh mục, kho, đơn hàng, voucher, nội dung
  vũ trụ và nhiệm vụ.
- Hai vai trò nghiệp vụ chính: `ADMIN` và `USER`.
- Người dùng không được tự đăng ký tài khoản Admin.

### 2.4. Đối tượng sử dụng

- **Khách truy cập:** Xem toàn bộ nội dung công khai (cửa hàng, chi
  tiết sản phẩm, khám phá 3D) như các website bán hàng thông thường.
  Các chức năng gắn với tài khoản (giỏ hàng, yêu thích, đặt hàng,
  đánh giá, nhiệm vụ, XP) yêu cầu đăng nhập.
- **USER:** Đăng ký, đăng nhập, mua hàng, quản lý hồ sơ, tham gia
  nhiệm vụ, tích lũy XP và nhận phần thưởng.
- **ADMIN:** Quản lý và vận hành toàn bộ hệ thống.

---

## 3. Phạm vi chức năng

### 3.1. Trong phạm vi dự án

- Đăng ký, đăng nhập, đăng xuất và đổi mật khẩu bằng mật khẩu hiện tại.
- Phân quyền ADMIN/USER.
- Hồ sơ người dùng và địa chỉ nhận hàng.
- Cửa hàng, danh mục, sản phẩm, hình ảnh và mô hình 3D sản phẩm.
- Tìm kiếm, lọc, sắp xếp và phân trang sản phẩm.
- Giỏ hàng, đặt hàng và thanh toán COD.
- Theo dõi trạng thái đơn và yêu cầu hủy đơn.
- Quản lý kho nâng cao: nhập, xuất, tồn, giữ hàng và lịch sử biến
  động.
- Voucher do Admin tạo và voucher nhận từ nhiệm vụ.
- Đánh giá sản phẩm theo điều kiện người mua hợp lệ.
- Danh sách yêu thích.
- Khám phá 3D: Hệ Mặt Trời, thiên hà, tinh vân và hố đen.
- Hành trình khám phá theo chương và điều kiện mở khóa.
- Nhiệm vụ tự động, nhiệm vụ khám phá và nhiệm vụ bí mật.
- XP, cấp độ, huy hiệu và phần thưởng.
- Thông báo trong website.
- Dashboard và các chức năng quản trị.
- Nhật ký thao tác quan trọng và thống kê cơ bản.
- Responsive và tối ưu hiệu năng.

### 3.2. Ngoài phạm vi hiện tại

- Marketplace/multi-vendor.
- Nhiều cửa hàng hoặc nhiều nhà bán hàng.
- Đăng nhập Google/Facebook hoặc social login khác.
- Thanh toán trực tuyến qua cổng thanh toán; giai đoạn hiện tại chỉ
  COD.
- Ứng dụng mobile native riêng.
- Push notification trên thiết bị.
- Gửi email dưới mọi hình thức, bao gồm xác minh địa chỉ, khôi phục mật
  khẩu, đổi thông tin liên hệ và thông báo.
- Kiến trúc microservices.
- Các framework frontend như Angular hoặc Vue nếu chưa có quyết định
  thay đổi stack.
- Các tính năng game phức tạp vượt quá hệ thống nhiệm vụ và khám phá
  đã thống nhất.

---

## 4. Người dùng, tài khoản và phân quyền

### 4.1. Vai trò

---

Vai trò Quyền chính

---

`ADMIN` Quản trị người dùng, sản phẩm,
danh mục, kho, đơn hàng, voucher,
đánh giá, nội dung 3D, nhiệm vụ,
huy hiệu, cấu hình và thống kê

`USER` Quản lý hồ sơ, địa chỉ, giỏ hàng,
đơn hàng, yêu thích, đánh giá hợp
lệ, khám phá, nhiệm vụ và phần
thưởng

---

Phải kiểm tra quyền ở backend. Việc ẩn nút hoặc trang trên frontend
không được xem là cơ chế bảo mật.

### 4.2. Đăng ký

Thông tin dự kiến: - Họ tên. - Email. - Số điện thoại. - Mật khẩu.

- Email và số điện thoại phải duy nhất sau khi chuẩn hóa. Email được
  lưu để nhận diện tài khoản và dùng làm định danh đăng nhập.
- Backend băm mật khẩu bằng BCrypt. Đăng ký công khai cấp vai trò USER.
- Ô chọn quốc gia điền sẵn mã gọi. Người dùng nhập lại mật khẩu để xác
  nhận và có thể đăng nhập ngay sau khi tạo tài khoản.

### 4.3. Đăng nhập và bảo mật tài khoản

- Đăng nhập bằng email **hoặc** số điện thoại kết hợp mật khẩu.
- Sau khi đăng nhập, ADMIN vào khu vực riêng tại `/quan-tri`. USER vào
  Trang chủ. Nếu phiên có cả hai vai trò, giao diện mở Quản trị.
- Không hỗ trợ social login trong phạm vi hiện tại.
- Có đăng xuất.
- Có chức năng đổi mật khẩu khi đang đăng nhập và biết mật khẩu hiện tại.
- Khôi phục mật khẩu khi đã quên và tự đổi email hoặc số điện thoại được
  hoãn đến khi chốt một quy trình xác minh không dùng email.
- Admin có thể khóa/mở khóa tài khoản.
- Có giới hạn tần suất đăng nhập và cơ chế giảm rủi ro dò mật khẩu.
- Không tiết lộ tài khoản có tồn tại hay không qua thông báo lỗi nhạy
  cảm.

### 4.4. Hồ sơ người dùng --- Cosmic Identity

Hồ sơ có thể bao gồm: - Ảnh đại diện. - Explorer ID. - Tên hiển thị. -
Cấp độ và tổng XP. - Huy hiệu đã đạt. - Nhiệm vụ đã hoàn thành. - Các
khám phá đã ghi nhận. - Bộ sưu tập. - Voucher đang sở hữu. - Lịch sử đơn
hàng. - Danh sách yêu thích. - Sổ địa chỉ.

---

## 5. Cửa hàng và sản phẩm

### 5.1. Danh mục

- Admin được tạo, sửa, ẩn/hiện và quản lý danh mục.
- Hệ thống hỗ trợ nhiều nhóm sản phẩm chủ đề vũ trụ.
- Các nhóm như Planet Collection, Space Figures, Cosmic Decor và Space
  Accessories chỉ là ví dụ tham khảo, không phải danh sách cố định.
- Không hard-code danh mục vào frontend.

### 5.2. Sản phẩm

Thông tin sản phẩm dự kiến: - SKU/mã sản phẩm. - Tên. - Danh mục. - Mô
tả ngắn và mô tả chi tiết. - Giá bán. - Giá so sánh (nếu có). - Trạng
thái hiển thị. - Trạng thái kinh doanh. - Thông tin tồn kho. - Hình
ảnh. - Mô hình 3D nếu có. - Thời điểm tạo/cập nhật.

Tính năng phía người dùng: - Tìm kiếm. - Lọc theo danh mục và thuộc tính
phù hợp. - Sắp xếp. - Phân trang. - Trang chi tiết. - Xem nhanh. - Sản phẩm liên quan. - Yêu thích. -
Hiển thị tình trạng còn hàng/hết hàng. - Đánh giá sản phẩm. - Xem mô
hình 3D đối với sản phẩm có model hợp lệ.

### 5.3. Mô hình 3D sản phẩm

- Người dùng dự án sẽ tự tìm và cung cấp mô hình 3D từ Sketchfab hoặc
  nguồn phù hợp.
- Ưu tiên định dạng `.glb`/`.gltf`.
- Nên tải và lưu trữ/host tài nguyên hợp lệ để hiển thị trong viewer
  nội bộ; không mặc định dùng iframe cho mọi sản phẩm.
- Kiểm tra quyền sử dụng, giấy phép thương mại và yêu cầu ghi công của
  từng tài nguyên.
- Không mặc định mọi mô hình trên Sketchfab đều miễn phí hoặc được
  phép dùng thương mại.
- Mỗi mô hình cần metadata: tên file, sản phẩm liên kết, định dạng,
  dung lượng, license, nguồn, tác giả/attribution và trạng thái duyệt.

### 5.4. Đánh giá sản phẩm

- Chỉ tài khoản đã mua và nhận sản phẩm thành công mới được đánh giá
  sản phẩm tương ứng.
- Không cho phép đánh giá sản phẩm chưa mua hoặc đơn chưa giao thành
  công.
- Giới hạn một đánh giá cho mỗi người dùng, mỗi đơn hàng và mỗi sản
  phẩm (đã chốt 10/11/2026); cần thể hiện rõ bằng ràng buộc dữ liệu và
  kiểm tra backend. Mua lại ở đơn khác thì được đánh giá lại.
- Người dùng được sửa đánh giá của mình tự do, không giới hạn thời hạn
  (đã chốt 10/11/2026).
- Khi người dùng sửa đánh giá, lưu bản trước vào lịch sử và giữ trạng
  thái hiển thị hiện tại (đã chốt 12/11/2026).
- Admin có thể kiểm duyệt hoặc ẩn đánh giá vi phạm.
- Không cho phép client tự gửi trạng thái "đã mua" để vượt điều kiện.

### 5.5. Yêu thích

- USER có thể thêm/xóa sản phẩm khỏi danh sách yêu thích.
- Không bắt buộc khách chưa đăng nhập có dữ liệu yêu thích đồng bộ
  giữa thiết bị.

---

## 6. Giỏ hàng, đặt hàng và thanh toán

### 6.1. Giỏ hàng

- Người dùng có thể thêm, cập nhật số lượng và xóa sản phẩm.
- Backend phải kiểm tra sản phẩm còn kinh doanh và số lượng hợp lệ.
- Không tin giá hoặc tổng tiền do frontend gửi lên.
- Khi checkout, backend tính lại giá, voucher, phí vận chuyển và tổng
  tiền.

### 6.2. Đặt hàng

Thông tin đơn hàng dự kiến: - Mã đơn. - Người đặt. - Người nhận. - Số
điện thoại nhận. - Địa chỉ giao hàng dạng snapshot. - Danh sách sản
phẩm. - Đơn giá snapshot tại thời điểm đặt. - Số lượng. - Tạm tính. -
Giảm giá. - Phí vận chuyển snapshot. - Tổng thanh toán. - Phương thức
COD. - Trạng thái đơn hàng. - Ghi chú. - Thời điểm tạo/cập nhật.

### 6.3. COD

- Chỉ hỗ trợ thanh toán khi nhận hàng (COD).
- Tạo đơn không đồng nghĩa đã thu tiền.
- Trạng thái thanh toán cần tách riêng khỏi trạng thái đơn hàng.
- Khi giao thành công và thu tiền, cập nhật trạng thái thanh toán phù
  hợp.
- Không xây dựng tích hợp cổng thanh toán trực tuyến trong phạm vi
  hiện tại.
- Áp dụng một mức phí giao hàng COD cố định do Admin cấu hình cho
  toàn quốc (đã chốt 12/11/2026).

### 6.4. Trạng thái đơn hàng

Bộ trạng thái đề xuất: - `CHO_XAC_NHAN` - `DA_XAC_NHAN` -
`DANG_CHUAN_BI` - `DANG_GIAO` - `DA_GIAO_THANH_CONG` - `DA_HUY` -
`GIAO_THAT_BAI`

Yêu cầu: - Lịch sử trạng thái phải được lưu. - Không cho phép chuyển
trạng thái tùy ý; backend kiểm tra chuyển trạng thái hợp lệ. - Trạng
thái thanh toán COD được quản lý riêng. - Trạng thái yêu cầu hủy được
quản lý riêng, không trộn lẫn với trạng thái đơn hàng.

Quy tắc giao thất bại (đã chốt 10/11/2026): khi đơn chuyển
`GIAO_THAT_BAI`, hệ thống giải phóng ngay lượng hàng đang giữ của đơn
đó và đơn kết thúc; không có giao lại.

### 6.5. Quy tắc hủy đơn --- APPROVE (ĐÃ CHỐT)

**Trước khi Admin xác nhận đơn:** - Khách hàng được tự hủy đơn. - Vì đơn
chưa được giữ hàng theo quy tắc kho, thao tác này không cần giải phóng
lượng hàng đã giữ.
- Admin cũng được hủy đơn đang `CHO_XAC_NHAN` (đã chốt 10/11/2026), kèm
  lý do và ghi audit log; vì chưa giữ hàng nên không có hàng để giải
  phóng.

**Sau khi Admin xác nhận đơn:** - Khách hàng không tự chuyển đơn sang đã
hủy. - Khách hàng gửi yêu cầu hủy, cung cấp lý do. - Yêu cầu chuyển sang
`CHO_DUYET`. - Admin có quyền duyệt hoặc từ chối. - Nếu từ chối: yêu cầu
chuyển `TU_CHOI`; đơn tiếp tục quy trình bình thường. - Nếu duyệt: yêu
cầu chuyển `DA_DUYET`; đơn chuyển `DA_HUY`; lượng hàng đã giữ được giải
phóng. - Lưu người xử lý, thời gian xử lý, lý do và lịch sử trạng
thái. - Mọi cập nhật liên quan đến đơn, yêu cầu hủy và kho phải được
thực hiện nhất quán trong transaction. - Không giải phóng hàng ngay khi
khách vừa gửi yêu cầu; hàng tiếp tục được giữ cho đến khi Admin duyệt.

**Ràng buộc cần triển khai:** - Chống gửi nhiều yêu cầu hủy đang chờ cho
cùng một đơn. - Chống Admin duyệt cùng một yêu cầu nhiều lần. - Kiểm tra
đơn có đang ở trạng thái cho phép yêu cầu hủy hay không. - Ghi audit log
cho thao tác duyệt/từ chối. - Ghi lịch sử giữ/giải phóng hàng. - Không
cho phép hủy đơn đã giao thành công theo luồng hủy thông thường.

Khách được gửi yêu cầu hủy từ `DA_XAC_NHAN` đến hết `DANG_CHUAN_BI`
(đã chốt 12/11/2026).

---

## 7. Quản lý kho hàng

### 7.1. Mức độ quản lý

Chọn quản lý kho nâng cao: - Quản lý nhập kho. - Quản lý xuất kho. -
Theo dõi tồn kho. - Theo dõi hàng đang giữ. - Theo dõi hàng khả dụng. -
Lịch sử biến động kho. - Cảnh báo tồn kho thấp/hết hàng.

### 7.2. Các khái niệm tồn kho

Phải phân biệt: - `so_luong_thuc_te`: số lượng hàng vật lý đang có. -
`so_luong_dang_giu`: số lượng được giữ cho các đơn đã xác nhận. -
`so_luong_kha_dung`: số lượng có thể tiếp tục bán.

Công thức:

`so_luong_kha_dung = so_luong_thuc_te - so_luong_dang_giu`

Ví dụ: thực tế 20, đang giữ 5, khả dụng 15.

### 7.3. Thời điểm giữ hàng --- CONFIRM (ĐÃ CHỐT)

1.  Khách tạo đơn: trạng thái chờ xác nhận, chưa giữ hàng.
2.  Admin xác nhận: backend kiểm tra tồn kho; nếu đủ thì giữ hàng trong
    cùng quy trình transaction. Nếu không đủ (đã chốt 10/11/2026): chặn
    xác nhận và báo lỗi rõ cho Admin; đơn vẫn ở `CHO_XAC_NHAN`, không
    xác nhận một phần, không tự hủy.
3.  Khi đơn được giao thành công: ghi nhận xuất bán/giảm hàng thực tế và
    kết thúc giữ hàng.
4.  Khi đơn bị hủy được duyệt hoặc giao thất bại (xem 6.4, không giao
    lại): giải phóng lượng hàng giữ của đơn đó.
5.  Mọi biến động phải được ghi vào lịch sử kho.

### 7.4. Cảnh báo kho --- BOTH (ĐÃ CHỐT)

- Admin nhận cảnh báo khi tồn kho thấp hoặc hết hàng.
- Khách hàng nhìn thấy trạng thái còn hàng, sắp hết hàng hoặc hết hàng
  theo ngưỡng cấu hình.
- Dùng một ngưỡng chung, một giá trị áp dụng toàn hệ thống do Admin
  cấu hình (đã chốt 10/11/2026): khi số lượng khả dụng ≤ ngưỡng thì
  Admin nhận cảnh báo và khách thấy "sắp hết hàng". Không đặt riêng
  theo sản phẩm; không khóa cứng giá trị trong code.

### 7.5. An toàn dữ liệu kho

- Dùng transaction và cơ chế khóa/kiểm soát đồng thời khi xác nhận
  đơn.
- Không để hai đơn cùng giữ vượt quá số lượng khả dụng.
- Mọi lần nhập, xuất, điều chỉnh, giữ và giải phóng hàng phải có bản
  ghi lịch sử.
- Không cho phép sửa trực tiếp số lượng tồn mà không có lý do và lịch
  sử điều chỉnh.
- Khi xử lý yêu cầu hủy được duyệt, chỉ giải phóng đúng số lượng đã
  giữ của đơn đó.

---

## 8. Voucher và khuyến mãi

### 8.1. Nguồn voucher --- BOTH (ĐÃ CHỐT)

Hệ thống hỗ trợ hai nguồn: 1. Voucher do Admin tạo và quản lý. 2.
Voucher người dùng nhận được từ nhiệm vụ/hoạt động khám phá.

### 8.2. Thuộc tính voucher

- Mã voucher.
- Loại giảm giá (phần trăm hoặc số tiền).
- Giá trị giảm.
- Mức giảm tối đa (nếu có).
- Giá trị đơn hàng tối thiểu (nếu có).
- Thời gian bắt đầu/kết thúc.
- Số lượt sử dụng tối đa.
- Số lượt đã sử dụng.
- Điều kiện áp dụng.
- Trạng thái.
- Người sở hữu nếu là voucher cá nhân.

### 8.3. Quy tắc

- Backend xác minh điều kiện áp dụng.
- Không tin số tiền giảm do client gửi.
- Có giới hạn lượt dùng, thời hạn và điều kiện.
- Không cho sử dụng vượt quota.
- Ghi nhận lịch sử sử dụng.
- Xác định rõ voucher dùng chung hay voucher cấp riêng cho người dùng.
- Khi đơn bị hủy, voucher không được hoàn lại lượt dùng (đã chốt
  10/11/2026).

---

## 9. Khám phá vũ trụ 3D

### 9.1. Phạm vi nội dung

Các khu vực chính: 1. Solar System --- Hệ Mặt Trời. 2. Galaxy Explorer
--- Khám phá thiên hà. 3. Nebula Explorer --- Khám phá tinh vân. 4.
Black Hole Explorer --- Khám phá hố đen.

Đã chốt ngày 12/11/2026: Thiên hà xoay và thu phóng toàn cảnh,
Tinh vân xem nền 360°, Hố đen xoay và thu phóng mô hình. Cả ba dùng
thông tin khoa học cơ bản, ưu tiên NASA và ghi nguồn.

Bản chỉnh Giai đoạn 7 áp dụng cho cả bốn khu vực. Cảnh phủ khung
trình duyệt, có nút toàn màn hình, khung chuyển khu vực có hiệu ứng
và bảng thông tin thu gọn. Dùng các gói mô hình gốc cho ba khu vực,
cận cảnh 11 thiên thể và lựa chọn xem cảnh Hệ Mặt Trời gốc.

### 9.2. Phong cách hình ảnh

- Phong cách: **Cinematic Realism**.
- Ưu tiên lựa chọn **B --- Điện ảnh**: hình ảnh, ánh sáng, màu sắc và
  trải nghiệm ấn tượng; dữ liệu khoa học quan trọng vẫn phải chính
  xác.
- Không đánh đổi độ đúng của dữ liệu khoa học chỉ để tạo hiệu ứng đẹp.
- Có thể cách điệu ánh sáng, màu sắc, bố cục và tỷ lệ trình bày để tối
  ưu trải nghiệm.

### 9.3. Solar System

- Có Mặt Trời, 8 hành tinh, Pluto (hành tinh lùn) và Mặt Trăng.
- Thể hiện quỹ đạo.
- Có chuyển động tự quay và quay quanh Mặt Trời ở tốc độ mô phỏng phù
  hợp.
- Người dùng có thể xoay, zoom và di chuyển camera.
- Có thể chọn bất kỳ thiên thể nào trong Hệ Mặt Trời (Mặt Trời, 8
  hành tinh, Pluto, Mặt Trăng) để camera tự động dẫn đến và zoom vào
  mô hình 3D chi tiết, chân thực của thiên thể đó.
- Khi chọn thiên thể, hiển thị panel thông tin.
- Có điều khiển tốc độ mô phỏng và góc nhìn phù hợp.
- Không yêu cầu tỷ lệ kích thước/khoảng cách tuyệt đối giữa các thiên
  thể; sử dụng tỷ lệ trực quan và ghi rõ đây là mô phỏng.
- Dùng số liệu NASA cho Hệ Mặt Trời và ghi liên kết nguồn trong bảng
  thông tin (đã chốt 12/11/2026).

### 9.4. Camera và tương tác --- HYBRID (ĐÃ CHỐT)

- Người dùng được tự do xoay, zoom và điều hướng.
- Người dùng có thể chọn thiên thể để camera tự động di chuyển đến vị
  trí quan sát.
- Chuyển động camera cần mượt, có thể kiểm soát và không gây mất
  phương hướng.

### 9.5. Tài nguyên --- MIXED (ĐÃ CHỐT)

Kết hợp: - Mô hình/texture 3D có sẵn do nhóm chuẩn bị. - Code tạo hiệu
ứng, ánh sáng, chuyển động và tương tác. - Có thể dùng shader tùy trường
hợp. - Không phụ thuộc hoàn toàn vào một nguồn asset hoặc một iframe bên
ngoài.

### 9.6. Công nghệ 3D định hướng

- Three.js.
- React Three Fiber.
- Drei.
- GSAP cho timeline/chuyển cảnh phù hợp.
- GLSL shader khi thực sự cần.
- Blender có thể dùng để kiểm tra/tối ưu asset.

### 9.7. Hiệu năng 3D

- Lazy loading theo khu vực.
- LOD khi phù hợp.
- Tối ưu texture và kích thước model.
- Instancing cho đối tượng lặp.
- Frustum culling.
- Dynamic resolution/adaptive quality.
- Cache tài nguyên hợp lý.
- Giải phóng tài nguyên GPU khi không còn sử dụng.
- Có chế độ giảm hiệu ứng hoặc fallback khi WebGL không hỗ trợ.
- Mục tiêu tham khảo: hướng tới 60 FPS trên máy phổ thông và 30 FPS ổn
  định trên thiết bị yếu; đây là mục tiêu kiểm thử, không phải cam kết
  tuyệt đối cho mọi thiết bị.

### 9.8. Tài nguyên và giấy phép

- Ưu tiên `.glb`/`.gltf`.
- Lưu metadata nguồn, tác giả, license và attribution.
- Kiểm tra quyền sử dụng trước khi đưa vào website.
- Tài liệu riêng cần có: `docs/3d-visual-spec.md` và
  `docs/3d-assets.md`.
- Thư mục gợi ý:

```text
assets/
└── 3d/
    ├── solar-system/
    │   ├── sun/
    │   ├── mercury/
    │   ├── venus/
    │   ├── earth/
    │   ├── moon/
    │   ├── mars/
    │   ├── jupiter/
    │   ├── saturn/
    │   ├── uranus/
    │   ├── neptune/
    │   └── pluto/
    ├── galaxies/
    ├── nebulae/
    ├── black-holes/
    ├── textures/
    ├── environments/
    └── references/
```

---

## 10. Gamification --- nhiệm vụ và phần thưởng

### 10.1. Định hướng --- B kết hợp một phần C (ĐÃ CHỐT)

- Nhiệm vụ tự động ghi nhận tiến độ.
- Có nhiệm vụ bí mật cần tìm kiếm, giải đố hoặc khám phá vị trí/đối
  tượng ẩn.
- Một phần nội dung được tổ chức thành hành trình theo chương, mở khóa
  dần.
- Không biến website thành game quá phức tạp.
- Hoạt động khám phá không bắt buộc người dùng phải mua hàng.

### 10.2. Nhóm nhiệm vụ

- Daily.
- Weekly.
- Discovery.
- Hidden.
- Collection.
- Special Events.

### 10.3. Ví dụ nhiệm vụ

- Khám phá đủ 8 hành tinh, Pluto và Mặt Trăng.
- Tìm hiểu 3 thiên hà.
- Hoàn thành hành trình Hệ Mặt Trời.
- Tìm vật thể ẩn.
- Thu thập huy hiệu.
- Tham gia sự kiện khám phá theo mùa.

Đây là ví dụ minh họa; nội dung, điều kiện và phần thưởng cụ thể sẽ được
Admin cấu hình.

Phạm vi quản trị đã chốt cho đợt bàn giao: Admin được tạo nhiệm vụ theo
một trong ba điều kiện backend đang xác minh: khám phá một thiên thể
trong bộ hành trình, hoàn thành một chương hoặc tìm dấu hiệu ẩn. Admin
được sửa tên và mô tả của ba chương. Thứ tự và danh sách thiên thể của
các chương theo bộ đã duyệt. Nhiệm vụ mới có thể cấp XP, huy hiệu và
voucher cá nhân theo cơ chế hiện có.

### 10.4. Phần thưởng

- XP.
- Huy hiệu.
- Voucher.
- Vật phẩm độc quyền.
- Khung hồ sơ hoặc quyền mở khóa nội dung.

XP không mặc định quy đổi thành tiền.

### 10.5. Xác minh và chống gian lận

- Backend là nơi xác minh điều kiện hoàn thành.
- Không tin dữ liệu tiến độ/phần thưởng do client tự khai báo.
- Chống nhận thưởng trùng.
- Ghi lịch sử cộng/trừ XP.
- Ghi lịch sử nhận thưởng.
- Nhiệm vụ bí mật không gửi toàn bộ đáp án/điều kiện ẩn cho client nếu
  điều đó làm lộ đáp án.
- Có thể cung cấp gợi ý cho nhiệm vụ phù hợp; nếu gợi ý ảnh hưởng phần
  thưởng thì phải công bố quy tắc trước.
- Voucher từ nhiệm vụ phải tuân theo điều kiện, thời hạn và quota.

### 10.6. Sự kiện theo mùa

Có thể hỗ trợ sự kiện giới hạn thời gian, ví dụ: - Lunar Exploration. -
Mars Expedition. - Galaxy Week. - Cosmic Anniversary.

Tên trên chỉ là ví dụ; Admin có thể quản lý nội dung sự kiện.

---

## 11. Thông báo và quản trị

### 11.1. Thông báo USER

- Đơn hàng được xác nhận/cập nhật.
- Yêu cầu hủy được duyệt hoặc từ chối.
- Voucher sắp hết hạn.
- Nhiệm vụ hoàn thành.
- Phần thưởng được cấp.
- Sự kiện mới.

### 11.2. Thông báo ADMIN

- Có đơn hàng mới.
- Có yêu cầu hủy đơn.
- Tồn kho thấp/hết hàng.
- Có đánh giá mới.
- Có hoạt động bất thường cần xem xét.

### 11.3. Phạm vi thông báo

- Sử dụng thông báo trong website (in-app).
- Chưa triển khai push notification/mobile app trong phạm vi hiện tại.
- Website không gửi email thông báo.

### 11.4. Admin dashboard

Các khu vực quản trị: - Dashboard tổng quan. - Quản lý tài khoản/người
dùng. - Quản lý sản phẩm. - Quản lý danh mục. - Quản lý hình ảnh và mô
hình 3D. - Quản lý kho và phiếu nhập. - Quản lý đơn hàng và yêu cầu
hủy. - Quản lý voucher. - Quản lý đánh giá. - Quản lý thiên thể/nội dung
khám phá. - Quản lý hành trình. - Quản lý nhiệm vụ. - Quản lý huy
hiệu/phần thưởng. - Quản lý sự kiện. - Thống kê. - Cấu hình hệ thống. -
Nhật ký hoạt động.

### 11.5. Audit log

Ghi nhận tối thiểu các thao tác quan trọng: - Khóa/mở khóa tài khoản. -
Tạo/sửa/ẩn sản phẩm. - Điều chỉnh kho. - Xác nhận/hủy đơn. - Duyệt/từ
chối yêu cầu hủy. - Tạo/sửa voucher. - Thay đổi nhiệm vụ/phần thưởng. -
Thay đổi cấu hình quan trọng.

---

## 12. Định hướng giao diện và trải nghiệm

### 12.1. Phong cách tổng thể

**Premium Futuristic + Cinematic + Immersive 3D + Interactive.**

### 12.2. Màu sắc và không gian

- Nền Deep Space/Nebula/Galaxy.
- Navy/midnight blue.
- Purple.
- Electric blue.
- Cyan.
- Có thể dùng gradient aurora làm điểm nhấn.
- Ánh sáng/glow có kiểm soát, tránh lạm dụng.

### 12.3. Thành phần giao diện

- Glassmorphism ở những khu vực phù hợp.
- Soft glow.
- Card và panel có chiều sâu.
- Hiệu ứng hover tinh tế.
- Chuyển cảnh mượt.
- Animation theo cuộn trang khi phù hợp.
- Tương tác 3D có phản hồi rõ ràng.
- Typography dễ đọc, phân cấp rõ.
- Khoảng trắng và bố cục nhất quán.
- Responsive trên desktop, tablet và mobile.

### 12.4. Nguyên tắc UX

- Không để hiệu ứng che khuất nội dung hoặc thao tác.
- Không tự phát âm thanh; nếu có âm thanh phải do người dùng chủ động
  bật.
- Có trạng thái loading, empty, error và success rõ ràng.
- Form phải có validation phía client và backend.
- Hạn chế animation nặng trên thiết bị yếu.
- Hỗ trợ `prefers-reduced-motion` hoặc cơ chế giảm chuyển động tương
  đương.
- Các thao tác quan trọng như đặt hàng, hủy đơn, duyệt yêu cầu phải có
  xác nhận và phản hồi rõ ràng.

---

## 13. Định hướng công nghệ

> **Lưu ý:** Stack kỹ thuật dưới đây đã được người dùng duyệt ngày
> 10/11/2026. Nếu trong quá trình làm cần thêm công nghệ ngoài danh
> sách, phải đề xuất và được người dùng duyệt trước khi dùng.

### 13.1. Stack đề xuất

Lớp Công nghệ đề xuất

---

Backend Java, Spring Boot 3
Bảo mật Spring Security
ORM Spring Data JPA / Hibernate
API REST API
Validation Bean Validation
Build Maven
Frontend React + TypeScript + Vite
UI/CSS Tailwind CSS
3D Three.js + React Three Fiber + Drei
Animation GSAP kết hợp animation của Three.js
Database MySQL 8
Migration Flyway
Kiểm thử API Postman
Quản lý mã nguồn Git/GitHub
Thiết kế UI Figma

### 13.2. Kiến trúc đề xuất

- Modular Monolith.
- Không dùng Microservices trong phạm vi hiện tại.
- Backend chia module theo miền nghiệp vụ.
- Frontend tách component, page, feature và service/API.
- Tách cấu hình môi trường khỏi mã nguồn.
- Không để frontend truy cập trực tiếp database.
- API phải xác thực, phân quyền và validate ở backend.

### 13.3. Quy tắc naming

Yêu cầu đã thống nhất: tên bảng, cột và identifier dùng tiếng Việt không
dấu để nhất quán và tránh vấn đề tương thích.

- Database: `snake_case`, chữ thường, tiếng Việt không dấu. Ví dụ:
  `nguoi_dung`, `san_pham`, `don_hang`, `nhiem_vu`.
- Java class: PascalCase, tiếng Việt không dấu. Ví dụ: `NguoiDung`,
  `SanPhamController`.
- Biến/phương thức: camelCase, tiếng Việt không dấu.
- Tên hiển thị UI, tài liệu và comment: tiếng Việt có dấu.
- Không trộn Anh--Việt tùy tiện trong cùng một identifier.
- Tên chuẩn kỹ thuật bắt buộc của framework/API/thư viện được giữ
  nguyên.

---

## 14. Định hướng thiết kế database

### 14.1. Nguyên tắc

- MySQL 8.
- Với phân hệ tài khoản, hoàn tất data dictionary trong `docs/database.md` trước khi viết migration Flyway. ERD được vẽ sau khi hoàn thiện website, theo xác nhận bước 3.3.
- Chuẩn hóa dữ liệu hợp lý, tránh lưu trùng thông tin có thể suy ra.
- Có PK, FK, UNIQUE, CHECK/index phù hợp.
- Dùng `DECIMAL` cho tiền, không dùng `FLOAT`.
- Dùng kiểu dữ liệu thời gian nhất quán.
- Có `created_at`, `updated_at` cho bảng nghiệp vụ phù hợp.
- Dùng soft delete hoặc trạng thái ẩn cho dữ liệu cần giữ lịch sử.
- Đơn hàng phải lưu snapshot giá và thông tin giao nhận.
- Tách trạng thái đơn hàng, trạng thái yêu cầu hủy và trạng thái thanh
  toán.
- XP, voucher, kho và phần thưởng phải có ledger/lịch sử.
- Các thao tác đa bảng cần transaction.

### 14.2. Các nhóm bảng dự kiến

Danh sách này định hướng các nhóm bảng. Tên và quan hệ của phân hệ tài
khoản được đặc tả trong `docs/database.md` trước migration.

**Tài khoản** - `nguoi_dung` - `vai_tro` - `nguoi_dung_vai_tro` -
`dia_chi_nguoi_dung` - `thong_bao`

Các bảng và cột xác minh cũ được giữ theo migration đã phát hành. Ứng
dụng hiện không sử dụng chúng.

**Cửa hàng** - `danh_muc` - `san_pham` - `hinh_anh_san_pham` -
`mo_hinh_3d_san_pham` - `danh_gia` - `yeu_thich`

**Giỏ hàng và đơn hàng** - `gio_hang` - `chi_tiet_gio_hang` -
`don_hang` - `chi_tiet_don_hang` - `yeu_cau_huy_don` -
`lich_su_trang_thai_don`

**Kho** - `ton_kho` - `phieu_nhap_kho` - `chi_tiet_phieu_nhap` -
`giao_dich_kho` - `lich_su_giu_hang`

**Voucher** - `voucher` - `voucher_nguoi_dung` -
`lich_su_su_dung_voucher`

**Khám phá 3D** - `loai_thien_the` - `thien_the` - `tai_nguyen_3d` -
`hanh_trinh_kham_pha` - `chi_tiet_hanh_trinh`

**Gamification** - `nhiem_vu` - `tien_do_nhiem_vu` - `phan_thuong` -
`lich_su_nhan_thuong` - `huy_hieu` - `huy_hieu_nguoi_dung` -
`diem_kham_pha` - `cap_do`

**Hệ thống** - `nhat_ky_hoat_dong` - `cau_hinh_he_thong` - `su_kien`

### 14.3. Quan hệ chính cần thể hiện

- Người dùng 1--N địa chỉ.
- Người dùng 1--N đơn hàng.
- Người dùng 1--1 giỏ hàng hoạt động (đã chốt 10/11/2026: mỗi người
  dùng một giỏ).
- Danh mục 1--N sản phẩm.
- Sản phẩm 1--N hình ảnh.
- Sản phẩm 1--N mô hình 3D nếu cần nhiều biến thể/tài nguyên.
- Đơn hàng 1--N chi tiết đơn hàng.
- Đơn hàng 1--N lịch sử trạng thái.
- Đơn hàng 1--N yêu cầu hủy theo lịch sử; chỉ cho phép tối đa một yêu
  cầu đang chờ tại một thời điểm.
- Sản phẩm 1--1 bản ghi tồn kho.
- Người dùng 1--N tiến độ nhiệm vụ.
- Người dùng 1--N giao dịch XP.
- Thiên thể N--1 loại thiên thể.
- Thiên thể 1--N tài nguyên 3D.
- Các quan hệ N--N được biểu diễn bằng bảng liên kết.

---

## 15. Bảo mật, độ tin cậy và chất lượng

### 15.1. Bảo mật

- BCrypt cho mật khẩu.
- Kiểm tra mật khẩu hiện tại khi đổi mật khẩu và vô hiệu hóa các phiên cũ
  sau khi đổi thành công.
- Phân quyền ở backend.
- Validate dữ liệu đầu vào ở backend.
- Chống SQL injection bằng ORM/parameter binding.
- Giới hạn tần suất với endpoint nhạy cảm.
- Không lưu secret trong Git.
- CORS và cấu hình cookie/token cần phù hợp với phương thức xác thực
  đã chọn.
- Không trả stack trace hoặc thông tin nhạy cảm cho người dùng cuối.

### 15.2. Tính nhất quán

- Đặt hàng, giữ kho, hủy đơn và giải phóng kho phải có transaction phù
  hợp.
- Không tin tổng tiền, giá, XP hoặc trạng thái do client gửi.
- Chống double submit và xử lý lặp.
- Có unique constraint/idempotency phù hợp ở các luồng dễ bị gửi lặp.
- Có log để truy vết thao tác quản trị và biến động quan trọng.

### 15.3. Kiểm thử

- Unit test cho nghiệp vụ quan trọng.
- Integration test cho repository, transaction và API.
- Kiểm thử phân quyền.
- Kiểm thử luồng đặt hàng COD.
- Kiểm thử đồng thời giữ kho.
- Kiểm thử hủy đơn trước/sau khi xác nhận.
- Kiểm thử duyệt/từ chối yêu cầu hủy.
- Kiểm thử chống nhận XP/voucher trùng.
- Kiểm thử điều kiện đánh giá sản phẩm.
- Kiểm thử responsive và khả năng fallback 3D.
- Kiểm thử hiệu năng các khu vực 3D trên thiết bị mục tiêu.

---

## 16. Cấu trúc thư mục và tài liệu dự kiến

```text
GALAXORA/
├── plan.md
├── README.md
├── AGENTS.md
├── docs/
│   ├── requirements.md
│   ├── database.md
│   ├── architecture.md
│   ├── design-system.md
│   ├── api-specification.md
│   ├── 3d-visual-spec.md
│   ├── 3d-assets.md
│   └── development-roadmap.md
├── backend/
├── frontend/
├── database/
│   ├── migrations/
│   └── seed-data/
└── assets/
    ├── images/
    ├── textures/
    └── 3d-models/
```

### 16.1. Vai trò tài liệu

- `plan.md`: mục tiêu, phạm vi, quyết định và kế hoạch tổng thể.
- `README.md`: hướng dẫn cài đặt, cấu hình và chạy dự án.
- `AGENTS.md`: quy tắc bắt buộc cho AI Coding Agent.
- `requirements.md`: yêu cầu chức năng/phi chức năng và use case.
- `database.md`: ERD, data dictionary, quy tắc dữ liệu và migration.
- `architecture.md`: kiến trúc backend/frontend và luồng tích hợp.
- `design-system.md`: màu sắc, typography, component và UX.
- `api-specification.md`: endpoint, request/response, auth và lỗi.
- `3d-visual-spec.md`: phạm vi, camera, tương tác và tiêu chuẩn hiển
  thị 3D.
- `3d-assets.md`: danh mục model, texture, license, nguồn và trạng
  thái.
- `development-roadmap.md`: giai đoạn, công việc, người phụ trách và
  tiêu chí nghiệm thu.

---

## 17. Kế hoạch triển khai theo giai đoạn

Kế hoạch dưới đây là lộ trình tham khảo. Thời lượng thực tế phụ thuộc
năng lực nhóm, mức độ sẵn sàng của asset 3D và tiến độ tích hợp.

---

Giai đoạn Công việc chính Kết quả cần có

---

1\. Phân tích Chốt requirements, use Bộ yêu cầu được duyệt
case, quy tắc nghiệp vụ

2\. Thiết kế ERD, data dictionary, Tài liệu thiết kế
kiến trúc,  
 wireframe/design system

3\. Nền tảng Khởi tạo Đăng ký/đăng nhập/phân
backend/frontend, cấu quyền
hình DB, migration,  
 auth

4\. Cửa hàng Danh mục, sản phẩm, Cửa hàng hoạt động
hình ảnh, tìm kiếm/lọc

5\. Kho Nhập kho, tồn kho, giữ Quản lý kho đúng nghiệp
hàng, lịch sử, cảnh báo vụ

6\. Đơn hàng Giỏ hàng, checkout, Luồng mua hàng hoàn
COD, trạng thái, hủy chỉnh
đơn

7\. 3D Solar System, Galaxy, Khu vực 3D có tương tác
Nebula, Black Hole,  
 product viewer

8\. Gamification Nhiệm vụ, tiến độ, XP, Hệ thống khám phá/phần
cấp độ, huy hiệu, thưởng
voucher

9\. Quản trị Dashboard và các module Admin vận hành được hệ
quản lý thống

10\. Hoàn thiện Kiểm thử, tối ưu, sửa Bản nghiệm thu
lỗi, tài liệu, triển  
 khai

---

### 17.1. Phân chia nhóm

Với nhóm 2--3 người, có thể phân công:

- **Thành viên phụ trách backend/database:** tài khoản, phân quyền,
  API, database, kho và đơn hàng.
- **Thành viên phụ trách frontend/3D:** UI/UX, React, cửa hàng, trải
  nghiệm khám phá 3D.
- **Thành viên thứ ba nếu có:** giỏ hàng/checkout, trang quản trị,
  kiểm thử, tích hợp và tài liệu.

Nếu chỉ có 2 người, cần thống nhất API contract và database sớm; hai
thành viên phối hợp kiểm thử tích hợp thường xuyên. Phân công cuối cùng
phải dựa trên kỹ năng và thời gian thực tế của nhóm.

---

## 18. Tiêu chí nghiệm thu tổng thể

Dự án chỉ được xem là đạt khi:

1.  Người dùng có thể đăng ký, đăng nhập ngay bằng email hoặc số điện
    thoại kết hợp mật khẩu, đăng xuất và đổi mật khẩu khi biết mật khẩu
    hiện tại. Các chức năng hoạt động mà không cần dịch vụ gửi email.
2.  Phân quyền USER/ADMIN được kiểm tra ở backend.
3.  Admin quản lý được danh mục và sản phẩm.
4.  Người dùng tìm kiếm, xem chi tiết và quản lý giỏ hàng.
5.  Checkout tạo được đơn COD với giá/phí được backend tính lại.
6.  Đơn hàng có lịch sử trạng thái.
7.  Hủy đơn trước xác nhận và yêu cầu hủy sau xác nhận hoạt động đúng
    quy tắc APPROVE.
8.  Kho giữ hàng khi Admin xác nhận, không oversell và giải phóng đúng
    khi hủy được duyệt.
9.  Lịch sử nhập/xuất/điều chỉnh/giữ/giải phóng kho được lưu.
10. Voucher Admin tạo và voucher từ nhiệm vụ được xác minh đúng điều
    kiện.
11. Chỉ người mua đã nhận hàng thành công mới đánh giá được sản phẩm.
12. Các khu vực 3D có điều khiển, tương tác và fallback phù hợp.
13. Nhiệm vụ tự động/ẩn có xác minh backend, không nhận thưởng trùng.
14. Hồ sơ hiển thị XP, cấp độ, huy hiệu và tiến trình khám phá.
15. Admin có thể quản lý nội dung, nhiệm vụ, voucher và hoạt động chính.
16. Giao diện responsive, có trạng thái lỗi/loading/empty rõ ràng.
17. Các luồng nghiệp vụ quan trọng có kiểm thử.
18. Tài liệu cài đặt, cấu hình và sử dụng được cập nhật.

---

## 19. Các quyết định đã chốt và các mục còn chờ xác nhận

### 19.1. Đã chốt

---

Hạng mục Quyết định

---

Tên dự án GALAXORA

Mô hình kinh doanh Một cửa hàng duy nhất

Vai trò ADMIN, USER

Database định hướng MySQL

Naming database/code Tiếng Việt không dấu

Phạm vi 3D Hệ Mặt Trời, thiên hà, tinh vân,
hố đen

Thiên thể Hệ Mặt Trời Mặt Trời, 8 hành tinh, Pluto (hành tinh
lùn) và Mặt Trăng; chọn thiên thể thì
camera zoom vào mô hình chi tiết

Khách truy cập Xem toàn bộ nội dung công khai (cửa
hàng, chi tiết sản phẩm, khám phá 3D);
chức năng gắn tài khoản cần đăng nhập

Phong cách 3D Cinematic Realism, ưu tiên điện
ảnh nhưng dữ liệu quan trọng chính
xác

Camera Hybrid: tự do điều khiển và chọn
thiên thể để camera dẫn đến

Tài nguyên 3D Mixed: asset có sẵn kết hợp code
hiệu ứng/tương tác

Gamification Tự động + nhiệm vụ bí mật + một
phần hành trình theo chương

Kho Advanced

Cảnh báo kho Admin và khách hàng đều nhìn thấy
trạng thái phù hợp

Thời điểm giữ hàng Khi Admin xác nhận đơn

Hủy trước xác nhận Khách tự hủy

Hủy sau xác nhận APPROVE: khách gửi yêu cầu, Admin
duyệt/từ chối

Voucher Admin tạo và người dùng nhận từ
nhiệm vụ

Đánh giá Chỉ người mua đã nhận hàng thành
công

Phí vận chuyển Mức cố định do Admin cấu hình, áp dụng toàn quốc

Thanh toán COD

Social login Không triển khai trong phạm vi
hiện tại

Thông báo In-app; chưa có push/mobile app

Gửi email Đã bỏ toàn bộ theo quyết định ngày 19/11/2026

Stack kỹ thuật Duyệt theo mục 13 (Spring Boot 3, React +
TypeScript + Vite, Tailwind, Three.js/R3F/Drei,
MySQL 8, Flyway); thêm công nghệ khi làm phải
được người dùng duyệt trước

Xác thực phiên Session phía server qua cookie

Giỏ hàng Mỗi người dùng một giỏ hoạt động

Giao thất bại Giải phóng ngay hàng đang giữ, đơn kết
thúc, không giao lại

Voucher khi hủy Không hoàn lại voucher/lượt dùng

Đánh giá (sửa) Cho sửa tự do, không giới hạn thời hạn, lưu bản trước và giữ trạng thái hiển thị

Thời hạn gửi yêu cầu hủy Đến hết `DANG_CHUAN_BI`

Đánh giá (giới hạn) Một đánh giá cho mỗi người dùng, mỗi
sản phẩm và mỗi đơn

Ngưỡng tồn kho Một ngưỡng chung, một giá trị toàn hệ
thống do Admin cấu hình

Thiếu hàng khi xác nhận Chặn xác nhận, báo lỗi cho Admin; đơn giữ
nguyên `CHO_XAC_NHAN`

Admin hủy đơn Được hủy đơn `CHO_XAC_NHAN`, kèm lý do và
audit log

Lịch sử xem gần đây Không làm (đã chốt 11/11/2026); xem
nhanh và sản phẩm liên quan thuộc phạm vi

---

### 19.2. Cần xác nhận hoặc đặc tả chi tiết trước khi triển khai liên quan

---

Hạng mục Nội dung cần chốt

---

Khôi phục tài khoản và đổi thông tin liên hệ Chờ chốt quy trình xác minh
không dùng email trước khi triển khai.

Phí vận chuyển Giá cố định cụ thể do Admin nhập khi cấu hình

Nhiệm vụ Ba chương đã chốt gồm bốn hành tinh đá, bốn hành tinh ngoài,
Mặt Trăng và Pluto. Mở bảng thông tin khi đăng nhập ghi nhận khám phá
kể cả ở 2D. Nhiệm vụ bí mật dùng dấu hiệu trong Hệ Mặt Trời. Hệ thống
giữ quyền nhận voucher nếu chưa cấp được. Xem
`docs/development-roadmap.md` mục 4.

Asset 3D Danh sách file, license, nguồn,
tác giả và attribution

Triển khai Hosting, tên miền, môi trường production. Đợt bàn giao đã
chốt nghiệm thu cục bộ, có hướng dẫn sao lưu và kiểm tra khôi phục MySQL.

Nơi lưu session Session lưu ở đâu (bộ nhớ, database hay
nơi khác) và cấu hình cookie khi triển khai;
sẽ đề xuất trong `docs/architecture.md`

Tagline Xác nhận có sử dụng tagline tham
khảo hay không

---

Không tự suy đoán các mục này. Có thể tiếp tục thiết kế phần không phụ
thuộc chúng, nhưng phải ghi rõ giả định và không khóa cứng các giá trị
chưa được duyệt.

---

## 20. Chỉ dẫn bắt buộc cho AI Coding Agent

1.  Đọc `plan.md`, `AGENTS.md` và tài liệu liên quan trước khi code.
2.  Trước mỗi giai đoạn, nêu rõ mục tiêu, file dự kiến thay đổi và tiêu
    chí hoàn thành.
3.  Không tự ý đổi stack, database, naming convention hoặc nghiệp vụ đã
    chốt.
4.  Không tự ý xóa hoặc viết lại toàn bộ module khi chỉ cần sửa một lỗi.
5.  Không thay đổi UI/UX ngoài phạm vi yêu cầu được giao.
6.  Không tạo endpoint giả, dữ liệu giả hoặc nút bấm không có chức năng
    để trình diễn như tính năng thật.
7.  Không để frontend quyết định giá, quyền, XP, tồn kho hoặc kết quả
    duyệt hủy.
8.  Dùng migration có kiểm soát; không sửa schema production bằng thao
    tác thủ công không ghi nhận.
9.  Với thay đổi database, phải cập nhật ERD/data dictionary/API liên
    quan.
10. Với thay đổi API, phải cập nhật API specification và kiểm tra
    frontend tương thích.
11. Với thay đổi nghiệp vụ kho/đơn hàng/XP/voucher, phải bổ sung hoặc
    cập nhật test.
12. Không đưa secret, mật khẩu, API key hoặc thông tin môi trường vào
    Git.
13. Kiểm tra license của asset trước khi tích hợp.
14. Tối ưu tài nguyên 3D; không tải tất cả model lớn ngay khi mở trang.
15. Sau mỗi thay đổi, chạy build và các test liên quan; báo cáo kết quả
    thực tế.
16. Nếu chưa thể chạy test, phải nói rõ lý do; không tuyên bố đã kiểm
    thử thành công.
17. Không đánh dấu task hoàn thành nếu còn lỗi build, lỗi test hoặc chức
    năng chưa hoạt động.
18. Khi phát hiện yêu cầu mâu thuẫn, dừng phần bị ảnh hưởng và hỏi người
    dùng trước khi quyết định.

---

## 21. Trạng thái hiện tại và bước tiếp theo

`plan.md` là tài liệu tổng hợp định hướng và các quyết định đã có. Sau
khi người dùng duyệt nội dung, trình tự công việc tiếp theo:

1.  Chốt các mục còn mở có ảnh hưởng trực tiếp đến kiến trúc (đã chốt
    một phần ngày 10/11/2026, xem 19.1; các mục còn lại xem 19.2).
2.  Đặc tả dữ liệu tài khoản trong `docs/database.md` trước migration
    Flyway của phân hệ. Vẽ ERD sau khi hoàn thiện website.
3.  Duy trì `docs/requirements.md` và `docs/database.md` theo các
    quyết định đã duyệt.
4.  Chốt kiến trúc và API contract.
5.  Xây dựng `AGENTS.md` để hướng dẫn AI Coding Agent.
6.  Chỉ sau khi tài liệu nền tảng được duyệt mới bắt đầu khởi tạo source
    code và triển khai theo giai đoạn.

**Nguyên tắc cuối cùng:** GALAXORA được phát triển theo từng giai đoạn
có kiểm soát. Tính đúng nghiệp vụ, tính nhất quán dữ liệu và khả năng
bảo trì quan trọng không kém giao diện 3D đẹp mắt.
