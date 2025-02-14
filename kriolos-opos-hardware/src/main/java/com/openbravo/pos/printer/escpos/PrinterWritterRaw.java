/*
 * Copyright (C) 2022 KriolOS
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
package com.openbravo.pos.printer.escpos;

import com.openbravo.pos.forms.AppLocal;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.JobName;

public final class PrinterWritterRaw extends PrinterWritter {
    private static final Logger LOGGER = Logger.getLogger(PrinterWritterRaw.class.getName());
    
    private byte[] m_printData;
    private PrintService printService;
    private final DocFlavor DOC_Flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
    private PrinterBuffer printerBuffer = null;
    private final static String DOC_NAME = "Ticket";
    private final String printerName;


    public PrinterWritterRaw(String printerName) {
        this.printerName = printerName;
        this.m_printData = null;
        this.printerBuffer = new PrinterBuffer();

        init();

        PrintService[] services = PrintServiceLookup.lookupPrintServices(DOC_Flavor, null);
        for (PrintService ps : services) {
            if (ps.getName().contains(printerName)) {
                // if we have found the prineter the start our print routine
                printService = ps;
                write(ESCPOS.INIT);
                break;
            }
        }
    }

    public void init() {
        byte[] inicode = concatByteArrays(ESCPOS.SELECT_PRINTER, new UnicodeTranslatorInt().getCodeTable());
        this.m_printData = concatByteArrays(inicode, this.m_printData);
    }

    @Override
    public void write(byte[] data) {
        m_printData = concatByteArrays(m_printData, data);
    }

    @Override
    public void write(String sValue) {
        printerBuffer.putData(sValue.getBytes());
    }

    @Override
    protected void internalWrite(byte[] data) {
    }

    @Override
    protected void internalClose() {
    }

    @Override
    protected void internalFlush() {
    }


    @Override
    public void flush() {
        printJob();
    }  
    
    private byte[] concatByteArrays(byte[] a, byte[] b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        byte[] concat = new byte[a.length + b.length];
        System.arraycopy(a, 0, concat, 0, a.length);
        System.arraycopy(b, 0, concat, a.length, b.length);
        return concat;
    }

    private void printJob() {
        if (null != printService) {
            try {
                DocPrintJob pj = printService.createPrintJob();
                DocAttributeSet docattributes = new HashDocAttributeSet();

                docattributes.add(new DocumentName(DOC_NAME, Locale.getDefault()));
                PrintRequestAttributeSet jobattributes = new HashPrintRequestAttributeSet();

                jobattributes.add(new JobName(AppLocal.APP_NAME, Locale.getDefault()));
                Doc doc = new SimpleDoc(m_printData, DOC_Flavor, docattributes);
                pj.print(doc, jobattributes);
            } catch (PrintException ex) {
                LOGGER.log(Level.WARNING, "Exception on print: ", ex);
            } finally {
                m_printData = null;
            }
        }
    }

    private class PrinterBuffer {

        private final LinkedList<Object> m_list;

        /**
         * Creates a new instance of PrinterBuffer
         */
        public PrinterBuffer() {
            m_list = new LinkedList<>();
        }

        public synchronized void putData(Object data) {
            m_list.addFirst(data);
            notifyAll();
        }

        public synchronized Object getData() {
            while (m_list.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "Exception on wait: ", e);
                }
            }
            notifyAll();
            return m_list.removeLast();
        }
    }
}
