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
 * along with this program.  If not, see <http://gnu.org>.
 */
package com.openbravo.pos.printer.ticket;

import java.awt.Font;
import java.awt.geom.AffineTransform;

/**
 * Screen implementation of a basic receipt ticket view.
 * Uses Font.MONOSPACED to guarantee that text width metrics calculations 
 * for center and right alignments match pixels perfectly.
 * 
 * @author JG uniCenta
 * @author KriolOS
 */
public class BasicTicketForScreen extends BasicTicket {

    // Using Font.MONOSPACED ensures alignment math doesn't break across operating systems
    private static final Font BASE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12)
            .deriveFont(AffineTransform.getScaleInstance(1.0, 1.20)); // Adjusted vertical scale to match line metrics
            
    private static final int FONT_HEIGHT = 16; // Perfectly bound to 12pt font geometry
    private static final double IMAGE_SCALE = 1.0;

    /**
     * @return The immutable monospaced font instance for correct pixel-width calculations
     */
    @Override
    protected Font getBaseFont() {
        return BASE_FONT;
    }

    /**
     * @return The height allowance allocated for each text line
     */
    @Override
    protected int getFontHeight() {
        return FONT_HEIGHT;
    }

    /**
     * @return The image scaling factor applied to logo or graphical renders
     */
    @Override
    protected double getImageScale() {
        return IMAGE_SCALE;
    }
}
