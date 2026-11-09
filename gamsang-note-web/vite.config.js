import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// 개발 서버(5173)에서 /api 로 시작하는 요청을 Spring Boot(8080)로 넘겨 준다.
// 이렇게 하면 브라우저가 같은 주소로 요청하는 것처럼 보여서 CORS 설정이 필요 없다.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
