package com.example.wtascopilot.data.repository

import com.example.wtascopilot.data.modle.SmsTransaction
import com.example.wtascopilot.data.modle.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveLocal(transaction: Transaction)

    suspend fun insertSms(smsTransaction: SmsTransaction)

    suspend fun sendToServer(transaction: Transaction): Boolean

    suspend fun getUnSyncedTransactions(): List<Transaction>

    fun getAllLocalTransactions(): Flow<List<Transaction>>

    suspend fun markAsSynced(hash: String)

    suspend fun toggleSyncStatus(hash: String)

    suspend fun isTransactionExist(transactionId: String): Boolean

    fun getAllLocalSms(): Flow<List<SmsTransaction>>



}