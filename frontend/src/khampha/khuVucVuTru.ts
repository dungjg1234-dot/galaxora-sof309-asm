export const cacKhuVucVuTru = {
  'thien-ha': {
    ten: 'Thiên hà',
    thuMuc: 'galaxies',
    chuThich: 'Mô hình minh họa bằng đám mây điểm.',
    tomTat: 'Thiên hà là tập hợp các sao, khí và bụi liên kết với nhau bởi lực hấp dẫn. Hệ Mặt Trời nằm trong Ngân Hà.',
    diemChinh: [
      ['Hình dạng', 'Thiên hà có thể có dạng xoắn ốc, elip hoặc bất định.'],
      ['Quy mô', 'Kích thước và số lượng sao rất khác nhau giữa các thiên hà.'],
    ],
    nasa: 'https://science.nasa.gov/universe/galaxies/',
    tacPham: 'Need some space?',
    tacGia: 'Loïc Norgeot',
    nguonMoHinh: 'https://sketchfab.com/3d-models/need-some-space-d6521362b37b48e3a82bce4911409303',
  },
  'tinh-van': {
    ten: 'Tinh vân',
    thuMuc: 'nebulae',
    chuThich: 'Bầu trời 360° minh họa tinh vân.',
    tomTat: 'Tinh vân là đám mây khí và bụi trong không gian. Một số tinh vân là nơi các sao mới hình thành.',
    diemChinh: [
      ['Ánh sáng', 'Khí có thể phát sáng, bụi có thể phản chiếu hoặc che khuất ánh sáng từ các sao.'],
      ['Nguồn gốc', 'Một số tinh vân hình thành từ vật chất do sao đang chết hoặc vụ nổ siêu tân tinh để lại.'],
    ],
    nasa: 'https://science.nasa.gov/mission/hubble/science/universe-uncovered/hubble-nebulae/',
    tacPham: 'Space nebula HDRI panorama 360 skydome',
    tacGia: 'Aliaksandr.melas',
    nguonMoHinh: 'https://sketchfab.com/3d-models/space-nebula-hdri-panorama-360-skydome-99bbafab741e406593796cc77b357b2a',
  },
  'ho-den': {
    ten: 'Hố đen',
    thuMuc: 'black-holes',
    chuThich: 'Mô hình minh họa hố đen và vùng sáng xung quanh.',
    tomTat: 'Hố đen có lực hấp dẫn mạnh đến mức ánh sáng đã đi qua chân trời sự kiện không thể thoát ra.',
    diemChinh: [
      ['Chân trời sự kiện', 'Ranh giới mà vật chất và ánh sáng đã đi qua sẽ không thể thoát ra ngoài.'],
      ['Đĩa bồi tụ', 'Vật chất nóng quay quanh một số hố đen có thể tạo thành đĩa sáng.'],
    ],
    nasa: 'https://science.nasa.gov/universe/black-holes/anatomy/',
    tacPham: 'Black Hole',
    tacGia: 'NestaEric',
    nguonMoHinh: 'https://sketchfab.com/3d-models/black-hole-e410da98b1e5445eae2acafaaa53587d',
  },
} as const

export type MaKhuVucVuTru = keyof typeof cacKhuVucVuTru

export function laKhuVucVuTru(ma: string): ma is MaKhuVucVuTru {
  return Object.hasOwn(cacKhuVucVuTru, ma)
}
