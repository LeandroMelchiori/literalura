import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// En desarrollo, /api se proxya al backend local; en producción el frontend
// usa VITE_API_URL (dominio de la API) y CORS.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});
