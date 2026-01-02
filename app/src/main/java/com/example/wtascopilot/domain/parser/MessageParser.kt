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
        val msgHash = generateMD5(s + message , subId)

        // التوجيه بناءً على اسم المرسل (الأولوية الأولى)
        val transaction = when {
            s.contains("vf-cash") -> vodafoneParser.parseVodafone(message)
            s.contains("e& money") || s.contains("etisalat") -> etisalatParser.parseEtisalat(message)
            s.contains("orange") -> orangeParser.parseOrange(message)
            else -> {
                // محاولة التخمين من النص إذا كان المرسل غير معروف (الأولوية الثانية)
                when (detectWallet(message)) {
                    WalletType.VODAFONE_CASH -> vodafoneParser.parseVodafone(message)
                    WalletType.ETISALAT_CASH -> etisalatParser.parseEtisalat(message)
                    WalletType.ORANGE_MONEY -> orangeParser.parseOrange(message)
                    else -> null
                }
            }
        }

        return transaction?.copy(messageHash = msgHash)
    }

    private fun generateMD5(input: String, subId: Int): String {
        val finalInput = input + subId.toString()
        val bytes = MessageDigest.getInstance("MD5").digest(finalInput.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun detectWallet(message: String): WalletType {
        return when {
            message.contains("فودافون كاش", true) || message.contains("VF-Cash", true) -> WalletType.VODAFONE_CASH
            message.contains("اتصالات كاش", true) || message.contains("e& money", true) -> WalletType.ETISALAT_CASH
            message.contains("أورانج كاش", true) || message.contains("Orange Cash", true) -> WalletType.ORANGE_MONEY
            else -> WalletType.UNKNOWN
        }
    }
}

enum class WalletType { VODAFONE_CASH, ETISALAT_CASH, ORANGE_MONEY, UNKNOWN }