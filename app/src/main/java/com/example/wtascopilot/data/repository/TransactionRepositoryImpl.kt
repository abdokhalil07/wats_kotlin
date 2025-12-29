package com.example.wtascopilot.data.repository

import android.content.Context
import android.util.Log
import com.example.wtascopilot.data.local.DatabaseProvider
import com.example.wtascopilot.data.local.SimStorage
import com.example.wtascopilot.data.mapper.toEntity
import com.example.wtascopilot.data.mapper.toModel
import com.example.wtascopilot.data.mapper.toRequest
import com.example.wtascopilot.data.modle.Transaction
import com.example.wtascopilot.data.remote.ApiService
import com.example.wtascopilot.data.remote.RetrofitClient


class TransactionRepositoryImpl(private val context: Context) : TransactionRepository {

    private val dao = DatabaseProvider.getDatabase(context).transactionDao()


    override suspend fun saveLocal(transaction: Transaction) {
        // 1. قبل الحفظ، نتأكد أن رقم العملية مش موجود قبل كده
        val exists = dao.checkIfExists(transaction.transactionId)

        if (exists == 0) {
            dao.insert(transaction.toEntity())

            Log.d("Repo", "تم حفظ العملية بنجاح: ${transaction.transactionId}")
        } else {
            Log.w("Repo", "تم تجاهل العملية لأنها مسجلة مسبقاً: ${transaction.transactionId}")
            // هنا ممكن نوقف الدالة عشان منبعتش للسيرفر تاني لو مش محتاجين
            // return
        }
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

    override suspend fun isTransactionExist(transactionId: String): Boolean {
        val count = dao.checkIfExists(transactionId)
        return count > 0 // لو العدد أكبر من صفر، يبقى موجودة
    }

}

