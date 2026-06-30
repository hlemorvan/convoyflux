// SPDX-License-Identifier: GPL-3.0-or-later
import { describe, it, expect } from 'vitest'
import reducer, {
  telemetryReceived,
  vehicleSelected,
  filterChanged,
  type TelemetryEvent,
} from '../../store/vehiclesSlice'

const evt = (id: string): TelemetryEvent => ({
  vehicleId: id, region: 'idf', lat: 48.85, lng: 2.35,
  speed: 60, heading: 90, timestamp: '2026-06-30T10:00:00Z',
})

describe('vehiclesSlice', () => {
  it('adds vehicle on first telemetry', () => {
    const state = reducer(undefined, telemetryReceived(evt('v-001')))
    expect(state.vehicles['v-001']).toBeDefined()
    expect(state.vehicles['v-001'].trail).toHaveLength(1)
  })

  it('appends trail on subsequent events', () => {
    let state = reducer(undefined, telemetryReceived(evt('v-001')))
    state     = reducer(state,     telemetryReceived({ ...evt('v-001'), lat: 48.86 }))
    expect(state.vehicles['v-001'].trail).toHaveLength(2)
  })

  it('caps trail at 50 points', () => {
    let state = reducer(undefined, { type: 'noop' })
    for (let i = 0; i < 60; i++) {
      state = reducer(state, telemetryReceived({ ...evt('v-001'), lat: 48.85 + i * 0.001 }))
    }
    expect(state.vehicles['v-001'].trail).toHaveLength(50)
  })

  it('sets selected vehicle', () => {
    const state = reducer(undefined, vehicleSelected('v-001'))
    expect(state.selectedVehicleId).toBe('v-001')
  })

  it('clears selected vehicle', () => {
    let state = reducer(undefined, vehicleSelected('v-001'))
    state     = reducer(state,     vehicleSelected(null))
    expect(state.selectedVehicleId).toBeNull()
  })

  it('updates filter', () => {
    const state = reducer(undefined, filterChanged({ searchId: 'v-0', minSpeed: 30 }))
    expect(state.filter.searchId).toBe('v-0')
    expect(state.filter.minSpeed).toBe(30)
  })
})
