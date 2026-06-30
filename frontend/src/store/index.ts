// SPDX-License-Identifier: GPL-3.0-or-later
import { configureStore } from '@reduxjs/toolkit'
import vehiclesReducer   from './vehiclesSlice'
import connectionReducer from './connectionSlice'

export const store = configureStore({
  reducer: {
    vehicles:   vehiclesReducer,
    connection: connectionReducer,
  },
})

export type RootState   = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
