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
package com.openbravo.pos.printer;

/**
 *
 * @author adrianromero
 */
public class BlinkAnimator extends BaseAnimator {
    
    /**
     *
     * @param line1
     * @param line2
     */
    public BlinkAnimator(String line1, String line2) {
        baseLine1 = DeviceTicket.alignLeft(line1, 20);
        baseLine2 = DeviceTicket.alignLeft(line2, 20);
    }
    
    /**
     *
     * @param i
     */
    @Override
    public void setTiming(int i) {
        
        if ((i % 10) < 5) {
            currentLine1 = " ".repeat(baseLine1.length());
            currentLine2 = " ".repeat(baseLine2.length());
        } else {
            currentLine1 = baseLine1;
            currentLine2 = baseLine2;
        }
    }
}
