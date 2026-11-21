import {
  AdditiveBlending, BufferGeometry, CanvasTexture, Float32BufferAttribute, HalfFloatType, Mesh, MeshStandardMaterial,
  Points, PointsMaterial, ShaderMaterial, Vector2, Vector3, WebGLRenderTarget,
  type Object3D, type PerspectiveCamera, type Scene, type WebGLRenderer,
} from 'three'
import { EffectComposer } from 'three/addons/postprocessing/EffectComposer.js'
import { OutputPass } from 'three/addons/postprocessing/OutputPass.js'
import { RenderPass } from 'three/addons/postprocessing/RenderPass.js'
import { UnrealBloomPass } from 'three/addons/postprocessing/UnrealBloomPass.js'
import { hoTroAnhSangHDR } from './doHoaKhamPha.ts'

export function taoQuangSangKhamPha(renderer: WebGLRenderer, scene: Scene, camera: PerspectiveCamera, hoDen: boolean) {
  if (!hoTroAnhSangHDR(renderer)) return null
  const dich = new WebGLRenderTarget(32, 32, { type: HalfFloatType })
  const boDiemAnh = new EffectComposer(renderer, dich)
  boDiemAnh.setPixelRatio(1)
  const veCanh = new RenderPass(scene, camera)
  const quang = new UnrealBloomPass(new Vector2(32, 32), hoDen ? .42 : .30, hoDen ? .34 : .37, hoDen ? .60 : .52)
  const xuat = new OutputPass()
  boDiemAnh.addPass(veCanh)
  boDiemAnh.addPass(quang)
  boDiemAnh.addPass(xuat)
  let rongCu = 32
  let caoCu = 32
  return {
    ve: () => boDiemAnh.render(),
    datKichThuoc(rong: number, cao: number) {
      const tiLe = Math.min(1, 1920 / Math.max(rong, cao))
      const kichThuocRong = Math.max(32, Math.floor(rong * tiLe))
      const kichThuocCao = Math.max(32, Math.floor(cao * tiLe))
      if (kichThuocRong !== rongCu || kichThuocCao !== caoCu) {
        boDiemAnh.setSize(kichThuocRong, kichThuocCao)
        rongCu = kichThuocRong
        caoCu = kichThuocCao
      }
      return kichThuocCao
    },
    dong() {
      quang.dispose()
      xuat.dispose()
      boDiemAnh.dispose()
    },
  }
}

export function dungHatThienHa(model: Object3D, chieuCaoDiemAnh: { value: number }) {
  const anh = document.createElement('canvas')
  anh.width = anh.height = 32
  const but = anh.getContext('2d')
  if (!but) return
  const loang = but.createRadialGradient(16, 16, 0, 16, 16, 16)
  loang.addColorStop(0, 'rgba(255,255,255,1)')
  loang.addColorStop(.34, 'rgba(255,255,255,.86)')
  loang.addColorStop(.7, 'rgba(255,255,255,.28)')
  loang.addColorStop(1, 'rgba(255,255,255,0)')
  but.fillStyle = loang
  but.fillRect(0, 0, 32, 32)
  const map = new CanvasTexture(anh)
  const quang: { vat: Points; lop: Points }[] = []
  let coHat = false
  model.traverse(vat => {
    if (!(vat instanceof Points) || !(vat.material instanceof PointsMaterial)) return
    coHat = true
    const hat = vat.material
    hat.map = map
    hat.size = 1.85
    hat.sizeAttenuation = false
    hat.transparent = true
    hat.depthWrite = false
    hat.blending = AdditiveBlending
    hat.opacity = .64
    hat.onBeforeCompile = shader => {
      shader.vertexShader = shader.vertexShader.replace('gl_PointSize = size;', `
        float diem = fract(sin(dot(position, vec3(13.17, 79.83, 41.37))) * 43758.5453);
        gl_PointSize = size * (0.57 + 0.8 * pow(diem, 3.0) + 1.95 * step(0.994, diem));
      `)
    }
    hat.needsUpdate = true
    const lop = dungQuangCumSao(vat, chieuCaoDiemAnh)
    if (lop) quang.push({ vat, lop })
  })
  if (!coHat) map.dispose()
  for (const { vat, lop } of quang) vat.add(lop)
}

// Gom điểm sao theo ô không gian để mỗi quầng đại diện cho mật độ và màu thực của một cụm.
function dungQuangCumSao(vat: Points, chieuCaoDiemAnh: { value: number }) {
  const hinh = vat.geometry
  const viTri = hinh.getAttribute('position')
  const mau = hinh.getAttribute('color')
  if (!viTri || !mau || !viTri.count) return null
  hinh.computeBoundingBox()
  const hop = hinh.boundingBox!
  const dai = hop.getSize(new Vector3())
  const kichThuocO = Math.max(dai.x, dai.y, dai.z) / 22
  if (!kichThuocO) return null
  const cacO = new Map<string, { viTri: Vector3; mau: Vector3; soSao: number }>()
  const diem = new Vector3()
  const mauDiem = new Vector3()
  for (let i = 0; i < viTri.count; i++) {
    diem.fromBufferAttribute(viTri, i)
    const x = Math.floor((diem.x - hop.min.x) / kichThuocO)
    const y = Math.floor((diem.y - hop.min.y) / kichThuocO)
    const z = Math.floor((diem.z - hop.min.z) / kichThuocO)
    const ma = x + ':' + y + ':' + z
    let o = cacO.get(ma)
    if (!o) {
      o = { viTri: new Vector3(), mau: new Vector3(), soSao: 0 }
      cacO.set(ma, o)
    }
    o.viTri.add(diem)
    o.mau.add(mauDiem.fromBufferAttribute(mau, i))
    o.soSao++
  }
  const tam: number[] = []
  const tint: number[] = []
  const sang: number[] = []
  const xanh = new Vector3(.19, .28, .49)
  const am = new Vector3(.64, .47, .32)
  for (const o of cacO.values()) {
    if (o.soSao < 3) continue
    const tb = o.mau.divideScalar(o.soSao)
    const heSoAm = Math.min(1, Math.max(0, (tb.x - tb.z + .045) * 4))
    mauDiem.copy(xanh).lerp(am, heSoAm).multiply(tb)
    tint.push(mauDiem.x, mauDiem.y, mauDiem.z)
    const p = o.viTri.divideScalar(o.soSao)
    tam.push(p.x, p.y, p.z)
    sang.push(Math.min(.15, Math.sqrt(o.soSao) * .0053))
  }
  const nhom = new BufferGeometry()
  nhom.setAttribute('position', new Float32BufferAttribute(tam, 3))
  nhom.setAttribute('mauCum', new Float32BufferAttribute(tint, 3))
  nhom.setAttribute('doSang', new Float32BufferAttribute(sang, 1))
  const chatLieu = new ShaderMaterial({
    uniforms: { buoc: { value: kichThuocO }, chieuCaoDiemAnh },
    transparent: true,
    depthWrite: false,
    blending: AdditiveBlending,
    vertexShader: `
      attribute vec3 mauCum;
      attribute float doSang;
      uniform float buoc;
      uniform float chieuCaoDiemAnh;
      varying vec3 mauQuang;
      varying float sangQuang;
      void main() {
        vec4 viTri = modelViewMatrix * vec4(position, 1.0);
        gl_Position = projectionMatrix * viTri;
        float coTheGioi = buoc * length(modelViewMatrix[0].xyz);
        gl_PointSize = clamp(2.1 * coTheGioi * chieuCaoDiemAnh * projectionMatrix[1][1] / max(-viTri.z, 0.08), 3.0, 160.0);
        mauQuang = mauCum;
        sangQuang = doSang;
      }
    `,
    fragmentShader: `
      varying vec3 mauQuang;
      varying float sangQuang;
      void main() {
        vec2 lech = gl_PointCoord * 2.0 - 1.0;
        float r2 = dot(lech, lech);
        float quang = exp(-4.0 * r2) * (1.0 - smoothstep(0.48, 1.0, r2));
        gl_FragColor = vec4(mauQuang, sangQuang * quang);
        #include <tonemapping_fragment>
        #include <colorspace_fragment>
      }
    `,
  })
  const lop = new Points(nhom, chatLieu)
  lop.renderOrder = vat.renderOrder - 1
  return lop
}

const mauDiaBoiTu = {
  black_hole_light1: { mau: 'vec3(1.0, 0.51, 0.73)', dinh: 'vec3(1.0, 0.88, 0.95)', sang: '2.05', lanh: '0.62' },
  black_hole_light2: { mau: 'vec3(1.0, 0.61, 0.31)', dinh: 'vec3(1.0, 0.92, 0.82)', sang: '1.18', lanh: '0.0' },
  black_hole_light3: { mau: 'vec3(1.0, 0.49, 0.14)', dinh: 'vec3(1.0, 0.92, 0.82)', sang: '0.78', lanh: '0.0' },
} as const

export function dungMauDiaBoiTu(model: Object3D) {
  const daXuLy = new Set<MeshStandardMaterial>()
  model.traverse(vat => {
    if (!(vat instanceof Mesh)) return
    for (const chatLieu of Array.isArray(vat.material) ? vat.material : [vat.material]) {
      if (!(chatLieu instanceof MeshStandardMaterial) || daXuLy.has(chatLieu)) continue
      const profile = mauDiaBoiTu[chatLieu.name as keyof typeof mauDiaBoiTu]
      if (!profile) continue
      daXuLy.add(chatLieu)
      const bienDichGoc = chatLieu.onBeforeCompile
      const khoaGoc = chatLieu.customProgramCacheKey()
      chatLieu.customProgramCacheKey = () => khoaGoc + ':dia-boi-tu:' + chatLieu.name
      chatLieu.onBeforeCompile = (shader, renderer) => {
        bienDichGoc.call(chatLieu, shader, renderer)
        shader.fragmentShader = shader.fragmentShader
          .replace('#include <map_fragment>', `
            #include <map_fragment>
            float mucMauDia = max(max(diffuseColor.r, diffuseColor.g), diffuseColor.b);
            diffuseColor.rgb = mix(diffuseColor.rgb, mucMauDia * ${profile.mau}, 0.88);
          `)
          .replace('#include <emissivemap_fragment>', `
            #include <emissivemap_fragment>
            float doNong = max(max(totalEmissiveRadiance.r, totalEmissiveRadiance.g), totalEmissiveRadiance.b);
            float phanLanh = smoothstep(0.0, 0.55, totalEmissiveRadiance.b - totalEmissiveRadiance.r);
            vec3 mauNong = mix(${profile.mau}, ${profile.dinh}, smoothstep(0.24, 1.4, doNong));
            mauNong = mix(mauNong, vec3(0.58, 0.72, 1.0), phanLanh * ${profile.lanh});
            totalEmissiveRadiance = mauNong * doNong * ${profile.sang};
          `)
      }
      chatLieu.needsUpdate = true
    }
  })
}
