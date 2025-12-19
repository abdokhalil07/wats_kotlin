package com.example.wtascopilot.data.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.runBlocking

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val repo = TransactionRepositoryImpl(context)

    override fun doWork(): Result = runBlocking {
        try {
            val unsynced = repo.getUnSyncedTransactions()

            unsynced.forEach { transaction ->
                val sent = repo.sendToServer(transaction)
                if (sent) {
                    repo.markAsSynced(transaction.id)
                }
            }

            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
