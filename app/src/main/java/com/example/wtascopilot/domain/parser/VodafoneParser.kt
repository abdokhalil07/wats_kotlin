package com.example.wtascopilot.domain.parser

import android.icu.text.SimpleDateFormat
import com.example.wtascopilot.data.modle.Transaction
import java.security.MessageDigest
import java.util.Date
import java.util.Locale

class VodafoneParser {
    fun parseVodafone(message: String): Transaction? {
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
            simNumber = null
        )
    }
    /* ================= Type ================= */
    private fun detectType(msg: String): String? = when {
        Regex("""تم\s+استلام|إستلام|Received""", RegexOption.IGNORE_CASE).containsMatchIn(msg) ->
            "Receive"

        Regex("""تم\s+تحويل|Transferred|has been transferred|sent|تحويل""",
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
                """تم\s+استلام\s+(?:مبلغ\s+)?(\d+(?:\.\d+)?)""",
                """إستلام مبلغ (\d+\.\d{2}) ج\.م""",
                """تم\s+(?:تحويل|سحب|دفع)\s+(\d+(?:\.\d+)?)""",
                """Received\s+EGP\s*(\d+(?:\.\d+)?)""",
                """Amount\s*:\s*(\d+(?:\.\d+)?)""",
                """(?:مبلغ|قيمة)\s+(\d+(?:\.\d+)?)""",
                """EGP\s*(\d+(?:\.\d+)?)""",



                )
        )?.toDouble() ?: 0.0
    }
    /* ================= Fees ================= */
    private fun extractFees(msg: String): Double? =
        matchFirst(
            msg,
            listOf(
                """مصاريف\s+(?:الخدمة)?\s*(\d+(?:\.\d+)?)""",
                """رسوم\s*(\d+(?:\.\d+)?)""",
                """رسوم التحويل\s*(\d+(?:\.\d+)?)""",
                """رسوم التحويل \s*(\d+(?:\.\d+)?)""",
                """Fees:\s*EGP\s*(\d+(?:\.\d+)?)"""
            )
        )?.toDouble()
    /* ================= Phone ================= */
    private fun extractSenderNumber(msg: String): String? =
        matchFirst(
            msg,
            listOf(
                """(?:من|إلى|الى|لرقم|ل|from|to)\s+(?:رقم\s+)?(01\d{9})""",
                """(?:من|إلى|ل|from|to)\s+(?:رقم\s+)?(002\d{9})"""
            )
        )
    /* ================= Name ================= */
    private fun extractSenderName(msg: String): String? {
        return matchFirst(
            msg,
            listOf(

                """المسجل بإسم\s+([A-Za-z\u0600-\u06FF\s]+?)(?:[\.;]|\s+رصيد)""",
                """المسجل باسم\s*(.*?)\s*بنجاح\.""",
                """من\s+([^،]*)\s*،""",
                // إنجليزي Instapay
                """from\s+([A-Za-z\s]+)""",
            )
        )?.trim()
    }
    /* ================= Ref ================= */
    private fun extractTransactionId(msg: String): String =
        matchFirst(
            msg,
            listOf(
                """رقم\s+العملية|المعاملة\s*(\d+)""",
                """رقم العملية \s*(\d+)""",
                """Ref:\s*(\d+)""",
                """Transaction ID\s*(\d+)"""

            )
        ) ?: "Unknown"
    /* ================= Balance ================= */
    private fun extractBalance(msg: String): Double =
        matchFirst(
            msg,
            listOf(
                """رصيد.*?(\d+(?:\.\d+)?)""",
                """Balance.*?(\d+(?:\.\d+)?)""",
                """Available Balance:\s*(\d+(?:\.\d+)?)"""
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