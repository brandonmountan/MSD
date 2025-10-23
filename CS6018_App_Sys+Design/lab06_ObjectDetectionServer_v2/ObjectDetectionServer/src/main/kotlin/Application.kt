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

// Data models
@Serializable
data class UserCredentials(val username: String, val password: String)

@Serializable
data class LoginResponse(val success: Boolean, val token: String? = null, val message: String? = null)

@Serializable
data class PhotoInfo(val id: String, val filename: String, val timestamp: Long)

// Not used - we use token-based auth via Authorization header instead
// data class UserSession(val userId: String, val token: String)

// Storage with LDAP integration
object Storage {
    private val tokens = mutableMapOf<String, String>() // token -> username
    private val photosDir = File("/app/photos").apply { mkdirs() }

    // LDAP client - gets config from environment variables
    private val ldapClient: LdapClient by lazy {
        val host = System.getenv("LDAP_HOST") ?: "localhost"
        val port = System.getenv("LDAP_PORT")?.toIntOrNull() ?: 389
        val baseDN = System.getenv("LDAP_BASE_DN") ?: "dc=objectdetection,dc=com"
        val adminDN = System.getenv("LDAP_ADMIN_DN") ?: "cn=admin,dc=objectdetection,dc=com"
        val adminPassword = System.getenv("LDAP_ADMIN_PASSWORD") ?: "admin_password"

        println("🔧 Connecting to LDAP at $host:$port")
        LdapClient(host, port, baseDN, adminDN, adminPassword)
    }

    fun registerUser(username: String, password: String): Boolean {
        val success = ldapClient.registerUser(username, password)
        if (success) {
            File(photosDir, username).mkdirs() // Create user's photo directory
        }
        return success
    }

    fun validateUser(username: String, password: String): Boolean {
        return ldapClient.authenticateUser(username, password)
    }

    fun createToken(username: String): String {
        val token = UUID.randomUUID().toString()
        tokens[token] = username
        return token
    }

    fun getUserFromToken(token: String): String? {
        return tokens[token]
    }

    fun removeToken(token: String) {
        tokens.remove(token)
    }

    fun savePhoto(username: String, photoId: String, bytes: ByteArray) {
        val userDir = File(photosDir, username)
        File(userDir, "$photoId.jpg").writeBytes(bytes)
    }

    fun getPhoto(username: String, photoId: String): ByteArray? {
        val file = File(File(photosDir, username), "$photoId.jpg")
        return if (file.exists()) file.readBytes() else null
    }

    fun getUserPhotos(username: String): List<PhotoInfo> {
        val userDir = File(photosDir, username)
        if (!userDir.exists()) return emptyList()

        return userDir.listFiles()?.filter { it.extension == "jpg" }?.map {
            PhotoInfo(
                id = it.nameWithoutExtension,
                filename = it.name,
                timestamp = it.lastModified()
            )
        }?.sortedByDescending { it.timestamp } ?: emptyList()
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }

        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
        }

        routing {
            // Health check
            get("/") {
                call.respondText("Object Detection Server Running with LDAP Authentication")
            }

            // Register new user (creates user in LDAP)
            post("/register") {
                val credentials = call.receive<UserCredentials>()

                if (credentials.username.isBlank() || credentials.password.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest,
                        LoginResponse(false, message = "Username and password required"))
                    return@post
                }

                val success = Storage.registerUser(credentials.username, credentials.password)
                if (success) {
                    call.respond(LoginResponse(true, message = "Registration successful"))
                } else {
                    call.respond(HttpStatusCode.Conflict,
                        LoginResponse(false, message = "Username already exists"))
                }
            }

            // Login (authenticates against LDAP)
            post("/login") {
                val credentials = call.receive<UserCredentials>()

                if (Storage.validateUser(credentials.username, credentials.password)) {
                    val token = Storage.createToken(credentials.username)
                    call.respond(LoginResponse(true, token = token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized,
                        LoginResponse(false, message = "Invalid credentials"))
                }
            }

            // Logout
            post("/logout") {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                if (token != null) {
                    Storage.removeToken(token)
                }
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            }

            // Upload photo (requires authentication)
            post("/photos/upload") {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@post
                }

                try {
                    val multipart = call.receiveMultipart()
                    var photoBytes: ByteArray? = null

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                // Read the file bytes from the input stream
                                val inputStream = part.streamProvider()
                                photoBytes = inputStream.readBytes()
                            }
                            else -> {}
                        }
                        part.dispose()
                    }

                    if (photoBytes != null) {
                        val photoId = UUID.randomUUID().toString()
                        Storage.savePhoto(username, photoId, photoBytes!!)
                        call.respond(mapOf("success" to true, "photoId" to photoId))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No photo data"))
                    }
                } catch (e: Exception) {
                    println("Error uploading photo: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Upload failed: ${e.message}"))
                }
            }

            // Get user's photos list
            get("/photos") {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@get
                }

                val photos = Storage.getUserPhotos(username)
                call.respond(photos)
            }

            // Get specific photo
            get("/photos/{photoId}") {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                val username = token?.let { Storage.getUserFromToken(it) }

                if (username == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
                    return@get
                }

                val photoId = call.parameters["photoId"]!!
                val photoBytes = Storage.getPhoto(username, photoId)

                if (photoBytes != null) {
                    call.respondBytes(photoBytes, ContentType.Image.JPEG)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Photo not found"))
                }
            }
        }
    }.start(wait = true)
}