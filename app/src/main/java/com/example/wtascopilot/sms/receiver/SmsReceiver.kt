package com.example.wtascopilot.sms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
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

            // 1. استخدام goAsync لمنع النظام من قتل العملية أثناء المعالجة في الخلفية
            val pendingResult = goAsync()

            val bundle = intent.extras
            // استخراج ID الشريحة المستقبلة من النظام
            val incomingSubId = bundle?.getInt("subscription", -1) ?: -1

            // استخراج ID الشريحة المحفوظة في التطبيق
            val savedSubId = SimStorage.getSavedSubId(context)

            // بدء المعالجة في خيط (Thread) منفصل
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 2. التحقق من تطابق الشريحة
                    if (incomingSubId != -1 && incomingSubId == savedSubId) {

                        // 3. الطريقة الحديثة والآمنة لاستخراج الرسائل (بدلاً من PDUs)
                        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

                        messages?.forEach { sms ->
                            val messageBody = sms.displayMessageBody
                            val sender = sms.displayOriginatingAddress

                            if (isVodafoneCashMessage(sender, messageBody)) {
                                // استخدام applicationContext لتجنب أي مشاكل في الذاكرة
                                handleIncomingMessage(context.applicationContext, messageBody)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // 4. إنهاء العملية وإخبار النظام بأن العمل انتهى
                    pendingResult.finish()
                }
            }
        }
    }

    private fun isVodafoneCashMessage(sender: String?, body: String): Boolean {
        if (sender == null) return false
        return sender.contains("7001") ||
                sender.contains("Vodafone") ||
                body.contains("فودافون كاش")
    }

    private suspend fun handleIncomingMessage(context: Context, message: String) {
        // تحليل النص
        val transaction = parser.parseMessage(message)

        if (transaction != null) {
            val repo = TransactionRepositoryImpl(context)

            // 1. حفظ محلي
            repo.saveLocal(transaction)

            // 2. محاولة إرسال للسيرفر
            val sent = repo.sendToServer(transaction)

            if (sent) {
                repo.markAsSynced(transaction.id)
            } else {
                // جدولة المزامنة اللاحقة في حال الفشل
                WorkScheduler.scheduleSync(context)
            }
        }
    }
}