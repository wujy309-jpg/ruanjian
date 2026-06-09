import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  build: {
    outDir: 'dist', // 打包输出目录
    assetsDir: 'static' // 静态资源目录
  },
  server: {
    port: 3080, // 前端服务端口
    //现在配置的绝对地址，不走代理！后台处理跨域
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true
      }
    }
  }
}) 