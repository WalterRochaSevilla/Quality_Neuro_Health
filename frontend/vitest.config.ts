import { defineConfig } from 'vitest/config'
import { resolve } from 'path'

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['zone.js', 'zone.js/testing', './src/test-setup.ts'],
    include: ['src/**/*.spec.ts'],
    exclude: ['node_modules/', 'dist/'],
    css: true,
    coverage: { reporter: ['text', 'html'] },
    pool: 'threads',
    // 👇 Esto le dice a Vitest qué tsconfig usar
    alias: { '@': resolve(__dirname, 'src') },
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
})
