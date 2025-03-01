package dev.pinta.lounge.repository

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface BaseRepository<E : Any, D : LongEntity> {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    fun daoToModel(dao: D): E

    suspend fun all(): List<E>

    suspend fun findById(id: Long): E?

    suspend fun create(entity: E): E

    suspend fun update(entity: E): E?

    suspend fun deleteById(id: Long): Unit?

}