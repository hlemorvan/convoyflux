// SPDX-License-Identifier: GPL-3.0-or-later
/**
 * Spec dédié à la capture d'écran pour la documentation.
 * Utilise page.route() pour mocker le flux SSE — pas de backend requis.
 * Lancer avec : npx playwright test screenshots --headed (optionnel)
 */
import { test, Page } from '@playwright/test'

// ---------- helpers ----------

function ssePayload(vehicles: Array<{
  vehicleId: string; lat: number; lng: number;
  speed: number; heading: number
}>) {
  return vehicles.map(v =>
    `event: telemetry\ndata: ${JSON.stringify({
      ...v, region: 'ile-de-france',
      timestamp: new Date().toISOString()
    })}\n\n`
  ).join('')
}

const FLEET = [
  { vehicleId: 'v-001', lat: 48.8746, lng: 2.2951, speed: 45,  heading: 52  },
  { vehicleId: 'v-002', lat: 48.8608, lng: 2.3258, speed: 72,  heading: 130 },
  { vehicleId: 'v-003', lat: 48.8530, lng: 2.3499, speed: 110, heading: 200 },
  { vehicleId: 'v-004', lat: 48.8460, lng: 2.3697, speed: 18,  heading: 320 },
  { vehicleId: 'v-005', lat: 48.8660, lng: 2.3122, speed: 63,  heading: 88  },
  { vehicleId: 'v-006', lat: 48.8780, lng: 2.3350, speed: 34,  heading: 275 },
  { vehicleId: 'v-007', lat: 48.8510, lng: 2.2850, speed: 95,  heading: 160 },
  { vehicleId: 'v-008', lat: 48.8680, lng: 2.3720, speed: 27,  heading: 45  },
  { vehicleId: 'v-009', lat: 48.8420, lng: 2.3100, speed: 81,  heading: 240 },
  { vehicleId: 'v-010', lat: 48.8590, lng: 2.2780, speed: 55,  heading: 15  },
  { vehicleId: 'v-011', lat: 48.8730, lng: 2.3580, speed: 42,  heading: 110 },
  { vehicleId: 'v-012', lat: 48.8480, lng: 2.3420, speed: 130, heading: 70  },
]

async function mockSse(page: Page, fleet = FLEET) {
  await page.route('/api/stream', async route => {
    const body = ssePayload(fleet)
    await route.fulfill({
      status: 200,
      headers: {
        'Content-Type':  'text/event-stream',
        'Cache-Control': 'no-cache',
        'Connection':    'keep-alive',
      },
      body,
    })
  })
}

async function waitForMarkers(page: Page, min = 3) {
  await page.waitForFunction(
    (n) => document.querySelectorAll('.vehicle-marker').length >= n, min,
    { timeout: 10_000 }
  )
}

// ---------- captures ----------

test.describe('Screenshots documentation', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('01 — vue d\'ensemble (mode clair)', async ({ page }) => {
    await mockSse(page)
    await page.goto('/')
    // Force le mode clair
    await page.evaluate(() => document.documentElement.classList.remove('dark'))
    await waitForMarkers(page)
    await page.waitForTimeout(1200) // laisse les marqueurs se positionner
    await page.screenshot({
      path: '../docs/screenshots/overview.png',
      fullPage: false,
    })
  })

  test('02 — carte temps réel avec marqueurs (mode clair)', async ({ page }) => {
    await mockSse(page)
    await page.goto('/')
    await page.evaluate(() => document.documentElement.classList.remove('dark'))
    await waitForMarkers(page, 5)
    await page.waitForTimeout(800)
    await page.screenshot({ path: '../docs/screenshots/map-live.png' })
  })

  test('03 — panneau de détail véhicule', async ({ page }) => {
    await mockSse(page)
    await page.goto('/')
    await page.evaluate(() => document.documentElement.classList.remove('dark'))
    await waitForMarkers(page, 3)
    await page.locator('.vehicle-marker').first().click()
    await page.waitForSelector('[data-testid="vehicle-panel"]', { timeout: 3_000 })
    await page.waitForTimeout(400)
    await page.screenshot({ path: '../docs/screenshots/vehicle-panel.png' })
  })

  test('04 — barre de filtres', async ({ page }) => {
    await mockSse(page)
    await page.goto('/')
    await page.evaluate(() => document.documentElement.classList.remove('dark'))
    await waitForMarkers(page)
    // Tape dans le champ de recherche
    await page.fill('input[placeholder*="Rechercher"]', 'v-0')
    await page.waitForTimeout(400)
    await page.screenshot({
      path: '../docs/screenshots/filters.png',
      clip: { x: 0, y: 0, width: 1440, height: 120 },
    })
  })

  test('05 — mode sombre', async ({ page }) => {
    await mockSse(page)
    await page.goto('/')
    // Force le mode sombre
    await page.evaluate(() => document.documentElement.classList.add('dark'))
    await waitForMarkers(page)
    await page.waitForTimeout(1200)
    await page.screenshot({ path: '../docs/screenshots/dark-mode.png' })
  })

  test('06 — badge connexion (états)', async ({ page }) => {
    // Capture la barre de contrôles en bas à droite (badge + bouton thème)
    // route.fulfill() envoie la réponse SSE puis ferme la connexion :
    // on capture juste après la réception des marqueurs (état "connecté" ou "reconnexion")
    await mockSse(page)
    await page.goto('/')
    await page.evaluate(() => document.documentElement.classList.remove('dark'))
    await waitForMarkers(page)
    // Petite pause pour que React render le badge
    await page.waitForTimeout(300)
    await page.screenshot({
      path: '../docs/screenshots/connection-badge.png',
      clip: { x: 1100, y: 800, width: 340, height: 100 },
    })
  })

  test('07 — mode clair vs sombre côte à côte (composite)', async ({ browser }) => {
    // Ouvre deux contextes pour une capture côte à côte
    const ctxLight = await browser.newContext({ viewport: { width: 720, height: 900 } })
    const ctxDark  = await browser.newContext({ viewport: { width: 720, height: 900 } })

    const pageLight = await ctxLight.newPage()
    const pageDark  = await ctxDark.newPage()

    await mockSse(pageLight)
    await mockSse(pageDark)

    await pageLight.goto('/')
    await pageDark.goto('/')

    await pageLight.evaluate(() => document.documentElement.classList.remove('dark'))
    await pageDark.evaluate(()  => document.documentElement.classList.add('dark'))

    await Promise.all([
      waitForMarkers(pageLight),
      waitForMarkers(pageDark),
    ])
    await pageLight.waitForTimeout(1000)
    await pageDark.waitForTimeout(1000)

    await pageLight.screenshot({ path: '../docs/screenshots/light-mode.png' })
    await pageDark.screenshot({ path: '../docs/screenshots/dark-mode-full.png' })

    await ctxLight.close()
    await ctxDark.close()
  })
})
