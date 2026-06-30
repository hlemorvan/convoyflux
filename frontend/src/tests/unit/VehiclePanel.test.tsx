// SPDX-License-Identifier: GPL-3.0-or-later
import { describe, it, expect } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'
import vehiclesReducer, { telemetryReceived, vehicleSelected } from '../../store/vehiclesSlice'
import connectionReducer from '../../store/connectionSlice'
import VehiclePanel from '../../components/Panel/VehiclePanel'

function makeStore(selectedId: string | null = null) {
  const store = configureStore({
    reducer: { vehicles: vehiclesReducer, connection: connectionReducer },
  })
  store.dispatch(telemetryReceived({
    vehicleId: 'v-001', region: 'idf', lat: 48.85, lng: 2.35,
    speed: 55, heading: 90, timestamp: '2026-06-30T10:00:00Z',
  }))
  if (selectedId) store.dispatch(vehicleSelected(selectedId))
  return store
}

describe('VehiclePanel', () => {
  it('renders nothing when no vehicle is selected', () => {
    const { container } = render(
      <Provider store={makeStore()}><VehiclePanel /></Provider>
    )
    expect(container).toBeEmptyDOMElement()
  })

  it('renders vehicle info when selected', () => {
    render(<Provider store={makeStore('v-001')}><VehiclePanel /></Provider>)
    expect(screen.getByText('v-001')).toBeInTheDocument()
    expect(screen.getByText('55.0 km/h')).toBeInTheDocument()
  })

  it('closes on button click', () => {
    const store = makeStore('v-001')
    render(<Provider store={store}><VehiclePanel /></Provider>)
    fireEvent.click(screen.getByLabelText('Fermer'))
    expect(store.getState().vehicles.selectedVehicleId).toBeNull()
  })
})
