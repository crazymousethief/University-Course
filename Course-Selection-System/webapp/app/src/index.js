import React from 'react'
import ReactDOM from 'react-dom'
import { HashRouter } from 'react-router-dom'
import './index.css'
import App from './App'
import registerServiceWorker from './registerServiceWorker'

ReactDOM.render(
  <HashRouter>
    <App />
  </HashRouter>,
  document.getElementById('root'),
)

registerServiceWorker()

if (module.hot) {
  module.hot.accept('./App', () => {
    var NextApp = require('./App').default
    ReactDOM.render(
      <HashRouter>
        <NextApp />
      </HashRouter>,
      document.getElementById('root'),
    )
  })
}
