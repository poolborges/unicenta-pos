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
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See thecom.openbravo.pos.voucher
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.voucher;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SerializerRead;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.BeanFactoryDataSingle;

/**
 *
 * @author poolborges
 */
public class DataLogicVouchers  extends BeanFactoryDataSingle {

    private Session dbSession;
    
    @Override
    public void init(Session dbSession) {
        this.dbSession = dbSession;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Voucher">
    public final PreparedSentence getVoucherNumber() {
        return new PreparedSentence(this.dbSession,
                "SELECT SUBSTRING(MAX(VOUCHER_NUMBER),10,3) AS LAST_NUMBER FROM vouchers "
                + "WHERE SUBSTRING(VOUCHER_NUMBER,1,8) = ?",
                SerializerWriteString.INSTANCE, (SerializerRead<String>) (DataRead dr) -> dr.getString(1));
    }

    public final VoucherInfo getVoucherInfo(String id) throws BasicException {
        return (VoucherInfo) new PreparedSentence(this.dbSession,
                "SELECT vouchers.ID, VOUCHER_NUMBER, CUSTOMER, "
                + "customers.NAME, AMOUNT, STATUS "
                + "FROM vouchers "
                + "JOIN customers ON customers.id = vouchers.CUSTOMER "
                + "WHERE STATUS='A' AND vouchers.ID=?" //"WHERE STATUS='A' "                         
                ,
                 SerializerWriteString.INSTANCE,
                VoucherInfo.getSerializerRead()).<VoucherInfo>find(id);
    }

    public final VoucherInfo getVoucherInfoAll(String id) throws BasicException {
        return (VoucherInfo) new PreparedSentence(this.dbSession,
                "SELECT vouchers.ID, VOUCHER_NUMBER, CUSTOMER, "
                + "customers.NAME, AMOUNT, STATUS "
                + "FROM vouchers "
                + "JOIN customers ON customers.id = vouchers.CUSTOMER  "
                + "WHERE vouchers.ID=?",
                SerializerWriteString.INSTANCE,
                VoucherInfo.getSerializerRead()).<VoucherInfo>find(id);
    }
    // </editor-fold>
}
