// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

const SSE_HEADERS = { 'Content-Type': 'text/event-stream', 'Cache-Control': 'no-cache' }
const SSE_BODY = `event: telemetry\ndata: ${JSON.stringify({ vehicleId: 'v-001', lat: 48.8566, lng: 2.3522, speed: 45, heading: 90, region: 'ile-de-france', timestamp: '2026-01-01T00:00:00Z' })}\n\n`

test.describe('Reconnexion SSE', () => {
  test('le badge passe en reconnexion si le flux est coupé puis se rétablit', async ({ page }) => {
    // Phase 1 : connexions avortées → badge en erreur/reconnexion
    // Phase 2 : connexion rétablie → badge en connecté
    let phase = 1

    await page.route('/api/stream', async route => {
      if (phase === 1) {
        await route.abort()
      } else {
        await route.fulfill({ status: 200, headers: SSE_HEADERS, body: SSE_BODY })
      }
    })

    await page.goto('/')

    // Phase 1 : la connexion SSE est bloquée → badge en erreur
    await expect(page.getByText(/erreur|reconnexion/i)).toBeVisible({ timeout: 10_000 })

    // Phase 2 : rétablir le flux (le hook reconnecte automatiquement via backoff)
    phase = 2
    await expect(page.getByText('✓ Connecté')).toBeVisible({ timeout: 30_000 })
  })
})
