const cacMoHinh = import.meta.glob<string>([
  '../../../assets/3d/{galaxies,nebulae,black-holes,solar-system/*}/ban-goc.glb',
  '../../../assets/3d/{galaxies,black-holes,solar-system/*}/ban-goc.glb.gz',
  // Bỏ bản chưa nén khỏi bản build khi thư mục đã có bản gzip đã đối chiếu.
  '!../../../assets/3d/{galaxies,black-holes}/ban-goc.glb',
  '!../../../assets/3d/solar-system/{earth,jupiter,neptune,pluto,saturn,toan-canh,uranus}/ban-goc.glb',
], { query: '?url&no-inline', import: 'default' })

export function timMoHinhKhamPha(thuMuc: string) {
  const duongDan = '../../../assets/3d/' + thuMuc + '/ban-goc.glb'
  return cacMoHinh[duongDan + '.gz'] ?? cacMoHinh[duongDan]
}

export async function taiDuLieuMoHinh(duongDan: URL, tinHieu: AbortSignal): Promise<ArrayBuffer> {
  const traLoi = await fetch(duongDan, { signal: tinHieu })
  if (!traLoi.ok) throw new Error('Không tải được mô hình.')
  if (!duongDan.pathname.endsWith('.gz') || traLoi.headers.get('Content-Encoding') === 'gzip') {
    return traLoi.arrayBuffer()
  }
  if (traLoi.body && typeof DecompressionStream === 'function') {
    return new Response(traLoi.body.pipeThrough(new DecompressionStream('gzip'))).arrayBuffer()
  }

  const tepNen = new Uint8Array(await traLoi.arrayBuffer())
  const { gunzipSync } = await import('three/addons/libs/fflate.module.js')
  if (tinHieu.aborted) throw new DOMException('Đã hủy tải mô hình.', 'AbortError')
  return gunzipSync(tepNen).buffer
}
