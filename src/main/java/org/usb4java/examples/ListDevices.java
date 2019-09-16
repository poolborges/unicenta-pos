/*
 * Copyright (C) 2016 jack
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
/*
 * Copyright (C) 2014 Klaus Reimer <k@ailis.de>
 * See LICENSE.txt for licensing information.
 */

package org.usb4java.examples;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 * Simply lists all available USB devices.
 * 
 * @author Klaus Reimer <k@ailis.de>
 */
public class ListDevices
{
    /**
     * Main method.
     * 
     * @param args
     *            Command-line arguments (Ignored)
     */
    public static void main(String[] args)
    {
        // Create the libusb context
        Context context = new Context();

        // Initialize the libusb context
        int result = LibUsb.init(context);
        if (result < 0)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Read the USB device list
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);
        if (result < 0)
        {
            throw new LibUsbException("Unable to get device list", result);
        }

        try
        {
            // Iterate over all devices and list them
            for (Device device: list)
            {
                int address = LibUsb.getDeviceAddress(device);
                int busNumber = LibUsb.getBusNumber(device);
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result < 0)
                {
                    throw new LibUsbException(
                        "Unable to read device descriptor", result);
                }
                System.out.format(
                    "Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
                    busNumber, address, descriptor.idVendor(),
                    descriptor.idProduct());
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Deinitialize the libusb context
        LibUsb.exit(context);
    }
}