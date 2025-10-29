// Import the LDAP client we created
// Import Ktor framework for building web servers
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.ktor.http.content.*
// Import serialization for converting objects to/from JSON
import kotlinx.serialization.Serializable
// Import file handling and UUID generation
import java.io.File
import java.util.*

// Data models
// Define a data class to represent username and password from the user
@Serializable
data class UserCredentials(val username: String, val password: String)

// Define a data class for login response with success status, token, and optional message
@Serializable
data class LoginResponse(val success: Boolean, val token: String? = null, val message: String? = null)

// Define a data class to represent information about a photo
@Serializable
data class PhotoInfo(val id: String, val filename: String, val timestamp: Long)

// Not used - we use token-based auth via Authorization header instead
// data class UserSession(val userId: String, val token: String)

// Storage with LDAP integration
// Create an object to manage all our data storage and LDAP operations
object Storage {
    // Map to store authentication tokens - each token maps to a username
    private val tokens = mutableMapOf<String, String>() // token -> username
    // Create a directory to store all user photos
    private val photosDir = File("/app/photos").apply { mkdirs() }

    // LDAP client - gets config from environment variables
    // Create the LDAP client lazily (only when first needed)
    private val ldapClient: LdapClient by lazy {
        // Get LDAP server address from environment, or use "localhost" as default
        val host = System.getenv("LDAP_HOST") ?: "localhost"
        // Get LDAP port from environment, or use 389 as default
        val port = System.getenv("LDAP_PORT")?.toIntOrNull() ?: 389
        // Get base DN from environment, or use default
        val baseDN = System.getenv("LDAP_BASE_DN") ?: "dc=objectdetection,dc=com"
        // Get admin DN from environment, or use default
        val adminDN = System.getenv("LDAP_ADMIN_DN") ?: "cn=admin,dc=objectdetection,dc=com"
        // Get admin password from environment, or use default
        val adminPassword = System.getenv("LDAP_ADMIN_PASSWORD") ?: "admin_password"

        // Print connection info
        println("🔧 Connecting to LDAP at $host:$port")
        // Create and return the LDAP client
        LdapClient(host, port, baseDN, adminDN, adminPassword)
    }

    // Function to register a new user
    fun registerUser(username: String, password: String): Boolean {
        // Try to register the user in LDAP
        val success = ldapClient.registerUser(username, password)
        // If successful, create a folder for their photos
        if (success) {
            File(photosDir, username).mkdirs() // Create user's photo directory
        }
        // Return whether registration was successful
        return success
    }

    // Function to check if username and password are correct
    fun validateUser(username: String, password: String): Boolean {
        // Use LDAP to authenticate the user
        return ldapClient.authenticateUser(username, password)
    }

    // Function to create a new authentication token for a user
    fun createToken(username: String): String {
        // Generate a random unique token
        val token = UUID.randomUUID().toString()
        // Store the token and link it to the username
        tokens[token] = username
        // Return the token
        return token
    }

    // Function to get username from an authentication token
    fun getUserFromToken(token: String): String? {
        // Look up and return the username associated with this token
        return tokens[token]
    }

    // Function to remove a token (for logout)
    fun removeToken(token: String) {
        // Delete the token from our map
        tokens.remove(token)
    }

    // Function to save a photo for a user
    fun savePhoto(username: String, photoId: String, bytes: ByteArray) {
        // Get the user's photo directory
        val userDir = File(photosDir, username)
        // Write the photo bytes to a file with the photoId as the filename
        File(userDir, "$photoId.jpg").writeBytes(bytes)
    }

    // Function to retrieve a photo for a user
    fun getPhoto(username: String, photoId: String): ByteArray? {
        // Build the path to the photo file
        val file = File(File(photosDir, username), "$photoId.jpg")
        // If the file exists, return its bytes, otherwise return null
        return if (file.exists()) file.readBytes() else null
    }

    // Function to get a list of all photos for a user
    fun getUserPhotos(username: String): List<PhotoInfo> {
        // Get the user's photo directory
        val userDir = File(photosDir, username)
        // If the directory doesn't exist, return empty list
        if (!userDir.exists()) return emptyList()

        // List all files in the directory, filter for JPG files, and map to PhotoInfo objects
        return userDir.listFiles()?.filter { it.extension == "jpg" }?.map {
            // Create a PhotoInfo object for each photo
            PhotoInfo(
                // Use filename without extension as ID
                id = it.nameWithoutExtension,
                // Store the full filename
                filename = it.name,
                // Store when the file was last modified
                timestamp = it.lastModified()
            )
        }?.sortedByDescending { it.timestamp } ?: emptyList() // Sort by newest first, or return empty list if null
    }
}

// Main function - entry point of the application
fun main() {
    // Create and start a web server
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Install content negotiation to handle JSON automatically
        install(ContentNegotiation) {
            // Enable JSON serialization
            json()
        }

        // Install CORS to allow requests from web browsers
        install(CORS) {
            // Allow requests from any domain
            anyHost()
            // Allow Content-Type header
            allowHeader(HttpHeaders.ContentType)
            // Allow Authorization header (for tokens)
            allowHeader(HttpHeaders.Authorization)
            // Allow GET requests
            allowMethod(HttpMethod.Get)
            // Allow POST requests
            allowMethod(HttpMethod.Post)
            // Allow DELETE requests
            allowMethod(HttpMethod.Delete)
        }

        // Define all the API routes
        routing {
            // Health check
            // Route for GET request to root path
            get("/") {
                // Return a simple text message
                call.respondText("Object Detection Server Running with LDAP Authentication")
            }

            // Register new user (creates user in LDAP)
            // Route for POST request to register a new user
            post("/register") {
                // Parse the request body as UserCredentials
                val credentials = call.receive<UserCredentials>()

                // Check if username or password is blank
                if (credentials.username.isBlank() || credentials.password.isBlank()) {
                    // Return error response if validation fails
                    call.respond(HttpStatusCode.BadRequest,
                        LoginResponse(false, message = "Username and password required"))
                    // Exit the function early
                    return@post
                }

                // Try to register the user
                val success = Storage.registerUser(credentials.username, credentials.password)
                // If successful, return success response
                if (success) {
                    call.respond(LoginResponse(true, message = "Registration successful"))
                } else {
                    // If failed (user exists), return conflict error
                    call.respond(HttpStatusCode.Conflict,
                        LoginResponse(false, message = "Username already exists"))
                }
            }

            // Login (authenticates against LDAP)
            // Route for POST request to log in
            post("/login") {
                // Parse the request body as UserCredentials
                val credentials = call.receive<UserCredentials>()

                // Check if credentials are valid
                if (Storage.validateUser(credentials.username, credentials.password)) {
                    // If valid, create a new token for the user
                    val token = Storage.createToken(credentials.username)
                    // Return success response with the token
                    call.respond(LoginResponse(true, token = token))
                } else {
                    // If invalid, return unauthorized error
                    call.respond(HttpStatusCode.Unauthorized,
                        LoginResponse(false, message = "Invalid credentials"))
                }
            }

            // Logout
            // Route for POST request to log out
            post("/logout") {
                // Get the token from the Authorization header and remove "Bearer " prefix
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                // If token exists, remove it from storage
                if (token != null) {
                    Storage.removeToken(token)
                }
                // Return success response
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            }

            // Upload photo (requires authentication)
            // Route for POST request to upload a photo
            post("/photos/upload") {
                // Get the token from the Authorization header
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                // Look up the username associated with this token
                val username = token?.let { Storage.getUserFromToken(it) }

                // If no valid token/username, return unauthorized error
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    // Exit early
                    return@post
                }

                try {
                    // Receive the multipart form data (file upload)
                    val multipart = call.receiveMultipart()
                    // Variable to store the photo bytes
                    var photoBytes: ByteArray? = null

                    // Loop through each part of the multipart data
                    multipart.forEachPart { part ->
                        // Check what type of part this is
                        when (part) {
                            // If it's a file
                            is PartData.FileItem -> {
                                // Read the file bytes from the input stream
                                val inputStream = part.streamProvider()
                                // Store the bytes
                                photoBytes = inputStream.readBytes()
                            }
                            // Ignore other part types
                            else -> {}
                        }
                        // Clean up the part
                        part.dispose()
                    }

                    // If we received photo bytes
                    if (photoBytes != null) {
                        // Generate a unique ID for this photo
                        val photoId = UUID.randomUUID().toString()
                        // Save the photo
                        Storage.savePhoto(username, photoId, photoBytes!!)
                        // Return success response with the photo ID
                        call.respond(mapOf("success" to true, "photoId" to photoId))
                    } else {
                        // If no photo data received, return error
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No photo data"))
                    }
                } catch (e: Exception) {
                    // If any error occurs, print it
                    println("Error uploading photo: ${e.message}")
                    e.printStackTrace()
                    // Return error response
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Upload failed: ${e.message}"))
                }
            }

            // Get user's photos list
            // Route for GET request to retrieve list of user's photos
            get("/photos") {
                // Get the token from the Authorization header
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                // Look up the username from the token
                val username = token?.let { Storage.getUserFromToken(it) }

                // If no valid authentication, return error
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    // Exit early
                    return@get
                }

                // Get the list of all photos for this user
                val photos = Storage.getUserPhotos(username)
                // Return the list as JSON
                call.respond(photos)
            }

            // Get specific photo
            // Route for GET request to retrieve a specific photo by ID
            get("/photos/{photoId}") {
                // Get the token from the Authorization header
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                // Look up the username from the token
                val username = token?.let { Storage.getUserFromToken(it) }

                // If no valid authentication, return error
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    // Exit early
                    return@get
                }

                // Get the photo ID from the URL parameter
                val photoId = call.parameters["photoId"]!!
                // Try to get the photo bytes
                val photoBytes = Storage.getPhoto(username, photoId)

                // If photo exists, return it
                if (photoBytes != null) {
                    call.respondBytes(photoBytes, ContentType.Image.JPEG)
                } else {
                    // If photo doesn't exist, return not found error
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Photo not found"))
                }
            }
        }
    }.start(wait = true) // Start the server and wait (keep it running)
}