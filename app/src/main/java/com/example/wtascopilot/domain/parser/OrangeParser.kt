package com.example.wtascopilot.domain.parser

import android.icu.text.SimpleDateFormat
import com.example.wtascopilot.data.modle.Transaction
import java.util.Date
import java.util.Locale


class OrangeParser {
    fun parseOrange(message: String, subId: Int): Transaction? {
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
            body = message,
            simNumber = null
        )
    }
    /* ================= Type ================= */
    private fun detectType(msg: String): String? = when {
        Regex("""تم إستلام عملية تحويل أموال""", RegexOption.IGNORE_CASE).containsMatchIn(msg) ->
            "Receive"

        Regex("""عملية تحويل أموال ناجحة""",
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
                """أموال بمبلغ\s(\d+(?:[.,]\d+)?)""",
                """ناجحة بمبلغ\s(\d+(?:[.,]\d+)?)"""

                )
        )?.toDouble() ?: 0.0
    }
    /* ================= Fees ================= */
    private fun extractFees(msg: String): Double? =
        matchFirst(
            msg,
            listOf(
                """رسوم التحويل\s(\d+(?:[.,]\d+)?)\sجنيه،""",

            )
        )?.toDouble()
    /* ================= Phone ================= */
    private fun extractSenderNumber(msg: String): String? =
        matchFirst(
            msg,
            listOf(
                """لرقم\s(\d+(?:[.,]\d+)?),"""
            )
        )
    /* ================= Name ================= */
    private fun extractSenderName(msg: String): String? {
        return matchFirst(
            msg,
            listOf(
                """\s*من\s*(.*?)\s*،"""
            )
        )?.trim()
    }
    /* ================= Ref ================= */
    private fun extractTransactionId(msg: String): String =
        matchFirst(
            msg,
            listOf(
                """رقم المعاملة\s*(\d+)""",
                """رقم العملية\s*(\d+)"""
            )
        ) ?: "Unknown"
    /* ================= Balance ================= */
    private fun extractBalance(msg: String): Double =
        matchFirst(
            msg,
            listOf(
                """رصيدك الحالي\s(\d+(?:[.,]\d+)?)\s*""",
                """رصيدك الحالي\s(\d+(?:[.,]\d+)?)\s*جنيه\s."""
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