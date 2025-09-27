/*
 * Copyright (C) 2025 Paulo Borges
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

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class QRCodeReader {

    public String readQRCode(String base64Image) {
        String encodedContent = null;
        try {
            byte[] imageBytes = Base64Encoder.decode(base64Image);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            encodedContent = readQRCode(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedContent;
    }

    public String readQRCode(File qrCodeFile) {
        String encodedContent = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(qrCodeFile);

            encodedContent = readQRCode(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedContent;
    }

    public String readQRCode(BufferedImage bufferedImage) {
        String encodedContent = null;
        try {
            BufferedImageLuminanceSource bufferedImageLuminanceSource = new BufferedImageLuminanceSource(bufferedImage);
            HybridBinarizer hybridBinarizer = new HybridBinarizer(bufferedImageLuminanceSource);
            BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);
            MultiFormatReader multiFormatReader = new MultiFormatReader();

            Result result = multiFormatReader.decode(binaryBitmap);
            encodedContent = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return encodedContent;
    }
}