// Import the Header component
import Header from '../components/Layout/Header'

// Dashboard component receives user data and onLogout function as props
function Dashboard({ user, onLogout }) {
  return (
    <div className="dashboard">
      {/* Use the Header component */}
      <Header user={user} onLogout={onLogout} />

      {/* Main dashboard content */}
      <main className="dashboard-main">
        <div className="dashboard-content">
          {/* Welcome section */}
          <section className="welcome-section">
            <h2>Restaurant Management Dashboard</h2>
            <p>Manage your restaurant's menu, promotions, events, and rewards system.</p>
          </section>

          {/* Quick stats/cards section */}
          <section className="dashboard-cards">
            <div className="card">
              <h3>Menu Items</h3>
              <p className="card-number">0</p>
              <p className="card-subtitle">items added</p>
            </div>
            
            <div className="card">
              <h3>Active Promotions</h3>
              <p className="card-number">0</p>
              <p className="card-subtitle">promotions running</p>
            </div>
            
            <div className="card">
              <h3>Upcoming Events</h3>
              <p className="card-number">0</p>
              <p className="card-subtitle">events scheduled</p>
            </div>
            
            <div className="card">
              <h3>Rewards Members</h3>
              <p className="card-number">0</p>
              <p className="card-subtitle">customers enrolled</p>
            </div>
          </section>

          {/* Quick actions section */}
          <section className="quick-actions">
            <h3>Quick Actions</h3>
            <div className="action-buttons">
              <button className="action-btn">Add Menu Item</button>
              <button className="action-btn">Create Promotion</button>
              <button className="action-btn">Schedule Event</button>
              <button className="action-btn">Manage Rewards</button>
            </div>
          </section>

          {/* Restaurant info section */}
          {user?.restaurantName && (
            <section className="restaurant-info">
              <h3>Restaurant Information</h3>
              <div className="info-card">
                <p><strong>Restaurant:</strong> {user.restaurantName}</p>
                <p><strong>Email:</strong> {user.email}</p>
                <p><strong>Role:</strong> {user.role}</p>
              </div>
            </section>
          )}
        </div>
      </main>
    </div>
  )
}

// Export the component
export default Dashboard