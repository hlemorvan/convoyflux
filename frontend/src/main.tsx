// SPDX-License-Identifier: GPL-3.0-or-later
import React from 'react'
import ReactDOM from 'react-dom/client'
import { Provider } from 'react-redux'
import 'maplibre-gl/dist/maplibre-gl.css'
import './index.css'
import App from './App'
import { store } from './store'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </React.StrictMode>
)
