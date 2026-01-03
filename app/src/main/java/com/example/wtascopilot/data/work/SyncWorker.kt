package com.example.wtascopilot.data.work

import android.content.Context
import android.util.Log
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
            Log.d("SyncWorker", "عدد العمليات المطلوب مزامنتها: ${unsynced.size}")

            unsynced.forEach { transaction ->
                try {
                    // 1. الإرسال للسيرفر
                    val isSentSuccessfully = repo.sendToServer(transaction)
                    if (isSentSuccessfully) {
                        // 2. التحديث في قاعدة البيانات (أهم سطر)
                        repo.markAsSynced(transaction.messageHash)

                        // 3. تأكيد إضافي: اطبع في الـ Logcat عشان تشوفها بعينك
                        Log.d("SyncWorker", "تم تحديث الحالة بنجاح للـ Hash: ${transaction.messageHash}")
                    } else {
                        Log.e("SyncWorker", "السيرفر رفض العملية أو الـ Response فشل")
                    }
                } catch (e: Exception) {
                    Log.e("SyncWorker", "خطأ في عملية فردية: ${e.message}")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "خطأ عام في الـ Worker: ${e.message}")
            Result.retry()
        }
    }
}