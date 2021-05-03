package com.openbravo.pos.payment

import uk.co.pos_apps.PosApps
import uk.co.pos_apps.common.AppContext

class PaymentGatewayVantiv :PaymentGateway {

    val PAYMENT_PROCESSOR = "Vantiv"

    override fun execute(payinfo: PaymentInfoMagcard?) {

        AppContext.paymentComplete = false
        AppContext.paymentResult = null
        AppContext.P_S_PDQ_STATUS = "Initializing..."

        var timer = 0
        val timeout = 180

        PosApps.initPayment(PAYMENT_PROCESSOR, payinfo?.total)

        while (!AppContext.paymentComplete) {
            Thread.sleep(1000)
            timer += 1
            if (timer > timeout) break
        }

        if (AppContext.paymentResult == null) {
            payinfo?.paymentError("Transaction Error! Please try again", "No Response")
        }
        if (AppContext.paymentResult.transactionResult == "SUCCESSFUL"){
            payinfo?.cardName = AppContext.paymentResult.cardSchemeName
            payinfo?.setVerification(AppContext.paymentResult.paymentMethod)
            payinfo?.chipAndPin = true
            payinfo?.paymentOK(AppContext.paymentResult.authCode, AppContext.paymentResult.transactionId, AppContext.paymentResult.transactionResult)
        }
        else {
            payinfo?.paymentError("Transaction Error! Please try again", AppContext.paymentResult.transactionResult)
        }
    }
}