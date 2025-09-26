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
    protected int fontheight;
    protected int textsize;
    protected List<StyledText> m_atext;


    public PrintItemLine(int textsize, Font font, int fontheight) {
        this.textsize = textsize;
        this.font = font;
        this.fontheight = fontheight;
        m_atext = new ArrayList<>();
    }


    public void addText(int style, String text) {
        m_atext.add(new StyledText(style, text));
    }


    @Override
    public void draw(Graphics2D g, int x, int y, int width) {

        PrinterFontState ps = new PrinterFontState(textsize);
        double left = x;
        for (int i = 0; i < m_atext.size(); i++) {
            StyledText text = m_atext.get(i);
            g.setFont(ps.getFont(font, text.style));
            g.drawString(text.text, (float)left, y);
            left += g.getFontMetrics().getStringBounds(text.text, g).getWidth();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int getHeight() {
        return fontheight * DevicePrinter.FontSize.getLineMultiplier(textsize);
    }

    protected static class StyledText {

        public StyledText(int style, String text) {
            this.style = style;
            this.text = text;
        }

        private int style;
        private String text;
    }
}
