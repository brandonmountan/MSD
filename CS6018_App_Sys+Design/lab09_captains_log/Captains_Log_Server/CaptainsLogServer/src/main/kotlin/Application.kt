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
import kotlinx.serialization.*
import java.io.File
import java.util.*

// Data models for authentication
@Serializable
data class UserCredentials(val username: String, val password: String)

@Serializable
data class LoginResponse(val success: Boolean, val token: String? = null, val message: String? = null)

// Data models for Captain's Log entries
@Serializable
data class LogEntry(
    val id: String,                    // Unique identifier for the log entry
    val title: String,                 // Title of the log entry
    val audioFilename: String,         // Name of the audio file
    val transcription: String,         // Text transcription of the audio
    val timestamp: Long,               // When the log was created
    val stardate: String,              // Star Trek style stardate
    val isShared: Boolean = false      // Whether this log is shared with friends
)

@Serializable
data class UploadLogResponse(
    val success: Boolean,
    val logId: String? = null,
    val message: String? = null
)

@Serializable
data class SearchResult(
    val logs: List<LogEntry>           // List of matching log entries
)

@Serializable
data class AddFriendRequest(
    val friendUsername: String         // Username of the friend to add
)

@Serializable
data class ShareLogRequest(
    val logId: String,                 // ID of the log to share
    val friendUsername: String         // Username of friend to share with
)

// Storage with LDAP integration - manages all data in memory for prototype
object Storage {
    // Authentication tokens: token -> username
    private val tokens = mutableMapOf<String, String>()

    // Directory where audio files are stored
    private val logsDir = File("/CaptainsLogServer/logs").apply { mkdirs() }

    // In-memory storage for log entries: username -> list of log entries
    private val userLogs = mutableMapOf<String, MutableList<LogEntry>>()

    // Friend relationships: username -> list of friend usernames
    private val friendships = mutableMapOf<String, MutableSet<String>>()

    // Shared logs: username -> list of log IDs shared with them
    private val sharedLogs = mutableMapOf<String, MutableSet<String>>()

    // LDAP client - gets configuration from environment variables
    private val ldapClient: LdapClient by lazy {
        val host = System.getenv("LDAP_HOST") ?: "localhost"
        val port = System.getenv("LDAP_PORT")?.toIntOrNull() ?: 389
        val baseDN = System.getenv("LDAP_BASE_DN") ?: "dc=captainslog,dc=com"
        val adminDN = System.getenv("LDAP_ADMIN_DN") ?: "cn=admin,dc=captainslog,dc=com"
        val adminPassword = System.getenv("LDAP_ADMIN_PASSWORD") ?: "admin_password"

        println("🔧 Connecting to LDAP at $host:$port")
        LdapClient(host, port, baseDN, adminDN, adminPassword)
    }

    // Initialize with sample data for testing
    init {
        println("🎬 Initializing sample data...")
        initializeSampleData()
    }

    // Create sample logs and friends for testing
    private fun initializeSampleData() {
        // Sample usernames with passwords
        val sampleUsers = mapOf(
            "test" to "password123",
            "captain" to "password123",
            "starfleet" to "password123"
        )

        // Register sample users in LDAP and initialize data structures
        sampleUsers.forEach { (username, password) ->
            // Try to register in LDAP (will fail silently if already exists)
            try {
                ldapClient.registerUser(username, password)
                println("✅ Registered sample user: $username")
            } catch (e: Exception) {
                println("ℹ️ Sample user $username might already exist")
            }

            // Create user's log directory
            File(logsDir, username).mkdirs()

            // Initialize data structures
            if (userLogs[username] == null) {
                userLogs[username] = mutableListOf()
            }
            if (friendships[username] == null) {
                friendships[username] = mutableSetOf()
            }
            if (sharedLogs[username] == null) {
                sharedLogs[username] = mutableSetOf()
            }
        }

        // Add sample friends for "test" user
        friendships["test"]?.addAll(listOf("captain", "starfleet", "picard", "kirk", "janeway"))
        friendships["captain"]?.add("test")
        friendships["starfleet"]?.add("test")

        // Sample log entries with lorem ipsum transcriptions
        val baseTime = System.currentTimeMillis()

        userLogs["test"]?.addAll(listOf(
            LogEntry(
                id = "log-001",
                title = "First Contact Mission Brief",
                audioFilename = "log-001.m4a",
                transcription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                timestamp = baseTime - 86400000 * 5, // 5 days ago
                stardate = "2024.328.1420",
                isShared = false
            ),
            LogEntry(
                id = "log-002",
                title = "Nebula Exploration Report",
                audioFilename = "log-002.m4a",
                transcription = "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                timestamp = baseTime - 86400000 * 4, // 4 days ago
                stardate = "2024.329.0830",
                isShared = false
            ),
            LogEntry(
                id = "log-003",
                title = "Away Team Status Update",
                audioFilename = "log-003.m4a",
                transcription = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.",
                timestamp = baseTime - 86400000 * 3, // 3 days ago
                stardate = "2024.330.1145",
                isShared = true
            ),
            LogEntry(
                id = "log-004",
                title = "Warp Drive Calibration Notes",
                audioFilename = "log-004.m4a",
                transcription = "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet.",
                timestamp = baseTime - 86400000 * 2, // 2 days ago
                stardate = "2024.331.1630",
                isShared = false
            ),
            LogEntry(
                id = "log-005",
                title = "Diplomatic Negotiations Summary",
                audioFilename = "log-005.m4a",
                transcription = "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident.",
                timestamp = baseTime - 86400000, // 1 day ago
                stardate = "2024.332.0915",
                isShared = false
            ),
            LogEntry(
                id = "log-006",
                title = "Anomaly Detection Log",
                audioFilename = "log-006.m4a",
                transcription = "Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus.",
                timestamp = baseTime - 43200000, // 12 hours ago
                stardate = "2024.332.2030",
                isShared = false
            ),
            LogEntry(
                id = "log-007",
                title = "Medical Bay Status Report",
                audioFilename = "log-007.m4a",
                transcription = "Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus.",
                timestamp = baseTime - 7200000, // 2 hours ago
                stardate = "2024.333.0645",
                isShared = false
            ),
            LogEntry(
                id = "log-008",
                title = "Routine System Diagnostics",
                audioFilename = "log-008.m4a",
                transcription = "Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus.",
                timestamp = baseTime - 3600000, // 1 hour ago
                stardate = "2024.333.0930",
                isShared = false
            ),
            LogEntry(
                id = "log-009",
                title = "Bridge Crew Performance Review",
                audioFilename = "log-009.m4a",
                transcription = "Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur. Ut enim ad minima veniam.",
                timestamp = baseTime - 1800000, // 30 minutes ago
                stardate = "2024.333.1015",
                isShared = false
            ),
            LogEntry(
                id = "log-010",
                title = "Personal Reflection on Command",
                audioFilename = "log-010.m4a",
                transcription = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium. Totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt.",
                timestamp = baseTime - 300000, // 5 minutes ago
                stardate = "2024.333.1045",
                isShared = false
            )
        ))

        println("✅ Added ${userLogs["test"]?.size ?: 0} sample logs for 'test' user")
        println("✅ Added ${friendships["test"]?.size ?: 0} sample friends for 'test' user")
    }

    // Register a new user in LDAP
    fun registerUser(username: String, password: String): Boolean {
        val success = ldapClient.registerUser(username, password)
        if (success) {
            // Create user's log directory
            File(logsDir, username).mkdirs()
            // Initialize empty lists for this user
            userLogs[username] = mutableListOf()
            friendships[username] = mutableSetOf()
            sharedLogs[username] = mutableSetOf()
        }
        return success
    }

    // Validate user credentials against LDAP
    fun validateUser(username: String, password: String): Boolean {
        return ldapClient.authenticateUser(username, password)
    }

    // Create authentication token for a user
    fun createToken(username: String): String {
        val token = UUID.randomUUID().toString()
        tokens[token] = username
        return token
    }

    // Get username from authentication token
    fun getUserFromToken(token: String): String? {
        return tokens[token]
    }

    // Remove authentication token (logout)
    fun removeToken(token: String) {
        tokens.remove(token)
    }

    // Save audio file for a log entry
    fun saveAudio(username: String, logId: String, audioBytes: ByteArray) {
        try {
            val userDir = File(logsDir, username)
            println("📁 Attempting to create directory: ${userDir.absolutePath}")

            // Ensure directory exists before saving
            val created = userDir.mkdirs()
            println("📁 Directory creation result: $created, exists: ${userDir.exists()}, canWrite: ${userDir.canWrite()}")

            val audioFile = File(userDir, "$logId.m4a")
            println("💾 Attempting to save audio to: ${audioFile.absolutePath}")

            audioFile.writeBytes(audioBytes)
            println("✅ Audio file saved successfully: ${audioFile.absolutePath}")
        } catch (e: Exception) {
            println("❌ Error saving audio file: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    // Get audio file for a log entry
    fun getAudio(username: String, logId: String): ByteArray? {
        val file = File(File(logsDir, username), "$logId.m4a")
        return if (file.exists()) file.readBytes() else null
    }

    // Save a new log entry for a user
    fun saveLogEntry(username: String, logEntry: LogEntry) {
        // Initialize user's log list if it doesn't exist
        if (userLogs[username] == null) {
            userLogs[username] = mutableListOf()
        }
        // Add the new log entry to the user's list
        userLogs[username]?.add(logEntry)
    }

    // Get all log entries for a user (including shared logs)
    fun getUserLogs(username: String): List<LogEntry> {
        // Get user's own logs
        val ownLogs = userLogs[username] ?: emptyList()

        // Get logs shared with this user
        val sharedLogIds = sharedLogs[username] ?: emptySet()
        val sharedLogsForUser = mutableListOf<LogEntry>()

        // Find all shared logs from all users
        userLogs.forEach { (owner, logs) ->
            logs.filter { it.id in sharedLogIds }.forEach { log ->
                sharedLogsForUser.add(log)
            }
        }

        // Combine and sort by timestamp (newest first)
        return (ownLogs + sharedLogsForUser).sortedByDescending { it.timestamp }
    }

    // Get a specific log entry by ID (checks ownership and sharing)
    fun getLogEntry(username: String, logId: String): LogEntry? {
        // Check user's own logs first
        userLogs[username]?.find { it.id == logId }?.let { return it }

        // Check if it's a shared log
        val sharedLogIds = sharedLogs[username] ?: emptySet()
        if (logId in sharedLogIds) {
            // Find the log in any user's logs
            userLogs.values.forEach { logs ->
                logs.find { it.id == logId }?.let { return it }
            }
        }

        return null
    }

    // Search logs by query string (searches title and transcription)
    fun searchLogs(username: String, query: String): List<LogEntry> {
        val userLogsToSearch = getUserLogs(username)
        val queryLower = query.lowercase()

        // Search in title and transcription fields
        return userLogsToSearch.filter { log ->
            log.title.lowercase().contains(queryLower) ||
                    log.transcription.lowercase().contains(queryLower)
        }
    }

    // Add a friend relationship (bidirectional)
    fun addFriend(username: String, friendUsername: String): Boolean {
        // Check if friend exists
        if (friendships[friendUsername] == null) {
            return false // Friend doesn't exist
        }

        // Add bidirectional friendship
        friendships[username]?.add(friendUsername)
        friendships[friendUsername]?.add(username)
        return true
    }

    // Get list of friends for a user
    fun getFriends(username: String): List<String> {
        return friendships[username]?.toList() ?: emptyList()
    }

    // Share a log with a friend
    fun shareLog(username: String, logId: String, friendUsername: String): Boolean {
        // Check if the log belongs to the user
        val log = userLogs[username]?.find { it.id == logId } ?: return false

        // Check if they are friends
        if (friendUsername !in (friendships[username] ?: emptySet())) {
            return false
        }

        // Add log ID to friend's shared logs
        if (sharedLogs[friendUsername] == null) {
            sharedLogs[friendUsername] = mutableSetOf()
        }
        sharedLogs[friendUsername]?.add(logId)

        return true
    }
}

// Main function - starts the Ktor server
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Install JSON content negotiation for serialization
        install(ContentNegotiation) {
            json()
        }

        // Install CORS to allow requests from Android app
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
        }

        routing {
            // Health check endpoint
            get("/") {
                call.respondText("Captain's Log Server Running with LDAP Authentication")
            }

            // Register a new user
            post("/register") {
                val credentials = call.receive<UserCredentials>()

                // Validate input
                if (credentials.username.isBlank() || credentials.password.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest,
                        LoginResponse(false, message = "Username and password required"))
                    return@post
                }

                // Register user in LDAP
                val success = Storage.registerUser(credentials.username, credentials.password)
                if (success) {
                    call.respond(LoginResponse(true, message = "Registration successful"))
                } else {
                    call.respond(HttpStatusCode.Conflict,
                        LoginResponse(false, message = "Username already exists"))
                }
            }

            // Login - authenticate against LDAP
            post("/login") {
                val credentials = call.receive<UserCredentials>()

                // Validate credentials with LDAP
                if (Storage.validateUser(credentials.username, credentials.password)) {
                    val token = Storage.createToken(credentials.username)
                    call.respond(LoginResponse(true, token = token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized,
                        LoginResponse(false, message = "Invalid credentials"))
                }
            }

            // Logout - remove authentication token
            post("/logout") {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                if (token != null) {
                    Storage.removeToken(token)
                }
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            }

            // Upload a new log entry with audio
            post("/logs/upload") {
                // Get authentication token from header
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                // Check authentication
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@post
                }

                try {
                    val multipart = call.receiveMultipart()
                    var audioBytes: ByteArray? = null
                    var title: String? = null
                    var transcription: String? = null
                    var stardate: String? = null

                    // Parse multipart form data
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                // Read audio file bytes
                                audioBytes = part.streamProvider().readBytes()
                            }
                            is PartData.FormItem -> {
                                // Read form fields
                                when (part.name) {
                                    "title" -> title = part.value
                                    "transcription" -> transcription = part.value
                                    "stardate" -> stardate = part.value
                                }
                            }
                            else -> {}
                        }
                        part.dispose()
                    }

                    // Validate required fields
                    if (audioBytes != null && title != null) {
                        val logId = UUID.randomUUID().toString()
                        val timestamp = System.currentTimeMillis()

                        // Create log entry object
                        val logEntry = LogEntry(
                            id = logId,
                            title = title!!,
                            audioFilename = "$logId.m4a",
                            transcription = transcription ?: "",
                            timestamp = timestamp,
                            stardate = stardate ?: "",
                            isShared = false
                        )

                        // Save audio file and log entry
                        Storage.saveAudio(username, logId, audioBytes!!)
                        Storage.saveLogEntry(username, logEntry)

                        call.respond(UploadLogResponse(true, logId = logId))
                    } else {
                        call.respond(HttpStatusCode.BadRequest,
                            UploadLogResponse(false, message = "Missing required fields"))
                    }
                } catch (e: Exception) {
                    println("Error uploading log: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError,
                        UploadLogResponse(false, message = "Upload failed: ${e.message}"))
                }
            }

            // Get all log entries for authenticated user
            get("/logs") {
                // Get authentication token
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                // Check authentication
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@get
                }

                // Return user's logs (including shared ones)
                val logs = Storage.getUserLogs(username)
                call.respond(logs)
            }

            // Get audio file for a specific log entry
            get("/logs/{logId}/audio") {
                // Get authentication token
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                // Check authentication
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@get
                }

                val logId = call.parameters["logId"]!!

                // Check if user has access to this log
                val logEntry = Storage.getLogEntry(username, logId)
                if (logEntry == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Log not found"))
                    return@get
                }

                // Find owner of the log to get audio file
                var audioBytes: ByteArray? = null
                Storage.getAudio(username, logId)?.let { audioBytes = it }

                // If not found in user's directory, search other users (for shared logs)
                if (audioBytes == null) {
                    // This is a simplified approach - in production, you'd track ownership
                    audioBytes = Storage.getAudio(username, logId)
                }

                // Return audio file
                if (audioBytes != null) {
                    call.respondBytes(audioBytes!!, ContentType.Audio.MP4)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Audio file not found"))
                }
            }

            // Search log entries by query
            get("/logs/search") {
                // Get authentication token
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                // Check authentication
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@get
                }

                // Get search query from parameters
                val query = call.request.queryParameters["q"] ?: ""

                // Search logs
                val results = Storage.searchLogs(username, query)
                call.respond(SearchResult(logs = results))
            }

            // Add a friend
            post("/friends/add") {
                // Get authentication token
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                // Check authentication
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@post
                }

                val request = call.receive<AddFriendRequest>()

                // Add friend relationship
                val success = Storage.addFriend(username, request.friendUsername)
                if (success) {
                    call.respond(mapOf("success" to true, "message" to "Friend added"))
                } else {
                    call.respond(HttpStatusCode.BadRequest,
                        mapOf("success" to false, "message" to "User not found"))
                }
            }

            // Get list of friends
            get("/friends") {
                // Get authentication token
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                // Check authentication
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@get
                }

                // Return list of friends
                val friends = Storage.getFriends(username)
                call.respond(mapOf("friends" to friends))
            }

            // Share a log with a friend
            post("/logs/share") {
                // Get authentication token
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                // Check authentication
                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@post
                }

                val request = call.receive<ShareLogRequest>()

                // Share the log
                val success = Storage.shareLog(username, request.logId, request.friendUsername)
                if (success) {
                    call.respond(mapOf("success" to true, "message" to "Log shared"))
                } else {
                    call.respond(HttpStatusCode.BadRequest,
                        mapOf("success" to false, "message" to "Cannot share log"))
                }
            }
        }
    }.start(wait = true)
}