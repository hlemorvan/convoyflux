// SPDX-License-Identifier: GPL-3.0-or-later
/**
 * Config Playwright dédiée aux captures d'écran pour la documentation.
 * Démarre le serveur Vite dev automatiquement — pas de Docker requis.
 * L'API SSE est mockée dans chaque test via page.route().
 */
import { defineConfig, devices } from '@playwright/test'

export default defineConfig({
  testDir: './src/tests/e2e',
  testMatch: '**/screenshots.spec.ts',
  fullyParallel: false,   // séquentiellement pour des captures stables
  retries: 1,
  reporter: [['html', { open: 'never', outputFolder: 'playwright-report-screenshots' }]],

  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },

  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: true,
    timeout: 30_000,
  },

  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
})
