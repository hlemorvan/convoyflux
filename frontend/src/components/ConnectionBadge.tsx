// SPDX-License-Identifier: GPL-3.0-or-later
import { useSelector } from 'react-redux'
import type { RootState } from '../store'

const STATUS_STYLES: Record<string, string> = {
  connecting:   'bg-yellow-400 text-yellow-900',
  connected:    'bg-green-500  text-white',
  reconnecting: 'bg-orange-400 text-white',
  error:        'bg-red-500    text-white',
}

const STATUS_LABELS: Record<string, string> = {
  connecting:   '⟳ Connexion…',
  connected:    '✓ Connecté',
  reconnecting: '⟳ Reconnexion…',
  error:        '✕ Erreur',
}

export default function ConnectionBadge() {
  const { status, errorMessage } = useSelector((s: RootState) => s.connection)

  return (
    <div className="flex flex-col items-end gap-1">
      <span className={`px-3 py-1 rounded-full text-sm font-medium ${STATUS_STYLES[status] ?? ''}`}>
        {STATUS_LABELS[status] ?? status}
      </span>
      {errorMessage && (
        <span className="text-xs text-red-400 max-w-48 text-right">{errorMessage}</span>
      )}
    </div>
  )
}
