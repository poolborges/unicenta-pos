package com.openbravo.pos.payment;

import java.awt.Component;
import java.awt.ComponentOrientation;

/**
 * PaymentSelect for Refund Payment.
 *
 * @author KriolOS
 */
public class JPaymentSelectRefund extends JPaymentSelect {

    private static final long serialVersionUID = 1L;

    public JPaymentSelectRefund(Component parent, ComponentOrientation o) {
        super(parent, o);
    }

    public JPaymentSelectRefund(Component parent) {
        this(parent, parent != null ? parent.getComponentOrientation() : null);
    }

    public JPaymentSelectRefund() {
        this((Component) null, (ComponentOrientation) null);
    }

    @Deprecated
    protected JPaymentSelectRefund(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, o);
    }

    @Deprecated
    protected JPaymentSelectRefund(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
        super(parent, o);
    }

    public static JPaymentSelect getDialog(Component parent) {
        return new JPaymentSelectRefund(parent);
    }

    @Override
    protected void addTabs() {
        addTabPayment(new JPaymentSelect.JPaymentCashRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentChequeRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentVoucherRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardRefundCreator());
    }

    @Override
    protected void setStatusPanel(boolean isPositive, boolean isComplete) {
        setAddEnabled(isPositive && !isComplete);
        setOKEnabled(isComplete);
    }

    @Override
    protected PaymentInfo getDefaultPayment(double total) {
        return new PaymentInfoTicket(total, "cashrefund");
    }
}
