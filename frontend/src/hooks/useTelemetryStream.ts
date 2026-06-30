// SPDX-License-Identifier: GPL-3.0-or-later
import { useEffect, useRef } from 'react'
import { useDispatch } from 'react-redux'
import { telemetryReceived } from '../store/vehiclesSlice'
import { statusChanged, errorOccurred } from '../store/connectionSlice'
import type { AppDispatch } from '../store'

const SSE_URL        = '/api/stream'
const MAX_BACKOFF_MS = 30_000
const BASE_DELAY_MS  = 1_000

export function useTelemetryStream(): void {
  const dispatch    = useDispatch<AppDispatch>()
  const retryCount  = useRef(0)
  const timerRef    = useRef<ReturnType<typeof setTimeout> | null>(null)
  const esRef       = useRef<EventSource | null>(null)

  useEffect(() => {
    function connect() {
      dispatch(statusChanged(retryCount.current === 0 ? 'connecting' : 'reconnecting'))

      const es = new EventSource(SSE_URL)
      esRef.current = es

      es.addEventListener('telemetry', (e: MessageEvent) => {
        try {
          const data = JSON.parse(e.data)
          dispatch(telemetryReceived(data))
          if (retryCount.current > 0) {
            retryCount.current = 0
            dispatch(statusChanged('connected'))
          } else {
            dispatch(statusChanged('connected'))
          }
        } catch {
          // ignore malformed frames
        }
      })

      es.onerror = () => {
        es.close()
        esRef.current = null
        retryCount.current++
        const delay = Math.min(BASE_DELAY_MS * 2 ** retryCount.current, MAX_BACKOFF_MS)
        dispatch(errorOccurred(`Connexion perdue — reconnexion dans ${Math.round(delay / 1000)}s`))
        timerRef.current = setTimeout(connect, delay)
      }
    }

    connect()

    return () => {
      esRef.current?.close()
      if (timerRef.current) clearTimeout(timerRef.current)
    }
  }, [dispatch])
}
