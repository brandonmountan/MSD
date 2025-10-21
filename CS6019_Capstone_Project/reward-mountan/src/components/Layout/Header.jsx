// Import Link from React Router for navigation
import { Link } from 'react-router-dom'

// Header component receives user data and onLogout function as props
function Header({ user, onLogout, showUserSection = true }) {
  // Function to handle logout button click
  const handleLogout = () => {
    // Confirm user wants to logout
    if (window.confirm('Are you sure you want to logout?')) {
      onLogout()  // Call the logout function passed from parent
    }
  }

  return (
    <header className="dashboard-header">
      <div className="header-content">
        {/* Brand/Logo section */}
        <div className="brand-section">
          <h1>Reward Mountan</h1>
        </div>
        
        {/* Navigation menu */}
        <nav className="header-nav">
          <ul className="nav-list">
            <li><Link to="/dashboard" className="nav-link">Dashboard</Link></li>
            <li><Link to="/menu" className="nav-link">Menu</Link></li>
            <li><Link to="/promotions" className="nav-link">Promotions</Link></li>
            <li><Link to="/events" className="nav-link">Events</Link></li>
            <li><Link to="/rewards" className="nav-link">Rewards</Link></li>
          </ul>
        </nav>
        
        {/* User section - only show if showUserSection is true */}
        {showUserSection && user && (
          <div className="user-section">
            <span className="user-greeting">
              Welcome, {user?.name || user?.email}!
            </span>
            <button onClick={handleLogout} className="logout-btn">
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  )
}

// Export the component
export default Header