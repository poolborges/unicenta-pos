/*
 * Copyright (C) 2022 KiolOS<https://github.com/kriolos>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.voucher;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.SerializerRead;

public class VoucherInfo implements IKeyed {

    public static final String VOUCHER_STATUS_AVAILABLE = "A";
    public static final String VOUCHER_STATUS_REDEEMED = "D";
    
    public static final String VOUCHER_TYPE_IN = "voucherin";
    public static final String VOUCHER_TYPE_OUT = "voucherout";

    private String id;
    private String voucherNumber;
    private String customerId;
    private String customerName;
    private double amount;
    private String status;

    public VoucherInfo() {
    }

    public VoucherInfo(String id,
            String voucherNumber,
            String customerId,
            String customerName,
            double amount,
            String status) {
        this.id = id;
        this.voucherNumber = voucherNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public Object getKey() {
        return getId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the voucherNumber
     */
    public String getVoucherNumber() {
        return voucherNumber;
    }

    /**
     * @param voucherNumber the voucherNumber to set
     */
    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    /**
     * @return the customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return voucherNumber;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public static SerializerRead<VoucherInfo> getSerializerRead() {
        return new SerializerRead<VoucherInfo>() {
            @Override
            public VoucherInfo readValues(DataRead dr) throws BasicException {
                return new VoucherInfo(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getDouble(5),
                        dr.getString(6));
            }
        };
    }
    
    public String printStatus() {
        return switch (getStatus()) {
            case VOUCHER_STATUS_AVAILABLE -> "Available";
            case VOUCHER_STATUS_REDEEMED -> "Redeemed";
            default -> "Unknow";
        };
    }
}
