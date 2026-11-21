# Sơ đồ use case cập nhật

## Tổng quát và Khám phá 3D

```mermaid
flowchart LR
    K["Khách truy cập"]
    U["Người dùng"]
    A["Quản trị viên"]
    subgraph C["GALAXORA · Nội dung công khai"]
        H(["Duyệt cửa hàng và xem sản phẩm"])
        V(["Khám phá vũ trụ 3D"])
        T(["Chọn thiên thể trong Hệ Mặt Trời"])
        Z(["Camera tiến đến mô hình chi tiết"])
        N(["Đọc thông tin thiên thể"])
        V --- T
        T --> Z
        T --> N
    end
    K --> H
    K --> V
    U --> H
    U --> V
    U --> GM(["Ghi nhận khám phá và nhiệm vụ"])
    A --> Q(["Quản lý nội dung, cửa hàng, kho và đơn"])
```

Danh sách chọn của Hệ Mặt Trời gồm Mặt Trời, tám hành tinh, Mặt Trăng và Pluto.

## Cửa hàng và đơn hàng

```mermaid
flowchart LR
    U["Người dùng"] --> D(["Đánh giá sản phẩm trong đơn đã giao"])
    U --> S(["Sửa đánh giá của mình"])
    S --> L(["Lưu phiên bản trước và giữ trạng thái hiển thị"])
    A["Quản trị viên"] --> H(["Hủy đơn đang chờ xác nhận"])
    H --> G(["Ghi lý do và nhật ký"])
    A --> Y(["Duyệt hoặc từ chối yêu cầu hủy"])
    A --> C(["Xác nhận đơn và giữ hàng"])
```

Sơ đồ cập nhật D1, D2, D13 và D14. Các hình JPG lưu bản use case ban đầu.
