package com.example.wtascopilot.domain.parser


import com.example.wtascopilot.data.modle.Transaction
import java.util.regex.Pattern

class MessageParser {

    fun parseMessage(message: String): Transaction? {
        val type = detectType(message)
        if (type == "Unknown") return null

        return try {
            Transaction(
                transactionType = type,
                amount = extractAmount(message),
                fees = extractFees(message),
                senderNumber = extractSenderNumber(message),
                senderName = extractSenderName(message),
                transactionId = extractTransactionId(message),
                dateTime = extractDateTime(message),
                balance = extractBalance(message),
                simNumber = null // سيتم تعيينه لاحقاً عند الإرسال
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun detectType(message: String): String = when {
        // أنماط اللغة العربية
        message.contains("تم دفع") || message.contains("خدمة لفودافون") -> "Payment"
        message.contains("تم سحب") -> "Withdrawal"
        message.contains("تم تحويل") || message.contains("تحويل مبلغ") -> "Transfer"
        message.contains("تم استلام") -> "Receive"
        // أنماط اللغة الإنجليزية
        message.contains("Received", ignoreCase = true) -> "Receive"
        message.contains("Transfer", ignoreCase = true) -> "Transfer"
        message.contains("Payment", ignoreCase = true) -> "Payment"
        else -> "Unknown"
    }

    private fun extractAmount(message: String): Double {
        // قائمة الأنماط المحتملة للمبلغ مرتبة حسب الأولوية
        val patterns = listOf(
            // حالة: تم تحويل 240.0 (بدون كلمة مبلغ)
            """تم\s+(?:تحويل|سحب|دفع)\s+(\d+(?:\.\d+)?)""",
            // حالة: تم استلام مبلغ 400.00
            """(?:مبلغ|قيمة)\s+(\d+(?:\.\d+)?)""",
            // حالة الإنجليزي: Received EGP10
            """EGP\s*(\d+(?:\.\d+)?)""",
            """Amount\s*:\s*(\d+(?:\.\d+)?)"""
        )
        return matchFirst(message, patterns)?.toDouble() ?: 0.0
    }

    private fun extractFees(message: String): Double? {
        val patterns = listOf(
            """مصاريف الخدمة\s+(\d+(?:\.\d+)?)""", //
            """رسوم\s+(?:خدمة|الخدمة)?\s*(\d+(?:\.\d+)?)""" // [cite: 22]
        )
        return matchFirst(message, patterns)?.toDouble()
    }

    private fun extractSenderNumber(message: String): String? {
        val patterns = listOf(
            // حالة العربي: من رقم ... أو لرقم ... [cite: 18, 27]
            """(?:من|ل)??رقم\s+(\d{11})""",
            // حالة الإنجليزي: from 002...
            """from\s+(\d+)"""
        )
        return matchFirst(message, patterns)
    }

    private fun extractSenderName(message: String): String? {
        // الاسم غالباً يأتي بعد "المسجل بإسم" وينتهي عند نقطة أو بداية جملة الرصيد
        // [cite: 18, 31]
        val regex = Regex("""المسجل بإسم\s+([A-Za-z\u0600-\u06FF\s]+?)(?:[\.;]|\s+رصيد)""")
        return regex.find(message)?.groups?.get(1)?.value?.trim()
    }

    private fun extractTransactionId(message: String): String {
        val patterns = listOf(
            // حالة الإنجليزي: Ref: ...
            """Ref:\s*(\d+)""",
            // حالة العربي: رقم العملية ... (قد يسبقها فاصلة منقوطة أو مسافة) [cite: 19, 25]
            """رقم العملية[^\d]*(\d+)"""
        )
        return matchFirst(message, patterns) ?: "Unknown"
    }

    private fun extractDateTime(message: String): String {
        val patterns = listOf(
            // حالة الإنجليزي في البداية: Dec 29, 2025...
            """^([A-Za-z]{3}\s+\d{1,2},\s+\d{4}\s+\d{1,2}:\d{2}:\d{2}\s+[AP]M)""",
            // حالة العربي: تاريخ العملية 24-09-25... [cite: 19]
            """تاريخ العملية\s+([0-9\-/]+\s+[0-9:]+)"""
        )

        // البحث باستخدام Regex العادي لأن ^ (بداية السطر) تتطلب تعاملاً خاصاً
        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.MULTILINE)
            val match = regex.find(message)
            if (match != null) {
                return match.groups[1]?.value?.trim() ?: continue
            }
        }
        return "Unknown"
    }

    private fun extractBalance(message: String): Double {
        val patterns = listOf(
            // حالة الإنجليزي: Available Balance: 73.58
            """Available Balance:\s*(\d+(?:\.\d+)?)""",
            // حالة العربي: رصيد محفظتك الحالي أو رصيد حسابك [cite: 18, 22, 24]
            """رصيد(?:ك|.*الحالي)\s+(\d+(?:\.\d+)?)"""
        )
        return matchFirst(message, patterns)?.toDouble() ?: 0.0
    }

    // دالة مساعدة لتجربة أكثر من نمط Regex وإرجاع أول نتيجة صحيحة
    private fun matchFirst(text: String, patterns: List<String>): String? {
        for (pattern in patterns) {
            val regex = Regex(pattern, setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
            val match = regex.find(text)
            if (match != null) {
                // نحاول استرجاع المجموعة رقم 1 (القيمة المطلوبة)
                return match.groups[1]?.value
            }
        }
        return null
    }
}