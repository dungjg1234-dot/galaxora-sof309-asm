import {
  AmbientLight, AnimationMixer, Box3, Color, DirectionalLight, Group, MathUtils, Mesh, NeutralToneMapping, PerspectiveCamera,
  PointLight, Raycaster, Scene, SRGBColorSpace, TextureLoader, Vector2, Vector3, WebGLRenderer,
} from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { taiDuLieuMoHinh } from './taiMoHinhKhamPha.ts'
import { taoBoTaiMoHinhGoc } from './taiMoHinhGoc.ts'
import { giaiPhongCanh, taoQuyDao, taoSaoNen, taoThienThe } from './dungHinhHeMatTroi.ts'
import { locTextureGoc, taoAnhSangPhanXa } from './doHoaKhamPha.ts'
import { taoChatLuongKhamPha } from './chatLuongKhamPha.ts'
import { taoLichVeCanh } from './lichVeCanh.ts'
import { phaQuyDao, viTriQuyDao } from './quyDao.ts'
import { cacThienThe, theoMa, type MaThienThe } from './thienThe.ts'
import { anhBeMat, moHinhChiTiet } from './taiNguyenHeMatTroi.ts'

export type GocNhin = { ma: MaThienThe | null; tuTren: boolean }
export type TrangThaiMoHinh = 'dang-tai' | 'da-tai' | 'loi'
type TuyChonCanh = {
  chon: (ma: MaThienThe) => void
  baoLoi: () => void
  baoMoHinh: (ma: MaThienThe, trangThai: TrangThaiMoHinh) => void
}
type ThienTheTrongCanh = ReturnType<typeof taoThienThe> & {
  quyDao?: ReturnType<typeof taoQuyDao>
}
const VONG = 2 * Math.PI
const TRUC_DOC = new Vector3(0, 1, 0)

export function lapCanhHeMatTroi(canvas: HTMLCanvasElement, nhan: Record<MaThienThe, HTMLSpanElement | null>,
  tuyChon: TuyChonCanh) {
  const context = canvas.getContext('webgl2', { antialias: true, alpha: true })
  if (!context) return null
  const renderer = new WebGLRenderer({ canvas, context })
  const chatLuong = taoChatLuongKhamPha(1.65)
  renderer.toneMapping = NeutralToneMapping
  renderer.toneMappingExposure = 1.07
  renderer.outputColorSpace = SRGBColorSpace
  const scene = new Scene()
  scene.background = new Color('#070b16')
  const vatThe = new Group()
  scene.add(vatThe)
  const denNen = new AmbientLight('#dfeaff', 1.1)
  const denMatTroi = new PointLight('#ffe6cc', 2.6, 0, 0)
  const denQuanSat = new DirectionalLight('#fff4e3')
  denQuanSat.visible = false
  scene.add(denNen, denMatTroi, denQuanSat)
  let phanXa: ReturnType<typeof taoAnhSangPhanXa> | undefined
  vatThe.add(taoSaoNen())

  const camera = new PerspectiveCamera(43, 1, .025, 410)
  const controls = new OrbitControls(camera, canvas)
  controls.maxDistance = 200
  controls.minDistance = 1.2
  controls.listenToKeyEvents(canvas)
  const ds = new Map<MaThienThe, ThienTheTrongCanh>()
  const coTheChon = new Map<Mesh, MaThienThe>()
  const textureLoader = new TextureLoader()
  let daDung = false
  const lichVe = taoLichVeCanh(canvas, ve)

  for (const thienThe of cacThienThe) {
    const vat = taoThienThe(thienThe)
    const quyDao = thienThe.quyDao && taoQuyDao(thienThe.quyDao, thienThe.mau)
    ds.set(thienThe.ma, { ...vat, quyDao })
    vatThe.add(vat.goc)
    if (quyDao) vatThe.add(quyDao)
    coTheChon.set(vat.matCau, thienThe.ma)

    const url = anhBeMat(thienThe.ma)
    if (url) {
      textureLoader.load(url, texture => {
        if (daDung) { texture.dispose(); return }
        texture.colorSpace = SRGBColorSpace
        texture.anisotropy = Math.min(8, renderer.capabilities.getMaxAnisotropy())
        vat.matCau.material.map = texture
        vat.matCau.material.color.set('#ffffff')
        vat.matCau.material.needsUpdate = true
        lichVe.yeuCauVe()
      })
    }
  }

  const nhanTam = new Vector3()
  const viTriTam = new Vector3()
  const toaDoMoi = new Vector3()
  const dichChuyen = new Vector3()
  const tamCu = new Vector3()
  let trangThaiGoc: GocNhin = { ma: null, tuTren: false }
  let thoiGian = 0
  let tocDo = 1
  let chuyenDongNhe = false
  let rong = 0
  let cao = 0
  let bay: { batDau: number; camera: Vector3; tam: Vector3 } | null = null
  let moHinh: { ma: MaThienThe; nhom: Group; mixer?: AnimationMixer } | null = null
  let huyTai: AbortController | null = null

  function capNhatViTri() {
    for (const duLieu of cacThienThe) {
      const vat = ds.get(duLieu.ma)!
      if (duLieu.quyDao) {
        viTriQuyDao(duLieu.quyDao, phaQuyDao(duLieu.quyDao, thoiGian), vat.goc.position)
        if (duLieu.quayQuanh) {
          const tam = ds.get(duLieu.quayQuanh)!.goc.position
          vat.goc.position.add(tam)
          vat.quyDao?.position.copy(tam)
        }
      }
      const xoay = duLieu.ma === 'moon' && duLieu.quyDao
        ? phaQuyDao(duLieu.quyDao, thoiGian) : VONG * thoiGian / duLieu.vongXoayMoPhong
      vat.matCau.rotation.y = -xoay
      if (moHinh?.ma === duLieu.ma && !moHinh.mixer) moHinh.nhom.rotation.y = -xoay
    }
  }

  function layDich(cameraDich: Vector3, tam: Vector3) {
    const ma = trangThaiGoc.ma
    if (!ma) {
      tam.set(0, 0, 0)
      const khoangCach = Math.max(trangThaiGoc.tuTren ? 153 : 108, 143 / Math.max(.58, camera.aspect))
      cameraDich.set(.12, trangThaiGoc.tuTren ? 1 : .65, trangThaiGoc.tuTren ? .012 : 1)
        .normalize().multiplyScalar(khoangCach)
      return
    }
    const vat = ds.get(ma)!
    const banKinh = theoMa[ma].banKinh
    tam.copy(vat.goc.position)
    // Hướng về phần được Mặt Trời chiếu sáng, đồng thời giữ độ nghiêng dễ quan sát.
    cameraDich.set(-tam.x, 0, -tam.z)
    if (cameraDich.lengthSq() < .1) cameraDich.set(.25, 0, 1)
    cameraDich.normalize().add(new Vector3(.13, .40, .23)).normalize()
    const heSo = ma === 'saturn' ? 7.5 : 4.65
    cameraDich.multiplyScalar(banKinh * heSo * Math.max(1, .9 / camera.aspect)).add(tam)
  }

  function capNhatCamera(luc: number) {
    if (bay) {
      const phan = chuyenDongNhe ? 1 : Math.min(1, (luc - bay.batDau) / 790)
      const tyLe = MathUtils.smootherstep(phan, 0, 1)
      layDich(toaDoMoi, viTriTam)
      camera.position.lerpVectors(bay.camera, toaDoMoi, tyLe)
      controls.target.lerpVectors(bay.tam, viTriTam, tyLe)
      if (phan === 1) bay = null
    } else if (trangThaiGoc.ma) {
      viTriTam.copy(ds.get(trangThaiGoc.ma)!.goc.position)
      dichChuyen.subVectors(viTriTam, tamCu)
      camera.position.add(dichChuyen)
      controls.target.add(dichChuyen)
    }
    if (trangThaiGoc.ma) tamCu.copy(ds.get(trangThaiGoc.ma)!.goc.position)
    controls.update()
  }

  function capNhatNhan() {
    const daDat: { x: number; y: number }[] = []
    for (const { ma, banKinh } of cacThienThe) {
      const oNhan = nhan[ma]
      if (!oNhan) continue
      nhanTam.copy(ds.get(ma)!.goc.position)
      nhanTam.y += banKinh * 1.2 + .3
      nhanTam.project(camera)
      const x = (nhanTam.x * .5 + .5) * rong
      const y = (-nhanTam.y * .5 + .5) * cao
      const hopLe = (!trangThaiGoc.ma || trangThaiGoc.ma === ma)
        && nhanTam.z < 1 && nhanTam.z > -1 && x > 45 && x < rong - 45 && y > 24 && y < cao - 25
        && !daDat.some(diem => Math.abs(diem.x - x) < 88 && Math.abs(diem.y - y) < 19)
      oNhan.hidden = !hopLe
      if (hopLe) {
        oNhan.style.transform = 'translate(' + x + 'px,' + y + 'px) translate(-50%, -100%)'
        daDat.push({ x, y })
      }
    }
  }

  function datDemDiemAnh() {
    if (rong && cao) renderer.setDrawingBufferSize(rong, cao, chatLuong.tiLe(rong, cao, window.devicePixelRatio || 1))
  }

  function ve(luc: number, delta: number, khoangMs: number) {
    if (chatLuong.doKhoangKhung(khoangMs)) datDemDiemAnh()
    if (tocDo > 0) {
      thoiGian += delta * tocDo
      moHinh?.mixer?.update(delta * tocDo)
      capNhatViTri()
    }
    capNhatCamera(luc)
    renderer.render(scene, camera)
    capNhatNhan()
    return tocDo > 0 || bay !== null
  }

  function thayKichThuoc() {
    rong = canvas.clientWidth
    cao = canvas.clientHeight
    if (!rong || !cao) return
    camera.aspect = rong / cao
    camera.updateProjectionMatrix()
    datDemDiemAnh()
    lichVe.yeuCauVe()
  }
  const thayDoi = new ResizeObserver(thayKichThuoc)
  thayDoi.observe(canvas)
  thayKichThuoc()
  capNhatViTri()
  layDich(camera.position, controls.target)
  controls.update()

  function batDauThaoTac() { bay = null }
  controls.addEventListener('start', batDauThaoTac)
  controls.addEventListener('change', lichVe.yeuCauVe)

  const ray = new Raycaster()
  const cacMesh = [...coTheChon.keys()]
  let diemAn: Vector2 | null = null
  function nhanChuot(e: PointerEvent) {
    if (e.button === 0) diemAn = new Vector2(e.clientX, e.clientY)
  }
  function nhaChuot(e: PointerEvent) {
    if (!diemAn) return
    const diChuyen = diemAn.distanceTo(new Vector2(e.clientX, e.clientY))
    diemAn = null
    if (diChuyen > 6) return
    const hop = canvas.getBoundingClientRect()
    if (!hop.width || !hop.height) return
    ray.setFromCamera(new Vector2((e.clientX - hop.left) / hop.width * 2 - 1,
      1 - (e.clientY - hop.top) / hop.height * 2), camera)
    const vat = ray.intersectObjects(cacMesh.filter(mesh => ds.get(coTheChon.get(mesh)!)?.goc.visible), false)[0]?.object
    const ma = vat instanceof Mesh ? coTheChon.get(vat) : undefined
    if (ma) tuyChon.chon(ma)
  }
  function huyChuot() { diemAn = null }
  function matContext(e: Event) {
    e.preventDefault()
    lichVe.dong()
    tuyChon.baoLoi()
  }
  canvas.addEventListener('pointerdown', nhanChuot)
  canvas.addEventListener('pointerup', nhaChuot)
  canvas.addEventListener('pointercancel', huyChuot)
  canvas.addEventListener('webglcontextlost', matContext)

  function boMoHinh() {
    huyTai?.abort()
    huyTai = null
    if (!moHinh) return
    const vat = ds.get(moHinh.ma)!
    vat.matCau.visible = true
    vat.trangTri.visible = true
    vat.truc.remove(moHinh.nhom)
    moHinh.mixer?.stopAllAction()
    giaiPhongCanh(moHinh.nhom)
    moHinh = null
    denQuanSat.visible = false
    denNen.intensity = 1.1
    denMatTroi.intensity = 2.6
    scene.environment = null
    renderer.toneMappingExposure = 1.07
    controls.maxDistance = 200
    camera.far = 410
    camera.updateProjectionMatrix()
  }

  async function taiMoHinh(ma: MaThienThe) {
    const layDuongDan = moHinhChiTiet(ma)
    if (!layDuongDan) { tuyChon.baoMoHinh(ma, 'loi'); return }
    const boHuy = new AbortController()
    huyTai = boHuy
    tuyChon.baoMoHinh(ma, 'dang-tai')
    try {
      const duongDan = new URL(await layDuongDan(), location.href)
      if (boHuy.signal.aborted) return
      const tep = await taiDuLieuMoHinh(duongDan, boHuy.signal)
      if (boHuy.signal.aborted) return
      const gltf = await taoBoTaiMoHinhGoc().parseAsync(tep, new URL('.', duongDan).href)
      if (boHuy.signal.aborted || daDung) {
        giaiPhongCanh(gltf.scene)
        return
      }
      const toanBo = new Box3().setFromObject(gltf.scene)
      // Các mô hình có vành lấy thân chính làm tâm. Vành và vệ tinh vẫn nằm trong cảnh.
      const tenThan: Partial<Record<MaThienThe, string>> = {
        jupiter: 'jupiter1_A_jupiter1_A_0', saturn: '0', uranus: '0', neptune: 'neptune1_A_neptune1_A_0',
      }
      const than = tenThan[ma] ? gltf.scene.getObjectByName(tenThan[ma]!) : undefined
      const hop = than ? new Box3().setFromObject(than) : toanBo
      const co = hop.getSize(new Vector3())
      const duongKinh = Math.max(co.x, co.y, co.z)
      if (!Number.isFinite(duongKinh) || duongKinh <= 0) {
        giaiPhongCanh(gltf.scene)
        throw new Error('Mô hình rỗng.')
      }
      locTextureGoc(gltf.scene, renderer)
      if (phanXa === undefined) phanXa = taoAnhSangPhanXa(renderer)
      const nhom = new Group()
      const vat = ds.get(ma)!
      gltf.scene.position.sub(hop.getCenter(new Vector3()))
      nhom.add(gltf.scene)
      const tiLe = 2 * theoMa[ma].banKinh / duongKinh
      nhom.scale.setScalar(tiLe)
      const kichThuocToanBo = toanBo.getSize(new Vector3()).length() * tiLe
      controls.maxDistance = Math.max(200, kichThuocToanBo * 2)
      camera.far = Math.max(410, kichThuocToanBo * 4)
      camera.updateProjectionMatrix()
      const mixer = gltf.animations.length ? new AnimationMixer(gltf.scene) : undefined
      if (mixer) {
        for (const clip of gltf.animations) mixer.clipAction(clip).play()
        mixer.update(0)
      } else {
        nhom.rotation.y = vat.matCau.rotation.y
      }
      vat.truc.add(nhom)
      vat.matCau.visible = false
      vat.trangTri.visible = false
      const huongDen = new Vector3()
      const tamDen = new Vector3()
      layDich(huongDen, tamDen)
      huongDen.sub(tamDen).normalize().applyAxisAngle(TRUC_DOC, -.78)
      huongDen.y += .28
      denQuanSat.position.copy(huongDen)
      denQuanSat.intensity = ma === 'saturn' ? 2.75 : ma === 'sun' ? 1 : 2.4
      denQuanSat.visible = ma !== 'earth'
      denNen.intensity = ma === 'saturn' ? 1.15 : ma === 'earth' ? 1.05 : .85
      denMatTroi.intensity = ma === 'earth' ? 0 : .45
      scene.environment = phanXa?.texture ?? null
      scene.environmentIntensity = ma === 'earth' ? 1.35 : ma === 'saturn' ? 1 : .64
      renderer.toneMappingExposure = ma === 'saturn' ? 1.24 : 1.07
      moHinh = { ma, nhom, mixer }
      lichVe.yeuCauVe()
      tuyChon.baoMoHinh(ma, 'da-tai')
    } catch {
      if (!boHuy.signal.aborted && !daDung) tuyChon.baoMoHinh(ma, 'loi')
    }
  }

  return {
    datGoc(goc: GocNhin) {
      if (goc.ma !== trangThaiGoc.ma) {
        boMoHinh()
        if (goc.ma) void taiMoHinh(goc.ma)
      }
      trangThaiGoc = goc
      const lienQuan = goc.ma === 'earth' || goc.ma === 'moon'
      for (const [ma, vat] of ds) {
        vat.goc.visible = !goc.ma || ma === goc.ma || (lienQuan && (ma === 'earth' || ma === 'moon'))
        if (vat.quyDao) vat.quyDao.visible = !goc.ma || (lienQuan && ma === 'moon')
      }
      controls.minDistance = goc.ma ? theoMa[goc.ma].banKinh * 1.5 : 1.2
      bay = { batDau: performance.now(), camera: camera.position.clone(), tam: controls.target.clone() }
      lichVe.yeuCauVe()
    },
    datTocDo(giaTri: number) {
      tocDo = giaTri
      lichVe.yeuCauVe()
    },
    datChuyenDongNhe(giaTri: boolean) { chuyenDongNhe = giaTri },
    dong() {
      daDung = true
      lichVe.dong()
      boMoHinh()
      thayDoi.disconnect()
      canvas.removeEventListener('pointerdown', nhanChuot)
      canvas.removeEventListener('pointerup', nhaChuot)
      canvas.removeEventListener('pointercancel', huyChuot)
      canvas.removeEventListener('webglcontextlost', matContext)
      controls.dispose()
      giaiPhongCanh(vatThe)
      phanXa?.dispose()
      renderer.dispose()
      queueMicrotask(() => { if (!canvas.isConnected) renderer.forceContextLoss() })
    },
  }
}
