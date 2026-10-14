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

public class PdfPrinter58mm extends BasePdfDevicePrinter {

    public static final float WIDTH_58MM = 164.41f;

    public PdfPrinter58mm(String printerName, File outputDirectory) {
        super(printerName, outputDirectory);
    }

    @Override
    public String getPrinterDescription() {
        return "Thermal 58mm PDF Virtual Printer";
    }

    @Override public float getPageWidth() { return WIDTH_58MM; }
    @Override public float getMarginHorizontal() { return 8.0f; }
    @Override public float getMarginVertical() { return 10.0f; }
    @Override public float getLineHeight() { return 10.0f; }
    @Override public float getFontSize() { return 7.5f; }
    @Override public int getLineLength() { return 32; }
    @Override public boolean isContinuousRoll() { return true; }
}