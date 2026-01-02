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
                    // التحقق من أن الرسالة قادمة من الشريحة المسجلة محلياً
                    if (incomingSubId != -1 && incomingSubId == savedSubId) {

                        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                        val sender = messages[0].displayOriginatingAddress
                        val fullMessageBody = StringBuilder()

                        // تجميع أجزاء الرسالة (في حال كانت الرسالة طويلة ومقسمة)
                        for (sms in messages) {
                            fullMessageBody.append(sms.displayMessageBody)
                        }

                        val finalBody = fullMessageBody.toString()

                        // التحقق هل المرسل من ضمن القائمة البيضاء (White List) للمحافظ
                        if (isVodafoneCashMessage(sender, finalBody)) {
                            // تم إضافة sender هنا لتمريره للمحلل
                            handleIncomingMessage(context.applicationContext, sender, finalBody)
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
        // التحقق من الأسماء الرسمية للمرسلين لضمان الدقة
        return s.contains("vf-cash") ||
                s.contains("e& money") ||
                s.contains("etisalat") ||
                s.contains("orange") ||
                body.contains("Orange Cash", ignoreCase = true)
    }

    private suspend fun handleIncomingMessage(context: Context, sender: String?, message: String) {
        // قمنا بتمرير الـ sender هنا لتمكين الـ MessageParser من اختيار الدالة المناسبة من الـ 9 دوال
        val transaction = parser.parseMessage(sender, message)

        if (transaction != null) {
            val repo = TransactionRepositoryImpl(context)

            // التحقق من عدم تكرار العملية قبل الحفظ (Idempotency)
            val isDuplicate = repo.isTransactionExist(transaction.messageHash)

            if (!isDuplicate) {
                Log.d("SmsReceiver", "New transaction found: ${transaction.transactionId}")
                repo.saveLocal(transaction) // حفظ في Room
                repo.sendToServer(transaction) // محاولة الإرسال للسيرفر فوراً
            } else {
                Log.d("SmsReceiver", "Duplicate transaction ignored: ${transaction.transactionId}")
            }
        } else {
            Log.d("SmsReceiver", "Message received but could not be parsed.")
        }
    }
}