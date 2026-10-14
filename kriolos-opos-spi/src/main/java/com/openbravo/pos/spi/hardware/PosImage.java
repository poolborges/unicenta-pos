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

/**
 * UI-agnostic representation of an image for printing and display devices.
 *
 * <p>Decouples the SPI contract from desktop-specific graphics libraries
 * (such as {@code java.awt.image.BufferedImage}), enabling execution across
 * desktop, headless, container, and web environments.</p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public interface PosImage {

    /**
     * Returns the image width in pixels/dots.
     *
     * @return width in pixels
     */
    int getWidth();

    /**
     * Returns the image height in pixels/dots.
     *
     * @return height in pixels
     */
    int getHeight();

    /**
     * Returns raw image bytes (e.g. PNG, JPEG, or bitmap data).
     *
     * @return byte array containing image data, or empty array if unavailable
     */
    byte[] getBytes();

    /**
     * Resolves the underlying image object for the requested graphics framework.
     *
     * @param <T> the expected image type
     * @param targetType the target class (e.g. {@code BufferedImage.class} in desktop runtimes)
     * @return the unwrapped image instance, or {@code null} if not supported
     */
    default <T> T unwrap(Class<T> targetType) {
        return null;
    }

    /**
     * Creates a lightweight {@link PosImage} from raw bytes and dimensions.
     *
     * @param bytes raw encoded image bytes
     * @param width image width in pixels
     * @param height image height in pixels
     * @return a {@link PosImage} instance
     */
    static PosImage of(byte[] bytes, int width, int height) {
        final byte[] data = bytes != null ? bytes : new byte[0];
        return new PosImage() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public byte[] getBytes() {
                return data;
            }
        };
    }

    /**
     * Creates a {@link PosImage} carrying raw bytes, dimensions, and an optional
     * native framework payload resolvable via {@link #unwrap(Class)}.
     *
     * @param bytes raw encoded image bytes
     * @param width image width in pixels
     * @param height image height in pixels
     * @param payload optional native image object to unwrap
     * @return a {@link PosImage} instance
     */
    static PosImage of(byte[] bytes, int width, int height, Object payload) {
        final byte[] data = bytes != null ? bytes : new byte[0];
        return new PosImage() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public byte[] getBytes() {
                return data;
            }

            @Override
            public <T> T unwrap(Class<T> targetType) {
                if (targetType != null && targetType.isInstance(payload)) {
                    return targetType.cast(payload);
                }
                return null;
            }
        };
    }
}

