// SPDX-License-Identifier: GPL-3.0-or-later
import { test, expect } from '@playwright/test'

test.describe('Reconnexion SSE', () => {
  test('le badge passe en reconnexion si le flux est coupé puis se rétablit', async ({ page }) => {
    await page.goto('/')

    // Attend l'état connecté initial
    await expect(page.getByText('✓ Connecté')).toBeVisible({ timeout: 15_000 })

    // Simule une coupure réseau en bloquant les requêtes SSE
    await page.route('/api/stream', route => route.abort())

    // Le badge doit passer en reconnexion ou erreur
    await expect(
      page.getByText(/reconnexion|erreur/i)
    ).toBeVisible({ timeout: 10_000 })

    // Rétablit le flux
    await page.unroute('/api/stream')

    // Le badge repasse en connecté
    await expect(page.getByText('✓ Connecté')).toBeVisible({ timeout: 40_000 })
  })
})
