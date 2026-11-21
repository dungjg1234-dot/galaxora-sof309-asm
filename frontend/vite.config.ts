import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath } from 'node:url'
import { defineConfig, searchForWorkspaceRoot } from 'vite'

// Khi chạy dev, mọi yêu cầu /api được chuyển tới backend để dùng cùng origin (cookie session không cần CORS).
export default defineConfig({
  plugins: [react(), tailwindcss()],
  assetsInclude: ['**/*.glb'],
  server: {
    fs: {
      allow: [searchForWorkspaceRoot(process.cwd()), fileURLToPath(new URL('../assets/3d/', import.meta.url))],
    },
    proxy: {
      '/api': {
        target: process.env.GALAXORA_BACKEND_URL ?? 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
