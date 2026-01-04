package com.example.wtascopilot.foreground


import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.wtascopilot.MainActivity
import com.example.wtascopilot.R

class SmsMonitorService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "sms_monitor_channel")
            .setContentTitle("مراقب العمليات يعمل")
            .setContentText("التطبيق يراقب رسائل التحويل الآن لضمان المزامنة.")
            .setSmallIcon(android.R.drawable.stat_notify_chat) // غيره لأيقونة تطبيقك
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
            .build()

        // السطر ده هو اللي بيحولها لـ Foreground
        startForeground(1, notification)

        return START_STICKY // عشان لو النظام قفلها يرجع يشغلها تاني لوحده
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sms_monitor_channel",
                "SMS Monitor Channel",
                NotificationManager.IMPORTANCE_LOW // Low عشان ميزعجش المستخدم بصوت كل شوية
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}