package com.example.wtascopilot.domain.parser

import com.example.wtascopilot.data.modle.Transaction

class MessageParser {

    fun parseMessage(message: String): Transaction? {
        return try {
            Transaction(
                transactionType = detectType(message),
                amount = extractAmount(message),
                fees = extractFees(message),
                senderNumber = extractSenderNumber(message),
                senderName = extractSenderName(message),
                transactionId = extractTransactionId(message),
                dateTime = extractDateTime(message),
                balance = extractBalance(message)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun detectType(message: String): String = when {
        message.contains("تم دفع") -> "Payment"
        message.contains("تم سحب") -> "Withdrawal"
        message.contains("تم تحويل") -> "Transfer"
        message.contains("تم استلام") -> "Receive"
        else -> "Unknown"
    }

    private fun extractAmount(message: String): Double {
        val regex = Regex("""\d+(\.\d+)?""")
        val match = regex.find(message)
        return match?.value?.toDouble() ?: 0.0
    }

    private fun extractFees(message: String): Double? {
        val regex = Regex("""مصاريف الخدمة\s+(\d+(\.\d+)?)""")
        return regex.find(message)?.groups?.get(1)?.value?.toDouble()
    }

    private fun extractSenderNumber(message: String): String? {
        val regex = Regex("""رقم\s+(\d{11})""")
        return regex.find(message)?.groups?.get(1)?.value
    }

    private fun extractSenderName(message: String): String? {
        val regex = Regex("""المسجل بإسم\s+([A-Za-z\s]+)""")
        return regex.find(message)?.groups?.get(1)?.value
    }

    private fun extractTransactionId(message: String): String {
        val regex = Regex("""رقم العملية[^\d]*(\d+)""")
        return regex.find(message)?.groups?.get(1)?.value ?: "Unknown"
    }

    private fun extractDateTime(message: String): String {
        val regex = Regex("""تاريخ العملية\s+([0-9\-]+\s+[0-9:]+)""")
        return regex.find(message)?.groups?.get(1)?.value ?: "Unknown"
    }

    private fun extractBalance(message: String): Double {
        val regex = Regex("""رصيد(?:ك| محفظتك الحالي)\s+([0-9]+\.\d+)""")
        return regex.find(message)?.groups?.get(1)?.value?.toDouble() ?: 0.0
    }
}
