package dev.pinta.lounge.repository

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

@Serializable
data class Message(
    val id: Int,
    val sender: Int,
    val recipient: Int,
    val content: String,
    @Contextual
    val date: Instant,
)

object MessageTable : IntIdTable("messages") {
    val sender = integer("sender")
    val recipient = integer("recipient")
    val content = varchar("content", 65535)
    val date = timestamp("date")
}

class MessageDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MessageDAO>(MessageTable)

    var sender by MessageTable.sender
    var recipient by MessageTable.recipient
    var content by MessageTable.content
    var date by MessageTable.date
}

class MessagesRepository : BaseRepository<Int, Message, MessageDAO> {
    override fun daoToModel(dao: MessageDAO) = Message(
        dao.id.value,
        dao.sender,
        dao.recipient,
        dao.content,
        dao.date,
    )

    override suspend fun all(): List<Message> = suspendTransaction {
        MessageDAO.all().map(::daoToModel)
    }

    override suspend fun deleteById(id: Int): Unit = suspendTransaction {
        MessageDAO.findById(id)?.delete()
    }

    override suspend fun update(entity: Message): Unit = suspendTransaction {
        MessageDAO.findByIdAndUpdate(entity.id) {
            it.sender = entity.sender
            it.recipient = entity.recipient
            it.content = entity.content
            it.date = entity.date
        }
    }

    override suspend fun create(entity: Message): Unit = suspendTransaction {
        MessageDAO.new {
            sender = entity.sender
            recipient = entity.recipient
            content = entity.content
            date = entity.date
        }
    }

    override suspend fun findById(id: Int): Unit = suspendTransaction {
        MessageDAO.findById(id)
    }


}

