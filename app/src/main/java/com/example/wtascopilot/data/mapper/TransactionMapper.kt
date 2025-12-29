package com.example.wtascopilot.data.mapper

import com.example.wtascopilot.data.local.entity.TransactionEntity
import com.example.wtascopilot.data.modle.Transaction
import com.example.wtascopilot.data.remote.model.TransactionRequest

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        transactionType = this.transactionType,
        amount = this.amount,
        fees = this.fees,
        senderNumber = this.senderNumber,
        senderName = this.senderName,
        transactionId = this.transactionId,
        dateTime = this.dateTime,
        balance = this.balance,
        isSynced = this.isSynced
    )
}

fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = this.id,
        transactionType = this.transactionType,
        amount = this.amount,
        // بما أن الـ Entity لا تخزن رقم الشريحة، نعيده كـ null عند القراءة من الـ DB
        // سيتم تزويده لاحقاً عند الإرسال عبر toRequest
        simNumber = null,
        fees = this.fees,
        senderNumber = this.senderNumber,
        senderName = this.senderName,
        transactionId = this.transactionId,
        dateTime = this.dateTime,
        balance = this.balance,
        isSynced = this.isSynced
    )
}

// هذه الدالة ممتازة وتخدم المنطق الخاص بك لحقن رقم الشريحة قبل الإرسال
fun Transaction.toRequest(simNumber: String?): TransactionRequest {
    return TransactionRequest(
        transactionType = transactionType,
        amount = amount,
        fees = fees,
        senderNumber = senderNumber,
        senderName = senderName,
        transactionId = transactionId,
        dateTime = dateTime,
        balance = balance,
        simNumber = simNumber // هنا يتم ربط رقم الشريحة
    )
}