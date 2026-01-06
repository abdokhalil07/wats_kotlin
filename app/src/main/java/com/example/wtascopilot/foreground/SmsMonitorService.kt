package com.example.wtascopilot.foreground


import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.wtascopilot.data.local.SimStorage
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import com.example.wtascopilot.domain.parser.MessageParser
import com.example.wtascopilot.data.work.WorkScheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import android.util.Log
import com.example.wtascopilot.data.modle.SmsTransaction

class SmsMonitorService : Service() {

    companion object {
        const val CHANNEL_ID = "sms_monitor_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_PUSH_SMS = "PUSH_SMS"
    }

    private val parser = MessageParser()

    // Scope خاص بالخدمة
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Channel محدود عشان الأمان
    private val smsChannel = Channel<SmsData>(capacity = Channel.BUFFERED)

    private lateinit var repository: TransactionRepositoryImpl

    override fun onCreate() {
        super.onCreate()

        // ⚠️ مهم جدًا: foreground أولًا
        startMyForeground()

        repository = TransactionRepositoryImpl(applicationContext)

        startSmsProcessor()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == ACTION_PUSH_SMS) {
            val sender = intent.getStringExtra("sender").orEmpty()
            val body = intent.getStringExtra("body").orEmpty()
            val slotId = intent.getIntExtra("slot", -1)


            serviceScope.launch {
                smsChannel.send(SmsData(sender, body, slotId))
            }
        }

        return START_STICKY
    }

    private fun startSmsProcessor() {
        serviceScope.launch {
            for (sms in smsChannel) {
                processSms(sms)
            }
        }
    }

    private suspend fun processSms(sms: SmsData) {
        try {
            // 1. ✅ حفظ الرسالة فوراً في جدول الـ Raw (للـ Logger)
            // تأكد إن عندك دالة في الـ repository اسمها insertRawSms أو ما شابه
            val rawEntity = SmsTransaction(
                sender = sms.sender,
                body = sms.body,
                timestamp = System.currentTimeMillis()
            )
            repository.insertSms(rawEntity)

            Log.d("SmsService", "تم حفظ الرسالة في الـ Log: ${sms.sender}")

            // 2. محاولة جلب رقم الشريحة (Dual SIM Support)
            val simNumber = SimStorage.getPhoneNumberForSub(applicationContext, sms.slotId)

            if (simNumber == null) {
                Log.d("SmsService", "تجاهل البارسر: الشريحة ${sms.slotId} غير مسجلة")
                return
            }

            // 3. محاولة الـ Parsing
            val transaction = parser.parseMessage(sms.sender, sms.body, sms.slotId)
            if (transaction != null) {
                val finalTx = transaction.copy(simNumber = simNumber, subId = sms.slotId)
                repository.saveLocal(finalTx)
                WorkScheduler.scheduleSync(applicationContext)
            }

        } catch (e: Exception) {
            Log.e("SmsService", "خطأ في معالجة SMS", e)
        }
    }


    private fun startMyForeground() {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("مراقبة الرسائل")
            .setContentText("يتم تحليل الرسائل الواردة في الخلفية")
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SMS Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        smsChannel.close()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

data class SmsData(
    val sender: String,
    val body: String,
    val slotId: Int
)
