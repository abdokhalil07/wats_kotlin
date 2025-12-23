package com.example.wtascopilot.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("user_name") // لازم يطابق user_name في السيرفر
    val username: String,

    @SerializedName("password_") // لاحظ الشرطة السفلية حسب كود البايثون
    val password: String
)
