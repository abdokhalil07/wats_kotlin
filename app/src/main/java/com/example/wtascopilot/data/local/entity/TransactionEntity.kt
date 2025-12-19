package com.example.wtascopilot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val transactionType: String,
    val amount: Double,
    val fees: Double?,
    val senderNumber: String?,
    val senderName: String?,
    val transactionId: String,
    val dateTime: String,
    val balance: Double,
    val isSynced: Boolean = false
)
