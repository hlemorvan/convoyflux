// SPDX-License-Identifier: GPL-3.0-or-later
import { useState, useEffect } from 'react'
import FleetMap        from './components/Map/FleetMap'
import VehiclePanel    from './components/Panel/VehiclePanel'
import FilterBar       from './components/Filters/FilterBar'
import ConnectionBadge from './components/ConnectionBadge'
import { useTelemetryStream } from './hooks/useTelemetryStream'

export default function App() {
  useTelemetryStream()

  const [dark, setDark] = useState(() =>
    window.matchMedia('(prefers-color-scheme: dark)').matches
  )

  useEffect(() => {
    document.documentElement.classList.toggle('dark', dark)
  }, [dark])

  return (
    <div className="h-screen w-screen flex flex-col bg-gray-50 dark:bg-gray-950 overflow-hidden">
      {/* Barre de filtres */}
      <FilterBar />

      {/* Zone carte — plein écran */}
      <div className="relative flex-1 overflow-hidden">
        <FleetMap />

        {/* Panneau véhicule sélectionné */}
        <VehiclePanel />

        {/* Contrôles en bas à droite */}
        <div className="absolute bottom-4 right-4 z-10 flex flex-col items-end gap-2">
          <ConnectionBadge />
          <button
            onClick={() => setDark(d => !d)}
            aria-label="Basculer mode sombre/clair"
            className="px-3 py-1.5 rounded-lg text-sm font-medium
                       bg-white dark:bg-gray-800
                       text-gray-700 dark:text-gray-200
                       shadow border border-gray-200 dark:border-gray-700
                       hover:bg-gray-50 dark:hover:bg-gray-700 transition"
          >
            {dark ? '☀ Clair' : '☾ Sombre'}
          </button>
        </div>

        {/* Attribution OpenFreeMap / OSM */}
        <div className="absolute bottom-1 left-1 z-10 text-[10px] text-gray-500 dark:text-gray-400">
          © <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noreferrer"
               className="underline">OpenStreetMap contributors</a> / OpenFreeMap
        </div>
      </div>
    </div>
  )
}
