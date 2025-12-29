package com.example.wtascopilot.data.remote.model
import com.google.gson.annotations.SerializedName
data class TransactionRequest(
    @SerializedName("transactionType")
    val transactionType: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("fees")
    val fees: Double?,
    @SerializedName("senderNumber")
    val senderNumber: String?,
    @SerializedName("senderName")
    val senderName: String?,
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("dateTime")
    val dateTime: String,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("simNumber")
    val simNumber: String? // هنضيفها لاحقًا من SIM Screen
)
