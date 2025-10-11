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

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.format.Formats;

public class PaymentInfoTicket extends PaymentInfo implements SerializableRead {

    private static final long serialVersionUID = 8865238639097L;
    
    private double amountToPay;             //Amount to Pay   (Bill Amount)
    private String paymentMethod;           //Payment Method
    private String transactionID;           //Transaction ID
    private double amountTendered;          //Amount Tendered (Amount Received)
    private String cardName;                //Card Name
    private String voucherNumber;           //Voucher Number

    /**
     * Used For: Cheque, Bank , Slip, Voucher
     *
     * @param ticketAmount Amount
     * @param paymentMethod Payment Method (e.g: cheque,  bank, slip, voucherin, debt, debtpaid, cashrefund)
     */
    public PaymentInfoTicket(double ticketAmount, String paymentMethod) {
        this(ticketAmount, paymentMethod, null, null);
    }

    /**
     * Used For: Voucher
     *
     * @param ticketAmount Amount
     * @param paymentMethod Payment Method (e.g: cheque,  bank, slip, voucherin, debt, debtpaid, cashrefund)
     * @param transactionID Transaction ID (Give the Context: what is begin payed)
     * @param voucherNumber Voucher Number
     */
    public PaymentInfoTicket(double ticketAmount, String paymentMethod, String transactionID, String voucherNumber) {
        this.paymentMethod = paymentMethod;
        this.amountToPay = ticketAmount;
        this.transactionID = transactionID;
        this.voucherNumber = voucherNumber;
        this.cardName = null;
        this.amountTendered = 0.00;
    }

    /**
     * Used for: Voucher
     *
     * @param ticketAmount
     * @param paymentMethod (e.g: cheque,  bank, slip, voucherin, debt, debtpaid, cashrefund)
     * @param voucherNumber Voucher Number
     */
    public PaymentInfoTicket(double ticketAmount, String paymentMethod, String voucherNumber) {
        this(ticketAmount, paymentMethod, null, voucherNumber);
    }

    public PaymentInfoTicket() {
        this(0.0, null, null, null);
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        this.paymentMethod = dr.getString(1);
        this.amountToPay = dr.getDouble(2);
        this.transactionID = dr.getString(3);
        if (dr.getDouble(4) != null) {
            this.amountTendered = dr.getDouble(4);
        }
        this.cardName = dr.getString(5);    
    }

    @Override
    public PaymentInfo copyPayment() {
        return new PaymentInfoTicket(this.amountToPay, this.paymentMethod);
    }

    @Override
    public String getName() {
        return this.paymentMethod;
    }

    @Override
    public double getTotal() {
        return this.amountToPay;
    }

    @Override
    public String getTransactionID() {
        return this.transactionID;
    }

    @Override
    public double getPaid() {
        return (0.0);
    }

    @Override
    public double getChange() {
        return this.amountTendered - this.amountToPay;
    }

    @Override
    public double getTendered() {
        return (0.0);
    }

    @Override
    public String getCardName() {
        return this.cardName;
    }

    public String printPaid() {
        return Formats.CURRENCY.formatValue(this.amountToPay);
    }

    public String printVoucherTotal() {
        return Formats.CURRENCY.formatValue(-this.amountToPay);
    }

    public String printChange() {
        return Formats.CURRENCY.formatValue(getChange());
    }

    public String printTendered() {
        return Formats.CURRENCY.formatValue(this.amountTendered);
    }

    @Override
    public String getVoucher() {
        return this.voucherNumber;
    }

    public String printVoucher() {
        return this.voucherNumber;
    }
}
