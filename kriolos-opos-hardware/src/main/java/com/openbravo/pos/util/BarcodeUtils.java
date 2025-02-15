/*
 * Copyright (C) 2023 Paulo Borges
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
package com.openbravo.pos.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.*;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.openbravo.format.Formats;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BarcodeUtils {

    private BitMatrix byteMatrix;

    private BufferedImage image;
    
    private String hredablecode;

    public BufferedImage getQRCode(String textcode, int size) {
        try {
            if (textcode.startsWith("zatcaksa:")) {
                String[] tlvs = textcode.substring(9).split("#");
                if (tlvs.length > 0) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    for (int j = 0; j < tlvs.length; j++) {
                        if (j == 2) {
                            tlvs[j] = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).format(Formats.TIMESTAMP.parseValue(tlvs[j]));
                        }
                        baos.write((byte) j + 1);
                        byte[] dataBytes = tlvs[j].getBytes("UTF-8");
                        baos.write((byte) dataBytes.length);
                        baos.write(dataBytes);
                    }
                    textcode = Base64.getEncoder().encodeToString(baos.toByteArray());
                }
            }
            HashMap<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            this.byteMatrix = qrCodeWriter.encode(textcode, BarcodeFormat.QR_CODE, size, size, hintMap);
            int imageWidth = this.byteMatrix.getWidth();
            this.image = new BufferedImage(imageWidth, imageWidth, 1);
            this.image.createGraphics();
            Graphics2D graphics = (Graphics2D) this.image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imageWidth, imageWidth);
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageWidth; j++) {
                    if (this.byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            return this.image;
        } catch (Exception e) {
            return null;
        }
    }

    public static BufferedImage getCode(String textcode, String codeType) {
        return new BarcodeUtils().getBarcode(textcode, codeType, 0, 0);
    }
    
    public BufferedImage getBarcode(String textcode, String codeType) {
        return getBarcode(textcode, codeType, 0, 0);
    }

    public BufferedImage getBarcode(String codeText, String codeType, int bcWidth, int bcHeight) {
        bcWidth = (codeText.length() >= 8) ? 200 : bcWidth;
        bcWidth = (bcWidth == 0) ? 150 : bcWidth;
        bcHeight = (bcHeight == 0) ? 20 : bcHeight;
        try {
            Writer writer;
            HashMap<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            switch (codeType) {
                case "QR_CODE":
                case "QRCODE":
                case "QR-CODE":
                    return getQRCode(codeText, bcWidth);
                case "CODE_39":
                case "CODE39":
                case "CODE-39":
                    writer = new Code39Writer();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.CODE_39, bcWidth, bcHeight, hintMap);
                    return createBarcode();
                case "CODE_93":
                case "CODE93":
                case "CODE-93":
                    writer = new Code93Writer();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.CODE_93, bcWidth, bcHeight, hintMap);
                    return createBarcode();
                case "CODE_128":
                case "CODE128":
                case "CODE-128":
                    writer = new Code128Writer();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.CODE_128, bcWidth, bcHeight, hintMap);
                    return createBarcode();
                case "EAN_13":
                case "EAN13":
                case "EAN-13":
                    writer = new EAN13Writer();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.EAN_13, bcWidth, bcHeight, hintMap);
                    return createBarcode();
                case "EAN_8":
                case "EAN8":
                case "EAN-8":
                    writer = new EAN8Writer();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.EAN_8, bcWidth, bcHeight, hintMap);
                    return createBarcode();
                case "CODABAR":
                    writer = new CodaBarWriter();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.CODABAR, bcWidth, bcHeight, hintMap);
                    return createBarcode();
                case "UPC_A":
                case "UPCA":
                case "UPC-A":
                    writer = new UPCAWriter();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.UPC_A, bcWidth, bcHeight, hintMap);
                    return createBarcode();
                case "UPC_E":
                case "UPCE":
                case "UPC-E":
                    writer = new UPCEWriter();
                    this.byteMatrix = writer.encode(codeText, BarcodeFormat.UPC_E, bcWidth, bcHeight, hintMap);
                    return createBarcode();
            }
        } catch (Exception ex) {
            Logger.getLogger(BarcodeUtils.class.getName()).log(Level.SEVERE, (String) null, (Throwable) ex);
        }
        return null;
    }

    private BufferedImage createBarcode() {
        int imageWidth = this.byteMatrix.getWidth();
        int imageHeight = this.byteMatrix.getHeight();
        int fontSize = 12;
        int areaHeigth = imageHeight + fontSize + 2;
        this.image = new BufferedImage(imageWidth, areaHeigth, BufferedImage.TYPE_INT_RGB);
        this.image.createGraphics();
        Graphics2D graphics = (Graphics2D) this.image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, areaHeigth);
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                if (this.byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        
        
        //Set X position 
        int textX = imageWidth / 4;
        // Set Y position
        int textY = areaHeigth-4;
        
        //GENERATE HUMAN REDABLE IN BOTTON POSITION
        //Font font = graphics.getFont().deriveFont((float)fontSize);
        Font font = new Font("Monospaced", Font.PLAIN, fontSize);
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);
        graphics.drawString("Paulo Borges", textX, textY);

        return this.image;
    }
}
