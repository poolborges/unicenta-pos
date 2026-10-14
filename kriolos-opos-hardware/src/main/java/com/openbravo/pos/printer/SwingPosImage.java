/*
 * Copyright (C) 2026 KriolOS
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

import com.openbravo.data.loader.ImageUtils;
import java.awt.image.BufferedImage;

/**
 * Swing/AWT adapter implementing {@link PosImage} by wrapping a {@link BufferedImage}.
 *
 * <p>Lives in {@code kriolos-opos-hardware} so that {@code kriolos-opos-spi} and
 * {@code kriolos-opos-base} remain completely decoupled.</p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class SwingPosImage implements PosImage {

    private final BufferedImage image;
    private byte[] cachedBytes;

    public SwingPosImage(BufferedImage image) {
        this.image = image;
    }

    public static SwingPosImage of(BufferedImage image) {
        return image != null ? new SwingPosImage(image) : null;
    }

    /**
     * Converts any {@link PosImage} to a {@link BufferedImage}, either by unwrapping
     * or by decoding raw bytes via {@link ImageUtils}.
     *
     * @param posImage the source PosImage
     * @return a {@link BufferedImage} or {@code null} if conversion is not possible
     */
    public static BufferedImage toBufferedImage(PosImage posImage) {
        if (posImage == null) {
            return null;
        }
        BufferedImage unwrapped = posImage.unwrap(BufferedImage.class);
        if (unwrapped != null) {
            return unwrapped;
        }
        byte[] bytes = posImage.getBytes();
        if (bytes != null && bytes.length > 0) {
            return ImageUtils.readImage(bytes);
        }
        return null;
    }

    @Override
    public int getWidth() {
        return image != null ? image.getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return image != null ? image.getHeight() : 0;
    }

    @Override
    public byte[] getBytes() {
        if (cachedBytes == null && image != null) {
            cachedBytes = ImageUtils.writeImage(image);
        }
        return cachedBytes != null ? cachedBytes : new byte[0];
    }

    @Override
    public <T> T unwrap(Class<T> targetType) {
        if (targetType != null && targetType.isInstance(image)) {
            return targetType.cast(image);
        }
        return null;
    }

    public BufferedImage getBufferedImage() {
        return image;
    }
}
