//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.printer.ticket;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JG uniCenta
 */
public class PrintItemLine implements PrintItem {

    /**
     *
     */
    protected Font font;

    /**
     *
     */
    protected int fontheight;

    /**
     *
     */
    protected int textsize;

    /**
     *
     */
    protected List<StyledText> m_atext;

    /** Creates a new instance of PrinterItemLine
     * @param textsize
     * @param font
     * @param fontheight */
    public PrintItemLine(int textsize, Font font, int fontheight) {
        this.textsize = textsize;
        this.font = font;
        this.fontheight = fontheight;

// JG 16 May 12 use diamond inference
        m_atext = new ArrayList<>();
    }

    /**
     *
     * @param style
     * @param text
     */
    public void addText(int style, String text) {
        m_atext.add(new StyledText(style, text));
    }

    /**
     *
     * @param g
     * @param x
     * @param y
     * @param width
     */
    @Override
    public void draw(Graphics2D g, int x, int y, int width) {

        MyPrinterState ps = new MyPrinterState(textsize);
        float left = x;
        for (int i = 0; i < m_atext.size(); i++) {
            StyledText t = m_atext.get(i);
            g.setFont(ps.getFont(font, t.style));
            g.drawString(t.text, left, (float) y);
            left += g.getFontMetrics().getStringBounds(t.text, g).getWidth();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int getHeight() {
        return fontheight * MyPrinterState.getLineMult(textsize);
    }

    /**
     *
     */
    protected static class StyledText {

        /**
         *
         * @param style
         * @param text
         */
        public StyledText(int style, String text) {
            this.style = style;
            this.text = text;
        }

        /**
         *
         */
        public int style;

        /**
         *
         */
        public String text;
    }
}
