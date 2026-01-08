package com.example.wtascopilot.data.remote.model
import com.google.gson.annotations.SerializedName
data class TransactionRequest(
    @SerializedName("transaction_Type")
    val transactionType: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("fees")
    val fees: Double?,
    @SerializedName("sender_Number")
    val senderNumber: String?,
    @SerializedName("sender_Name")
    val senderName: String?,
    @SerializedName("transaction_Id")
    val transactionId: String,
    @SerializedName("dateTime")
    val dateTime: String,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("sim_Number")
    val simNumber: String?,
    @SerializedName("body")
    val body: String?
)
