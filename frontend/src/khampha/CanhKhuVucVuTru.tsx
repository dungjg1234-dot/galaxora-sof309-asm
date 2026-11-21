import { useEffect, useRef, type KeyboardEvent } from 'react'
import {
  AgXToneMapping, AnimationMixer, Box3, Color, DirectionalLight, HemisphereLight, MathUtils, NeutralToneMapping,
  PerspectiveCamera, Scene, Spherical, SRGBColorSpace, Vector3, WebGLRenderer, type Group,
} from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { taiDuLieuMoHinh, timMoHinhKhamPha } from './taiMoHinhKhamPha.ts'
import { taoBoTaiMoHinhGoc } from './taiMoHinhGoc.ts'
import { giaiPhongCanh } from './dungHinhHeMatTroi.ts'
import { taoChatLuongKhamPha } from './chatLuongKhamPha.ts'
import { taoLichVeCanh } from './lichVeCanh.ts'
import { locTextureGoc, taoAnhSangPhanXa } from './doHoaKhamPha.ts'
import { dungHatThienHa, dungMauDiaBoiTu, taoQuangSangKhamPha } from './quangSangVuTru.ts'
import { cacKhuVucVuTru, type MaKhuVucVuTru } from './khuVucVuTru.ts'

type Props = {
  khuVuc?: MaKhuVucVuTru
  tocDo?: number
  baoSanSang: () => void
  baoLoi: () => void
}

export default function CanhKhuVucVuTru({ khuVuc, tocDo = 0, baoSanSang, baoLoi }: Props) {
  const canvas = useRef<HTMLCanvasElement>(null)
  const dieuKhien = useRef<OrbitControls | null>(null)
  const tocDoHienTai = useRef(tocDo)
  const yeuCauVe = useRef<(() => void) | null>(null)
  useEffect(() => {
    tocDoHienTai.current = tocDo
    yeuCauVe.current?.()
  }, [tocDo])
  const toanCanh = khuVuc === 'tinh-van'
  const duLieu = khuVuc ? cacKhuVucVuTru[khuVuc] : undefined
  const thuMuc = duLieu?.thuMuc ?? 'solar-system/toan-canh'
  const ten = duLieu?.ten ?? 'Hệ Mặt Trời gốc'

  useEffect(() => {
    const matVe = canvas.current
    if (!matVe) return
    let renderer: WebGLRenderer
    try {
      renderer = new WebGLRenderer({ canvas: matVe, antialias: true })
    } catch {
      baoLoi()
      return
    }

    const chatLuong = taoChatLuongKhamPha(1.5)
    renderer.outputColorSpace = SRGBColorSpace
    renderer.toneMapping = toanCanh ? AgXToneMapping : NeutralToneMapping
    renderer.toneMappingExposure = toanCanh ? 1.12 : khuVuc === 'ho-den' ? .9 : 1
    const scene = new Scene()
    scene.background = new Color(khuVuc === 'thien-ha' || khuVuc === 'ho-den' ? '#020207' : khuVuc ? '#05060d' : '#060915')
    const coAnhSangVatLieu = khuVuc === 'ho-den' || !khuVuc
    if (coAnhSangVatLieu) {
      scene.add(new HemisphereLight('#e4ecff', '#322b37', khuVuc === 'ho-den' ? .48 : 1.05))
      const den = new DirectionalLight('#fff2df', khuVuc === 'ho-den' ? 1.45 : 2.5)
      den.position.set(-2.3, 3.4, 4.5)
      scene.add(den)
    }

    const camera = new PerspectiveCamera(toanCanh ? 72 : 43, 1, 0.001, 240)
    if (toanCanh) camera.position.set(-0.0088, -0.0006, 0.005)
    else if (khuVuc === 'thien-ha') camera.position.set(0.6, 1.35, 2.5)
    else if (khuVuc === 'ho-den') camera.position.set(0.2, 0.65, 2.85)
    else camera.position.set(0.45, 2.6, 2.65)
    const control = new OrbitControls(camera, matVe)
    dieuKhien.current = control
    control.enablePan = false
    control.enableZoom = !toanCanh
    control.rotateSpeed = toanCanh ? -0.7 : 0.8
    control.minDistance = toanCanh ? 0.01 : 1.15
    control.maxDistance = toanCanh ? 0.011 : khuVuc === 'ho-den' ? 110 : 12
    control.minPolarAngle = 0.05
    control.maxPolarAngle = Math.PI - 0.05
    control.update()
    control.saveState()

    let quangSang: ReturnType<typeof taoQuangSangKhamPha>
    const chieuCaoDiemAnh = { value: 1 }
    let rong = 0
    let cao = 0

    const capNhatQuangSang = () => {
      chieuCaoDiemAnh.value = quangSang?.datKichThuoc(matVe.width, matVe.height) ?? matVe.height
    }

    const datDemDiemAnh = () => {
      if (!rong || !cao) return
      renderer.setDrawingBufferSize(rong, cao, chatLuong.tiLe(rong, cao, window.devicePixelRatio || 1))
      capNhatQuangSang()
    }
    const ve = () => {
      if (quangSang) quangSang.ve()
      else renderer.render(scene, camera)
    }
    let mixer: AnimationMixer | undefined
    const lichVe = taoLichVeCanh(matVe, (_luc, delta, khoangMs) => {
      if (chatLuong.doKhoangKhung(khoangMs)) datDemDiemAnh()
      const dangChay = mixer !== undefined && tocDoHienTai.current > 0
      if (dangChay) mixer?.update(delta * tocDoHienTai.current)
      ve()
      return dangChay
    })
    yeuCauVe.current = lichVe.yeuCauVe
    control.addEventListener('change', lichVe.yeuCauVe)
    const thayKichThuoc = () => {
      rong = matVe.clientWidth
      cao = matVe.clientHeight
      if (!rong || !cao) return
      camera.aspect = rong / cao
      if (!toanCanh) {
        const heSo = Math.min(1, camera.aspect)
        camera.fov = MathUtils.radToDeg(2 * Math.atan(Math.tan(MathUtils.degToRad(43 / 2)) / heSo))
      }
      camera.updateProjectionMatrix()
      datDemDiemAnh()
      lichVe.yeuCauVe()
    }
    const thayDoi = new ResizeObserver(thayKichThuoc)
    thayDoi.observe(matVe)
    thayKichThuoc()
    const huy = new AbortController()
    const matNguCanh = (suKien: Event) => {
      suKien.preventDefault()
      lichVe.dong()
      baoLoi()
    }
    matVe.addEventListener('webglcontextlost', matNguCanh)

    let model: Group | undefined
    let phanXa: ReturnType<typeof taoAnhSangPhanXa>
    async function taiCanh() {
      try {
        const layDuongDan = timMoHinhKhamPha(thuMuc)
        if (!layDuongDan) throw new Error('Không tìm thấy mô hình.')
        const url = new URL(await layDuongDan(), location.href)
        if (huy.signal.aborted) return
        const tep = await taiDuLieuMoHinh(url, huy.signal)
        if (huy.signal.aborted) return
        const gltf = await taoBoTaiMoHinhGoc().parseAsync(tep, new URL('.', url).href)
        if (huy.signal.aborted) {
          giaiPhongCanh(gltf.scene)
          return
        }
        model = gltf.scene
        const hop = new Box3()
        if (khuVuc === 'ho-den') {
          model.updateMatrixWorld(true)
          for (const tenNhom of ['black_hole', 'black_hole_ring']) {
            const nhom = model.getObjectByName(tenNhom)
            if (nhom) hop.expandByObject(nhom)
          }
        }
        if (hop.isEmpty()) hop.setFromObject(model)
        const kichThuoc = hop.getSize(new Vector3())
        const canhLonNhat = Math.max(kichThuoc.x, kichThuoc.y, kichThuoc.z)
        if (!Number.isFinite(canhLonNhat) || canhLonNhat <= 0) throw new Error('Thiếu hình học')
        const tiLe = (toanCanh ? 24 : 2.1) / canhLonNhat
        model.position.copy(hop.getCenter(new Vector3())).multiplyScalar(-tiLe)
        model.scale.multiplyScalar(tiLe)
        locTextureGoc(model, renderer)
        if (khuVuc === 'thien-ha') dungHatThienHa(model, chieuCaoDiemAnh)
        if (khuVuc === 'ho-den') dungMauDiaBoiTu(model)
        if (coAnhSangVatLieu) {
          phanXa = taoAnhSangPhanXa(renderer)
          scene.environment = phanXa?.texture ?? null
          scene.environmentIntensity = khuVuc === 'ho-den' ? .36 : .48
        }
        scene.add(model)
        if (khuVuc === 'thien-ha' || khuVuc === 'ho-den') {
          quangSang = taoQuangSangKhamPha(renderer, scene, camera, khuVuc === 'ho-den')
          capNhatQuangSang()
        }
        if (gltf.animations.length) {
          mixer = new AnimationMixer(model)
          for (const clip of gltf.animations) mixer.clipAction(clip).play()
          mixer.update(0)
        }
        lichVe.yeuCauVe()
        baoSanSang()
      } catch {
        if (!huy.signal.aborted) baoLoi()
      }
    }
    void taiCanh()

    return () => {
      huy.abort()
      lichVe.dong()
      yeuCauVe.current = null
      mixer?.stopAllAction()
      matVe.removeEventListener('webglcontextlost', matNguCanh)
      thayDoi.disconnect()
      control.dispose()
      dieuKhien.current = null
      if (model) giaiPhongCanh(model)
      phanXa?.dispose()
      quangSang?.dong()
      renderer.dispose()
      queueMicrotask(() => { if (!matVe.isConnected) renderer.forceContextLoss() })
    }
  }, [khuVuc, thuMuc, toanCanh, baoSanSang, baoLoi])

  function dieuHuong(phim: string) {
    const control = dieuKhien.current
    if (!control) return false
    const viTri = new Spherical().setFromVector3(control.object.position)
    switch (phim) {
      case 'ArrowLeft': viTri.theta -= .15; break
      case 'ArrowRight': viTri.theta += .15; break
      case 'ArrowUp': viTri.phi -= .15; break
      case 'ArrowDown': viTri.phi += .15; break
      case '+':
      case '=': if (!toanCanh) viTri.radius *= .85; break
      case '-': if (!toanCanh) viTri.radius /= .85; break
      default: return false
    }
    viTri.phi = Math.min(control.maxPolarAngle, Math.max(control.minPolarAngle, viTri.phi))
    viTri.radius = Math.min(control.maxDistance, Math.max(control.minDistance, viTri.radius))
    control.object.position.setFromSpherical(viTri)
    control.update()
    return true
  }

  function bamPhim(suKien: KeyboardEvent<HTMLCanvasElement>) {
    if (dieuHuong(suKien.key)) suKien.preventDefault()
  }

  return <>
    <div className="kp-san kp-san-goc">
      <canvas ref={canvas} tabIndex={0} className="kp-canvas" onKeyDown={bamPhim}
        aria-label={'Cảnh 3D ' + ten} aria-describedby="kp-dieu-huong-vu-tru" />
    </div>
    <div className="kp-thanh-dieu-khien">
      <button type="button" onClick={() => dieuKhien.current?.reset()}>Đặt lại góc nhìn</button>
      {!toanCanh && <>
        <button type="button" onClick={() => dieuHuong('+')}>Phóng to</button>
        <button type="button" onClick={() => dieuHuong('-')}>Thu nhỏ</button>
      </>}
    </div>
    <p className="kp-goi-y" id="kp-dieu-huong-vu-tru">
      {toanCanh ? 'Kéo hoặc dùng phím mũi tên để nhìn xung quanh.' :
        'Kéo hoặc dùng phím mũi tên để xoay. Cuộn, chụm hai ngón tay hoặc dùng nút để thu phóng.'}
    </p>
  </>
}
