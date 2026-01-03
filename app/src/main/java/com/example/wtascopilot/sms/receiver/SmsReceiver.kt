package com.example.wtascopilot.sms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.wtascopilot.data.local.SimStorage
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
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
                    if (incomingSubId != -1 && incomingSubId == savedSubId) {

                        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                        val sender = messages[0].displayOriginatingAddress
                        val fullMessageBody = StringBuilder()

                        for (sms in messages) {
                            fullMessageBody.append(sms.displayMessageBody)
                        }

                        val finalBody = fullMessageBody.toString()

                        if (isVodafoneCashMessage(sender, finalBody)) {
                            handleIncomingMessage(context.applicationContext, sender, finalBody, incomingSubId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SmsReceiver", "Error receiving SMS: ${e.message}")
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private fun isVodafoneCashMessage(sender: String?, body: String): Boolean {
        if (sender == null) return false
        val s = sender.lowercase()
        return s.contains("vf-cash") ||
                s.contains("e& money") ||
                s.contains("etisalat") ||
                s.contains("orange") ||
                body.contains("Orange Cash", ignoreCase = true)
    }

    private suspend fun handleIncomingMessage(context: Context, sender: String?, message: String, subid: Int) {
        val transaction = parser.parseMessage(sender, message, subid)

        if (transaction != null) {
            val repo = TransactionRepositoryImpl(context)

            val isDuplicate = repo.isTransactionExist(transaction.messageHash)

            if (!isDuplicate) {
                Log.d("SmsReceiver", "New transaction found: ${transaction.transactionId}")
                repo.saveLocal(transaction) // 1. الحفظ المبدئي (تظهر في الـ UI كغير متزامنة)

                // --- التعديل الجوهري هنا ---
                // نستقبل نتيجة الإرسال (true/false)
                val isSentSuccess = repo.sendToServer(transaction)

                if (isSentSuccess) {
                    // إذا تم الإرسال بنجاح، نحدث الحالة فوراً في الداتا بيز
                    // هذا يمنع الـ SyncWorker من إرسالها مرة أخرى
                    repo.markAsSynced(transaction.messageHash)
                    Log.d("SmsReceiver", "Sent immediately and marked as Synced.")
                } else {
                    Log.d("SmsReceiver", "Send failed, SyncWorker will retry later.")
                }
                // ---------------------------

            } else {
                Log.d("SmsReceiver", "Duplicate transaction ignored: ${transaction.transactionId}")
            }
        } else {
            Log.d("SmsReceiver", "Message received but could not be parsed.")
        }
    }
}