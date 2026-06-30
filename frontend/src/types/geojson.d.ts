// SPDX-License-Identifier: GPL-3.0-or-later
// Déclarations minimales GeoJSON utilisées dans FleetMap.tsx
// (maplibre-gl expose ses propres types mais pas l'espace de noms global GeoJSON)
declare namespace GeoJSON {
  interface FeatureCollection {
    type: 'FeatureCollection'
    features: Feature[]
  }
  interface Feature {
    type: 'Feature'
    geometry: Geometry
    properties: Record<string, unknown> | null
  }
  type Geometry = LineString | Point
  interface LineString {
    type: 'LineString'
    coordinates: [number, number][]
  }
  interface Point {
    type: 'Point'
    coordinates: [number, number]
  }
}
