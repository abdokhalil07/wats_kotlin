package com.example.wtascopilot.domain.parser


import com.example.wtascopilot.data.modle.Transaction
import java.security.MessageDigest

class MessageParser {
    private val vodafoneParser = VodafoneParser()
    private val etisalatParser = EtisalatParser()
    private val orangeParser = OrangeParser()

    // تمرير الـ sender وتوليد الـ Hash
    fun parseMessage(sender: String?, message: String, subId: Int): Transaction? {
        val s = sender?.lowercase() ?: ""

        // توليد البصمة الفريدة للرسالة (Hash)
        val msgHash = generateMD5(s , message , subId)

        // التوجيه بناءً على اسم المرسل (الأولوية الأولى)
        val transaction = when {
            s.contains("vf-cash") -> vodafoneParser.parseVodafone(message, subId)
            s.contains("e& money") -> etisalatParser.parseEtisalat(message, subId)
            s.contains("orangecash") -> orangeParser.parseOrange(message, subId)
            else -> {
                when (detectWallet(message)) {
                    WalletType.VODAFONE_CASH -> vodafoneParser.parseVodafone(message, subId)
                    WalletType.ETISALAT_CASH -> etisalatParser.parseEtisalat(message, subId)
                    WalletType.ORANGE_MONEY -> orangeParser.parseOrange(message, subId)
                    else -> null
                }
            }
        }

        return transaction?.copy(messageHash = msgHash)
    }

    private fun generateMD5(sender: String?, message: String, subId: Int): String {
        val finalInput = sender+ message + subId.toString()
        val bytes = MessageDigest.getInstance("MD5").digest(finalInput.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun detectWallet(message: String): WalletType {
        return when {
            message.contains("فودافون", true) || message.contains("VF-Cash", true) || message.contains("لفودافون كاش", true) -> WalletType.VODAFONE_CASH
            message.contains("إي اندكاش", true) || message.contains("e&money", true) -> WalletType.ETISALAT_CASH
            message.contains("اورنچ", true) || message.contains("orange", true) -> WalletType.ORANGE_MONEY
            else -> WalletType.UNKNOWN
        }
    }
}

enum class WalletType { VODAFONE_CASH, ETISALAT_CASH, ORANGE_MONEY, UNKNOWN }