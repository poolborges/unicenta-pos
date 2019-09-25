//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2016 uniCenta
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


/*---------------------------------------------------------------------------
 * Copyright (c) 1999,2000 Dallas Semiconductor Corporation, All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL DALLAS SEMICONDUCTOR BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Dallas Semiconductor
 * shall not be used except as stated in the Dallas Semiconductor
 * Branding Policy.
 *---------------------------------------------------------------------------
 */

package com.openbravo.pos.util;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.application.monitor.*;


/**
 * @version    0.00, 25 September 2000
 * @author     DS,BA,SH
 * @modified   JG uniCenta October 2017
 */
public class uOWWatch
    implements DeviceMonitorEventListener {

    public static String ibuttonid;
   
    /**
     * Method main
    */
    public static void iButtonOn () {
        OneWireContainer owd;

        try {
            DSPortAdapter adapter = OneWireAccessProvider.getDefaultAdapter();

            adapter.setSearchAllDevices();
            adapter.targetAllFamilies();
            adapter.setSpeed(DSPortAdapter.SPEED_REGULAR);

            uOWWatch nw = new uOWWatch(adapter);
        } catch (Exception e) {

        }

        return;
    }

   /** Network Monitor instance */
    private DeviceMonitor dm;

    /**
     * Create a 1-Wire Network Watcher
     * @param  adapter for 1-Wire Network to monitor
    */
    public uOWWatch (DSPortAdapter adapter) {

        dm = new DeviceMonitor(adapter);

        try {
            dm.addDeviceMonitorEventListener(this);
        } catch (Exception e){ }

        Thread t = new Thread(dm);
        t.start();
    }

    /**
     *
    */
    public void killWatch() {
        dm.killMonitor();
    }

    /**
     * Arrival event as a NetworkMonitorEventListener
     * @param devt
    */
   @Override
    public void deviceArrival (DeviceMonitorEvent devt) {
        int i;
        
        for(i=0; i<devt.getDeviceCount(); i++) {
            ibuttonid = devt.getAddressAsStringAt(i);
        }        
    }

    /**
     * Depart event as a NetworkMonitorEventListener
     * @param devt
    */
    @Override
    public void deviceDeparture (DeviceMonitorEvent devt) {
        int i;
        
        for(i=0; i<devt.getDeviceCount(); i++)
            ibuttonid = "";
    }

    /**
     * Depart event as a NetworkMonitorEventListener
     * @param dexc
    */
   @Override
   public void networkException (DeviceMonitorException dexc) {

    }
   
    public static String getibuttonid (){
       return ibuttonid;
    }   
  
}
