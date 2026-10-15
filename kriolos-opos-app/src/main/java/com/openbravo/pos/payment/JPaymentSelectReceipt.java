package com.openbravo.pos.payment;

import java.awt.Component;
import java.awt.ComponentOrientation;

/**
 * PaymentSelect for Receipt Payment.
 * 
 * @author KriolOS
 */
public class JPaymentSelectReceipt extends JPaymentSelect {

    private static final long serialVersionUID = 1L;

    public JPaymentSelectReceipt(Component parent, ComponentOrientation o) {
        super(parent, o);
    }

    public JPaymentSelectReceipt(Component parent) {
        this(parent, parent != null ? parent.getComponentOrientation() : null);
    }

    public JPaymentSelectReceipt() {
        this((Component) null, (ComponentOrientation) null);
    }

    @Deprecated
    protected JPaymentSelectReceipt(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, o);
    }

    @Deprecated
    protected JPaymentSelectReceipt(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
        super(parent, o);
    }

    public static JPaymentSelect getDialog(Component parent) {
        return new JPaymentSelectReceipt(parent);
    }

    @Override
    protected void addTabs() {
        addTabPayment(new JPaymentSelect.JPaymentCashCreator());
        // TODO: CARD addTabPayment(new JPaymentSelect.JPaymentCardCreator());
        addTabPayment(new JPaymentSelect.JPaymentChequeCreator());
        addTabPayment(new JPaymentSelect.JPaymentVoucherCreator());            
        addTabPayment(new JPaymentSelect.JPaymentMagcardCreator());                
        addTabPayment(new JPaymentSelect.JPaymentFreeCreator());                
        addTabPayment(new JPaymentSelect.JPaymentDebtCreator());
        addTabPayment(new JPaymentSelect.JPaymentBankCreator());        
        addTabPayment(new JPaymentSelect.JPaymentSlipCreator());  
    }

    @Override
    protected void setStatusPanel(boolean isPositive, boolean isComplete) {
        setAddEnabled(isPositive && !isComplete);
        setOKEnabled(isComplete);
    }

    @Override
    protected PaymentInfo getDefaultPayment(double total) {
        return new PaymentInfoCash(total, 0);        
    }
}
