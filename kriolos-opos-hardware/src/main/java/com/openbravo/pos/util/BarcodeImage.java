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
package com.openbravo.pos.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.*;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.impl.postnet.POSTNETBean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

/**
 *
 * @author adrian
 */
public class BarcodeImage {

    public static Image getCode(String textCode, String codeType){
        switch (codeType) {
            case "QR_CODE":
            case "QRCODE":
            case "QR-CODE":
                return getQRCode(textCode);
            case "CODE_128":
            case "CODE128":
            case "CODE-128":
                return getBarcodeCode128(textCode);
            case "CODE_93":
            case "CODE93":
            case "CODE-93":
                return getBarcodeCode39(textCode);
            case "CODE_39":
            case "CODE39":
            case "CODE-39":
                return getBarcodeCode39(textCode);
            case "EAN_13":
            case "EAN13":
            case "EAN-13":
                return getBarcodeEAN13(textCode);
            case "EAN_8":
            case "EAN8":
            case "EAN-8":
                return getBarcodeEAN8(textCode);
            case "CODABAR":
                return getBarcodeCodabar(textCode);
            case "UPC_E":
            case "UPCE":
            case "UPC-E":
                return getBarcodeUPCE(textCode);
            case "UPC_A":
            case "UPCA":
            case "UPC-A":
                return getBarcodeUPCA(textCode);
            case "POSTNET":
                return getBarcodePOSTNET(textCode);
            case "Interleaved2Of5":
                return getBarcodeInterleaved2Of5(textCode);
        }

        return null;
    }
    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodeCodabar(String value) {
        AbstractBarcodeBean barcode = new CodabarBean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodeCode39(String value) {
        AbstractBarcodeBean barcode = new Code39Bean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodeInterleaved2Of5(String value) {
        AbstractBarcodeBean barcode = new Interleaved2Of5Bean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodePOSTNET(String value) {
        AbstractBarcodeBean barcode = new POSTNETBean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodeUPCA(String value) {
        AbstractBarcodeBean barcode = new UPCABean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodeUPCE(String value) {
        AbstractBarcodeBean barcode = new UPCEBean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodeEAN13(String value) {
        AbstractBarcodeBean barcode = new EAN13Bean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcodeEAN8(String value) {
        AbstractBarcodeBean barcode = new EAN8Bean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        return getBarcode(value, barcode);
    }

    public static Image getBarcodeCode128(String value) {
        return getBarcode128(value);
    }
    /**
     *
     * @param value
     * @return
     */
    public static Image getBarcode128(String value) {
        AbstractBarcodeBean barcode = new Code128Bean();
        barcode.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        return getBarcode(value, barcode);
    }

    public static Image getQRCode(String value) {
        QRCodeWriter qRCodeWriter = new QRCodeWriter();
        try {
            BitMatrix matrix = qRCodeWriter.encode(value, BarcodeFormat.QR_CODE, 100, 100);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException ex) {
            return null;
        }
    }

    private static Image getBarcode(String value, AbstractBarcodeBean barcode) {

        barcode.setModuleWidth(1.0);
        barcode.setBarHeight(40.0);
        barcode.setFontSize(10.0);
        barcode.setQuietZone(10.0);
        barcode.doQuietZone(true);
        BarcodeDimension dim = barcode.calcDimensions(value);
        int width = (int) dim.getWidth(0) + 20;
        int height = (int) dim.getHeight(0);

        BufferedImage imgtext = new BufferedImage(width, height,  BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imgtext.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.BLACK);

        try {
            barcode.generateBarcode(new Java2DCanvasProvider(g2d, 0), value);
        } catch (IllegalArgumentException e) {
            g2d.drawRect(0, 0, width - 1, height - 1);
            g2d.drawString(value, 2, height - 3);
        }

        g2d.dispose();

        return imgtext;
    }
}
