/*
 * Copyright (C) 2022 KiolOS<https://github.com/kriolos>
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

import java.util.Base64;

/**
 *
 * @author JG uniCenta
 */
public class Base64Encoder {
    
    /**
     *
     * @param base64
     * @return
     */
    public static byte[] decode(String base64) {

        try {
            return Base64.getDecoder().decode(base64.getBytes());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param raw
     * @return
     */
    public static String encode(byte[] raw) {
        try {
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     *
     * @param raw
     * @return
     */
    public static String encodeChunked(byte[] raw) {
        try {
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            return null;
        }
    }
}