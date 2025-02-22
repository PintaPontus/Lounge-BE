package dev.pinta.lounge.repository

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface BaseRepository<ID, E : Any, D : IntEntity> {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    fun daoToModel(dao: D): E

    suspend fun all(): List<E>

    suspend fun findById(id: ID)

    suspend fun create(entity: E)

    suspend fun update(entity: E)

    suspend fun deleteById(id: ID)

}