package pt.socialfood.core.data.paging

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import pt.socialfood.core.database.AppDatabase

fun interface HomeCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

fun AppDatabase.asHomeCacheTransactionRunner(): HomeCacheTransactionRunner = HomeCacheTransactionRunner { block ->
    useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
}
