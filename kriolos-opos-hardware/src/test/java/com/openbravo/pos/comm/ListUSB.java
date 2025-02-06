/*
 * Copyright (C) 2024 Paulo Borges
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
package com.openbravo.pos.comm;

import java.util.*;
import javax.usb.*;
import org.usb4java.*;

/**
 *
 * @author poolb
 */
public class ListUSB {

    public static void listDevices(UsbHub hub) {
        List devices = hub.getAttachedUsbDevices();
        Iterator iterator = devices.iterator();
        while (iterator.hasNext()) {
            UsbDevice device = (UsbDevice) iterator.next();
            System.out.println(device);
            if (device.isUsbHub()) {
                listDevices((UsbHub) device);
            }
        }
    }

    public static void main(String[] args) throws UsbException {
        //Get UsbHub
        UsbServices services = UsbHostManager.getUsbServices();
        UsbHub root = services.getRootUsbHub();

        System.out.println("=== LIST USB");
        listPeripherique(root);
        System.out.println("=== LIST USB *****");
        listDevices(root);
        System.out.println("=== LIST USB");
        listUSB();
    }

    public static void listPeripherique(UsbHub hub) {
        //List all the USBs attached
        List perepheriques = hub.getAttachedUsbDevices();
        Iterator iterator = perepheriques.iterator();

        while (iterator.hasNext()) {

            UsbDevice perepherique = (UsbDevice) iterator.next();
            System.out.println(perepherique);

            if (perepherique.isUsbHub()) {
                listPeripherique((UsbHub) perepherique);
            }

        }
    }

    public static void listUSB() {
        
          DeviceList list = new DeviceList();
        
        final Context context = new Context();
        int result = LibUsb.init(context); // or null
        
        result = LibUsb.getDeviceList(context, list);
        for (Device device : list) {
            int address = LibUsb.getDeviceAddress(device);
            int busNumber = LibUsb.getBusNumber(device);
            DeviceDescriptor descriptor = new DeviceDescriptor();
            result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result < 0)
                {
                    throw new LibUsbException(
                        "Unable to read device descriptor", result);
                }
            System.out.format("Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
                    busNumber, address, descriptor.idVendor(), descriptor.idProduct());
        }

    }
}
