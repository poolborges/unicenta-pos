/*
 * Copyright (C) 2025 Paulo Borges
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
package io.github.kriolos.opos.example;

import java.util.List;

/**
 *
 * @author psb
 */
public class InvoiceModel {

    public static class Invoice {

        private String invoiceNumber;
        private String customerName;
        private String customerAddress;
        private List<InvoiceItem> items;
        private double totalAmount;

        // Constructor, getters, setters

        public Invoice() {
        }
        

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerAddress() {
            return customerAddress;
        }

        public void setCustomerAddress(String customerAddress) {
            this.customerAddress = customerAddress;
        }

        public List<InvoiceItem> getItems() {
            return items;
        }

        public void setItems(List<InvoiceItem> items) {
            this.items = items;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
        
    }

    public static class InvoiceItem {

        private String description;
        private int quantity;
        private double unitPrice;
        private double itemTotal;

        // Constructor, getters, setters
        public InvoiceItem(){}

        public InvoiceItem(String description, int quantity, double unitPrice, double itemTotal) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.itemTotal = itemTotal;
        }
        
        

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public double getItemTotal() {
            return itemTotal;
        }

        public void setItemTotal(double itemTotal) {
            this.itemTotal = itemTotal;
        }
        
        
    }
}
