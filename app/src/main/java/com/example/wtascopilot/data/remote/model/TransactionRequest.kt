package com.example.wtascopilot.data.remote.model
import com.google.gson.annotations.SerializedName
data class TransactionRequest(
    @SerializedName("account_id")
    val transactionType: String,
    @SerializedName("account_id")
    val amount: Double,
    @SerializedName("account_id")
    val fees: Double?,
    @SerializedName("account_id")
    val senderNumber: String?,
    @SerializedName("account_id")
    val senderName: String?,
    @SerializedName("account_id")
    val transactionId: String,
    @SerializedName("account_id")
    val dateTime: String,
    @SerializedName("account_id")
    val balance: Double,
    @SerializedName("account_id")
    val simNumber: String? // هنضيفها لاحقًا من SIM Screen
)
