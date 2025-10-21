// Import React hooks
import { useState } from 'react'
// Import Header component
import Header from '../components/Layout/Header'

// EventsPage component receives user data and onLogout function as props
function EventsPage({ user, onLogout }) {
  // State to store all events
  const [events, setEvents] = useState([])
  
  // State to store form input values
  const [formData, setFormData] = useState({
    eventName: '',      // Name of the event
    description: '',    // Event details
    date: '',           // Date of the event
    time: '',           // Time of the event
    capacity: '',       // Maximum number of attendees
    eventType: 'live_music'  // Type of event
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

  // Function to validate event date (must be in future)
  const validateDate = () => {
    const eventDate = new Date(formData.date + 'T' + formData.time)
    const now = new Date()
    
    if (eventDate <= now) {
      alert('Event date and time must be in the future')
      return false
    }
    return true
  }

  // Function to handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()  // Prevent default form submission
    
    // Validate date before submitting
    if (!validateDate()) {
      return
    }
    
    setIsLoading(true)  // Show loading state

    try {
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 500))

      // Create new event object with unique ID
      const newEvent = {
        id: Date.now(),  // Simple ID generation
        ...formData,
        capacity: parseInt(formData.capacity),  // Convert to number
        registrations: 0,  // Start with 0 registrations
        status: 'upcoming'  // Default status
      }

      // Add new event to the list
      setEvents(prevEvents => [...prevEvents, newEvent])

      // Reset form fields
      setFormData({
        eventName: '',
        description: '',
        date: '',
        time: '',
        capacity: '',
        eventType: 'live_music'
      })

      // Show success message
      alert('Event created successfully!')
    } catch (error) {
      console.error('Error creating event:', error)
      alert('Failed to create event. Please try again.')
    } finally {
      setIsLoading(false)  // Hide loading state
    }
  }

  // Function to delete an event
  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this event?')) {
      setEvents(prevEvents => prevEvents.filter(event => event.id !== id))
    }
  }

  // Function to format date and time for display
  const formatDateTime = (date, time) => {
    const eventDate = new Date(date + 'T' + time)
    return eventDate.toLocaleString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
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
            <h2>Events Management</h2>
            <p>Schedule and manage restaurant events</p>
          </div>

          {/* Two column layout */}
          <div className="page-grid">
            {/* Left column - Create event form */}
            <div className="form-section">
              <h3>Schedule New Event</h3>
              <form onSubmit={handleSubmit} className="page-form">
                {/* Event name */}
                <div className="form-group">
                  <label htmlFor="eventName">Event Name:</label>
                  <input
                    type="text"
                    id="eventName"
                    name="eventName"
                    value={formData.eventName}
                    onChange={handleChange}
                    required
                    placeholder="e.g., Wine Tasting Night"
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
                    placeholder="Describe the event..."
                    rows="3"
                  />
                </div>

                {/* Event type dropdown */}
                <div className="form-group">
                  <label htmlFor="eventType">Event Type:</label>
                  <select
                    id="eventType"
                    name="eventType"
                    value={formData.eventType}
                    onChange={handleChange}
                    required
                  >
                    <option value="live_music">Live Music</option>
                    <option value="wine_tasting">Wine Tasting</option>
                    <option value="cooking_class">Cooking Class</option>
                    <option value="private_dining">Private Dining</option>
                    <option value="special_menu">Special Menu Night</option>
                    <option value="holiday">Holiday Celebration</option>
                    <option value="other">Other</option>
                  </select>
                </div>

                {/* Date */}
                <div className="form-group">
                  <label htmlFor="date">Event Date:</label>
                  <input
                    type="date"
                    id="date"
                    name="date"
                    value={formData.date}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* Time */}
                <div className="form-group">
                  <label htmlFor="time">Event Time:</label>
                  <input
                    type="time"
                    id="time"
                    name="time"
                    value={formData.time}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* Capacity */}
                <div className="form-group">
                  <label htmlFor="capacity">Max Capacity:</label>
                  <input
                    type="number"
                    id="capacity"
                    name="capacity"
                    value={formData.capacity}
                    onChange={handleChange}
                    required
                    min="1"
                    placeholder="Maximum number of attendees"
                  />
                </div>

                {/* Submit button */}
                <button type="submit" className="submit-btn" disabled={isLoading}>
                  {isLoading ? 'Scheduling...' : 'Schedule Event'}
                </button>
              </form>
            </div>

            {/* Right column - Events list */}
            <div className="list-section">
              <h3>Upcoming Events ({events.length})</h3>
              
              {events.length === 0 ? (
                <p className="empty-message">No events scheduled. Create your first event!</p>
              ) : (
                <div className="items-list">
                  {events.map(event => (
                    <div key={event.id} className="item-card">
                      <div className="item-header">
                        <h4>{event.eventName}</h4>
                        <span className="item-badge status-upcoming">{event.status}</span>
                      </div>
                      <p className="item-description">{event.description}</p>
                      <div className="event-details">
                        <p><strong>Type:</strong> {event.eventType.replace('_', ' ')}</p>
                        <p><strong>When:</strong> {formatDateTime(event.date, event.time)}</p>
                        <p><strong>Capacity:</strong> {event.registrations}/{event.capacity} registered</p>
                      </div>
                      <div className="item-footer">
                        <button 
                          onClick={() => handleDelete(event.id)}
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
export default EventsPage