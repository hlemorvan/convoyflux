// SPDX-License-Identifier: GPL-3.0-or-later

/** Crée un élément HTML pour un marqueur MapLibre en forme de donut. */
export function createMarkerElement(heading: number, selected: boolean): HTMLDivElement {
  const el = document.createElement('div')
  el.className = 'vehicle-marker'
  el.style.cssText = `
    width: 26px;
    height: 26px;
    display: flex;
    align-items: center;
    justify-content: center;
    transform: rotate(${heading}deg);
    cursor: pointer;
    transition: transform 0.3s ease;
    filter: drop-shadow(0 1px 3px rgba(0,0,0,0.5));
  `

  const color = selected ? '#ef4444' : '#3b82f6'

  el.innerHTML = `
    <svg viewBox="0 0 24 24" width="26" height="26">
      <!-- Anneau externe coloré selon la vitesse -->
      <circle class="ring" cx="12" cy="12" r="10"
              fill="${color}" stroke="white" stroke-width="1.5"/>
      <!-- Trou central blanc -->
      <circle cx="12" cy="12" r="5" fill="white"/>
      <!-- Pastille de cap (pointe vers le Nord à 0°, pivote avec le marqueur) -->
      <circle cx="12" cy="4" r="2.5" fill="white" opacity="0.9"/>
    </svg>
  `
  return el
}
