package com.example.wtascopilot.data.repository

import android.content.Context
import com.example.wtascopilot.data.local.DatabaseProvider
import com.example.wtascopilot.data.local.SimStorage
import com.example.wtascopilot.data.mapper.toEntity
import com.example.wtascopilot.data.mapper.toModel
import com.example.wtascopilot.data.mapper.toRequest
import com.example.wtascopilot.data.modle.Transaction
import com.example.wtascopilot.data.remote.RetrofitClient


class TransactionRepositoryImpl(private val context: Context) : TransactionRepository {

    private val dao = DatabaseProvider.getDatabase(context).transactionDao()


    override suspend fun saveLocal(transaction: Transaction) {
        dao.insert(transaction.toEntity())
    }

    override suspend fun getUnSyncedTransactions(): List<Transaction> {
        return dao.getUnSynced().map { it.toModel() }
    }

    override suspend fun markAsSynced(id: Int) {
        dao.markAsSynced(id)
    }

    override suspend fun sendToServer(transaction: Transaction): Boolean {
        return try {
            val simNumber = SimStorage.getSavedPhoneNumber(context) ?: "Unknown"

            val request = transaction.toRequest(simNumber)

            val response = RetrofitClient.api.sendTransaction(request)

            response.isSuccessful && response.body()?.success == true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}

