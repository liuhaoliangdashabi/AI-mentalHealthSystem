import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'
import {resolve} from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve:{
    alias:{
      '@':resolve(__dirname,'src')
    }
  },
  server:{
    proxy:{
      '/api':{
        target:'http://159.75.169.224:1235',
        changeOrigin:true
      }
    }
  }
})
