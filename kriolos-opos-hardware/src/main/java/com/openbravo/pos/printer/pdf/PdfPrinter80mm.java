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

import java.io.File;

public class PdfPrinter80mm extends BasePdfDevicePrinter {

    public static final float WIDTH_80MM = 226.77f;

    public PdfPrinter80mm(String printerName, File outputDirectory) {
        super(printerName, outputDirectory);
    }

    @Override
    public String getPrinterDescription() {
        return "Thermal 80mm Standard POS Virtual Printer";
    }

    @Override public float getPageWidth() { return WIDTH_80MM; }
    @Override public float getMarginHorizontal() { return 12.0f; }
    @Override public float getMarginVertical() { return 14.0f; }
    @Override public float getLineHeight() { return 12.0f; }
    @Override public float getFontSize() { return 9.0f; }
    @Override public int getLineLength() { return 42; } // 42 chars
    @Override public boolean isContinuousRoll() { return true; }
}