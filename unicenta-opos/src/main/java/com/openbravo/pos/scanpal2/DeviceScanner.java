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

package com.openbravo.pos.scanpal2;

/**
 *
 * @author JG uniCenta
 */
public interface DeviceScanner {

    /**
     *
     * @throws DeviceScannerException
     */
    public void connectDevice() throws DeviceScannerException;

    /**
     *
     */
    public void disconnectDevice();

    /**
     *
     * @throws DeviceScannerException
     */
    public void startDownloadProduct() throws DeviceScannerException;

    /**
     *
     * @return
     * @throws DeviceScannerException
     */
    public ProductDownloaded recieveProduct() throws DeviceScannerException;
    
    /**
     *
     * @throws DeviceScannerException
     */
    public void startUploadProduct() throws DeviceScannerException;

    /**
     *
     * @param sName
     * @param sCode
     * @param dPrice
     * @throws DeviceScannerException
     */
    public void sendProduct(String sName, String sCode, Double dPrice) throws DeviceScannerException;

    /**
     *
     * @throws DeviceScannerException
     */
    public void stopUploadProduct() throws DeviceScannerException;    
}
