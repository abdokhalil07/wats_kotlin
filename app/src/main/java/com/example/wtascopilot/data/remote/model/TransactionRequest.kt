package com.example.wtascopilot.data.remote.model

data class TransactionRequest(
    val transactionType: String,
    val amount: Double,
    val fees: Double?,
    val senderNumber: String?,
    val senderName: String?,
    val transactionId: String,
    val dateTime: String,
    val balance: Double,
    val simNumber: String? // هنضيفها لاحقًا من SIM Screen
)
