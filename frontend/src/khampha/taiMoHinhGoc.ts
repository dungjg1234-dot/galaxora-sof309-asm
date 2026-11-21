import { Color, Mesh, MeshPhysicalMaterial, ShaderChunk, SRGBColorSpace } from 'three'
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'

const SPEC_GLOSS = 'KHR_materials_pbrSpecularGlossiness'

type VatLieuGoc = {
  diffuseFactor?: [number, number, number, number]
  diffuseTexture?: { index: number; texCoord?: number }
  specularFactor?: [number, number, number]
  glossinessFactor?: number
  specularGlossinessTexture?: { index: number; texCoord?: number }
}

// Các GLB giữ nguyên extension gốc. Shader dùng RGB cho phản xạ và alpha cho độ bóng.
export function taoBoTaiMoHinhGoc() {
  return new GLTFLoader().register(parser => {
    const vatLieu = (i: number): VatLieuGoc | undefined => parser.json.materials?.[i]?.extensions?.[SPEC_GLOSS]
    return {
      name: SPEC_GLOSS,
      getMaterialType: i => vatLieu(i) ? MeshPhysicalMaterial : null,
      extendMaterialParams(i, thamSo) {
        const goc = vatLieu(i)
        if (!goc) return null
        const [r, g, b, alpha] = goc.diffuseFactor ?? [1, 1, 1, 1]
        thamSo.color = new Color(r, g, b)
        thamSo.opacity = alpha
        thamSo.metalness = 0
        thamSo.roughness = 1 - (goc.glossinessFactor ?? 1)
        thamSo.specularColor = new Color(...(goc.specularFactor ?? [1, 1, 1]))
        const viec = []
        if (goc.diffuseTexture) viec.push(parser.assignTexture(thamSo, 'map', goc.diffuseTexture, SRGBColorSpace))
        if (goc.specularGlossinessTexture) {
          viec.push(parser.assignTexture(thamSo, 'specularColorMap', goc.specularGlossinessTexture, SRGBColorSpace))
        }
        return Promise.all(viec)
      },
      afterRoot(gltf) {
        const daXuLy = new Set<MeshPhysicalMaterial>()
        for (const scene of gltf.scenes) scene.traverse(vat => {
          if (!(vat instanceof Mesh)) return
          const danhSach = Array.isArray(vat.material) ? vat.material : [vat.material]
          for (const chatLieu of danhSach) {
            if (!(chatLieu instanceof MeshPhysicalMaterial) || daXuLy.has(chatLieu)) continue
            const index = parser.associations.get(chatLieu)?.materials
            if (index === undefined || !vatLieu(index)) continue
            daXuLy.add(chatLieu)
            chatLieu.customProgramCacheKey = () => SPEC_GLOSS
            chatLieu.onBeforeCompile = shader => {
              shader.fragmentShader = shader.fragmentShader
                .replace('#include <roughnessmap_fragment>', `
                  float roughnessFactor = roughness;
                  #ifdef USE_SPECULAR_COLORMAP
                    roughnessFactor = 1.0 - (1.0 - roughness) * texture2D(specularColorMap, vSpecularColorMapUv).a;
                  #endif
                `)
                .replace('#include <lights_physical_fragment>', ShaderChunk.lights_physical_fragment.replace(
                  /material\.specularColor = min\( pow2[^;]+;/,
                  'material.specularColor = clamp(specularColorFactor, 0.0, 1.0);\n' +
                  'material.diffuseContribution *= 1.0 - max(max(material.specularColor.r, material.specularColor.g), material.specularColor.b);',
                ))
            }
          }
        })
        return null
      },
    }
  })
}
