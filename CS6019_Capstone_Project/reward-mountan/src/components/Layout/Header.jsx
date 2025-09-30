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
        
        {/* Navigation menu (placeholder for future features) */}
        <nav className="header-nav">
          <ul className="nav-list">
            <li><a href="#dashboard" className="nav-link">Dashboard</a></li>
            <li><a href="#menu" className="nav-link">Menu</a></li>
            <li><a href="#promotions" className="nav-link">Promotions</a></li>
            <li><a href="#events" className="nav-link">Events</a></li>
            <li><a href="#rewards" className="nav-link">Rewards</a></li>
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