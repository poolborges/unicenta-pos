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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public class PrinterWritterFile extends PrinterWritter {

    private static final Logger LOGGER = Logger.getLogger(PrinterWritterFile.class.getName());
    private final String m_sFilePrinter;
    private OutputStream m_out;

    /**
     *
     * @param sFilePrinter
     */
    public PrinterWritterFile(String sFilePrinter) {
        m_sFilePrinter = sFilePrinter;
        m_out = null;
    }

    /**
     *
     * @param data
     */
    @Override
    protected void internalWrite(byte[] data) {

        try {
            File file = new File(m_sFilePrinter);
            if (!file.exists()) {
                Files.createFile(file.toPath());
            }
            if (m_out == null) {
                m_out = new FileOutputStream(m_sFilePrinter);  // No poner append = true.
            }
            m_out.write(data);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on write to file: "+m_sFilePrinter, e);
        }
    }

    /**
     *
     */
    @Override
    protected void internalFlush() {
        try {
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on flush to file: "+m_sFilePrinter, e);
        }
    }

    /**
     *
     */
    @Override
    protected void internalClose() {
        try {
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on close file: "+m_sFilePrinter, e);
        }
    }
}
