// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

test.describe('Mise à jour en temps réel', () => {
  test('les positions des marqueurs changent après quelques secondes', async ({ page }) => {
    await page.goto('/')
    await page.waitForFunction(
      () => document.querySelectorAll('.vehicle-marker').length > 0,
      { timeout: 15_000 }
    )

    // Capture la position initiale du premier marqueur
    const initialTransform = await page.locator('.vehicle-marker').first()
      .evaluate(el => el.style.transform)

    // Attend 3 secondes (les véhicules publient toutes les ~1s)
    await page.waitForTimeout(3_000)

    // Vérifie que le cap a changé (le transform change avec le heading)
    const markers = await page.locator('.vehicle-marker').all()
    const transforms = await Promise.all(
      markers.map(m => m.evaluate((el: HTMLElement) => el.style.transform))
    )
    // Au moins un marqueur doit avoir un transform différent de l'initial
    const changed = transforms.some(t => t !== initialTransform)
    expect(changed).toBeTruthy()
  })
})
