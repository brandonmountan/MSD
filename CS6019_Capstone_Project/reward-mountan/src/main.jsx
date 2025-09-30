// Import React library
import React from 'react'
// Import ReactDOM for rendering React components to the DOM
import ReactDOM from 'react-dom/client'
// Import our main App component
import App from './App.jsx'
// Import global CSS styles
import './index.css'

// Create a root element to render our React app
// document.getElementById('root') finds the div with id="root" in index.html
ReactDOM.createRoot(document.getElementById('root')).render(
  // StrictMode helps identify problems during development
  <React.StrictMode>
    {/* Render our main App component */}
    <App />
  </React.StrictMode>
)