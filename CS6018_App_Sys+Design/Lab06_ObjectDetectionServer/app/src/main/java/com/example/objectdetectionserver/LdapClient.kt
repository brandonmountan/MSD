package com.example

import com.unboundid.ldap.sdk.*
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedRequest
import java.io.Closeable

/**
 * Simple LDAP client for user authentication and management
 */
class LdapClient(
    private val host: String,
    private val port: Int,
    private val baseDN: String,
    private val adminDN: String,
    private val adminPassword: String
) : Closeable {

    private var connection: LDAPConnection? = null

    init {
        connect()
    }

    private fun connect() {
        try {
            connection = LDAPConnection(host, port, adminDN, adminPassword)
            println("✅ Connected to LDAP server at $host:$port")
        } catch (e: Exception) {
            println("❌ Failed to connect to LDAP: ${e.message}")
            throw e
        }
    }

    /**
     * Register a new user in LDAP
     */
    fun registerUser(username: String, password: String): Boolean {
        try {
            val userDN = "uid=$username,ou=users,$baseDN"

            // Check if user already exists
            if (userExists(username)) {
                println("User $username already exists")
                return false
            }

            // Create users OU if it doesn't exist
            createUsersOU()

            // Create the user entry
            val addRequest = AddRequest(
                userDN,
                Attribute("objectClass", "inetOrgPerson", "organizationalPerson", "person", "top"),
                Attribute("uid", username),
                Attribute("cn", username),
                Attribute("sn", username),
                Attribute("userPassword", password)
            )

            val result = connection?.add(addRequest)
            val success = result?.resultCode == ResultCode.SUCCESS

            if (success) {
                println("✅ Registered user: $username")
            } else {
                println("❌ Failed to register user: ${result?.resultCode}")
            }

            return success
        } catch (e: Exception) {
            println("❌ Error registering user: ${e.message}")
            return false
        }
    }

    /**
     * Authenticate a user against LDAP
     */
    fun authenticateUser(username: String, password: String): Boolean {
        var userConnection: LDAPConnection? = null
        try {
            val userDN = "uid=$username,ou=users,$baseDN"

            // Try to bind as the user
            userConnection = LDAPConnection(host, port, userDN, password)

            val success = userConnection.isConnected
            if (success) {
                println("✅ Authenticated user: $username")
            }

            return success
        } catch (e: LDAPException) {
            println("❌ Authentication failed for $username: ${e.resultCode}")
            return false
        } finally {
            userConnection?.close()
        }
    }

    /**
     * Check if a user exists
     */
    private fun userExists(username: String): Boolean {
        try {
            val searchRequest = SearchRequest(
                "ou=users,$baseDN",
                SearchScope.ONE,
                Filter.createEqualityFilter("uid", username)
            )

            val searchResult = connection?.search(searchRequest)
            return (searchResult?.entryCount ?: 0) > 0
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Create the users organizational unit if it doesn't exist
     */
    private fun createUsersOU() {
        try {
            val ouDN = "ou=users,$baseDN"

            // Check if OU exists
            val searchRequest = SearchRequest(
                baseDN,
                SearchScope.ONE,
                Filter.createEqualityFilter("ou", "users")
            )

            val searchResult = connection?.search(searchRequest)
            if ((searchResult?.entryCount ?: 0) > 0) {
                return // OU already exists
            }

            // Create the OU
            val addRequest = AddRequest(
                ouDN,
                Attribute("objectClass", "organizationalUnit", "top"),
                Attribute("ou", "users")
            )

            val result = connection?.add(addRequest)
            if (result?.resultCode == ResultCode.SUCCESS) {
                println("✅ Created users OU")
            }
        } catch (e: Exception) {
            println("ℹ️ Users OU might already exist: ${e.message}")
        }
    }

    override fun close() {
        connection?.close()
        println("ℹ️ Disconnected from LDAP")
    }
}