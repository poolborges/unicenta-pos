//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.editor;

/**
 *
 * @author JG uniCenta
 */
public interface EditorKeys {
    
    /**
     *
     */
    public final static int MODE_STRING = 0;

    /**
     *
     */
    public final static int MODE_DOUBLE = 1;

    /**
     *
     */
    public final static int MODE_DOUBLE_POSITIVE = 2;

    /**
     *
     */
    public final static int MODE_INTEGER = 3;

    /**
     *
     */
    public final static int MODE_INTEGER_POSITIVE = 4;
    
    /**
     *
     * @param e
     * @param imode
     */
    public void setActive(EditorComponent e, int imode);

    /**
     *
     * @param e
     */
    public void setInactive(EditorComponent e);
}
