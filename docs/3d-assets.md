# Tài nguyên 3D

Đã kiểm tra thư mục người dùng cung cấp ngày 12/11/2026. Cả 15 gói có một tệp scene.gltf phiên bản 2.0, một buffer nhị phân và license.txt. Các tham chiếu buffer và ảnh đều trỏ đến tệp có trong đúng gói. Thông tin dưới đây lấy từ license.txt và asset.extras trong tệp glTF.

Mỗi gói ghi giấy phép **CC BY 4.0**. Trang khám phá hiển thị tên tác phẩm, tác giả, liên kết nguồn và giấy phép. Bản triển khai giữ license.txt của từng mô hình. [Creative Commons Attribution 4.0](https://creativecommons.org/licenses/by/4.0/)

| Gói gốc | Tác phẩm và tác giả | Nén / giải nén (MiB) | Tích hợp | Nguồn |
|---|---|---:|---|---|
| sun.zip | Sun · SebastianSosnowski | 1,85 / 2,02 | Mặt Trời | [Sketchfab](https://sketchfab.com/3d-models/sun-9ef1c68fbb944147bcfcc891d3912645) |
| mercury_planet.zip | Mercury (planet) · SebastianSosnowski | 53,84 / 54,01 | Sao Thủy | [Sketchfab](https://sketchfab.com/3d-models/mercury-planet-ccb6c6a9ac3742109cc67c0f16032b49) |
| venus.zip | Venus · Akshat | 17,47 / 17,58 | Sao Kim | [Sketchfab](https://sketchfab.com/3d-models/venus-d497ce25553447f3b7b679110c85cfa1) |
| earth.zip | EARTH · Stéphane Agullo | 46,98 / 69,06 | Trái Đất | [Sketchfab](https://sketchfab.com/3d-models/earth-5ce4b1465c83432d9bb7e3c30232c02b) |
| moon.zip | Moon · Akshat | 19,77 / 19,89 | Mặt Trăng | [Sketchfab](https://sketchfab.com/3d-models/moon-4db2273f6dd943b8ad7fa5e3b1b2431a) |
| mars.zip | Mars · AirStudios | 12,83 / 13,63 | Sao Hỏa | [Sketchfab](https://sketchfab.com/3d-models/mars-5fc8b7168b044527a05ed3903c998b65) |
| jupiter.zip | Jupiter · Nestaeric | 55,30 / 102,61 | Sao Mộc | [Sketchfab](https://sketchfab.com/3d-models/jupiter-6380860bdb4247b98b30643615c3afab) |
| saturn.zip | Saturn · Nestaeric | 79,74 / 143,09 | Sao Thổ | [Sketchfab](https://sketchfab.com/3d-models/saturn-c09a1970148c43ad99db134a9d6d00b5) |
| uranus.zip | Uranus · Nestaeric | 34,03 / 83,17 | Sao Thiên Vương | [Sketchfab](https://sketchfab.com/3d-models/uranus-0009a69dbace44608c0bd09af9ba20db) |
| neptune.zip | Neptune · Nestaeric | 13,13 / 31,38 | Sao Hải Vương | [Sketchfab](https://sketchfab.com/3d-models/neptune-2a6f9ccc5c724a709912774caa197b77) |
| pluto.zip | Pluto · Akshat | 0,29 / 0,40 | Pluto | [Sketchfab](https://sketchfab.com/3d-models/pluto-a30ff3a5f4f3477d87fc534e0d1df7e2) |
| Solar System.zip | Solar System animation · Samer_Arab_S5 | 8,21 / 10,18 | Chế độ Cảnh gốc và bản đồ toàn cảnh của Sao Mộc, Sao Thổ, Sao Thiên Vương và Sao Hải Vương | [Sketchfab](https://sketchfab.com/3d-models/solar-system-animation-b7c69a6b655b47c99f871d5ec5aee854) |
| Galaxy Explorer.zip | Need some space? · Loïc Norgeot | 0,56 / 1,34 | Toàn cảnh Thiên hà | [Sketchfab](https://sketchfab.com/3d-models/need-some-space-d6521362b37b48e3a82bce4911409303) |
| Nebula Explorer.zip | Space nebula HDRI panorama 360 skydome · Aliaksandr.melas | 3,55 / 3,63 | Toàn cảnh 360° Tinh vân | [Sketchfab](https://sketchfab.com/3d-models/space-nebula-hdri-panorama-360-skydome-99bbafab741e406593796cc77b357b2a) |
| Black Hole Explorer.zip | Black Hole · NestaEric | 17,75 / 29,87 | Mô hình Hố đen | [Sketchfab](https://sketchfab.com/3d-models/black-hole-e410da98b1e5445eae2acafaaa53587d) |

## Bản Hệ Mặt Trời trên web

Tệp được phục vụ từ các thư mục con của [solar-system](../assets/3d/solar-system/). Mỗi thiên thể có `be-mat.webp` dùng ở toàn cảnh, `ban-goc.glb` tự chứa dùng khi chọn và `LICENSE.txt` gốc. [ghi-cong.json](../assets/3d/solar-system/ghi-cong.json) giữ metadata trong gói. Gói `Solar System.zip` được đóng riêng ở `toan-canh/ban-goc.glb`.

Toàn cảnh mô phỏng dùng các ảnh bề mặt nhẹ khoảng 228 KiB. Vành Sao Thổ và quầng Mặt Trời trong chế độ đó được dựng bằng mã, rồi ẩn khi mô hình chi tiết gốc đã tải. Các quỹ đạo, tỷ lệ và tốc độ ở chế độ mô phỏng thuộc ứng dụng. Chế độ Cảnh gốc giữ bố cục và hoạt ảnh của tác giả.

Bốn bản đồ toàn cảnh từ `Solar System.zip` dùng phép chiếu phù hợp với hình cầu tổng quan. Ghi công nằm trong mục **Nguồn mô hình và bản đồ**. Cảnh gốc khoảng **10,14 MiB**. Bản chi tiết lớn nhất là Sao Thổ, khoảng **143 MiB**, chỉ tải khi chọn.

## Bản Thiên hà, Tinh vân và Hố đen trên web

| Khu vực | Thư mục trong `assets/3d/` | GLB (MiB) | Dữ liệu gốc |
|---|---|---:|---|
| Thiên hà | `galaxies/` | 1,34 | 50.000 điểm và màu đỉnh |
| Tinh vân | `nebulae/` | 3,63 | Mặt cầu nền và ảnh phát sáng gốc |
| Hố đen | `black-holes/` | 29,85 | 534.196 tam giác, các lớp đĩa và hành tinh trong gói |

Mỗi thư mục có `ban-goc.glb` tự chứa, `goc-nhin.webp` chụp từ cảnh dựng trên web và `LICENSE.txt` gốc. Mỗi trang hiển thị tên tác phẩm, tác giả, nguồn và giấy phép. D12 đã chốt xem toàn cảnh ở cả Thiên hà và Tinh vân.

Trang chủ tái sử dụng ba ảnh `goc-nhin.webp` trên cùng với `earth/be-mat.webp` và `moon/be-mat.webp`. CSS cắt khung, chỉnh màu, tạo quầng và làm mờ mép ảnh. Các nguồn, tác giả, giấy phép CC BY 4.0 và ghi công tương ứng ở danh mục trên và trong từng thư mục. Bộ ảnh tải trên Trang chủ chỉ gồm ảnh 2D.

## Cách đóng gói và hiển thị

15 GLB gộp JSON glTF, buffer và ảnh PNG/JPEG gốc thành tệp tự chứa. Buffer hình học và từng ảnh đã được đối chiếu byte. Các node, mesh, vật liệu, extension và hoạt ảnh giữ nguyên. Trình dựng GALAXORA hỗ trợ vật liệu specular/glossiness cũ bằng cách đọc RGB cho phản xạ và alpha cho độ bóng. Camera, ánh sáng và cách hiển thị điểm vẫn theo trình dựng GALAXORA. Hố đen dùng vùng đĩa làm tâm camera mở đầu, phần hành tinh vẫn nằm trong mô hình.

Đợt chỉnh đồ họa dùng cùng 15 GLB và thay đổi cách lọc texture, ánh sáng và xử lý độ sáng trong trình dựng. Ba ảnh `goc-nhin.webp` được cập nhật từ khung hình sau khi chỉnh. [Đặc tả ánh sáng](3d-visual-spec.md)

Đợt đối chiếu riêng Thiên hà và Hố đen dùng 50.000 điểm gốc và ba texture phát sáng gốc. Màu lớp phát sáng được cân lúc dựng khung hình. Hai ảnh dự phòng `galaxies/goc-nhin.webp` và `black-holes/goc-nhin.webp` được chụp lại từ trình dựng mới.

## Gói tải tối ưu

Bản build giữ một tệp tải cho mỗi mô hình. Chín mô hình dùng gzip khi
giảm được ít nhất 15% dung lượng. Các GLB nguồn vẫn nằm trong từng thư
mục để bảo trì và kiểm tra. Mô hình giải nén có cùng SHA-256 với GLB
nguồn, gồm hình học, ảnh, vật liệu, extension và hoạt ảnh. Tên tác
phẩm, tác giả, giấy phép và đường dẫn ghi công tiếp tục dùng metadata
của nguồn.

| Mô hình | GLB nguồn (MiB) | Gói tải (MiB) | Giảm |
|---|---:|---:|---:|
| Hố đen | 29,85 | 17,99 | 39,8% |
| Thiên hà | 1,34 | 0,58 | 56,3% |
| Trái Đất | 69,04 | 47,73 | 30,9% |
| Sao Mộc | 102,59 | 55,45 | 45,9% |
| Sao Hải Vương | 31,37 | 13,14 | 58,1% |
| Pluto | 0,40 | 0,29 | 25,9% |
| Sao Thổ | 143,04 | 80,00 | 44,1% |
| Cảnh Hệ Mặt Trời gốc | 10,14 | 8,25 | 18,7% |
| Sao Thiên Vương | 83,15 | 34,04 | 59,1% |
| **Tổng 15 mô hình** | **581,68** | **368,23** | **36,7%** |

Tên tệp nén là `ban-goc.glb.gz`. Trình tải mở đúng tệp theo khu vực
hoặc thiên thể, giải nén rồi chuyển GLB cho trình dựng. Trình duyệt dùng
luồng giải nén có sẵn hoặc bộ giải nén đi kèm Three.js. Máy chủ tệp tĩnh
có thể phục vụ gói gzip dạng nhị phân. Nếu khai báo `Content-Encoding: gzip`,
trình duyệt nhận GLB đã giải nén trước khi chuyển cho trình tải. Khi thay
mô hình nguồn, tạo lại biến thể gzip cùng thư mục và đối chiếu SHA-256.
Nếu thêm hay bỏ biến thể gzip, cập nhật phạm vi glob trong [trình tải](../frontend/src/khampha/taiMoHinhKhamPha.ts).

## Bộ ảnh cửa hàng

Nguồn là thư mục “Ảnh sản phẩm bán hàng cho website golaxora” do người dùng cung cấp ngày 19/11/2026. Có 39 ảnh thuộc bốn nhóm. Frontend lưu bản WebP tối đa 480 px cho thẻ và 1.280 px cho khung lớn tại `public/anh-san-pham`. [Danh mục ảnh](../frontend/src/trangchu/anhSanPham.json)

Thư mục nguồn chưa kèm thông tin tác giả hoặc giấy phép của từng ảnh. Quyền sử dụng thương mại cần được xác nhận trước khi công khai.
