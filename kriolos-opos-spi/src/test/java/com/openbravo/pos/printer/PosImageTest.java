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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PosImage} contract and factory methods.
 *
 * @author KriolOS
 */
class PosImageTest {

    @Test
    @DisplayName("PosImage.of creates instance with correct dimensions and byte content")
    void testPosImageOfBytes() {
        byte[] data = new byte[]{1, 2, 3, 4};
        PosImage image = PosImage.of(data, 100, 50);

        assertEquals(100, image.getWidth());
        assertEquals(50, image.getHeight());
        assertArrayEquals(data, image.getBytes());
        assertNull(image.unwrap(String.class));
    }

    @Test
    @DisplayName("PosImage.of with payload supports unwrapping target object")
    void testPosImageOfWithPayload() {
        byte[] data = new byte[]{10, 20};
        String payload = "MockPayload";
        PosImage image = PosImage.of(data, 64, 64, payload);

        assertEquals(64, image.getWidth());
        assertEquals(64, image.getHeight());
        assertArrayEquals(data, image.getBytes());
        assertEquals(payload, image.unwrap(String.class));
        assertNull(image.unwrap(Integer.class));
    }

    @Test
    @DisplayName("PosImage.of handles null bytes gracefully")
    void testPosImageOfNullBytes() {
        PosImage image = PosImage.of(null, 10, 10);
        assertNotNull(image.getBytes());
        assertEquals(0, image.getBytes().length);
    }
}
