# Nạp 39 sản phẩm mẫu GALAXORA

Chạy trên localhost khi đã đăng nhập bằng tài khoản ADMIN của cửa hàng. Bộ seed chỉ chứa dữ liệu sản phẩm mẫu, kèm giá, ảnh và lượng hàng để thử nghiệm. Dùng mã có tiền tố `GX-MAU-` để tìm các dòng này trong Quản trị.

1. Khi cả frontend và backend đang chạy, chép `nap-san-pham-mau.html`, `napSanPhamMau.mjs` và `duLieuSanPhamMau.mjs` vào cùng một thư mục tạm có tên `.galaxora-seed-local` bên trong `frontend/`.
2. Mở `http://localhost:5173/.galaxora-seed-local/nap-san-pham-mau.html` bằng đúng phiên ADMIN. Bấm **Nạp sản phẩm mẫu** và đợi trang hiển thị bảng đối chiếu dưới dạng JSON.
3. Bấm lại để xem dữ liệu đã ghi. Seed dùng SKU cố định và một khóa phiếu nhập cố định. Sau khi có phiếu, seed chỉ đọc kết quả. Các chỉnh sửa và thao tác xóa sau đó được giữ.
4. Gỡ ba bản sao tạm khỏi frontend sau khi sử dụng. Giữ bộ seed gốc trong thư mục này.

Seed dùng API đang có của Quản trị để ghi sản phẩm và lập phiếu nhập có lý do. Nếu ngưỡng chung chưa được đặt, seed đặt 5 theo phạm vi nghiệm thu. Nếu đã được cấu hình, seed dùng giá trị hiện hành để phân nhóm tồn kho. Quản trị có thể sửa giá, chọn ảnh, điều chỉnh tồn có lý do hoặc xóa sản phẩm mẫu sau khi nghiệm thu.
