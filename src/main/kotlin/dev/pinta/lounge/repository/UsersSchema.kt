package dev.pinta.lounge.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

@Serializable
data class User(val username: String, val password: String, val email: String) {
    companion object {
        operator fun invoke(resultSet: ResultSet): User {
            val username = resultSet.getString("username")
            val password = resultSet.getString("password")
            val email = resultSet.getString("email")
            return User(username, password, email)
        }
    }
}

class UsersService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR, 
                password VARCHAR, 
                email VARCHAR
            );
        """
        private const val SELECT_BY_ID = """
            SELECT username, password, email 
            FROM users 
            WHERE id = ?
        """
        private const val INSERT_ONE = """
            INSERT INTO users (username, password, email) 
            VALUES (?, ?)
        """
        private const val UPDATE_ONE = """
            UPDATE users SET username = ?, password = ?, email = ? 
            WHERE id = ?
        """
        private const val DELETE_ONE = "DELETE FROM users WHERE id = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE)
    }

    // Create new user
    suspend fun create(user: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_ONE, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.username)
        statement.setString(2, user.password)
        statement.setString(3, user.email)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted city")
        }
    }

    // Read a user
    suspend fun read(id: Int): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext User(resultSet)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a user
    suspend fun update(id: Int, user: User) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_ONE)
        statement.setString(1, user.username)
        statement.setString(2, user.password)
        statement.setString(2, user.email)
        statement.setInt(3, id)
        statement.executeUpdate()
    }

    // Delete a user
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_ONE)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    suspend fun findByUsername(username: String) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            """
            SELECT username, password, email 
            FROM users 
            WHERE username = ?
        """.trimIndent()
        )

        statement.setString(1, username)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext User(resultSet)
        } else {
            return@withContext null
        }
    }

}

