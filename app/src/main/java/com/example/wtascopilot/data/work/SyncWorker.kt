package com.example.wtascopilot.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repo = TransactionRepositoryImpl(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val unsynced = repo.getUnSyncedTransactions()

            unsynced.forEach { transaction ->
                val sent = repo.sendToServer(transaction)

                if (sent) {
                    repo.markAsSynced(transaction.messageHash)
                }
            }

            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}