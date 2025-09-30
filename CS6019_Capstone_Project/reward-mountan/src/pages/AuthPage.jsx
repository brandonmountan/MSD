// Import React hooks
import { useState } from 'react'
// Import form components (we'll create these)
import LoginForm from '../components/Auth/LoginForm'
import SignupForm from '../components/Auth/SignupForm'
// Import CSS for styling
import '../styles/Auth.css'

// AuthPage component receives onLogin function as a prop from App.jsx
function AuthPage({ onLogin }) {
  // State to track whether to show login or signup form
  // true = show login, false = show signup
  const [isLogin, setIsLogin] = useState(true)

  // Function to switch between login and signup forms
  const toggleAuthMode = () => {
    setIsLogin(!isLogin)  // Flip the boolean value
  }

  // Function to handle successful authentication
  const handleAuthSuccess = (userData) => {
    // Call the onLogin function passed from parent (App.jsx)
    onLogin(userData)
  }

  return (
    <div className="auth-page">
      {/* Header section with branding */}
      <div className="auth-header">
        <h1>Reward Mountan</h1>
        <p>Restaurant Rewards & Management Platform</p>
      </div>

      {/* Main authentication container */}
      <div className="auth-container">
        {/* Card that contains the forms */}
        <div className="auth-card">
          {/* Title that changes based on current mode */}
          <h2>{isLogin ? 'Login' : 'Sign Up'}</h2>
          
          {/* Conditionally render login or signup form */}
          {isLogin ? (
            <LoginForm onSuccess={handleAuthSuccess} />
          ) : (
            <SignupForm onSuccess={handleAuthSuccess} />
          )}

          {/* Button to switch between login and signup */}
          <div className="auth-toggle">
            <p>
              {isLogin ? "Don't have an account? " : "Already have an account? "}
              <button 
                onClick={toggleAuthMode}
                className="toggle-btn"
              >
                {isLogin ? 'Sign Up' : 'Login'}
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

// Export the component
export default AuthPage