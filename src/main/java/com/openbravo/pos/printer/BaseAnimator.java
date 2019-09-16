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

package com.openbravo.pos.printer;

/**
 *
 * @author adrianromero
 */
public abstract class BaseAnimator implements DisplayAnimator {
    
    /**
     *
     */
    protected String baseLine1;

    /**
     *
     */
    protected String baseLine2;

    /**
     *
     */
    protected String currentLine1;

    /**
     *
     */
    protected String currentLine2;

    /**
     *
     */
    public BaseAnimator() {
        baseLine1 = null;
        baseLine2 = null;
    }

    /**
     *
     * @param line1
     * @param line2
     */
    public BaseAnimator(String line1, String line2) {
        baseLine1 = line1;
        baseLine2 = line2;
    }

    /**
     *
     * @return
     */
    @Override
    public String getLine1() {
        return currentLine1;
    }

    /**
     *
     * @return
     */
    @Override
    public String getLine2() {
        return currentLine2;
    }
}
