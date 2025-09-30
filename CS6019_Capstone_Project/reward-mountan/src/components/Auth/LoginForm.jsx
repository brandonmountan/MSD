// Import React hooks
import { useState } from 'react'

// LoginForm component receives onSuccess function as prop
function LoginForm({ onSuccess }) {
  // State to store form input values
  const [formData, setFormData] = useState({
    email: '',     // User's email
    password: ''   // User's password
  })

  // State to track if form is being submitted
  const [isLoading, setIsLoading] = useState(false)

  // Function to handle input field changes
  const handleChange = (e) => {
    // Get the name and value from the input field that changed
    const { name, value } = e.target
    
    // Update the formData state
    setFormData(prevData => ({
      ...prevData,      // Keep all existing data
      [name]: value     // Update only the field that changed
    }))
  }

  // Function to handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()  // Prevent default form submission behavior
    
    setIsLoading(true)  // Show loading state
    
    try {
      // Simulate API call delay (in real app, this would be actual API call)
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // For demo purposes, accept any email/password combination
      // In real app, this would validate against a database
      if (formData.email && formData.password) {
        // Create mock user data
        const userData = {
          id: 1,
          email: formData.email,
          name: formData.email.split('@')[0], // Use email prefix as name
          role: 'restaurant_owner'  // Default role
        }
        
        // Call the success function passed from parent
        onSuccess(userData)
      } else {
        alert('Please fill in all fields')
      }
    } catch (error) {
      console.error('Login error:', error)
      alert('Login failed. Please try again.')
    } finally {
      setIsLoading(false)  // Hide loading state
    }
  }

  return (
    <form onSubmit={handleSubmit} className="auth-form">
      {/* Email input field */}
      <div className="form-group">
        <label htmlFor="email">Email:</label>
        <input
          type="email"           // HTML5 email validation
          id="email"
          name="email"           // Must match the key in formData
          value={formData.email} // Controlled input
          onChange={handleChange} // Update state when user types
          required              // HTML5 required validation
          placeholder="Enter your email"
        />
      </div>

      {/* Password input field */}
      <div className="form-group">
        <label htmlFor="password">Password:</label>
        <input
          type="password"           // Hide password input
          id="password"
          name="password"           // Must match the key in formData
          value={formData.password} // Controlled input
          onChange={handleChange}   // Update state when user types
          required                 // HTML5 required validation
          placeholder="Enter your password"
        />
      </div>

      {/* Submit button */}
      <button 
        type="submit" 
        className="auth-btn"
        disabled={isLoading}  // Disable button while loading
      >
        {isLoading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  )
}

// Export the component
export default LoginForm