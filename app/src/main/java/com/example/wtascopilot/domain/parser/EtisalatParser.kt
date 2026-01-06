package com.example.wtascopilot.domain.parser

import android.icu.text.SimpleDateFormat
import com.example.wtascopilot.data.modle.Transaction
import java.util.Date
import java.util.Locale

class EtisalatParser {
    fun parseEtisalat(message: String, subId: Int): Transaction? {
        val type = detectType(message) ?: return null

        return Transaction(
            id = 0,
            transactionType = type,
            messageHash = "",
            dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            amount = extractAmount(message, type),
            fees = extractFees(message),
            senderNumber = extractSenderNumber(message),
            senderName = extractSenderName(message),
            transactionId = extractTransactionId(message),
            balance = extractBalance(message),
            subId = subId,
            simNumber = null
        )
    }
    /* ================= Type ================= */
    private fun detectType(msg: String): String? = when {
        Regex("""تم\s+استلام|إستلام مبلغ |Received""", RegexOption.IGNORE_CASE).containsMatchIn(msg) ->
            "Receive"

        Regex("""تم\s+تحويل""",
            RegexOption.IGNORE_CASE).containsMatchIn(msg) ->
            "Transfer"

        Regex("""تم\s+دفع|Payment|paid|purchase|شراء""",
            RegexOption.IGNORE_CASE).containsMatchIn(msg) ->
            "Payment"

        Regex("""تم\s+سحب|Withdrawal|withdrawn""",
            RegexOption.IGNORE_CASE).containsMatchIn(msg) ->
            "Withdrawal"

        else -> null
    }
    /* ================= Amount ================= */
    private fun extractAmount(msg: String, type: String): Double {
        return matchFirst(
            msg,
            listOf(
                """تم إستلام مبلغ\s(\d+(?:[.,]\d+)?)\sج.م""",
                """تم تحويل مبلغ\s(\d+(?:[.,]\d+)?)\sج.م"""
                )
        )?.toDouble() ?: 0.0
    }
    /* ================= Fees ================= */
    private fun extractFees(msg: String): Double? =
        matchFirst(
            msg,
            listOf(
                """رسوم التحويل\s(\d+(?:[.,]\d+)?)\s"""
            )
        )?.toDouble()
    /* ================= Phone ================= */
    private fun extractSenderNumber(msg: String): String? =
        matchFirst(
            msg,
            listOf(
                """من رقم\s(\d+(?:[.,]\d+)?)\s""",
                """الى رقم\s(\d+(?:[.,]\d+)?)\s"""
            )
        )
    /* ================= Name ================= */
    private fun extractSenderName(msg: String): String? {
        return matchFirst(
            msg,
            listOf(
                """المسجل باسم\s*(.*?)\s*\s"""
            )
        )?.trim()
    }
    /* ================= Ref ================= */
    private fun extractTransactionId(msg: String): String =
        matchFirst(
            msg,
            listOf(
            )
        ) ?: "Unknown"
    /* ================= Balance ================= */
    private fun extractBalance(msg: String): Double =
        matchFirst(
            msg,
            listOf(
                """رصيد محفظتك الحالى\s(\d+(?:[.,]\d+)?)\sج.م""",
                """رصيد محفظتك الحالى\s(\d+(?:[.,]\d+)?)"""
            )
        )?.toDouble() ?: 0.0
    /* ================= Helper ================= */
    private fun matchFirst(text: String, patterns: List<String>): String? {
        for (p in patterns) {
            val r = Regex(p, RegexOption.IGNORE_CASE)
            r.find(text)?.groups?.get(1)?.value?.let { return it }
        }
        return null
    }

}