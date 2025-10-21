// Import React hooks
import { useState } from 'react'
// Import Header component
import Header from '../components/Layout/Header'

// MenuPage component receives user data and onLogout function as props
function MenuPage({ user, onLogout }) {
  // State to store all menu items
  const [menuItems, setMenuItems] = useState([])
  
  // State to store form input values
  const [formData, setFormData] = useState({
    itemName: '',       // Name of the dish
    description: '',    // Description of the dish
    price: '',          // Price of the item
    category: 'appetizer'  // Category (appetizer, entree, etc.)
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

  // Function to handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()  // Prevent default form submission
    setIsLoading(true)  // Show loading state

    try {
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 500))

      // Create new menu item object with unique ID
      const newMenuItem = {
        id: Date.now(),  // Simple ID generation
        ...formData,
        price: parseFloat(formData.price)  // Convert price to number
      }

      // Add new item to the list
      setMenuItems(prevItems => [...prevItems, newMenuItem])

      // Reset form fields
      setFormData({
        itemName: '',
        description: '',
        price: '',
        category: 'appetizer'
      })

      // Show success message
      alert('Menu item added successfully!')
    } catch (error) {
      console.error('Error adding menu item:', error)
      alert('Failed to add menu item. Please try again.')
    } finally {
      setIsLoading(false)  // Hide loading state
    }
  }

  // Function to delete a menu item
  const handleDelete = (id) => {
    // Confirm before deleting
    if (window.confirm('Are you sure you want to delete this item?')) {
      // Filter out the item with matching ID
      setMenuItems(prevItems => prevItems.filter(item => item.id !== id))
    }
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
            <h2>Menu Management</h2>
            <p>Add and manage your restaurant menu items</p>
          </div>

          {/* Two column layout: form on left, list on right */}
          <div className="page-grid">
            {/* Left column - Add menu item form */}
            <div className="form-section">
              <h3>Add New Menu Item</h3>
              <form onSubmit={handleSubmit} className="page-form">
                {/* Item name input */}
                <div className="form-group">
                  <label htmlFor="itemName">Item Name:</label>
                  <input
                    type="text"
                    id="itemName"
                    name="itemName"
                    value={formData.itemName}
                    onChange={handleChange}
                    required
                    placeholder="e.g., Grilled Salmon"
                  />
                </div>

                {/* Description input */}
                <div className="form-group">
                  <label htmlFor="description">Description:</label>
                  <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    required
                    placeholder="Describe your dish..."
                    rows="4"
                  />
                </div>

                {/* Price input */}
                <div className="form-group">
                  <label htmlFor="price">Price ($):</label>
                  <input
                    type="number"
                    id="price"
                    name="price"
                    value={formData.price}
                    onChange={handleChange}
                    required
                    step="0.01"
                    min="0"
                    placeholder="0.00"
                  />
                </div>

                {/* Category dropdown */}
                <div className="form-group">
                  <label htmlFor="category">Category:</label>
                  <select
                    id="category"
                    name="category"
                    value={formData.category}
                    onChange={handleChange}
                    required
                  >
                    <option value="appetizer">Appetizer</option>
                    <option value="entree">Entree</option>
                    <option value="dessert">Dessert</option>
                    <option value="beverage">Beverage</option>
                    <option value="side">Side Dish</option>
                  </select>
                </div>

                {/* Submit button */}
                <button type="submit" className="submit-btn" disabled={isLoading}>
                  {isLoading ? 'Adding...' : 'Add Menu Item'}
                </button>
              </form>
            </div>

            {/* Right column - Menu items list */}
            <div className="list-section">
              <h3>Current Menu Items ({menuItems.length})</h3>
              
              {menuItems.length === 0 ? (
                <p className="empty-message">No menu items yet. Add your first item!</p>
              ) : (
                <div className="items-list">
                  {menuItems.map(item => (
                    <div key={item.id} className="item-card">
                      <div className="item-header">
                        <h4>{item.itemName}</h4>
                        <span className="item-badge">{item.category}</span>
                      </div>
                      <p className="item-description">{item.description}</p>
                      <div className="item-footer">
                        <span className="item-price">${item.price.toFixed(2)}</span>
                        <button 
                          onClick={() => handleDelete(item.id)}
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
export default MenuPage