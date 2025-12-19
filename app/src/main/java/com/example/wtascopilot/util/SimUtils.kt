package com.example.wtascopilot.util

import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager

object SimUtils {

    fun getSimCards(context: Context): List<SubscriptionInfo> {
        val manager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        return manager.activeSubscriptionInfoList ?: emptyList()
    }

    fun getSimNumber(info: SubscriptionInfo): String {
        return info.number ?: "غير متوفر"
    }
}
