package com.openbravo.pos.payment;

import java.awt.Component;
import java.awt.ComponentOrientation;

/**
 * PaymentSelect for Customer Payment.
 *
 * @author KriolOS
 */
public class JPaymentSelectCustomer extends JPaymentSelect {

    private static final long serialVersionUID = 1L;

    public JPaymentSelectCustomer(Component parent, ComponentOrientation o) {
        super(parent, o);
    }

    public JPaymentSelectCustomer(Component parent) {
        this(parent, parent != null ? parent.getComponentOrientation() : null);
    }

    public JPaymentSelectCustomer() {
        this((Component) null, (ComponentOrientation) null);
    }

    @Deprecated
    protected JPaymentSelectCustomer(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, o);
    }

    @Deprecated
    protected JPaymentSelectCustomer(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
        super(parent, o);
    }

    public static JPaymentSelect getDialog(Component parent) {
        return new JPaymentSelectCustomer(parent);
    }

    @Override
    protected void addTabs() {
        addTabPayment(new JPaymentSelect.JPaymentCashCreator());
        addTabPayment(new JPaymentSelect.JPaymentChequeCreator());
        addTabPayment(new JPaymentSelect.JPaymentVoucherCreator());
        addTabPayment(new JPaymentSelect.JPaymentBankCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCreator());
        addTabPayment(new JPaymentSelect.JPaymentSlipCreator());
    }

    @Override
    protected void setStatusPanel(boolean isPositive, boolean isComplete) {
        setAddEnabled(isPositive && !isComplete);
        setOKEnabled(isPositive);
    }

    @Override
    protected PaymentInfo getDefaultPayment(double total) {
        return new PaymentInfoCash(total, total);
    }
}
