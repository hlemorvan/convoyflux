// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

const SSE_HEADERS = { 'Content-Type': 'text/event-stream', 'Cache-Control': 'no-cache' }
const SSE_BODY = `event: telemetry\ndata: ${JSON.stringify({ vehicleId: 'v-001', lat: 48.8566, lng: 2.3522, speed: 45, heading: 90, region: 'ile-de-france', timestamp: '2026-01-01T00:00:00Z' })}\n\n`

test.describe('Sélection de véhicule', () => {
  test('cliquer sur un marqueur ouvre le panneau de détail', async ({ page }) => {
    await page.route('/api/stream', route =>
      route.fulfill({ status: 200, headers: SSE_HEADERS, body: SSE_BODY })
    )
    await page.goto('/')

    await page.waitForFunction(
      () => document.querySelectorAll('.vehicle-marker').length > 0,
      { timeout: 10_000 }
    )

    await page.locator('.vehicle-marker').first().click()
    await expect(page.getByTestId('vehicle-panel')).toBeVisible({ timeout: 2_000 })
  })

  test('le panneau se ferme avec le bouton ✕', async ({ page }) => {
    await page.route('/api/stream', route =>
      route.fulfill({ status: 200, headers: SSE_HEADERS, body: SSE_BODY })
    )
    await page.goto('/')
    await page.waitForFunction(
      () => document.querySelectorAll('.vehicle-marker').length > 0,
      { timeout: 10_000 }
    )
    await page.locator('.vehicle-marker').first().click()
    await page.getByLabel('Fermer').click()
    await expect(page.getByTestId('vehicle-panel')).not.toBeVisible()
  })
})
