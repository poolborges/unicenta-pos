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
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>

package com.unicenta.pos.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpUpload extends Thread {

    private static final int BUFFER_SIZE = 1024;
    private static String sMachine;

    @Override
    public void run() {
        try {
            sMachine = InetAddress.getLocalHost().getHostName();

            String filePath = System.getProperty("user.home") + "/" + sMachine + ".lau";
            String uploadPath = sMachine + ".lau";

            String ftpUrl = ""; //String.format("%s/%s;type=i", com.openbravo.pos.util.PosProviderConstants.PROVIDER_FTP_URL, uploadPath);

            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            try (OutputStream outputStream = conn.getOutputStream(); 
                    FileInputStream inputStream = new FileInputStream(filePath)) {
                
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
            }

        }
        catch (IOException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        }
    }
}