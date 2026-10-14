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

import com.openbravo.pos.spi.hardware.printer.DevicePrinter;
import java.io.File;

public final class PdfPrinterFactory {

    public static DevicePrinter create(String type, String name, File outputDir) {
        return switch (type.toLowerCase()) {
            case "pdf58", "58mm" -> new PdfPrinter58mm(name, outputDir);
            case "pdf80", "80mm" -> new PdfPrinter80mm(name, outputDir);
            case "pdfa4", "a4"   -> new PdfPrinterA4(name, outputDir);
            default              -> new PdfPrinter80mm(name, outputDir);
        };
    }
}
