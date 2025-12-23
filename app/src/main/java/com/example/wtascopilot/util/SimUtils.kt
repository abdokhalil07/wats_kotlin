package com.example.wtascopilot.util

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager

object SimUtils {

    // 1. Data Class لتخزين بيانات الشريحة بشكل منظم
    data class SimInfo(
        val phoneNumber: String,
        val carrierName: String,
        val slotIndex: Int,
        val subscriptionId: Int
    )

    // 2. دالة لجلب الشرائح وتحويلها للنوع SimInfo
    @SuppressLint("MissingPermission") // يجب التأكد من طلب الأذونات في الـ Activity
    fun getSimCards(context: Context): List<SimInfo> {
        val manager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        // الحصول على قائمة الشرائح النشطة
        val activeSims: List<SubscriptionInfo>? = manager.activeSubscriptionInfoList

        if (activeSims.isNullOrEmpty()) {
            return emptyList()
        }

        // تحويل كل SubscriptionInfo إلى SimInfo
        return activeSims.map { info ->
            SimInfo(
                // محاولة جلب الرقم، إذا كان null نضع نص توضيحي
                phoneNumber = if (!info.number.isNullOrEmpty()) info.number else "غير معروف",
                carrierName = info.carrierName?.toString() ?: "Unknown",
                slotIndex = info.simSlotIndex,
                subscriptionId = info.subscriptionId
            )
        }
    }
}