package dev.pinta.lounge.repository

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

@Serializable
data class User(
    val id: Long, val username: String, val password: String, val email: String
)

object UserTable : LongIdTable("users") {
    val username = varchar("username", 65535)
    val password = varchar("password", 65535)
    val email = varchar("email", 65535)
}

class UserDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserDAO>(UserTable)

    var username by UserTable.username
    var password by UserTable.password
    var email by UserTable.email
}

class UsersRepository : BaseRepository<User, UserDAO> {

    override fun daoToModel(dao: UserDAO) = User(
        dao.id.value,
        dao.username,
        dao.password,
        dao.email,
    )

    override suspend fun all(): List<User> = suspendTransaction {
        UserDAO.all()
            .map(::daoToModel)
    }

    override suspend fun update(entity: User) = suspendTransaction {
        UserDAO.findByIdAndUpdate(entity.id) {
            it.username = entity.username
            it.password = entity.password
            it.email = entity.email
        }
            ?.let { daoToModel(it) }
    }

    override suspend fun deleteById(id: Long) = suspendTransaction {
        UserDAO.findById(id)
            ?.delete()
    }

    override suspend fun create(entity: User) = suspendTransaction {
        UserDAO.new {
            username = entity.username
            password = entity.password
            email = entity.email
        }
            .let { daoToModel(it) }
    }

    override suspend fun findById(id: Long) = suspendTransaction {
        UserDAO.findById(id)
            ?.let { daoToModel(it) }
    }

    suspend fun findByUsername(username: String) = suspendTransaction {
        UserDAO.find { (UserTable.username eq username) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

}

