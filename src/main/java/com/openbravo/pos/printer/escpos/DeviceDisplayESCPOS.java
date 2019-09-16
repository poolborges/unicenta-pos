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

package com.openbravo.pos.printer.escpos;
    
import com.openbravo.pos.printer.DeviceTicket;

/**
 *
 * @author JG uniCenta
 */
public class DeviceDisplayESCPOS extends DeviceDisplaySerial {
       
    private UnicodeTranslator trans;

    /** Creates a new instance of DeviceDisplayESCPOS
     * @param display
     * @param trans */
    public DeviceDisplayESCPOS(PrinterWritter display, UnicodeTranslator trans) {
        this.trans = trans;
        init(display);       
    }

    /**
     *
     */
    @Override
    public void initVisor() {
        display.init(ESCPOS.INIT);
        display.write(ESCPOS.SELECT_DISPLAY); // Al visor
        display.write(trans.getCodeTable());
        display.write(ESCPOS.VISOR_HIDE_CURSOR);         
        display.write(ESCPOS.VISOR_CLEAR);
        display.write(ESCPOS.VISOR_HOME);
        display.flush();
    }
        
//    @Override
//    public void clearLines() {
//        display.write(ESCPOS.SELECT_DISPLAY);
//        display.write(ESCPOS.VISOR_CLEAR);
//        display.write(ESCPOS.VISOR_HOME);
//        display.flush();
//    }

    /**
     *
     */
    
    @Override
    public void repaintLines() {
        
        display.write(ESCPOS.SELECT_DISPLAY);
        display.write(ESCPOS.VISOR_CLEAR);
        display.write(ESCPOS.VISOR_HOME);
        display.write(trans.transString(DeviceTicket.alignLeft(m_displaylines.getLine1(), 20)));
        display.write(trans.transString(DeviceTicket.alignLeft(m_displaylines.getLine2(), 20)));        
        display.flush();
    }
}
