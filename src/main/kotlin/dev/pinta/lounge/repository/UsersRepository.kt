package dev.pinta.lounge.repository

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val password: String,
    val email: String
)

object UserTable : IntIdTable("users") {
    val username = varchar("username", 65535)
    val password = varchar("password", 65535)
    val email = varchar("email", 65535)
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable)

    var username by UserTable.username
    var password by UserTable.password
    var email by UserTable.email
}

class UsersRepository : BaseRepository<Int, User, UserDAO> {

    override fun daoToModel(dao: UserDAO) = User(
        dao.id.value,
        dao.username,
        dao.password,
        dao.email,
    )

    override suspend fun all(): List<User> = suspendTransaction {
        UserDAO.all().map(::daoToModel)
    }

    override suspend fun update(entity: User): Unit = suspendTransaction {
        UserDAO.findByIdAndUpdate(entity.id) {
            it.username = entity.username
            it.password = entity.password
            it.email = entity.email
        }
    }

    override suspend fun deleteById(id: Int): Unit = suspendTransaction {
        UserDAO.findById(id)?.delete()
    }

    override suspend fun create(entity: User): Unit = suspendTransaction {
        UserDAO.new {
            username = entity.username
            password = entity.password
            email = entity.email
        }
    }

    override suspend fun findById(id: Int): Unit = suspendTransaction {
        UserDAO.findById(id)
    }

    suspend fun findByUsername(username: String) = suspendTransaction {
        UserDAO
            .find { (UserTable.username eq username) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

}

