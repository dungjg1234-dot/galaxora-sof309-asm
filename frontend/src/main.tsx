import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import './giaodien/bienThietKe.css'
import './giaodien/thanhPhan.css'
import App from './app/App.tsx'
import './giaodien/hieuUng.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
