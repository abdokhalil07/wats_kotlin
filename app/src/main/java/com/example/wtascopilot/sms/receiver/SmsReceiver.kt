package com.example.wtascopilot.sms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import com.example.wtascopilot.data.work.WorkScheduler
import com.example.wtascopilot.domain.parser.MessageParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    private val parser = MessageParser()




    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {

            val bundle: Bundle? = intent.extras
            val pdus = bundle?.get("pdus") as? Array<*>

            pdus?.forEach { pdu ->
                val format = bundle.getString("format")
                val sms = SmsMessage.createFromPdu(pdu as ByteArray, format)

                val sender = sms.displayOriginatingAddress
                val messageBody = sms.displayMessageBody

                // فلترة رسائل Vodafone Cash فقط
                if (isVodafoneCashMessage(sender, messageBody)) {
                    handleIncomingMessage(context, messageBody)
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

    private fun handleIncomingMessage(context: Context, message: String) {
        val transaction = parser.parseMessage(message)

        if (transaction != null) {
            val repo = TransactionRepositoryImpl(context)

            CoroutineScope(Dispatchers.IO).launch {
                // خزّن الرسالة محليًا
                repo.saveLocal(transaction)

                // حاول تبعتها مباشرة
                val sent = repo.sendToServer(transaction)

                if (sent) {
                    repo.markAsSynced(transaction.id)
                } else {
                    // لو فشل → شغّل WorkManager
                    WorkScheduler.scheduleSync(context)
                }
            }
        }
    }





}