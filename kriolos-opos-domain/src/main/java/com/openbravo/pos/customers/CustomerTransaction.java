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

package com.openbravo.pos.customers;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializerRead;
import com.openbravo.format.Formats;
import java.util.Date;

/**
 *
 * @author JG uniCenta Gerrard 1 Nov 12
 * Used in Customer's transactions tab to display all this Customer's
 * ticketline values
 */
public class CustomerTransaction {

    String ticketId;
    String productName;
    String unit;
    Double amount;
    Double total;
    Date transactionDate;
    String customerId;   

    /**
     * Main method to return all customer's transactions 
     */
    public CustomerTransaction() {
    }

    /**
     *
     * @param ticketId
     * @param productName
     * @param unit
     * @param amount
     * @param total
     * @param transactionDate
     * @param cId
     */
    public CustomerTransaction(String ticketId, String productName, String unit, Double amount, Double total, Date transactionDate, String cId) {
        this.ticketId = ticketId;
        this.productName = productName;
        this.unit = unit;
        this.amount = amount;
        this.total = total;
        this.transactionDate = transactionDate;
//        this.customerName = name;
        this.customerId = cId;        
    }

    /**
     *
     * @return ticket id string
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     *
     * @param ticketId
     */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    /**
     *
     * @return ticket amount value
     */
    public Double getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     *
     * @param total
     */
    public void setTotal(Double  total) {
        this.total = total;
    }

    /**
     *
     * @return ticketline value
     */
    public Double getTotal() {
        return total;
    }

    /**
     *
     * @return ticketline's product name string 
     */
    public String getProductName() {
        return productName;
    }

    /**
     *
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     *
     * @return ticket's transaction date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     *
     * @param transactionDate
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     *
     * @return ticketline's quantity string value
     */
    public String getUnit() {
        return unit;
    }

    /**
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     *
    * @return customer's account name string
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    } 
    */
    
    /**
     *
     * @return customer's account name string
     */
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    
    
    
    /**
     *
     * @return ticketlines for this customer
     */
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {

            @Override
            public Object readValues(DataRead dr) throws BasicException {

                String ticketId = dr.getString(1);
                String productName = dr.getString(2);
                String unit = dr.getString(3);
                Double amount = dr.getDouble(4);
                Double total = dr.getDouble(5);
                String dateValue = dr.getString(6);
                String customerId = dr.getString(7);                

                Date date = Formats.DATETIME.parseValue(dateValue);
                return new CustomerTransaction(ticketId, productName, unit, amount, total, date, customerId);                
            }
        };
    }
}
