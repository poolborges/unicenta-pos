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
package io.github.kriolos.opos.example1;

import com.openbravo.pos.forms.AppLocal;
import java.io.ByteArrayInputStream;
import java.util.Locale;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.JobName;

/**
 *
 * @author psb
 */
public class InvoiceSample1 {

    String invoiceContent
            = "---------------------------------------\n"
            + "             INVOICE\n"
            + "---------------------------------------\n"
            + "Invoice Number: INV-2025-001\n"
            + "Date: 2025-09-22\n"
            + "Customer: John Doe\n"
            + "---------------------------------------\n"
            + "Item        Qty   Price    Total\n"
            + "---------------------------------------\n"
            + "Product A   2     10.00    20.00\n"
            + "Product B   1     25.50    25.50\n"
            + "---------------------------------------\n"
            + "Subtotal:            45.50\n"
            + "Tax (5%):             2.28\n"
            + "Total:               47.78\n"
            + "---------------------------------------\n"
            + "Thank You for Your Business!\n"
            + "---------------------------------------";

    String printerName = "PDF";

    public void printer() {
        /**
         *
         */
        var docf1 = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        var docf2 = DocFlavor.INPUT_STREAM.AUTOSENSE;
        var docf3 = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        var docf4 = DocFlavor.STRING.TEXT_PLAIN;
        
        DocFlavor flavor = docf2; // Or TEXT_PLAIN.UTF_8
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1)); // Print one copy

        /**
         *
         */
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null /*attributes*/);

        if (services.length == 0) {
            System.out.println("No suitable print services found.");
            return;
        }

        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService(); // Or choose from 'services' array

        if (defaultService == null) {
            for (PrintService ps : services) {
                if (ps.getName().toUpperCase().contains(printerName.toUpperCase())) {
                    // if we have found the prineter the start our print routine
                    defaultService = ps;
                    break;
                }
            }
        }

        if (defaultService == null) {
            System.out.println("No default print service found. Please select one from the available services.");
            // Handle selection from 'services' array
            return;
        }

        /**
         *
         */
        /*
        byte[] invoiceBytes = invoiceContent.getBytes();
        new ByteArrayInputStream(invoiceBytes);
       */
        DocAttributeSet docattributes = new HashDocAttributeSet();
        docattributes.add(new DocumentName("Ticket", Locale.getDefault()));
        Doc doc = new SimpleDoc(invoiceContent, flavor, docattributes);
        PrintRequestAttributeSet jobattributes = new HashPrintRequestAttributeSet();
        jobattributes.add(new JobName("Invoice_", Locale.getDefault()));
            DocPrintJob job = defaultService.createPrintJob();
        try {
            job.print(doc, attributes);
            System.out.println("Invoice sent to printer: "+defaultService.getName());
        } catch (PrintException e) {
            System.err.println("Error during printing: " + e.getMessage());
        }
        
    }

    public static void main(String[] args) {
        new InvoiceSample1().printer();
    }
}
