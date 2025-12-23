package com.example.wtascopilot.data.remote.model

import com.google.gson.annotations.SerializedName

// 1. موديل لفحص حالة الشريحة أو تسجيلها
data class SimRequest(
    @SerializedName("account_id")
    val accountId: Int,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("carrier_name") // اسم الشبكة (Vodafone, etc)
    val carrierName: String? = null,

    @SerializedName("slot_index") // رقم المكان (0 او 1)
    val slotIndex: Int? = 0
)

// 2. موديل الرد (هل هي مسجلة؟)
data class SimStatusResponse(
    @SerializedName("is_registered")
    val isRegistered: Boolean,

    @SerializedName("message")
    val message: String?
)