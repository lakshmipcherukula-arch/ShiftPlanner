import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/',
  server: {
    proxy: {
      '/shifts': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/users': 'http://localhost:8080',
      '/schedules': 'http://localhost:8080',
    },
  },
});
