//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Adrian Romero Corchado.
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.printer.escpos;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
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
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.JobName;

public final class PrinterWritterRaw extends PrinterWritter {

    private byte[] m_printData;
    private PrintService m_printService;
    private final DocFlavor m_docFlavor;
    private PrinterBuffer m_buff = null;
    private OutputStream m_out;

    public PrinterWritterRaw(String sRawPrinter) {
        m_printData = null;
        m_docFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        m_buff = new PrinterBuffer();

        init();

        PrintService[] services = PrintServiceLookup.lookupPrintServices(m_docFlavor, null);
        for (PrintService ps : services) {
            if (ps.getName().contains(sRawPrinter)) {
                // if we have found the prineter the start our print routine
                m_printService = ps;
                write(ESCPOS.INIT);
                return;
            }
        }
    }

    public void init() {
        byte[] inicode = concatByteArrays(ESCPOS.SELECT_PRINTER, new UnicodeTranslatorInt().getCodeTable());
        m_printData = concatByteArrays(inicode, m_printData);
    }

    @Override
    public void write(byte[] data) {
        m_printData = concatByteArrays(m_printData, data);
    }

    @Override
    public void write(String sValue) {
        m_buff.putData(sValue.getBytes());
    }

    @Override
    protected void internalWrite(byte[] data) {
    }

    @Override
    protected void internalClose() {
        try {
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
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
        if (null != m_printService) {
            try {
                DocPrintJob pj = m_printService.createPrintJob();
                DocAttributeSet docattributes = new HashDocAttributeSet();

                docattributes.add(new DocumentName("Ticket", Locale.getDefault()));
                PrintRequestAttributeSet jobattributes = new HashPrintRequestAttributeSet();

                jobattributes.add(new JobName("unicenta", Locale.getDefault()));
                Doc doc = new SimpleDoc(m_printData, m_docFlavor, docattributes);
                pj.print(doc, jobattributes);
            } catch (PrintException ex) {
            } finally {
                m_printData = null;
            }
        }
    }

    private class PrinterBuffer {

        private final LinkedList m_list;

        /**
         * Creates a new instance of PrinterBuffer
         */
        public PrinterBuffer() {
            m_list = new LinkedList();
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
                }
            }
            notifyAll();
            return m_list.removeLast();
        }
    }
}
