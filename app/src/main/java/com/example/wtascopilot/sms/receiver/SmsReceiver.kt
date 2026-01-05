package com.example.wtascopilot.sms.receiver


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.wtascopilot.foreground.SmsMonitorService

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        // ✅ الطريقة الصحيحة لجلب subscriptionId
        val subId = intent.extras?.getInt(
            "android.telephony.extra.SUBSCRIPTION_INDEX",
            -1
        ) ?: -1

        val sender = messages[0].displayOriginatingAddress ?: "Unknown"

        val body = buildString {
            for (sms in messages) {
                append(sms.messageBody)
            }
        }

        Log.d("SmsReceiver", "SMS from=$sender subId=$subId")

        val serviceIntent = Intent(context, SmsMonitorService::class.java).apply {
            action = SmsMonitorService.ACTION_PUSH_SMS
            putExtra("sender", sender)
            putExtra("body", body)
            putExtra("slot", subId)
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "فشل تشغيل الخدمة", e)
        }
    }
}
