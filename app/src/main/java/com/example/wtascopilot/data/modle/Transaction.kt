package com.example.wtascopilot.data.modle

data class Transaction(
    val id: Int = 0,
    val transactionType: String,
    val amount: Double,
    val simNumber: String?,
    val fees: Double?,
    val senderNumber: String?,
    val senderName: String?,
    val transactionId: String,
    val dateTime: String,
    val balance: Double,
    val isSynced: Int = 0,
    val messageHash: String
)