import {
  Color, DataTexture, DataUtils, EquirectangularReflectionMapping, HalfFloatType, LinearFilter,
  LinearSRGBColorSpace, Mesh, PMREMGenerator, RGBAFormat, Texture, Vector3,
  type Object3D, type WebGLRenderer,
} from 'three'

export function hoTroAnhSangHDR(renderer: WebGLRenderer) {
  return renderer.extensions.has('EXT_color_buffer_float') || renderer.extensions.has('EXT_color_buffer_half_float')
}

export function locTextureGoc(model: Object3D, renderer: WebGLRenderer) {
  const mucLoc = Math.min(8, renderer.capabilities.getMaxAnisotropy())
  const daLoc = new Set<Texture>()
  model.traverse(vat => {
    if (!(vat instanceof Mesh)) return
    for (const chatLieu of Array.isArray(vat.material) ? vat.material : [vat.material]) {
      for (const giaTri of Object.values(chatLieu)) {
        if (giaTri instanceof Texture && giaTri.generateMipmaps && !daLoc.has(giaTri)) {
          giaTri.anisotropy = mucLoc
          daLoc.add(giaTri)
        }
      }
    }
  })
}

export function taoAnhSangPhanXa(renderer: WebGLRenderer) {
  if (!hoTroAnhSangHDR(renderer)) return null
  const rong = 256
  const cao = 128
  const diemAnh = new Uint16Array(rong * cao * 4)
  const tia = new Vector3()
  const nguonChinh = new Vector3(-.55, .66, .5).normalize()
  const nguonPhu = new Vector3(.6, .16, -.45).normalize()
  const mauChinh = new Color('#fff4e1')
  const mauPhu = new Color('#b8cfff')
  // Hai vùng sáng rộng cho vật liệu phản xạ, dùng riêng để chiếu sáng và không hiện làm nền.
  for (let y = 0; y < cao; y++) {
    const viDo = Math.PI * ((y + .5) / cao - .5)
    const banKinh = Math.cos(viDo)
    for (let x = 0; x < rong; x++) {
      const kinhDo = 2 * Math.PI * ((x + .5) / rong - .5)
      tia.set(Math.cos(kinhDo) * banKinh, Math.sin(viDo), Math.sin(kinhDo) * banKinh)
      const sangChinh = 1.65 * Math.max(0, tia.dot(nguonChinh)) ** 4
      const sangPhu = .65 * Math.max(0, tia.dot(nguonPhu)) ** 3
      const nen = .1 + .08 * Math.max(tia.y, 0)
      const i = (y * rong + x) * 4
      diemAnh[i] = DataUtils.toHalfFloat(nen + mauChinh.r * sangChinh + mauPhu.r * sangPhu)
      diemAnh[i + 1] = DataUtils.toHalfFloat(nen + mauChinh.g * sangChinh + mauPhu.g * sangPhu)
      diemAnh[i + 2] = DataUtils.toHalfFloat(nen + mauChinh.b * sangChinh + mauPhu.b * sangPhu)
      diemAnh[i + 3] = DataUtils.toHalfFloat(1)
    }
  }
  const anh = new DataTexture(diemAnh, rong, cao, RGBAFormat, HalfFloatType)
  anh.colorSpace = LinearSRGBColorSpace
  anh.mapping = EquirectangularReflectionMapping
  anh.minFilter = LinearFilter
  anh.magFilter = LinearFilter
  anh.needsUpdate = true
  const pmrem = new PMREMGenerator(renderer)
  try {
    return pmrem.fromEquirectangular(anh)
  } finally {
    pmrem.dispose()
    anh.dispose()
  }
}
