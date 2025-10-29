// Import the LDAP library classes we need
import com.unboundid.ldap.sdk.*
import java.io.Closeable

/**
 * Simple LDAP client for user authentication and management
 */
// Define a class called LdapClient that can be closed when we're done with it
class LdapClient(
    // Store the LDAP server's address
    private val host: String,
    // Store the LDAP server's port number
    private val port: Int,
    // Store the base DN (Distinguished Name) which is like the root folder in LDAP
    private val baseDN: String,
    // Store the admin username to connect to LDAP
    private val adminDN: String,
    // Store the admin password to connect to LDAP
    private val adminPassword: String
) : Closeable {

    // Variable to hold our connection to the LDAP server
    private var connection: LDAPConnection? = null

    // This runs automatically when we create a new LdapClient object
    init {
        // Try to connect to the LDAP server
        connect()
    }

    // Function to establish connection to the LDAP server
    private fun connect() {
        try {
            // Create a new connection using the host, port, admin username and password
            connection = LDAPConnection(host, port, adminDN, adminPassword)
            // Print success message showing which server we connected to
            println("✅ Connected to LDAP server at $host:$port")
        } catch (e: Exception) {
            // If connection fails, print the error message
            println("❌ Failed to connect to LDAP: ${e.message}")
            // Throw the error so the caller knows something went wrong
            throw e
        }
    }

    /**
     * Register a new user in LDAP
     */
    // Function to add a new user to the LDAP directory
    fun registerUser(username: String, password: String): Boolean {
        try {
            // Build the user's full DN (like their full path in LDAP)
            val userDN = "uid=$username,ou=users,$baseDN"

            // Check if user already exists
            if (userExists(username)) {
                // Print message if user is already registered
                println("User $username already exists")
                // Return false because we can't register them again
                return false
            }

            // Create users OU if it doesn't exist
            createUsersOU()

            // Create the user entry
            // Build a request to add a new user with all their attributes
            val addRequest = AddRequest(
                // The user's full path in LDAP
                userDN,
                // Define what type of object this is (a person)
                Attribute("objectClass", "inetOrgPerson", "organizationalPerson", "person", "top"),
                // Set the user's ID
                Attribute("uid", username),
                // Set the user's common name
                Attribute("cn", username),
                // Set the user's surname (last name)
                Attribute("sn", username),
                // Set the user's password
                Attribute("userPassword", password)
            )

            // Send the add request to the LDAP server
            val result = connection?.add(addRequest)
            // Check if the operation was successful
            val success = result?.resultCode == ResultCode.SUCCESS

            // If successful, print success message
            if (success) {
                println("✅ Registered user: $username")
            } else {
                // If failed, print the error code
                println("❌ Failed to register user: ${result?.resultCode}")
            }

            // Return whether the operation was successful
            return success
        } catch (e: Exception) {
            // If any error occurs, print the error message
            println("❌ Error registering user: ${e.message}")
            // Return false to indicate failure
            return false
        }
    }

    /**
     * Authenticate a user against LDAP
     */
    // Function to check if a username and password are correct
    fun authenticateUser(username: String, password: String): Boolean {
        // Variable to hold the user's connection attempt
        var userConnection: LDAPConnection? = null
        try {
            // Build the user's full DN (their path in LDAP)
            val userDN = "uid=$username,ou=users,$baseDN"

            // Try to bind as the user
            // Try to create a connection using the user's credentials
            userConnection = LDAPConnection(host, port, userDN, password)

            // Check if the connection is active (if yes, credentials are correct)
            val success = userConnection.isConnected
            // If connected successfully, print success message
            if (success) {
                println("✅ Authenticated user: $username")
            }

            // Return whether authentication was successful
            return success
        } catch (e: LDAPException) {
            // If authentication fails, print the error
            println("❌ Authentication failed for $username: ${e.resultCode}")
            // Return false to indicate authentication failed
            return false
        } finally {
            // Always close the user's connection when we're done
            userConnection?.close()
        }
    }

    /**
     * Check if a user exists
     */
    // Helper function to see if a username already exists in LDAP
    private fun userExists(username: String): Boolean {
        try {
            // Create a search request to look for the user
            val searchRequest = SearchRequest(
                // Where to search (in the users organizational unit)
                "ou=users,$baseDN",
                // How deep to search (just one level)
                SearchScope.ONE,
                // What to search for (uid matching the username)
                Filter.createEqualityFilter("uid", username)
            )

            // Execute the search
            val searchResult = connection?.search(searchRequest)
            // If we found any entries, the user exists
            return (searchResult?.entryCount ?: 0) > 0
        } catch (e: Exception) {
            // If search fails, assume user doesn't exist
            return false
        }
    }

    /**
     * Create the users organizational unit if it doesn't exist
     */
    // Helper function to create the "users" folder in LDAP if it's not there
    private fun createUsersOU() {
        try {
            // Build the DN for the users organizational unit
            val ouDN = "ou=users,$baseDN"

            // Check if OU exists
            // Create a search to see if the users OU already exists
            val searchRequest = SearchRequest(
                // Search at the base level
                baseDN,
                // Search one level deep
                SearchScope.ONE,
                // Look for an OU named "users"
                Filter.createEqualityFilter("ou", "users")
            )

            // Execute the search
            val searchResult = connection?.search(searchRequest)
            // If we found it, return early (no need to create it)
            if ((searchResult?.entryCount ?: 0) > 0) {
                return // OU already exists
            }

            // Create the OU
            // Build a request to add the users organizational unit
            val addRequest = AddRequest(
                // The OU's full path
                ouDN,
                // Define it as an organizational unit
                Attribute("objectClass", "organizationalUnit", "top"),
                // Set its name to "users"
                Attribute("ou", "users")
            )

            // Send the request to create the OU
            val result = connection?.add(addRequest)
            // If successful, print success message
            if (result?.resultCode == ResultCode.SUCCESS) {
                println("✅ Created users OU")
            }
        } catch (e: Exception) {
            // If creation fails (maybe it already exists), print info message
            println("ℹ️ Users OU might already exist: ${e.message}")
        }
    }

    // Function called when we're done with the LDAP client
    override fun close() {
        // Close the connection to the LDAP server
        connection?.close()
        // Print that we disconnected
        println("ℹ️ Disconnected from LDAP")
    }
}