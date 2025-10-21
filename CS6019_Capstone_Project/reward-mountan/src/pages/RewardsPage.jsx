// Import React hooks
import { useState } from 'react'
// Import Header component
import Header from '../components/Layout/Header'

// RewardsPage component receives user data and onLogout function as props
function RewardsPage({ user, onLogout }) {
  // State to store reward tiers/levels
  const [rewardTiers, setRewardTiers] = useState([])
  
  // State to store form input values
  const [formData, setFormData] = useState({
    tierName: '',          // Name of the reward tier (e.g., Bronze, Silver)
    pointsRequired: '',    // Points needed to reach this tier
    rewardDescription: '', // What customer gets at this tier
    discountPercentage: '' // Discount percentage for this tier
  })

  // State for reward system settings
  const [rewardSettings, setRewardSettings] = useState({
    pointsPerDollar: 1,    // How many points earned per dollar spent
    isActive: false        // Whether reward system is active
  })

  // State to track if form is being submitted
  const [isLoading, setIsLoading] = useState(false)

  // Function to handle input field changes for tier form
  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prevData => ({
      ...prevData,
      [name]: value
    }))
  }

  // Function to handle reward settings changes
  const handleSettingsChange = (e) => {
    const { name, value, type, checked } = e.target
    setRewardSettings(prevSettings => ({
      ...prevSettings,
      [name]: type === 'checkbox' ? checked : value
    }))
  }

  // Function to handle tier form submission
  const handleSubmit = async (e) => {
    e.preventDefault()  // Prevent default form submission
    setIsLoading(true)  // Show loading state

    try {
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 500))

      // Create new reward tier object with unique ID
      const newTier = {
        id: Date.now(),  // Simple ID generation
        ...formData,
        pointsRequired: parseInt(formData.pointsRequired),     // Convert to number
        discountPercentage: parseInt(formData.discountPercentage)  // Convert to number
      }

      // Add new tier to the list
      setRewardTiers(prevTiers => [...prevTiers, newTier])

      // Reset form fields
      setFormData({
        tierName: '',
        pointsRequired: '',
        rewardDescription: '',
        discountPercentage: ''
      })

      // Show success message
      alert('Reward tier added successfully!')
    } catch (error) {
      console.error('Error adding reward tier:', error)
      alert('Failed to add reward tier. Please try again.')
    } finally {
      setIsLoading(false)  // Hide loading state
    }
  }

  // Function to delete a reward tier
  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this reward tier?')) {
      setRewardTiers(prevTiers => prevTiers.filter(tier => tier.id !== id))
    }
  }

  // Function to toggle reward system active status
  const handleToggleSystem = () => {
    setRewardSettings(prev => ({
      ...prev,
      isActive: !prev.isActive
    }))
    alert(`Reward system ${!rewardSettings.isActive ? 'activated' : 'deactivated'}!`)
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
            <h2>Rewards System Setup</h2>
            <p>Create and manage your customer rewards program</p>
          </div>

          {/* Reward system settings card */}
          <div className="settings-card">
            <h3>Reward System Settings</h3>
            <div className="settings-content">
              <div className="setting-item">
                <label htmlFor="pointsPerDollar">Points Earned Per Dollar Spent:</label>
                <input
                  type="number"
                  id="pointsPerDollar"
                  name="pointsPerDollar"
                  value={rewardSettings.pointsPerDollar}
                  onChange={handleSettingsChange}
                  min="1"
                  max="100"
                  className="settings-input"
                />
              </div>
              <div className="setting-item">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="isActive"
                    checked={rewardSettings.isActive}
                    onChange={handleSettingsChange}
                  />
                  <span>Reward System Active</span>
                </label>
              </div>
              <button onClick={handleToggleSystem} className="toggle-btn-large">
                {rewardSettings.isActive ? '🟢 System Active' : '🔴 System Inactive'}
              </button>
            </div>
          </div>

          {/* Two column layout */}
          <div className="page-grid">
            {/* Left column - Add reward tier form */}
            <div className="form-section">
              <h3>Add Reward Tier</h3>
              <form onSubmit={handleSubmit} className="page-form">
                {/* Tier name */}
                <div className="form-group">
                  <label htmlFor="tierName">Tier Name:</label>
                  <input
                    type="text"
                    id="tierName"
                    name="tierName"
                    value={formData.tierName}
                    onChange={handleChange}
                    required
                    placeholder="e.g., Bronze, Silver, Gold"
                  />
                </div>

                {/* Points required */}
                <div className="form-group">
                  <label htmlFor="pointsRequired">Points Required:</label>
                  <input
                    type="number"
                    id="pointsRequired"
                    name="pointsRequired"
                    value={formData.pointsRequired}
                    onChange={handleChange}
                    required
                    min="0"
                    placeholder="Points needed to reach this tier"
                  />
                </div>

                {/* Discount percentage */}
                <div className="form-group">
                  <label htmlFor="discountPercentage">Discount Percentage (%):</label>
                  <input
                    type="number"
                    id="discountPercentage"
                    name="discountPercentage"
                    value={formData.discountPercentage}
                    onChange={handleChange}
                    required
                    min="0"
                    max="100"
                    placeholder="Discount for this tier"
                  />
                </div>

                {/* Reward description */}
                <div className="form-group">
                  <label htmlFor="rewardDescription">Reward Description:</label>
                  <textarea
                    id="rewardDescription"
                    name="rewardDescription"
                    value={formData.rewardDescription}
                    onChange={handleChange}
                    required
                    placeholder="Describe the benefits of this tier..."
                    rows="3"
                  />
                </div>

                {/* Submit button */}
                <button type="submit" className="submit-btn" disabled={isLoading}>
                  {isLoading ? 'Adding...' : 'Add Reward Tier'}
                </button>
              </form>
            </div>

            {/* Right column - Reward tiers list */}
            <div className="list-section">
              <h3>Reward Tiers ({rewardTiers.length})</h3>
              
              {rewardTiers.length === 0 ? (
                <p className="empty-message">No reward tiers yet. Create your first tier!</p>
              ) : (
                <div className="items-list">
                  {/* Sort tiers by points required */}
                  {rewardTiers
                    .sort((a, b) => a.pointsRequired - b.pointsRequired)
                    .map(tier => (
                    <div key={tier.id} className="item-card reward-card">
                      <div className="item-header">
                        <h4>{tier.tierName}</h4>
                        <span className="item-badge discount-badge">{tier.discountPercentage}% OFF</span>
                      </div>
                      <div className="reward-info">
                        <p className="points-required">
                          <strong>{tier.pointsRequired} points</strong> required
                        </p>
                        <p className="item-description">{tier.rewardDescription}</p>
                      </div>
                      <div className="item-footer">
                        <button 
                          onClick={() => handleDelete(tier.id)}
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

          {/* Info box about how rewards work */}
          <div className="info-box">
            <h4>📊 How the Reward System Works:</h4>
            <ul>
              <li>Customers earn <strong>{rewardSettings.pointsPerDollar} point(s)</strong> for every dollar spent</li>
              <li>As customers accumulate points, they unlock higher reward tiers</li>
              <li>Each tier provides increasing discount percentages and benefits</li>
              <li>Points and tiers encourage repeat visits and customer loyalty</li>
            </ul>
          </div>
        </div>
      </main>
    </div>
  )
}

// Export the component
export default RewardsPage