// Import React hooks
import { useState } from 'react'

// SignupForm component receives onSuccess function as prop
function SignupForm({ onSuccess }) {
  // State to store form input values
  const [formData, setFormData] = useState({
    restaurantName: '',  // Name of the restaurant
    email: '',          // Owner's email
    password: '',       // Password
    confirmPassword: '' // Password confirmation
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

  // Function to validate form data
  const validateForm = () => {
    // Check if all fields are filled
    if (!formData.restaurantName || !formData.email || !formData.password || !formData.confirmPassword) {
      alert('Please fill in all fields')
      return false
    }
    
    // Check if passwords match
    if (formData.password !== formData.confirmPassword) {
      alert('Passwords do not match')
      return false
    }
    
    // Check minimum password length
    if (formData.password.length < 6) {
      alert('Password must be at least 6 characters long')
      return false
    }
    
    return true  // All validations passed
  }

  // Function to handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()  // Prevent default form submission behavior
    
    // Validate form before submitting
    if (!validateForm()) {
      return  // Stop if validation fails
    }
    
    setIsLoading(true)  // Show loading state
    
    try {
      // Simulate API call delay (in real app, this would be actual API call)
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      // Create mock user data for new restaurant owner
      const userData = {
        id: Date.now(),  // Simple ID generation for demo
        email: formData.email,
        name: formData.restaurantName,
        restaurantName: formData.restaurantName,
        role: 'restaurant_owner'
      }
      
      // Call the success function passed from parent
      onSuccess(userData)
    } catch (error) {
      console.error('Signup error:', error)
      alert('Signup failed. Please try again.')
    } finally {
      setIsLoading(false)  // Hide loading state
    }
  }

  return (
    <form onSubmit={handleSubmit} className="auth-form">
      {/* Restaurant name input field */}
      <div className="form-group">
        <label htmlFor="restaurantName">Restaurant Name:</label>
        <input
          type="text"
          id="restaurantName"
          name="restaurantName"           // Must match the key in formData
          value={formData.restaurantName} // Controlled input
          onChange={handleChange}         // Update state when user types
          required                       // HTML5 required validation
          placeholder="Enter your restaurant name"
        />
      </div>

      {/* Email input field */}
      <div className="form-group">
        <label htmlFor="signup-email">Email:</label>
        <input
          type="email"                // HTML5 email validation
          id="signup-email"           // Unique ID to avoid conflicts with login form
          name="email"                // Must match the key in formData
          value={formData.email}      // Controlled input
          onChange={handleChange}     // Update state when user types
          required                   // HTML5 required validation
          placeholder="Enter your email"
        />
      </div>

      {/* Password input field */}
      <div className="form-group">
        <label htmlFor="signup-password">Password:</label>
        <input
          type="password"             // Hide password input
          id="signup-password"        // Unique ID to avoid conflicts
          name="password"             // Must match the key in formData
          value={formData.password}   // Controlled input
          onChange={handleChange}     // Update state when user types
          required                   // HTML5 required validation
          placeholder="Enter your password (min 6 characters)"
        />
      </div>

      {/* Confirm password input field */}
      <div className="form-group">
        <label htmlFor="confirmPassword">Confirm Password:</label>
        <input
          type="password"                   // Hide password input
          id="confirmPassword"
          name="confirmPassword"            // Must match the key in formData
          value={formData.confirmPassword}  // Controlled input
          onChange={handleChange}           // Update state when user types
          required                         // HTML5 required validation
          placeholder="Confirm your password"
        />
      </div>

      {/* Submit button */}
      <button 
        type="submit" 
        className="auth-btn"
        disabled={isLoading}  // Disable button while loading
      >
        {isLoading ? 'Creating Account...' : 'Sign Up'}
      </button>
    </form>
  )
}

// Export the component
export default SignupForm