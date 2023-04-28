//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
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

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;

/**
 *
 * @author JG uniCenta
 */
public class SerializerWriteBasicExt implements SerializerWrite<Object[]> {
    
    private final Datas[] paramsTypeOfData;
    private final int[] paramsIndexOfValue;

    public SerializerWriteBasicExt(Datas[] classes, int[] index) {
        paramsTypeOfData = classes;
        paramsIndexOfValue = index;
    }

    /**
     * 
     * @param dp Datawrite
     * @param obj Params value
     * @throws BasicException 
     */
    @Override
    public void writeValues(DataWrite dp, Object[] obj) throws BasicException {

        for (int i = 0; i < paramsIndexOfValue.length; i++) {
            paramsTypeOfData[paramsIndexOfValue[i]].setValue(dp, i + 1, obj[paramsIndexOfValue[i]]);
        }
    }
    
}