// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

test.describe('Carte principale', () => {
  test('la carte se charge et affiche des marqueurs', async ({ page }) => {
    await page.goto('/')
    await page.waitForSelector('[data-testid="fleet-map"]', { timeout: 10_000 })

    // Attend que des marqueurs apparaissent (le simulateur publie sous ~2s)
    await page.waitForFunction(
      () => document.querySelectorAll('.vehicle-marker').length > 0,
      { timeout: 15_000 }
    )

    const markers = await page.locator('.vehicle-marker').count()
    expect(markers).toBeGreaterThan(0)
  })

  test('la page contient le titre convoyflux', async ({ page }) => {
    await page.goto('/')
    await expect(page).toHaveTitle(/convoyflux/)
  })
})
