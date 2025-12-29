package com.example.wtascopilot.data.repository

import com.example.wtascopilot.data.modle.Transaction

interface TransactionRepository {
    suspend fun saveLocal(transaction: Transaction)

    suspend fun sendToServer(transaction: Transaction): Boolean

    suspend fun getUnSyncedTransactions(): List<Transaction>

    suspend fun markAsSynced(id: Int)

    suspend fun isTransactionExist(transactionId: String): Boolean

}