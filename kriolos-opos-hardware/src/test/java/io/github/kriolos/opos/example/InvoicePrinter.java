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
import java.awt.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author psb
 */
public class InvoicePrinter implements Printable {
    private Invoice invoice;

    public InvoicePrinter(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // Set up fonts and colors
        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font normalFont = new Font("SansSerif", Font.PLAIN, 12);
        Font boldFont = new Font("SansSerif", Font.BOLD, 12);

        // Draw company info
        g2d.setFont(headerFont);
        g2d.drawString("Your Company Name", 50, 50);
        g2d.setFont(normalFont);
        g2d.drawString("Your Company Address", 50, 70);
        g2d.drawString("Phone: XXX-XXX-XXXX", 50, 85);

        // Draw Invoice Details
        g2d.setFont(boldFont);
        g2d.drawString("INVOICE", 400, 50);
        g2d.setFont(normalFont);
        g2d.drawString("Invoice #: " + invoice.getInvoiceNumber(), 400, 70);
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormated = parser.format(new java.util.Date());
        g2d.drawString("Date: " + dateFormated, 400, 85);

        // Draw Customer Details
        g2d.setFont(boldFont);
        g2d.drawString("Bill To:", 50, 120);
        g2d.setFont(normalFont);
        g2d.drawString(invoice.getCustomerName(), 50, 140);
        g2d.drawString(invoice.getCustomerAddress(), 50, 155);

        // Draw Item Table Header
        int y = 200;
        g2d.setFont(boldFont);
        g2d.drawString("Description", 50, y);
        g2d.drawString("Quantity", 300, y);
        g2d.drawString("Unit Price", 400, y);
        g2d.drawString("Total", 500, y);
        y += 15;
        g2d.drawLine(50, y, 550, y);
        y += 10;

        // Draw Item Rows
        g2d.setFont(normalFont);
        for (InvoiceItem item : invoice.getItems()) {
            g2d.drawString(item.getDescription(), 50, y);
            g2d.drawString(String.valueOf(item.getQuantity()), 300, y);
            g2d.drawString(String.format("%.2f", item.getUnitPrice()), 400, y);
            g2d.drawString(String.format("%.2f", item.getItemTotal()), 500, y);
            y += 15;
        }

        // Draw Total
        y += 20;
        g2d.setFont(boldFont);
        g2d.drawString("Total Amount:", 300, y);
        g2d.drawString(String.format("%.2f", invoice.getTotalAmount()), 500, y);

        return PAGE_EXISTS;
    }
}