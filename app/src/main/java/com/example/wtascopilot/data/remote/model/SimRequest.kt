package com.example.wtascopilot.data.remote.model

import com.google.gson.annotations.SerializedName

// 1. موديل لفحص حالة الشريحة أو تسجيلها
data class SimRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
)


data class SimStatusResponse(
    @SerializedName("account_id")
    val accountId: Boolean,

    @SerializedName("is_registered")
    val isRegistered: String?,

    @SerializedName("state")
    val state: String?
)

data class SimAddRequest(
    @SerializedName("account_id")
    val accountId: Int,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("phone_name")
    val phoneName: String?,

    @SerializedName("carrierName")
    val carrierName: String,

    @SerializedName("slotIndex")
    val slotIndex: Int?,
)

data class SimAddResponse(
    @SerializedName("account_id")
    val accountId: Boolean,

    @SerializedName("is_registered")
    val isRegistered: String?,

    @SerializedName("state")
    val state: String?
)