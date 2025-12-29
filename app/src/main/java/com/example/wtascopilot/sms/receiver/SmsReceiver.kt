package com.example.wtascopilot.sms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.wtascopilot.data.local.SimStorage
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import com.example.wtascopilot.data.work.WorkScheduler
import com.example.wtascopilot.domain.parser.MessageParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    private val parser = MessageParser()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val pendingResult = goAsync()
            val bundle = intent.extras
            val incomingSubId = bundle?.getInt("subscription", -1) ?: -1
            val savedSubId = SimStorage.getSavedSubId(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // التحقق من الشريحة
                    if (incomingSubId != -1 && incomingSubId == savedSubId) {

                        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

                        // --- التعديل الجذري هنا ---
                        // 1. تجميع نص الرسالة كاملاً من كل الأجزاء
                        val fullMessageBody = StringBuilder()
                        var sender = ""

                        messages?.forEach { sms ->
                            fullMessageBody.append(sms.displayMessageBody)
                            // نأخذ اسم الراسل من أول جزء فقط
                            if (sender.isEmpty()) {
                                sender = sms.displayOriginatingAddress ?: ""
                            }
                        }

                        val finalBody = fullMessageBody.toString()

                        // 2. التحقق والمعالجة مرة واحدة فقط للنص المجمع
                        if (isVodafoneCashMessage(sender, finalBody)) {
                            handleIncomingMessage(context.applicationContext, finalBody)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private fun isVodafoneCashMessage(sender: String?, body: String): Boolean {
        if (sender == null) return false
        return sender.contains("7001") ||
                sender.contains("VF-Cash") ||
                body.contains("فودافون كاش")
    }

    private suspend fun handleIncomingMessage(context: Context, message: String) {
        val transaction = parser.parseMessage(message)

        if (transaction != null) {
            val repo = TransactionRepositoryImpl(context)

            // نتأكد هل هي موجودة ولا لأ
            val isDuplicate = repo.isTransactionExist(transaction.transactionId) // ستحتاج لإضافة هذه الدالة في الـ Repo

            if (!isDuplicate) {
                repo.saveLocal(transaction) // حفظ
                repo.sendToServer(transaction) // إرسال
            } else {
                Log.d("SmsReceiver", "الرسالة مكررة، تم تجاهلها.")
            }
        }
    }
}