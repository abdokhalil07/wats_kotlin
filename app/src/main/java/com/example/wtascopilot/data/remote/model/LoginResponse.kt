package com.example.wtascopilot.data.remote.model

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    // حذفنا success لأنها غير موجودة

    @SerializedName("account_id")
    val accountId: Int?,  // علامة الاستفهام مهمة جداً تحسباً لو رجع null

    @SerializedName("user_name")
    val userName: String?,

    @SerializedName("password_")
    val password: String?,

    @SerializedName("max_number")
    val maxNumber: Int?
)