//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.printer.escpos;

/**
 *
 * @author JG uniCenta
 */
public class CodesEpson extends Codes {
    
    private static final byte[] INITSEQUENCE = {};

    private static final byte[] CHAR_SIZE_0 = {0x1D, 0x21, 0x00};
    private static final byte[] CHAR_SIZE_1 = {0x1D, 0x21, 0x01};
    private static final byte[] CHAR_SIZE_2 = {0x1D, 0x21, 0x30};
    private static final byte[] CHAR_SIZE_3 = {0x1D, 0x21, 0x31};

    public static final byte[] BOLD_SET = {0x1B, 0x45, 0x01};
    public static final byte[] BOLD_RESET = {0x1B, 0x45, 0x00};

    public static final byte[] UNDERLINE_SET = {0x1B, 0x2D, 0x01};
    public static final byte[] UNDERLINE_RESET = {0x1B, 0x2D, 0x00};
    
    private static final byte[] OPEN_DRAWER = {0x1B, 0x70, 0x00, 0x32, -0x06};    
    private static final byte[] PARTIAL_CUT_1 = {0x1B, 0x69};
    private static final byte[] IMAGE_HEADER = {0x1D, 0x76, 0x30, 0x03};
    private static final byte[] NEW_LINE = {0x0D, 0x0A}; // Print and carriage return
    private static final byte[] IMAGE_LOGO = {0x1B, 0x1C, 0x70, 0x01, 0x00};
    
    /** Creates a new instance of CodesEpson */
    public CodesEpson() {
    }

    /**
     *
     * @return
     */
    @Override
    public byte[] getInitSequence() { return INITSEQUENCE; }
     
    /**
     *
     * @return
     */
    @Override
    public byte[] getSize0() { return CHAR_SIZE_0; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getSize1() { return CHAR_SIZE_1; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getSize2() { return CHAR_SIZE_2; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getSize3() { return CHAR_SIZE_3; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getBoldSet() { return BOLD_SET; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getBoldReset() { return BOLD_RESET; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getUnderlineSet() { return UNDERLINE_SET; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getUnderlineReset() { return UNDERLINE_RESET; }
    
    /**
     *
     * @return
     */
    @Override
    public byte[] getOpenDrawer() { return OPEN_DRAWER; }   

    /**
     *
     * @return
     */
    @Override
    public byte[] getCutReceipt() { return PARTIAL_CUT_1; }    

    /**
     *
     * @return
     */
    @Override
    public byte[] getNewLine() { return NEW_LINE; } 

    /**
     *
     * @return
     */
    @Override
    public byte[] getImageHeader() { return IMAGE_HEADER; }

    /**
     *
     * @return
     */
    @Override
    public int getImageWidth() { return 256; }

    /**
     *
     * @return
     */
    @Override
    public byte[] getImageLogo(){ return IMAGE_LOGO; }
}
