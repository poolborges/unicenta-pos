package com.openbravo.pos.payment

import uk.co.pos_apps.PosApps;
import uk.co.pos_apps.payment.dejavoo.DejavooProcessor;

class PaymentGatewayDejavoo :PaymentGateway {

    val PAYMENT_PROCESSOR = "Dejavoo"

    override fun execute(payinfo: PaymentInfoMagcard?) {
        DejavooProcessor.paymentComplete = false

        var timer = 0
        val timeout = 120

        PosApps.initPayment(PAYMENT_PROCESSOR, payinfo?.total)

        while (!DejavooProcessor.paymentComplete) {
            Thread.sleep(1000)
            timer += 1
            if (timer > timeout) break
        }

        if (DejavooProcessor.response == null) {
            payinfo?.paymentError("Transaction Error! Please try again", "No Response")
        }
        else if (DejavooProcessor.response.success == "0"){
            payinfo?.cardName = DejavooProcessor.response.cardType
            payinfo?.setVerification(DejavooProcessor.response.verification)
            payinfo?.chipAndPin = true
            payinfo?.paymentOK(DejavooProcessor.response.authCode, DejavooProcessor.response.transactionId, DejavooProcessor.response.message)
        }
        else if (DejavooProcessor.response.success == "1"){
            payinfo?.paymentError("Transaction Error! Please try again", DejavooProcessor.response.message)
        }

    }
}