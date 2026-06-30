// SPDX-License-Identifier: GPL-3.0-or-later
import { createSlice, PayloadAction } from '@reduxjs/toolkit'

export interface TelemetryEvent {
  vehicleId: string
  region:    string
  lat:       number
  lng:       number
  speed:     number
  heading:   number
  timestamp: string
}

export interface VehicleState extends TelemetryEvent {
  trail: Array<{ lat: number; lng: number }>
}

export interface FilterState {
  region:   string
  minSpeed: number
  maxSpeed: number
  searchId: string
}

interface VehiclesSliceState {
  vehicles:          Record<string, VehicleState>
  selectedVehicleId: string | null
  filter:            FilterState
}

const initialState: VehiclesSliceState = {
  vehicles:          {},
  selectedVehicleId: null,
  filter: { region: '', minSpeed: 0, maxSpeed: 200, searchId: '' },
}

const MAX_TRAIL = 50

const vehiclesSlice = createSlice({
  name: 'vehicles',
  initialState,
  reducers: {
    telemetryReceived(state, action: PayloadAction<TelemetryEvent>) {
      const t = action.payload
      const existing = state.vehicles[t.vehicleId]
      const prevTrail = existing?.trail ?? []
      state.vehicles[t.vehicleId] = {
        ...t,
        trail: [...prevTrail.slice(-MAX_TRAIL + 1), { lat: t.lat, lng: t.lng }],
      }
    },
    vehicleSelected(state, action: PayloadAction<string | null>) {
      state.selectedVehicleId = action.payload
    },
    filterChanged(state, action: PayloadAction<Partial<FilterState>>) {
      state.filter = { ...state.filter, ...action.payload }
    },
  },
})

export const { telemetryReceived, vehicleSelected, filterChanged } = vehiclesSlice.actions
export default vehiclesSlice.reducer
