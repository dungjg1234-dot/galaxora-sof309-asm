import { useEffect, useRef, useState } from 'react'
import { AmbientLight, Box3, DirectionalLight, Mesh, PerspectiveCamera, Scene, Texture, Vector3, WebGLRenderer,
  type Object3D } from 'three'
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { type MoHinh } from './duLieu.ts'
import { Khoi, useDuLieu } from './dungChung.tsx'

function giaiPhong(cay: Object3D) {
  const daGiaiPhong = new Set<object>()
  cay.traverse(vat => {
    if (!(vat instanceof Mesh)) return
    if (!daGiaiPhong.has(vat.geometry)) {
      vat.geometry.dispose()
      daGiaiPhong.add(vat.geometry)
    }
    for (const chatLieu of Array.isArray(vat.material) ? vat.material : [vat.material]) {
      if (daGiaiPhong.has(chatLieu)) continue
      for (const giaTri of Object.values(chatLieu)) {
        if (giaTri instanceof Texture && !daGiaiPhong.has(giaTri)) {
          giaTri.dispose()
          daGiaiPhong.add(giaTri)
        }
      }
      chatLieu.dispose()
      daGiaiPhong.add(chatLieu)
    }
  })
}

export default function XemMoHinh({ id }: { id: number }) {
  const canvas = useRef<HTMLCanvasElement>(null)
  const dieuKhien = useRef<OrbitControls | null>(null)
  const [trangThai, datTrangThai] = useState<'tai' | 'sanSang' | 'loi'>('tai')
  const api = '/api/cua-hang/san-pham/' + id + '/mo-hinh'
  const meta = useDuLieu<MoHinh>(api)

  useEffect(() => {
    const matVe = canvas.current
    if (!matVe) return
    let renderer: WebGLRenderer
    try {
      renderer = new WebGLRenderer({ canvas: matVe, antialias: true, alpha: true })
    } catch {
      queueMicrotask(() => datTrangThai('loi'))
      return
    }
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    const scene = new Scene()
    scene.add(new AmbientLight(0xffffff, 1.8))
    const den = new DirectionalLight(0xffffff, 2.8)
    den.position.set(3, 4, 5)
    scene.add(den)
    const camera = new PerspectiveCamera(42, 1, 0.01, 100)
    camera.position.set(2.4, 1.6, 2.4)
    const control = new OrbitControls(camera, matVe)
    dieuKhien.current = control
    control.enablePan = false
    control.minDistance = 0.7
    control.maxDistance = 9
    const render = () => renderer.render(scene, camera)
    control.addEventListener('change', render)
    const thayDoi = new ResizeObserver(() => {
      const rong = matVe.clientWidth
      const cao = matVe.clientHeight
      if (rong === 0 || cao === 0) return
      camera.aspect = rong / cao
      camera.updateProjectionMatrix()
      renderer.setSize(rong, cao, false)
      render()
    })
    thayDoi.observe(matVe)
    const huy = new AbortController()
    let model: Object3D | undefined
    fetch(api + '/noi-dung', { signal: huy.signal }).then(r => {
      if (!r.ok) throw new Error('Không tải được tệp.')
      return r.arrayBuffer()
    }).then(tep => new GLTFLoader().parseAsync(tep, '')).then(gltf => {
      if (huy.signal.aborted) { giaiPhong(gltf.scene); return }
      const hop = new Box3().setFromObject(gltf.scene)
      const kichThuoc = hop.getSize(new Vector3()).length()
      if (!Number.isFinite(kichThuoc) || kichThuoc <= 0) {
        giaiPhong(gltf.scene)
        throw new Error('Mô hình không có hình học để hiển thị.')
      }
      const tam = hop.getCenter(new Vector3())
      gltf.scene.position.sub(tam)
      const tiLe = 2 / kichThuoc
      gltf.scene.position.multiplyScalar(tiLe)
      gltf.scene.scale.multiplyScalar(tiLe)
      model = gltf.scene
      scene.add(model)
      control.saveState()
      render()
      datTrangThai('sanSang')
    }).catch(() => { if (!huy.signal.aborted) datTrangThai('loi') })
    return () => {
      huy.abort()
      thayDoi.disconnect()
      control.dispose()
      dieuKhien.current = null
      if (model) giaiPhong(model)
      renderer.dispose()
    }
  }, [api])

  return <Khoi tieuDe="Mô hình 3D">
    {trangThai === 'tai' && <p role="status">Đang tải mô hình…</p>}
    {trangThai === 'loi' && <p role="alert">Không hiển thị được mô hình 3D trên thiết bị này. Ảnh sản phẩm ở phía trên.</p>}
    <canvas ref={canvas} className={trangThai === 'loi' ? 'bh-an' : 'bh-canvas'}
      aria-label="Mô hình sản phẩm. Kéo để xoay, cuộn để thu phóng." />
    {trangThai === 'sanSang' && <div className="bh-hang">
      <button onClick={() => dieuKhien.current?.reset()}>Đặt lại góc nhìn</button>
      <p>Kéo để xoay, cuộn để thu phóng.</p>
    </div>}
    {meta.duLieu && <p>{meta.duLieu.ghiCong} · {meta.duLieu.giayPhep} · <a href={meta.duLieu.nguon}
      target="_blank" rel="noreferrer">Nguồn mô hình</a></p>}
  </Khoi>
}
