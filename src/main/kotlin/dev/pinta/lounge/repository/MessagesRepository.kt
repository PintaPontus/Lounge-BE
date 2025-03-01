package dev.pinta.lounge.repository

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.or
import java.time.Instant

@Serializable
data class Message(
    val id: Long,
    val sender: Long,
    val recipient: Long,
    val content: String,
    @Contextual
    val date: Instant,
)

object MessageTable : LongIdTable("messages") {
    val sender = long("sender")
    val recipient = long("recipient")
    val content = varchar("content", 65535)
    val date = timestamp("date")
}

class MessageDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MessageDAO>(MessageTable)

    var sender by MessageTable.sender
    var recipient by MessageTable.recipient
    var content by MessageTable.content
    var date by MessageTable.date
}

class MessagesRepository : BaseRepository<Message, MessageDAO> {
    override fun daoToModel(dao: MessageDAO) = Message(
        dao.id.value,
        dao.sender,
        dao.recipient,
        dao.content,
        dao.date,
    )

    override suspend fun all(): List<Message> = suspendTransaction {
        MessageDAO.all()
            .map(::daoToModel)
    }

    override suspend fun deleteById(id: Long) = suspendTransaction {
        MessageDAO.findById(id)
            ?.delete()
    }

    override suspend fun update(entity: Message) = suspendTransaction {
        MessageDAO.findByIdAndUpdate(entity.id) {
            it.sender = entity.sender
            it.recipient = entity.recipient
            it.content = entity.content
            it.date = entity.date
        }
            ?.let { daoToModel(it) }
    }

    override suspend fun create(entity: Message) = suspendTransaction {
        MessageDAO.new {
            sender = entity.sender
            recipient = entity.recipient
            content = entity.content
            date = entity.date
        }
            .let { daoToModel(it) }
    }

    override suspend fun findById(id: Long) = suspendTransaction {
        MessageDAO.findById(id)
            ?.let { daoToModel(it) }
    }

    suspend fun findByUserPaged(sender: Long, recipient: Long, page: Long, size: Int) = suspendTransaction {
        MessageDAO.find {
            ((MessageTable.sender eq sender) and (MessageTable.recipient eq recipient)) or ((MessageTable.sender eq recipient) and (MessageTable.recipient eq sender))
        }
            .orderBy(MessageTable.date to SortOrder.DESC)
            .limit(size)
            .offset(start = (page * size))
            .map(::daoToModel)
            .reversed()
    }

}

