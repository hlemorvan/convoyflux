// SPDX-License-Identifier: GPL-3.0-or-later
import { createSlice, PayloadAction } from '@reduxjs/toolkit'

export type ConnectionStatus = 'connecting' | 'connected' | 'reconnecting' | 'error'

interface ConnectionState {
  status:       ConnectionStatus
  errorMessage: string | null
}

const initialState: ConnectionState = {
  status:       'connecting',
  errorMessage: null,
}

const connectionSlice = createSlice({
  name: 'connection',
  initialState,
  reducers: {
    statusChanged(state, action: PayloadAction<ConnectionStatus>) {
      state.status = action.payload
      if (action.payload === 'connected') state.errorMessage = null
    },
    errorOccurred(state, action: PayloadAction<string>) {
      state.status       = 'error'
      state.errorMessage = action.payload
    },
  },
})

export const { statusChanged, errorOccurred } = connectionSlice.actions
export default connectionSlice.reducer
