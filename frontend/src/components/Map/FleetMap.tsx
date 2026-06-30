// SPDX-License-Identifier: GPL-3.0-or-later
import { useEffect, useRef } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import maplibregl from 'maplibre-gl'
import { vehicleSelected } from '../../store/vehiclesSlice'
import { createMarkerElement } from './VehicleMarker'
import type { RootState, AppDispatch } from '../../store'
import type { VehicleState } from '../../store/vehiclesSlice'

// Styles OpenFreeMap — gratuits, sans clé d'API
// © OpenStreetMap contributors / OpenFreeMap / OpenMapTiles
const STYLE_LIGHT = import.meta.env.VITE_MAP_STYLE_LIGHT
  ?? 'https://tiles.openfreemap.org/styles/liberty'
const STYLE_DARK  = import.meta.env.VITE_MAP_STYLE_DARK
  ?? 'https://tiles.openfreemap.org/styles/dark'

const PARIS = { lng: 2.3522, lat: 48.8566 }

function speedColor(speed: number): string {
  if (speed < 30)  return '#22c55e' // vert  — lent
  if (speed < 90)  return '#3b82f6' // bleu  — normal
  return '#ef4444'                   // rouge — rapide
}

export default function FleetMap() {
  const dispatch   = useDispatch<AppDispatch>()
  const vehicles   = useSelector((s: RootState) => s.vehicles.vehicles)
  const selectedId = useSelector((s: RootState) => s.vehicles.selectedVehicleId)
  const filter     = useSelector((s: RootState) => s.vehicles.filter)
  const isDark     = document.documentElement.classList.contains('dark')

  const mapRef      = useRef<maplibregl.Map | null>(null)
  const mapDivRef   = useRef<HTMLDivElement>(null)
  const markersRef  = useRef<Record<string, maplibregl.Marker>>({})

  // Initialise la carte
  useEffect(() => {
    if (!mapDivRef.current) return
    const map = new maplibregl.Map({
      container: mapDivRef.current,
      style:     isDark ? STYLE_DARK : STYLE_LIGHT,
      center:    [PARIS.lng, PARIS.lat],
      zoom:      12,
      attributionControl: {},
    })
    mapRef.current = map
    return () => {
      map.remove()
      mapRef.current = null
    }
  }, [])

  // Bascule de style clair/sombre
  useEffect(() => {
    mapRef.current?.setStyle(isDark ? STYLE_DARK : STYLE_LIGHT)
  }, [isDark])

  // Met à jour les marqueurs quand les véhicules changent
  useEffect(() => {
    const map = mapRef.current
    if (!map) return

    const visible = Object.values(vehicles).filter(v => matchesFilter(v, filter))
    const visibleIds = new Set(visible.map(v => v.vehicleId))

    // Supprime les marqueurs obsolètes
    Object.keys(markersRef.current).forEach(id => {
      if (!visibleIds.has(id)) {
        markersRef.current[id].remove()
        delete markersRef.current[id]
      }
    })

    // Ajoute ou met à jour les marqueurs
    visible.forEach(v => {
      const isSelected = v.vehicleId === selectedId
      if (markersRef.current[v.vehicleId]) {
        const el = markersRef.current[v.vehicleId].getElement()
        el.querySelector('.ring')?.setAttribute('fill',
          isSelected ? '#ef4444' : speedColor(v.speed))
        el.style.transform = `rotate(${v.heading}deg)`
        markersRef.current[v.vehicleId].setLngLat([v.lng, v.lat])
      } else {
        const el = createMarkerElement(v.heading, isSelected)
        el.addEventListener('click', () => dispatch(vehicleSelected(v.vehicleId)))
        const marker = new maplibregl.Marker({ element: el })
          .setLngLat([v.lng, v.lat])
          .addTo(map)
        markersRef.current[v.vehicleId] = marker
      }
    })

    // Trajectoire du véhicule sélectionné
    if (map.getSource('trail')) {
      const trailData = selectedId && vehicles[selectedId]
        ? buildTrailGeoJson(vehicles[selectedId])
        : emptyGeoJson()
      ;(map.getSource('trail') as maplibregl.GeoJSONSource).setData(trailData)
    }
  }, [vehicles, selectedId, filter, dispatch])

  // Ajoute la couche de trajectoire après le chargement de la carte
  useEffect(() => {
    const map = mapRef.current
    if (!map) return
    const onLoad = () => {
      if (!map.getSource('trail')) {
        map.addSource('trail', { type: 'geojson', data: emptyGeoJson() })
        map.addLayer({
          id: 'trail-line',
          type: 'line',
          source: 'trail',
          paint: {
            'line-color':   '#f59e0b',
            'line-width':   2,
            'line-opacity': 0.7,
          },
        })
      }
    }
    if (map.isStyleLoaded()) onLoad()
    else map.on('load', onLoad)
  }, [])

  return (
    <div
      ref={mapDivRef}
      className="w-full h-full"
      data-testid="fleet-map"
      aria-label="Carte de la flotte"
    />
  )
}

function matchesFilter(v: VehicleState, filter: RootState['vehicles']['filter']): boolean {
  if (filter.searchId && !v.vehicleId.toLowerCase().includes(filter.searchId.toLowerCase())) return false
  if (filter.region   && !v.region.toLowerCase().includes(filter.region.toLowerCase()))     return false
  if (v.speed < filter.minSpeed) return false
  return true
}

function buildTrailGeoJson(v: VehicleState): GeoJSON.FeatureCollection {
  return {
    type: 'FeatureCollection',
    features: [{
      type: 'Feature',
      geometry: {
        type: 'LineString',
        coordinates: v.trail.map(p => [p.lng, p.lat]),
      },
      properties: {},
    }],
  }
}

function emptyGeoJson(): GeoJSON.FeatureCollection {
  return { type: 'FeatureCollection', features: [] }
}
