/*
 * Copyright (C) 2026 KriolOS
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
package com.openbravo.pos.printer.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.io.File;

public class PdfPrinterA4 extends BasePdfDevicePrinter {

    public PdfPrinterA4(String printerName, File outputDirectory) {
        super(printerName, outputDirectory);
    }

    @Override
    public String getPrinterDescription() {
        return "A4 Document & Invoice PDF Printer";
    }

    @Override public float getPageWidth() { return PDRectangle.A4.getWidth(); } // 595.28 pt
    @Override public float getMarginHorizontal() { return 36.0f; } // 0.5 in
    @Override public float getMarginVertical() { return 40.0f; }
    @Override public float getLineHeight() { return 14.0f; }
    @Override public float getFontSize() { return 10.0f; }
    @Override public int getLineLength() { return 80; }
    @Override public boolean isContinuousRoll() { return false; }
}
