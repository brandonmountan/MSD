// Import React and necessary hooks
import { useState } from 'react'
// Import React Router components for navigation
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
// Import our custom components (we'll create these)
import AuthPage from './pages/AuthPage'
import Dashboard from './pages/Dashboard'
import MenuPage from './pages/MenuPage'
import PromotionsPage from './pages/PromotionsPage'
import EventsPage from './pages/EventsPage'
import RewardsPage from './pages/RewardsPage'
// Import CSS for styling
import './App.css'

function App() {
  // State to track if user is logged in (starts as false)
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  // State to store current user information
  const [currentUser, setCurrentUser] = useState(null)

  // Function to handle successful login
  const handleLogin = (userData) => {
    setIsLoggedIn(true)  // Set login status to true
    setCurrentUser(userData)  // Store user data
    console.log('User logged in:', userData)
  }

  // Function to handle logout
  const handleLogout = () => {
    setIsLoggedIn(false)  // Set login status to false
    setCurrentUser(null)  // Clear user data
    console.log('User logged out')
  }

  return (
    // Router wraps the entire app to enable routing
    <Router>
      <div className="App">
        {/* Routes define different pages/views */}
        <Routes>
          {/* If user is not logged in, show auth page */}
          <Route 
            path="/auth" 
            element={
              !isLoggedIn ? (
                <AuthPage onLogin={handleLogin} />
              ) : (
                <Navigate to="/dashboard" replace />
              )
            } 
          />
          
          {/* If user is logged in, show dashboard */}
          <Route 
            path="/dashboard" 
            element={
              isLoggedIn ? (
                <Dashboard user={currentUser} onLogout={handleLogout} />
              ) : (
                <Navigate to="/auth" replace />
              )
            } 
          />
          
          {/* Menu page route */}
          <Route 
            path="/menu" 
            element={
              isLoggedIn ? (
                <MenuPage user={currentUser} onLogout={handleLogout} />
              ) : (
                <Navigate to="/auth" replace />
              )
            } 
          />
          
          {/* Promotions page route */}
          <Route 
            path="/promotions" 
            element={
              isLoggedIn ? (
                <PromotionsPage user={currentUser} onLogout={handleLogout} />
              ) : (
                <Navigate to="/auth" replace />
              )
            } 
          />
          
          {/* Events page route */}
          <Route 
            path="/events" 
            element={
              isLoggedIn ? (
                <EventsPage user={currentUser} onLogout={handleLogout} />
              ) : (
                <Navigate to="/auth" replace />
              )
            } 
          />
          
          {/* Rewards page route */}
          <Route 
            path="/rewards" 
            element={
              isLoggedIn ? (
                <RewardsPage user={currentUser} onLogout={handleLogout} />
              ) : (
                <Navigate to="/auth" replace />
              )
            } 
          />
          
          {/* Default route - redirect based on login status */}
          <Route 
            path="/" 
            element={
              isLoggedIn ? (
                <Navigate to="/dashboard" replace />
              ) : (
                <Navigate to="/auth" replace />
              )
            } 
          />
        </Routes>
      </div>
    </Router>
  )
}

// Export the App component so it can be imported elsewhere
export default App