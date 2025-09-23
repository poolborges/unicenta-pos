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
package com.openbravo.pos.printer.escpos;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author psb
 */
public class PrinterBuffer {

    private static final Logger LOGGER = Logger.getLogger(PrinterBuffer.class.getName());

    private final LinkedList<Byte> m_list;

    public PrinterBuffer() {
        m_list = new LinkedList<>();
    }

    public synchronized void putData(Byte data) {
        m_list.addLast(data);
        notifyAll();
    }

    public synchronized void putData(String data) {
        byte[] dataBytes = data.getBytes();
        for (int pos = 0; pos < dataBytes.length; pos++) {
            m_list.addLast(dataBytes[pos]);
        }
        notifyAll();
    }

    public synchronized Byte getData() throws PrinterBufferException {
        while (m_list.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new PrinterBufferException("PrinterBufferException on wait for data: ", e);
            }
        }
        notifyAll();
        return m_list.removeFirst();
    }

    public static class PrinterBufferException extends Exception {

        private static final long serialVersionUID = 1L;

        public PrinterBufferException() {
            super();
        }

        public PrinterBufferException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
