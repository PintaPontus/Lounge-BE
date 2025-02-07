package dev.pinta.lounge.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

@Serializable
data class Message(
    val sender: Int,
    val recipient: Int,
    val content: String,
    val date: String
) {
    companion object {
        operator fun invoke(resultSet: ResultSet): Message {
            val sender = resultSet.getInt("sender")
            val recipient = resultSet.getInt("recipient")
            val content = resultSet.getString("content")
            val date = resultSet.getString("date")
            return Message(sender, recipient, content, date)
        }
    }
}

class MessagesService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS messages (
                id SERIAL PRIMARY KEY,
                sender INTEGER NOT NULL, 
                recipient INTEGER NOT NULL, 
                content VARCHAR NOT NULL,
                date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """
        private const val SELECT_BY_ID = """
            SELECT sender, recipient, content, date
            FROM messages 
            WHERE id = ?
        """
        private const val INSERT_ONE = """
            INSERT INTO messages (sender, recipient, content) 
            VALUES (?, ?, ?)
        """
        private const val UPDATE_ONE = """
            UPDATE messages SET content = ?
            WHERE id = ?
        """
        private const val DELETE_ONE = "DELETE FROM messages WHERE id = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE)
    }

    // Create new message
    suspend fun create(message: Message): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_ONE, Statement.RETURN_GENERATED_KEYS)
        statement.setInt(1, message.sender)
        statement.setInt(2, message.recipient)
        statement.setString(3, message.content)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted city")
        }
    }

    // Read a message
    suspend fun read(id: Int): Message = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext Message(resultSet)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a message
    suspend fun update(id: Int, message: Message) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_ONE)
        statement.setString(1, message.content)
        statement.setInt(2, id)
        statement.executeUpdate()
    }

    // Delete a message
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_ONE)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    suspend fun findByUser(user: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            """
            SELECT sender, recipient, content, date 
            FROM messages 
            WHERE sender = ? OR recipient = ?
        """.trimIndent()
        )

        statement.setInt(1, user)
        statement.setInt(2, user)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext Message(resultSet)
        } else {
            return@withContext null
        }
    }

}

