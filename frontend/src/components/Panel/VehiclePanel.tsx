// SPDX-License-Identifier: GPL-3.0-or-later
import { useDispatch, useSelector } from 'react-redux'
import { vehicleSelected } from '../../store/vehiclesSlice'
import type { RootState, AppDispatch } from '../../store'

export default function VehiclePanel() {
  const dispatch  = useDispatch<AppDispatch>()
  const vehicleId = useSelector((s: RootState) => s.vehicles.selectedVehicleId)
  const vehicle   = useSelector((s: RootState) =>
    vehicleId ? s.vehicles.vehicles[vehicleId] : null
  )

  if (!vehicleId || !vehicle) return null

  return (
    <div
      data-testid="vehicle-panel"
      className="absolute top-14 right-4 z-10 w-72
                 bg-white dark:bg-gray-900
                 rounded-xl shadow-xl border border-gray-200 dark:border-gray-700
                 p-4 space-y-3"
    >
      <div className="flex justify-between items-center">
        <h2 className="font-bold text-gray-800 dark:text-gray-100">{vehicleId}</h2>
        <button
          onClick={() => dispatch(vehicleSelected(null))}
          className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 text-lg"
          aria-label="Fermer"
        >✕</button>
      </div>

      <div className="grid grid-cols-2 gap-2 text-sm">
        <Stat label="Région"   value={vehicle.region} />
        <Stat label="Vitesse"  value={`${vehicle.speed.toFixed(1)} km/h`} />
        <Stat label="Cap"      value={`${vehicle.heading.toFixed(0)}°`} />
        <Stat label="Lat"      value={vehicle.lat.toFixed(5)} />
        <Stat label="Lng"      value={vehicle.lng.toFixed(5)} />
        <Stat label="Màj"      value={new Date(vehicle.timestamp).toLocaleTimeString()} />
      </div>

      <div>
        <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">
          Trajectoire ({vehicle.trail.length} points)
        </p>
        <div className="h-2 bg-blue-100 dark:bg-blue-900 rounded-full overflow-hidden">
          <div
            className="h-full bg-blue-500 rounded-full"
            style={{ width: `${(vehicle.trail.length / 50) * 100}%` }}
          />
        </div>
      </div>
    </div>
  )
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <span className="text-gray-500 dark:text-gray-400 text-xs">{label}</span>
      <p className="font-medium text-gray-800 dark:text-gray-100">{value}</p>
    </div>
  )
}
