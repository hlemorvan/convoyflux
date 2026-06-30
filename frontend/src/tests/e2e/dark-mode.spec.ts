// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

test.describe('Mode sombre / clair', () => {
  test('le bouton bascule le thème', async ({ page }) => {
    await page.goto('/')

    const toggle = page.getByRole('button', { name: /clair|sombre/i })
    await expect(toggle).toBeVisible()

    const htmlEl = page.locator('html')
    const initialHasDark = await htmlEl.evaluate(el => el.classList.contains('dark'))

    await toggle.click()
    const afterClick = await htmlEl.evaluate(el => el.classList.contains('dark'))
    expect(afterClick).toBe(!initialHasDark)

    // Revenir à l'état initial
    await toggle.click()
    const restored = await htmlEl.evaluate(el => el.classList.contains('dark'))
    expect(restored).toBe(initialHasDark)
  })
})
