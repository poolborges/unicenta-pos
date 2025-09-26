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
package com.openbravo.pos.printer.ticket;

import com.openbravo.pos.printer.DevicePrinter;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;


/**
 * Manages the font state (font derivation process).
 * 
 * @author poolborges
 */
public class PrinterFontState {

    private final DevicePrinter.FontSize m_fontSize;

    /**
     *
     * @param fontSize The desired font size configuration.
     */
    public PrinterFontState(DevicePrinter.FontSize fontSize) {
        this.m_fontSize = fontSize;
    }

    /**
     * Backwards-compatible constructor using the original integer constant.
     *
     * @param iSize The original integer size constant (0-3).
     */
    public PrinterFontState(int iSize) {
        this.m_fontSize = DevicePrinter.FontSize.fromInt(iSize);
    }

    /**
     * Returns the line multiplier associated with the current font size.
     *
     * @return The line multiplier.
     */
    public int getLineMult() {
        return m_fontSize.getLineMultiplier();
    }

    /**
     * Derives and returns a new Font object with the specified styling.
     *
     *
     * @param baseFont The starting font.
     * @param iStyle A bitmask integer for bold and underline styles.
     * @return A new Font instance with all attributes applied.
     */
    public Font getFont(Font baseFont, int iStyle) {
        Map<TextAttribute, Object> attributes = new HashMap<>();

        // Apply scaling via AffineTransform, pre-concatenating with the base font's transform.
        AffineTransform affineTransform = AffineTransform.getScaleInstance(
                m_fontSize.getWidthScale(),
                m_fontSize.getHeightScale()
        );
        if (baseFont.getTransform() != null && !baseFont.getTransform().isIdentity()) {
            affineTransform.preConcatenate(baseFont.getTransform());
        }
        attributes.put(TextAttribute.TRANSFORM, affineTransform);

        // Apply bold style based on the style flag.
        if ((iStyle & DevicePrinter.STYLE_BOLD) != 0) {
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }

        // Apply underline style based on the style flag.
        if ((iStyle & DevicePrinter.STYLE_UNDERLINE) != 0) {
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }

        // Derive the new font using the single map of attributes.
        return baseFont.deriveFont(attributes);
    }
}
