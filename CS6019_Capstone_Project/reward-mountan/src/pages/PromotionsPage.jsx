// Import React hooks
import { useState } from 'react'
// Import Header component
import Header from '../components/Layout/Header'

// PromotionsPage component receives user data and onLogout function as props
function PromotionsPage({ user, onLogout }) {
  // State to store all promotions
  const [promotions, setPromotions] = useState([])
  
  // State to store form input values
  const [formData, setFormData] = useState({
    title: '',          // Promotion title
    description: '',    // Promotion details
    discountType: 'percentage',  // percentage or fixed amount
    discountValue: '',  // Value of discount
    startDate: '',      // When promotion starts
    endDate: ''         // When promotion ends
  })

  // State to track if form is being submitted
  const [isLoading, setIsLoading] = useState(false)

  // Function to handle input field changes
  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prevData => ({
      ...prevData,
      [name]: value
    }))
  }

  // Function to validate dates
  const validateDates = () => {
    const start = new Date(formData.startDate)
    const end = new Date(formData.endDate)
    
    // Check if end date is after start date
    if (end <= start) {
      alert('End date must be after start date')
      return false
    }
    return true
  }

  // Function to handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()  // Prevent default form submission
    
    // Validate dates before submitting
    if (!validateDates()) {
      return
    }
    
    setIsLoading(true)  // Show loading state

    try {
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 500))

      // Create new promotion object with unique ID
      const newPromotion = {
        id: Date.now(),  // Simple ID generation
        ...formData,
        discountValue: parseFloat(formData.discountValue),  // Convert to number
        status: 'active'  // Default status
      }

      // Add new promotion to the list
      setPromotions(prevPromotions => [...prevPromotions, newPromotion])

      // Reset form fields
      setFormData({
        title: '',
        description: '',
        discountType: 'percentage',
        discountValue: '',
        startDate: '',
        endDate: ''
      })

      // Show success message
      alert('Promotion created successfully!')
    } catch (error) {
      console.error('Error creating promotion:', error)
      alert('Failed to create promotion. Please try again.')
    } finally {
      setIsLoading(false)  // Hide loading state
    }
  }

  // Function to delete a promotion
  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this promotion?')) {
      setPromotions(prevPromotions => prevPromotions.filter(promo => promo.id !== id))
    }
  }

  // Function to format date for display
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    })
  }

  return (
    <div className="page-container">
      {/* Header component */}
      <Header user={user} onLogout={onLogout} />

      {/* Main content */}
      <main className="page-main">
        <div className="page-content">
          {/* Page title */}
          <div className="page-header">
            <h2>Promotions Management</h2>
            <p>Create and manage promotional offers for your customers</p>
          </div>

          {/* Two column layout */}
          <div className="page-grid">
            {/* Left column - Create promotion form */}
            <div className="form-section">
              <h3>Create New Promotion</h3>
              <form onSubmit={handleSubmit} className="page-form">
                {/* Promotion title */}
                <div className="form-group">
                  <label htmlFor="title">Promotion Title:</label>
                  <input
                    type="text"
                    id="title"
                    name="title"
                    value={formData.title}
                    onChange={handleChange}
                    required
                    placeholder="e.g., Happy Hour Special"
                  />
                </div>

                {/* Description */}
                <div className="form-group">
                  <label htmlFor="description">Description:</label>
                  <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    required
                    placeholder="Describe the promotion..."
                    rows="3"
                  />
                </div>

                {/* Discount type dropdown */}
                <div className="form-group">
                  <label htmlFor="discountType">Discount Type:</label>
                  <select
                    id="discountType"
                    name="discountType"
                    value={formData.discountType}
                    onChange={handleChange}
                    required
                  >
                    <option value="percentage">Percentage Off (%)</option>
                    <option value="fixed">Fixed Amount Off ($)</option>
                  </select>
                </div>

                {/* Discount value */}
                <div className="form-group">
                  <label htmlFor="discountValue">
                    Discount Value {formData.discountType === 'percentage' ? '(%)' : '($)'}:
                  </label>
                  <input
                    type="number"
                    id="discountValue"
                    name="discountValue"
                    value={formData.discountValue}
                    onChange={handleChange}
                    required
                    step="0.01"
                    min="0"
                    max={formData.discountType === 'percentage' ? '100' : undefined}
                    placeholder={formData.discountType === 'percentage' ? '10' : '5.00'}
                  />
                </div>

                {/* Start date */}
                <div className="form-group">
                  <label htmlFor="startDate">Start Date:</label>
                  <input
                    type="date"
                    id="startDate"
                    name="startDate"
                    value={formData.startDate}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* End date */}
                <div className="form-group">
                  <label htmlFor="endDate">End Date:</label>
                  <input
                    type="date"
                    id="endDate"
                    name="endDate"
                    value={formData.endDate}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* Submit button */}
                <button type="submit" className="submit-btn" disabled={isLoading}>
                  {isLoading ? 'Creating...' : 'Create Promotion'}
                </button>
              </form>
            </div>

            {/* Right column - Promotions list */}
            <div className="list-section">
              <h3>Active Promotions ({promotions.length})</h3>
              
              {promotions.length === 0 ? (
                <p className="empty-message">No promotions yet. Create your first promotion!</p>
              ) : (
                <div className="items-list">
                  {promotions.map(promo => (
                    <div key={promo.id} className="item-card">
                      <div className="item-header">
                        <h4>{promo.title}</h4>
                        <span className="item-badge status-active">{promo.status}</span>
                      </div>
                      <p className="item-description">{promo.description}</p>
                      <div className="promo-details">
                        <p><strong>Discount:</strong> {promo.discountValue}{promo.discountType === 'percentage' ? '%' : '$'} off</p>
                        <p><strong>Duration:</strong> {formatDate(promo.startDate)} - {formatDate(promo.endDate)}</p>
                      </div>
                      <div className="item-footer">
                        <button 
                          onClick={() => handleDelete(promo.id)}
                          className="delete-btn"
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}

// Export the component
export default PromotionsPage