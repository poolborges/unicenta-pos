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

public class PaymentInfoFree extends PaymentInfo {

    private final double total;

    public PaymentInfoFree(double total) {
        this.total = total;
    }

    @Override
    public PaymentInfo copyPayment() {
        return new PaymentInfoFree(total);
    }

    @Override
    public String getTransactionID() {
        return TRANSACTION_ID_UNDEFINED;
    }

    @Override
    public String getName() {
        return "free";
    }

    @Override
    public double getTotal() {
        return total;
    }

    @Override
    public double getPaid() {
        return (0.0);
    }

    @Override
    public double getChange() {
        return (0.00);
    }

    @Override
    public double getTendered() {
        return (0.00);
    }

    @Override
    public String getCardName() {
        return null;
    }

    @Override
    public String getVoucher() {
        return null;
    }

}
