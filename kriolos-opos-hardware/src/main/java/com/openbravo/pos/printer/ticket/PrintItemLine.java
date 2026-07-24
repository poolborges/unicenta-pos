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
package com.openbravo.pos.printer.ticket;

import com.openbravo.pos.printer.DevicePrinter;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JG uniCenta
 */
public class PrintItemLine implements PrintItem {

    protected Font font;
    protected int fontHeight;
    protected int textSize;
    protected List<StyledText> textList;


    public PrintItemLine(int textSize, Font font, int fontHeight) {
        this.textSize = textSize;
        this.font = font;
        this.fontHeight = fontHeight;
        textList = new ArrayList<>();
    }


    public void addText(int style, String text) {
        textList.add(new StyledText(style, text));
    }


    @Override
    public void draw(Graphics2D g, int x, int y, int width) {

        PrinterFontState ps = new PrinterFontState(textSize);
        double left = x;
        for (StyledText text : textList) {
            g.setFont(ps.getFont(font, text.style));
            g.drawString(text.text, (float) left, (float) y);
            left += g.getFontMetrics().getStringBounds(text.text, g).getWidth();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int getHeight() {
        return fontHeight * DevicePrinter.FontSize.getLineMultiplier(textSize);
    }

    protected static class StyledText {

        public StyledText(int style, String text) {
            this.style = style;
            this.text = text;
        }

        private final int style;
        private final String text;
    }
}
