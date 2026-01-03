package com.example.wtascopilot
import com.example.wtascopilot.domain.parser.MessageParser
import com.example.wtascopilot.data.modle.Transaction
import com.example.wtascopilot.domain.parser.EtisalatParser
import com.example.wtascopilot.domain.parser.OrangeParser
import com.example.wtascopilot.domain.parser.VodafoneParser

fun main(){
    val vodafoneCashRecive = """
        تم استلام مبلغ 400.00 جنيه من رقم 01114055585؛ المسجل بإسم Moustafa A Salem. رصيدك الحالي 470.17 جنيه. تاريخ العملية ‎24-09-25 01:29‎ رقم العملية 014248068231 .دلوقتي تقدر تسحب من محفظتك برسوم ٥ جنيه بس بدل ١٪! كلم ‎*9*999# واشترك علشان تسحب لحد ٥٠٠٠ جنيه شهريًا من محفظتك برسوم ثابتة وأوفر! تابع كل مصروفاتك من تاريخ المعاملات على أبلكيشن أنا فودافون http://vf.eg/vfcash
    """.trimIndent()

    val vodafoneCashTransfare = """
        تم تحويل 240.0 جنيه لرقم 01061949119 مصاريف الخدمة 1.0 جنيه رصيد حسابك فى فودافون كاش الحالي 60741.6. دلوقتي تقدر تسحب من محفظتك برسوم ٥ جنيه بس بدل ١٪! كلم ‎*9*999# واشترك علشان تسحب لحد ٥٠٠٠ جنيه شهريًا من محفظتك برسوم ثابتة وأوفر! تابع كل مصروفاتك من تاريخ المعاملات على أبلكيشن أنا فودافون http://vf.eg/vfcash
    """.trimIndent()

    val vodafoneCashWithdraw = """
        تم سحب 5000.00 جنية من محفظة فودافون كاش. رصيد حسابك الحالي 141.60 جنيه. تاريخ العملية ‎14-09-25 00:59 رقم العملية; 014030689271. دلوقتي تقدر تسحب من محفظتك برسوم ٥ جنيه بس بدل ١٪! كلم ‎*9*999# واشترك علشان تسحب لحد ٥٠٠٠ جنيه شهريًا من محفظتك برسوم ثابتة وأوفر!
    """.trimIndent()

    val vodafoneCashPay = """
        تم دفع مبلغ 1.99 جنيه رسوم خدمة لفودافون كاش. رصيد محفظتك الحالي 468.18 جنيه. رقم العملية 014248503034 تاريخ العملية ‎24-09-25 02:12.  استخدم خدمات فودافون كاش وتابع كل مصروفاتك من تاريخ المعاملات على أبلكيشن أنا فودافون http://vf.eg/vfcash
    """.trimIndent()

    val etisalateRecive  = """
        تم إستلام مبلغ 5.00 ج.م من رقم 01007300937 المسجل باسمAbdelrahman A Salem   بنجاح. رصيد محفظتك الحالى 5.00 ج.م. لتقييم خدمات محفظة  e& money إطلب #14*777*
    """.trimIndent()


    val etisalateTransfare  = """
        إي اندكاش بقت e&money 
        تم تحويل مبلغ 4.00 ج.م الى رقم 01007300937 بنجاح. 
        رسوم التحويل 1.00  جنيه.
        رصيد محفظتك الحالى 1.00. لمعرفة رصيد محفظة e& money اطلب #4*777* مع e& money دلوقتي السحب بـ ٥ ج لحد ٥٠٠٠ ج والتحويل بـ ١ ج لأي محفظة وعندك فرصة تكسب موتوسيكل أو مليون جنيه  وكمان هتكسب كارت خصومات طبية ادخل على اللينك و استلم هديتك http://spr.ly/Spin_The_Wheel
    """.trimIndent()

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

    val instaPayTransfare = """
        Dec 29, 2025 3:26:48 PM: Received EGP10 from 00201114888976 to Mobile Account Number 5554. Ref: 016521644988 Available Balance: 73.58
    """.trimIndent()

    val vodafonecashList = arrayListOf<String>(
        vodafoneCashRecive, vodafoneCashTransfare, vodafoneCashWithdraw, vodafoneCashPay
    )

    val etisalateList = arrayListOf<String>(
        etisalateRecive, etisalateTransfare
    )

    val orangeList = arrayListOf<String>(
        orangeCashRecive, orangeCashTransfer
    )

    val instaList = arrayListOf<String>(
        instaPayTransfare
    )

    /*val  paservodafone = VodafoneParser()
    val  paseretisalate = EtisalatParser()
    val  paserOrange = OrangeParser()

    for (i in vodafonecashList){
        println(paservodafone.parseVodafone(i))
    }*/




}


