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

import io.github.kriolos.opos.example.InvoiceModel.Invoice;
import io.github.kriolos.opos.example.InvoiceModel.InvoiceItem;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author psb
 */
public class PrintInvoice {
    
    public static void main(String[] args) {
        // Create sample invoice data
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-2025-001");
        invoice.setCustomerName("John Doe");
        invoice.setCustomerAddress("123 Main St, Anytown, USA");

        List<InvoiceItem> items = new ArrayList<>();
        items.add(new InvoiceItem("Product A", 2, 10.00, 20.00));
        items.add(new InvoiceItem("Product B", 1, 25.50, 25.50));
        invoice.setItems(items);
        invoice.setTotalAmount(45.50);

        // Get a PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new InvoicePrinter(invoice));

        // Optional: Show print dialog
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                System.err.println("Printing error: " + e.getMessage());
            }
        }
    }
}
