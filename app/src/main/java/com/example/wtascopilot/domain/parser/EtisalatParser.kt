package com.example.wtascopilot.domain.parser

import android.icu.text.SimpleDateFormat
import com.example.wtascopilot.data.modle.Transaction
import java.util.Date
import java.util.Locale

class EtisalatParser {

    fun parseEtisalat(message: String): Transaction? {
        val type = detectType(message) ?: return null

        return Transaction(
            id = 0,
            transactionType = type,
            messageHash = "", // سيتم ملؤها في الكلاس الرئيسي
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
        Regex("""تم\s+استلام|Received""", RegexOption.IGNORE_CASE).containsMatchIn(msg) ->
            "Receive"

        Regex("""تم\s+تحويل|Transferred|has been transferred|sent""",
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
                """Fees:\s*EGP\s*(\d+(?:\.\d+)?)"""
            )
        )?.toDouble()

    /* ================= Phone ================= */

    private fun extractSenderNumber(msg: String): String? =
        matchFirst(
            msg,
            listOf(
                "من\\s+([A-Za-z\\u0600-\\u06FF\\s]+)",
                """(?:من|إلى|ل|from|to)\s+(?:رقم\s+)?(01\d{9})"""
            )
        )

    /* ================= Name ================= */

    private fun extractSenderName(msg: String): String? {
        return matchFirst(
            msg,
            listOf(
                // عربي: الاسم يأتي بعد "بإسم" ويتوقف عند الفاصلة أو النقطة أو كلمة رصيد
                """المسجل بإسم\s+([A-Za-z\u0600-\u06FF\s]+?)(?:[\.;]|\s+رصيد)""",

                // إنجليزي Instapay
                """from\s+([A-Za-z\s]+)""",

                // حالات التحويل لـ
                """to\s+([A-Za-z\u0600-\u06FF\s]+?)(?:[\.;]|\s+Amount)"""
            )
        )?.trim()
    }

    /* ================= Ref ================= */

    private fun extractTransactionId(msg: String): String =
        matchFirst(
            msg,
            listOf(
                """رقم\s+العملية\s*(\d+)""",
                """Transaction ID\s*(\d+)""",
                """Ref:\s*(\d+)"""
            )
        ) ?: "Unknown"

    /* ================= Date ================= */

    private fun extractDateTime(msg: String): String =
        matchFirst(
            msg,
            listOf(
                """\d{2}-\d{2}-\d{4}\s+\d{2}:\d{2}""",
                """\d{2}/\d{2}/\d{4}\s+\d{2}:\d{2}""",
                """[A-Za-z]{3}\s+\d{1,2},\s+\d{4}.*?(AM|PM)"""
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