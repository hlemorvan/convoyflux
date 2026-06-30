// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

test.describe('Sélection de véhicule', () => {
  test('cliquer sur un marqueur ouvre le panneau de détail', async ({ page }) => {
    await page.goto('/')

    // Attend qu'il y ait des marqueurs
    await page.waitForFunction(
      () => document.querySelectorAll('.vehicle-marker').length > 0,
      { timeout: 15_000 }
    )

    await page.locator('.vehicle-marker').first().click()
    await expect(page.getByTestId('vehicle-panel')).toBeVisible({ timeout: 2_000 })
  })

  test('le panneau se ferme avec le bouton ✕', async ({ page }) => {
    await page.goto('/')
    await page.waitForFunction(
      () => document.querySelectorAll('.vehicle-marker').length > 0,
      { timeout: 15_000 }
    )
    await page.locator('.vehicle-marker').first().click()
    await page.getByLabel('Fermer').click()
    await expect(page.getByTestId('vehicle-panel')).not.toBeVisible()
  })
})
