package com.example.wtascopilot.data.repository

import android.content.Context
import android.util.Log
import com.example.wtascopilot.data.local.DatabaseProvider
import com.example.wtascopilot.data.local.SimStorage
import com.example.wtascopilot.data.mapper.toEntity
import com.example.wtascopilot.data.mapper.toModel
import com.example.wtascopilot.data.mapper.toRequest
import com.example.wtascopilot.data.modle.SmsTransaction
import com.example.wtascopilot.data.modle.Transaction
import com.example.wtascopilot.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(private val context: Context) : TransactionRepository {

    private val dao = DatabaseProvider.getDatabase(context).transactionDao()

    override suspend fun saveLocal(transaction: Transaction) {
        // نستخدم الـ messageHash لمنع التكرار
        val exists = dao.checkIfExists(transaction.messageHash)

        if (exists == 0) {
            dao.insert(transaction.toEntity())
            Log.d("Repo", "تم حفظ العملية بالبصمة الفريدة: ${transaction.messageHash}")
        } else {
            Log.w("Repo", "العملية موجودة مسبقاً، تم التجاهل.")
        }
    }

    override suspend fun getUnSyncedTransactions(): List<Transaction> {
        return dao.getUnSynced().map { it.toModel() }
    }

    // تعديل المعامل ليصبح الـ hash ليتوافق مع الـ DAO المحدث
    override suspend fun markAsSynced(messageHash: String) {
        dao.markAsSynced(messageHash)
    }

    override suspend fun sendToServer(transaction: Transaction): Boolean {
        return try {
            val simNumber = SimStorage.getPhoneNumberForSub(context, transaction.subId) ?: "Unknown"
            val request = transaction.toRequest(simNumber)
            val response = RetrofitClient.api.sendTransaction(request)

            // التعديل هنا: لو السيرفر رد بـ 200 أو 201، نعتبرها نجحت فوراً
            // ده أضمن بكتير من فحص الـ body اللي ممكن يكون null أو فيه كلمة غلط
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isTransactionExist(messageHash: String): Boolean {

        val count = dao.checkIfExists(messageHash)
        return count > 0
    }

    override fun getAllLocalTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun toggleSyncStatus(hash: String) {
        dao.toggleSyncStatus(hash)
    }

    override suspend fun insertSms(smsTransaction: SmsTransaction) {
        // نستخدم الـ messageHash لمنع التكرار

            dao.insertSms(smsTransaction.toEntity())
            Log.d("Repo", "تم حفظ الرساله ")

    }

    override fun getAllLocalSms(): Flow<List<SmsTransaction>> {
        return dao.getAllSms().map { entities ->
            entities.map { it.toModel() }
        }
    }

}