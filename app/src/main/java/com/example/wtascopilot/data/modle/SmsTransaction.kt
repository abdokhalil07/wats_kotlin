package com.example.wtascopilot.data.modle

data class SmsTransaction(
    val id: Int=0,
    val sender: String?,
    val body: String?,
    val timestamp: Long?
)
