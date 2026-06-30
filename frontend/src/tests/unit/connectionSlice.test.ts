// SPDX-License-Identifier: GPL-3.0-or-later
import { describe, it, expect } from 'vitest'
import reducer, { statusChanged, errorOccurred } from '../../store/connectionSlice'

describe('connectionSlice', () => {
  it('starts connecting', () => {
    const state = reducer(undefined, { type: 'noop' })
    expect(state.status).toBe('connecting')
  })

  it('transitions to connected', () => {
    const state = reducer(undefined, statusChanged('connected'))
    expect(state.status).toBe('connected')
    expect(state.errorMessage).toBeNull()
  })

  it('records error and sets error status', () => {
    const state = reducer(undefined, errorOccurred('timeout'))
    expect(state.status).toBe('error')
    expect(state.errorMessage).toBe('timeout')
  })

  it('clears error on reconnected', () => {
    let state = reducer(undefined, errorOccurred('timeout'))
    state     = reducer(state,     statusChanged('connected'))
    expect(state.errorMessage).toBeNull()
  })
})
