//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.payment;

import com.openbravo.format.Formats;

public class PaymentInfoCash extends PaymentInfo {

    private double prePayAmount = 0.0;
    private double m_dPaid;
    private double m_dTotal;
    private double m_dTendered;
    private String m_dCardName =null;
//    private double m_dTip;    
    
    /**
     * Creates a new instance of PaymentInfoCash
     * @param dTotal
     * @param dPaid
     * @param dTendered
     */
    public PaymentInfoCash(double dTotal, double dPaid, double dTendered) {
        m_dTotal = dTotal;
        m_dPaid = dPaid;
        m_dTendered = dTendered;
    }

    /**
     * Creates a new instance of PaymentInfoCash
     * @param dTotal
     * @param dPaid
     * @param dTendered
     * @param prePayAmount
     */
    public PaymentInfoCash(double dTotal, double dPaid, double dTendered, double prePayAmount) {
        this(dTotal, dTendered, dPaid);
        this.prePayAmount = prePayAmount;
    }
    
    /** Creates a new instance of PaymentInfoCash
     * @param dTotal
     * @param dPaid */
    public PaymentInfoCash(double dTotal, double dPaid) {
        m_dTotal = dTotal;
        m_dPaid = dPaid;
    }
    
    @Override
    public PaymentInfo copyPayment() {
       return new PaymentInfoCash(m_dTotal, m_dPaid, m_dTendered, prePayAmount);
//        return new PaymentInfoCash(m_dTotal, m_dPaid, prePayAmount);        
    }

    @Override
    public String getTransactionID() {
        return "no ID";
    }
    
    @Override
    public String getName() {
        return "cash";
    }
    @Override
    public double getTotal() {
        return m_dTotal;
    }
//    public double getTip() {
//        return m_dTip;
//    }
    @Override
    public double getPaid() {
        return m_dPaid;
    }

    @Override
    public double getTendered() {
        return m_dTendered;
    }

    @Override
   public double getChange(){
       return m_dPaid - m_dTotal;
   }

    @Override
    public String getCardName() {
       return m_dCardName;
   }     

    public boolean hasPrePay() {
        if (prePayAmount > 0) {
            return true;
        }
        return false;
    }

    public double getPrePaid() {
        return prePayAmount;
    }

    public String printTendered() {
       return Formats.CURRENCY.formatValue(m_dTendered);
   }    
    public String printPaid() {
        return Formats.CURRENCY.formatValue(m_dPaid);
    }
    public String printChange() {
        return Formats.CURRENCY.formatValue(m_dPaid - m_dTotal);
    }

    @Override
    public String getVoucher() {
        return null;
    }    
    
}
