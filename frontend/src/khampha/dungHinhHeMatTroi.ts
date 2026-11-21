import {
  AdditiveBlending, BufferGeometry, CanvasTexture, Color, DoubleSide, Float32BufferAttribute, Group, LineBasicMaterial,
  LineLoop, Mesh, MeshBasicMaterial, MeshStandardMaterial, Points, PointsMaterial, RingGeometry, SphereGeometry,
  Sprite, SpriteMaterial, SRGBColorSpace, Texture, Vector3, type Material,
} from 'three'
import { viTriQuyDao, type QuyDao } from './quyDao.ts'
import type { ThienThe } from './thienThe.ts'

export function taoQuyDao(quyDao: QuyDao, mau: string) {
  const diem = new Vector3()
  const cacDiem = Array.from({ length: 160 }, (_, i) =>
    viTriQuyDao(quyDao, i / 160 * Math.PI * 2, diem).clone())
  return new LineLoop(new BufferGeometry().setFromPoints(cacDiem),
    new LineBasicMaterial({ color: mau, transparent: true, opacity: .27 }))
}

export function taoSaoNen() {
  // Hạt nền trang trí, cố định giữa các lần mở.
  let hat = 138297
  const ngauNhien = () => {
    hat = (Math.imul(1664525, hat) + 1013904223) >>> 0
    return hat / 4294967296
  }
  const viTri: number[] = []
  const mau: number[] = []
  const bangMau = ['#7186ab', '#c5d4f4', '#f0d5a4', '#95b8f7'].map(giaTri => new Color(giaTri))
  for (let i = 0; i < 1900; i++) {
    const y = ngauNhien() * 2 - 1
    const goc = ngauNhien() * Math.PI * 2
    const r = 175 + ngauNhien() * 70
    const day = Math.sqrt(1 - y * y)
    viTri.push(r * day * Math.cos(goc), r * y, r * day * Math.sin(goc))
    const tint = bangMau[Math.floor(ngauNhien() * bangMau.length)]
    const sang = .25 + ngauNhien() * .6
    mau.push(tint.r * sang, tint.g * sang, tint.b * sang)
  }
  const hinh = new BufferGeometry()
  hinh.setAttribute('position', new Float32BufferAttribute(viTri, 3))
  hinh.setAttribute('color', new Float32BufferAttribute(mau, 3))
  return new Points(hinh, new PointsMaterial({
    size: 1.25, sizeAttenuation: false, vertexColors: true, transparent: true, opacity: .82,
  }))
}

export function taoQuangSang(banKinh: number) {
  const canvas = document.createElement('canvas')
  canvas.width = canvas.height = 128
  const but = canvas.getContext('2d')
  if (!but) return undefined
  const loang = but.createRadialGradient(64, 64, 24, 64, 64, 64)
  loang.addColorStop(0, 'rgba(255,154,55,.65)')
  loang.addColorStop(.38, 'rgba(255,106,23,.19)')
  loang.addColorStop(1, 'rgba(255,68,15,0)')
  but.fillStyle = loang
  but.fillRect(0, 0, 128, 128)
  const map = new CanvasTexture(canvas)
  map.colorSpace = SRGBColorSpace
  const sprite = new Sprite(new SpriteMaterial({
    map, transparent: true, depthWrite: false, blending: AdditiveBlending, toneMapped: false,
  }))
  sprite.scale.setScalar(banKinh * 5)
  return sprite
}

export function taoThienThe(thienThe: ThienThe) {
  const { ma, mau, banKinh } = thienThe
  const goc = new Group()
  const truc = new Group()
  truc.rotation.z = thienThe.truc * Math.PI / 180
  goc.add(truc)
  const chatLieu = ma === 'sun'
    ? new MeshBasicMaterial({ color: mau, toneMapped: false })
    : new MeshStandardMaterial({ color: mau, roughness: 1 })
  const matCau = new Mesh(new SphereGeometry(banKinh, 56, 40), chatLieu)
  truc.add(matCau)
  const trangTri = new Group()
  if (ma === 'sun') {
    const quang = taoQuangSang(banKinh)
    if (quang) trangTri.add(quang)
    goc.add(trangTri)
  }
  if (ma === 'saturn') {
    trangTri.add(taoVanh(banKinh))
    truc.add(trangTri)
  }
  return { goc, truc, matCau, trangTri }
}

function taoVanh(banKinh: number) {
  const vanh = new Group()
  const trong = banKinh * 1.27
  const ngoai = banKinh * 2.30
  const beRong = ngoai - trong
  const cacDai = [
    { tu: 0, den: .18, mau: '#8b7560', mo: .43 },
    { tu: .18, den: .55, mau: '#ddc6a3', mo: .8 },
    { tu: .60, den: .83, mau: '#c4ab8c', mo: .64 },
    { tu: .84, den: 1, mau: '#bca68a', mo: .46 },
  ]
  for (const dai of cacDai) {
    const hinh = new RingGeometry(trong + dai.tu * beRong, trong + dai.den * beRong, 96)
    vanh.add(new Mesh(hinh, new MeshBasicMaterial({
      color: dai.mau, side: DoubleSide, transparent: true, opacity: dai.mo, depthWrite: false,
    })))
  }
  vanh.rotation.x = -Math.PI / 2
  return vanh
}

export function giaiPhongCanh(canh: Group) {
  const taiNguyen = new Set<BufferGeometry | Material | Texture>()
  const bitmaps = new Set<ImageBitmap>()
  canh.traverse(vat => {
    if (vat instanceof Mesh || vat instanceof LineLoop || vat instanceof Points) {
      taiNguyen.add(vat.geometry)
    }
    if (vat instanceof Mesh || vat instanceof LineLoop || vat instanceof Points || vat instanceof Sprite) {
      for (const chatLieu of Array.isArray(vat.material) ? vat.material : [vat.material]) {
        taiNguyen.add(chatLieu)
        for (const giaTri of Object.values(chatLieu)) {
          if (giaTri instanceof Texture) {
            taiNguyen.add(giaTri)
            if (giaTri.image instanceof ImageBitmap) bitmaps.add(giaTri.image)
          }
        }
      }
    }
  })
  taiNguyen.forEach(t => t.dispose())
  bitmaps.forEach(anh => anh.close())
}
