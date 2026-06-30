// SPDX-License-Identifier: GPL-3.0-or-later
import { useDispatch, useSelector } from 'react-redux'
import { filterChanged } from '../../store/vehiclesSlice'
import type { RootState, AppDispatch } from '../../store'

export default function FilterBar() {
  const dispatch = useDispatch<AppDispatch>()
  const filter   = useSelector((s: RootState) => s.vehicles.filter)

  return (
    <div className="flex gap-3 items-center flex-wrap px-4 py-2
                    bg-white/90 dark:bg-gray-900/90 backdrop-blur
                    border-b border-gray-200 dark:border-gray-700">
      <input
        type="text"
        placeholder="Rechercher un véhicule…"
        value={filter.searchId}
        onChange={e => dispatch(filterChanged({ searchId: e.target.value }))}
        className="border rounded px-2 py-1 text-sm dark:bg-gray-800 dark:border-gray-600
                   dark:text-gray-100 focus:outline-none focus:ring-1 focus:ring-blue-500"
      />
      <input
        type="text"
        placeholder="Région…"
        value={filter.region}
        onChange={e => dispatch(filterChanged({ region: e.target.value }))}
        className="border rounded px-2 py-1 text-sm dark:bg-gray-800 dark:border-gray-600
                   dark:text-gray-100 focus:outline-none focus:ring-1 focus:ring-blue-500"
      />
      <label className="flex items-center gap-2 text-sm dark:text-gray-200">
        Vitesse min
        <input
          type="range" min={0} max={200} step={10}
          value={filter.minSpeed}
          onChange={e => dispatch(filterChanged({ minSpeed: Number(e.target.value) }))}
          className="w-24"
        />
        <span className="w-10">{filter.minSpeed} km/h</span>
      </label>
    </div>
  )
}
