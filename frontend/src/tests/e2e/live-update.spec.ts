// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

const SSE_HEADERS = { 'Content-Type': 'text/event-stream', 'Cache-Control': 'no-cache' }

function sseBody(heading: number) {
  return `event: telemetry\ndata: ${JSON.stringify({
    vehicleId: 'v-001', lat: 48.8566, lng: 2.3522,
    speed: 45, heading, region: 'ile-de-france',
    timestamp: new Date().toISOString(),
  })}\n\n`
}

test.describe('Mise à jour en temps réel', () => {
  test('les positions des marqueurs changent après mise à jour SSE', async ({ page }) => {
    // Première réponse SSE : heading=90, deuxième : heading=180
    // Le hook reconnecte automatiquement après fermeture de la connexion (~2 s de backoff)
    let callCount = 0
    await page.route('/api/stream', async route => {
      callCount++
      await route.fulfill({
        status: 200,
        headers: SSE_HEADERS,
        body: sseBody(callCount === 1 ? 90 : 180),
      })
    })

    await page.goto('/')

    await page.waitForFunction(
      () => document.querySelectorAll('.vehicle-marker').length > 0,
      { timeout: 10_000 }
    )

    const initialTransform = await page.locator('.vehicle-marker').first()
      .evaluate((el: HTMLElement) => el.style.transform)

    // Attend que le transform change (reconnexion SSE automatique + nouveau heading)
    await page.waitForFunction(
      (expected) => {
        const el = document.querySelector<HTMLElement>('.vehicle-marker')
        return el !== null && el.style.transform !== expected
      },
      initialTransform,
      { timeout: 15_000 }
    )

    const newTransform = await page.locator('.vehicle-marker').first()
      .evaluate((el: HTMLElement) => el.style.transform)
    expect(newTransform).not.toBe(initialTransform)
  })
})
