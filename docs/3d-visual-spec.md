# Đặc tả trải nghiệm Khám phá vũ trụ 3D

## Toàn màn hình và chuyển khu vực

Giao diện bốn khu dùng viền sáng xanh tím, nền tinh vân trên lớp trang trí và bảng thông tin kính bên phải. Lớp trang trí ở ngoài canvas và cho thao tác chuột đi qua. Dock cuộn ngang của Hệ Mặt Trời có đúng 11 thiên thể từ danh sách của ứng dụng. Bảng thông tin dùng dữ liệu NASA hiện có và nội dung từ API khi có. XP trong Hành trình đọc từ hồ sơ đã đăng nhập.

Ảnh nền giao diện [tinh-van-giao-dien.webp](../frontend/src/assets/tinh-van-giao-dien.webp) được tạo bằng ImageGen. Ảnh rộng 16:9, có vùng sao tối ở giữa và các dải tinh vân xanh tím ở rìa để chừa không gian cho cảnh và chữ.

Prompt tạo ảnh bằng ImageGen tích hợp

> Create a polished cinematic deep-space texture for GALAXORA website authentification illustration and exploration interface decorative glass overlays, 16:9 wide. No text, no logos, no planets, no orbit lines, no interface. Near-black navy open center with tiny pinpoint stars; concentrated electric-blue and violet nebulosity and luminous dust curls along far left and right edges; brilliant small clusters, tasteful diffuse purple cyan light, astrophotographic fine granularity. Dark enough for white UI text, visual richness at edges, deeply dimensional, premium science-fiction atmosphere.

Cả bốn cảnh phủ khung trình duyệt khi mở. Nút Toàn màn hình ẩn thanh công cụ khi trình duyệt hỗ trợ và cho phép thoát về cửa sổ. Bốn khung khu vực chồng lên cảnh và chuyển trang trong cùng phiên trình xem. Địa chỉ thay đổi theo khu vực và dùng được với nút Quay lại/Tiến. Cảnh mới hiện dần sau khi tải. Khi bật giảm chuyển động, trang đổi cảnh trực tiếp. Bảng thông tin có thể mở hoặc thu gọn.

## Ánh sáng và độ rõ bề mặt

Trình dựng dùng lọc texture bất đẳng hướng khi GPU hỗ trợ để bề mặt và vành đọc rõ hơn ở góc xiên. Các cảnh có vật liệu phản xạ nhận ánh sáng môi trường từ hai vùng sáng rộng. Trái Đất ưu tiên ánh sáng tản để hạn chế điểm lóe trên đại dương. Sao Thổ có mức phơi sáng riêng để dễ nhìn các dải vành và vệ tinh trong mô hình gốc. Thiên hà dùng đốm sáng viền mềm trên đám mây điểm gốc. Tinh vân dùng AgX để nén vùng sáng. Ảnh dự phòng 2D của Thiên hà, Tinh vân và Hố đen được chụp lại từ trình dựng mới.

Ảnh Sao Thổ và Enceladus trên Sketchfab là mẫu tham chiếu cho ánh sáng và chi tiết. Ảnh kiểm tra cận Enceladus dùng góc camera thử trên Chrome. Trình xem trên website tiếp tục dùng các góc và thao tác đã duyệt.

Hai ảnh tham chiếu riêng của Thiên hà và Hố đen được dùng để cân quầng sáng và màu. Thiên hà vẽ điểm với kích thước biến thiên và dựng quầng từ mật độ, màu của các cụm điểm trong gói gốc. Hố đen cân màu ba lớp phát sáng theo độ sáng của texture. Vùng sát lõi ưu tiên trắng hồng và đĩa ngoài ưu tiên vàng nâu. Hiệu ứng lan sáng dùng bộ đệm HDR khi GPU hỗ trợ, với cạnh dài tối đa 1.920 điểm ảnh. Thiết bị thiếu bộ đệm HDR dùng cách dựng trực tiếp. Ảnh so sánh trước/sau của GALAXORA chụp ở cùng góc, còn ảnh Sketchfab dùng góc chụp trong ảnh người dùng gửi.

## Lập lịch dựng hình

Cảnh 3D chỉ dựng khi canvas nằm trong vùng nhìn và tab đang hiển thị. Cảnh đứng yên chỉ yêu cầu khung mới khi có thao tác, đổi kích thước hoặc nạp xong tài nguyên. Mô phỏng tiếp tục từ trạng thái trước đó khi quay lại tab. Các thay đổi liên tiếp từ thao tác xoay được gộp vào một khung hình của trình duyệt.

## Độ phân giải thích ứng

Bộ đệm WebGL lấy kích thước từ canvas và mật độ điểm ảnh của thiết bị,
với giới hạn 2,3 triệu điểm ảnh và 4.096 điểm ảnh mỗi cạnh. Khi khung
hình liên tiếp chậm khoảng nửa giây, cảnh hạ một mức trong dải
100%, 85%, 70%, 58% của độ phân giải tối đa. Cảnh cần khoảng ba giây
vẽ liên tiếp ở nhịp ổn định để tăng lại một mức. Các khoảng ngừng do
tạm dừng, cuộn khỏi cảnh hoặc chuyển tab được bỏ qua khi đo.

Giới hạn áp dụng cho Hệ Mặt Trời, Cảnh gốc, Thiên hà, Tinh vân và Hố đen.
Khi đổi độ phân giải, bố cục và tọa độ tương tác vẫn dùng kích thước CSS.
Bộ đệm HDR và kích thước hạt sáng của Thiên hà cập nhật theo kích thước
dựng thực tế. Các ngưỡng là mặc định kỹ thuật của bản hiện tại và có thể
điều chỉnh khi có số đo trên thiết bị mục tiêu.

## Hệ Mặt Trời

Trang công khai /kham-pha hiển thị Mặt Trời, tám hành tinh, Mặt Trăng và Pluto. Các thiên thể có quỹ đạo elip, mô phỏng độ lệch tâm và độ nghiêng. Mặt Trăng đi quanh Trái Đất. Cảnh hiển thị quỹ đạo và chuyển động tự quay bằng nhịp đã điều chỉnh để người xem quan sát được. Kích thước, khoảng cách, pha khởi đầu và thời gian trong cảnh phục vụ mô phỏng trực quan.

Người xem có thể xoay, thu phóng, di chuyển, chuyển giữa toàn cảnh và nhìn từ trên, đổi tốc độ hoặc tạm dừng. Chọn hình cầu trong cảnh hoặc dùng danh sách để camera tiến lại gần và tiếp tục bám thiên thể khi quỹ đạo chạy. Các nút trong danh sách dùng được bằng bàn phím. Khi bật giảm chuyển động ở hệ điều hành, mô phỏng mở ở trạng thái tạm dừng và camera đổi góc trực tiếp.

Góc nhìn cận cảnh ưu tiên thiên thể đang chọn. Khi chọn Trái Đất hoặc Mặt Trăng, cảnh giữ cả hai thiên thể cùng quỹ đạo của Mặt Trăng. Danh sách cho phép chuyển thẳng sang thiên thể khác.

Bảng thông tin nêu phân loại, đường kính, khoảng cách trung bình, chu kỳ quỹ đạo và chu kỳ tự quay khi có giá trị phù hợp. Khoảng cách của Mặt Trăng đo từ Trái Đất. Số liệu Mặt Trời dùng ước tính tại xích đạo cho tự quay. Số liệu khoa học hiển thị độc lập với kích thước và tốc độ mô phỏng. [NASA Planetary Fact Sheet](https://nssdc.gsfc.nasa.gov/planetary/factsheet/) · [NASA Sun Facts](https://science.nasa.gov/sun/facts/) · [NASA Moon Facts](https://science.nasa.gov/moon/facts/)

Toàn cảnh mô phỏng dùng hình cầu nhẹ với bản đồ bề mặt từ gói người dùng cung cấp. Chọn một thiên thể sẽ tải GLB đóng từ gói gốc tương ứng. Các lớp nhìn thấy được và hoạt ảnh gốc của Mặt Trời, Trái Đất được giữ. Nút **Cảnh gốc** mở gói `Solar System.zip` đã đóng GLB, với quỹ đạo, hình học và hoạt ảnh của tác giả. Nút **Về mô phỏng** mở lại cảnh chọn 11 thiên thể và bảng NASA.

Mỗi lần chỉ tải và giữ một GLB gốc đang xem. Khi đổi lựa chọn, tài nguyên cũ được giải phóng và lượt tải còn chạy được hủy. Nếu tệp chi tiết lỗi, hình cầu mô phỏng vẫn hiển thị. Nếu WebGL 2 không khả dụng hoặc mất ngữ cảnh, chế độ 2D vẫn cho phép chọn thiên thể và đọc số liệu. [Danh mục tài nguyên](3d-assets.md)

## Thiên hà, Tinh vân và Hố đen

| Khu vực | Đường dẫn | Tương tác | Nội dung khoa học |
|---|---|---|---|
| Thiên hà | `/kham-pha/thien-ha` | Xoay và thu phóng toàn cảnh | [NASA Galaxies](https://science.nasa.gov/universe/galaxies/) |
| Tinh vân | `/kham-pha/tinh-van` | Nhìn xung quanh từ bên trong cảnh 360° | [NASA Hubble’s Nebulae](https://science.nasa.gov/mission/hubble/science/universe-uncovered/hubble-nebulae/) |
| Hố đen | `/kham-pha/ho-den` | Xoay và thu phóng mô hình | [NASA Anatomy of a Black Hole](https://science.nasa.gov/universe/black-holes/anatomy/) |

Ba trang dùng mô hình minh họa gốc do người dùng cung cấp và bảng kiến thức cơ bản. Các phím mũi tên điều khiển góc nhìn. Thiên hà và Hố đen hỗ trợ cuộn, chụm hai ngón tay, phím cộng/trừ hoặc nút để thu phóng. Mỗi cảnh có nút đặt lại góc nhìn.

Mỗi trang tải một mô hình khi mở khu vực. Tùy dung lượng, trình tải nhận GLB trực tiếp hoặc giải nén gói gzip trước khi dựng. Cảnh vẽ lại theo thao tác và kích thước khung. Ba khu vực này mở ở chế độ đứng yên. Ảnh 2D chụp từ góc mở đầu đi cùng bảng kiến thức khi không thể dựng WebGL 2, tải lỗi hoặc mất ngữ cảnh. Ghi nhận hành trình theo chương thuộc phần Gamification. Quản lý dữ liệu thuộc phần Quản trị. [Dung lượng gói tải](3d-assets.md#gói-tải-tối-ưu) · [Roadmap](development-roadmap.md)
