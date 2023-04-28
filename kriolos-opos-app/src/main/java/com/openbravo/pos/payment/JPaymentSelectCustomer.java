//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.payment;

import java.awt.*;

/**
 *
 * @author adrianromero
 */
public class JPaymentSelectCustomer extends JPaymentSelect {

    private static final long serialVersionUID = 1L;

    protected JPaymentSelectCustomer(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, modal, o);
    }

    protected JPaymentSelectCustomer(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
        super(parent, modal, o);
    }

    public static JPaymentSelect getDialog(Component parent) {

        Window window = getWindow(parent);
        
        if (window instanceof Frame) { 
            return new JPaymentSelectCustomer((Frame) window, true, parent.getComponentOrientation());
        } else {
            return new JPaymentSelectCustomer((Dialog) window, true, parent.getComponentOrientation());
        } 
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
