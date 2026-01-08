package com.example.wtascopilot


import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val orangeCashRecive = """
        تم إستلام عملية تحويل أموال بمبلغ 5.00 جنيه من Abdelrahman A Salem، رصيدك الحالي 11.43 جنية. رقم المعاملة 2669336091

        حول، اودع، اشحن، ادفع، اسحب واستلم فلوس من الخارج على اورنچ كاش وليك فرصة تكسب لحد 10 جرام دهب!
        كاشك دهب مع اورنچ كاش!
        للاشتراك في مسابقة الدهب اطلب #71#

        للتحويل
    """.trimIndent()

    val orangeCashTransfer = """
        عملية تحويل أموال ناجحة بمبلغ 5.00 جنيه، لرقم 01114888976, رسوم التحويل 0.00 جنيه،  رصيدك الحالي 6.43 جنيه . رقم العملية 2669542695

        حول، اودع، اشحن، ادفع، اسحب واستلم فلوس من الخارج على اورنچ كاش وليك فرصة تكسب لحد 10 جرام دهب!
        كاشك دهب مع اورنچ كاش!
        للاشتراك في مسابقة الدهب اطلب #71#

        للتحويل
    """.trimIndent()

    val orangeList = arrayListOf<String>(
        orangeCashRecive, orangeCashTransfer
    )

    @Test
    fun addition_isCorrect() {
        val parser = OrangeParser1()

        println(parser.parseOrange(orangeCashRecive, 5))

    }
}


data class Transaction1(
    val id: Int = 0,
    val transactionType: String,
    val amount: Double,
    val simNumber: String?,
    val fees: Double?,
    val senderNumber: String?,
    val senderName: String?,
    val transactionId: String,
    val dateTime: String,
    val balance: Double,
    val isSynced: Int = 0,
    val subId: Int,
    val messageHash: String
)


class OrangeParser1 {
    fun parseOrange(message: String, subId: Int): Transaction1? {
        val type = detectType(message) ?: return null
        return Transaction1(
            id = 0,
            transactionType = type,
            messageHash = "",
            dateTime = "",
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

