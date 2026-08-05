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
import java.util.Objects;

/**
 *
 * @author JG uniCenta
 */
public class SerializerWriteBasicExt implements SerializerWrite<Object[]> {
    
    private final Datas[] paramsTypeOfData;
    private final int[] paramsIndexOfValue;

    
    /**
     * 
     * @param paramsDataTypes  The underlying data types schema configuration
     * @param paramsValueIndexes  The mapping indices pointing to data types and values
     */
    public SerializerWriteBasicExt(Datas[] paramsDataTypes, int[] paramsValueIndexes) {
        // Enforce non-null structures immediately
        this.paramsTypeOfData = Objects.requireNonNull(paramsDataTypes, "paramsDataTypes cannot be null");
        this.paramsIndexOfValue = Objects.requireNonNull(paramsValueIndexes, "paramsValueIndexes cannot be null");
        
        // OPTIMIZATION: Fail-fast schema validation at construction time
        for (int targetIndex : this.paramsIndexOfValue) {
            if (targetIndex < 0 || targetIndex >= this.paramsTypeOfData.length) {
                throw new IllegalArgumentException(
                    "Initialization error: Paramter value Index mapping " + targetIndex + 
                    " is out of bounds for paramsDataTypes (Length: " + this.paramsTypeOfData.length + ")"
                );
            }
        }
    }

    /**
     * 
     * @param dataWrite  Data write stream target
     * @param paramValues Parameters values
     * @throws BasicException If index mismatches occur or data writing fails 
     */
    @Override
    public void writeValues(DataWrite dataWrite, Object[] paramValues) throws BasicException {
        
        if (paramsIndexOfValue.length > 0) {
            Objects.requireNonNull(paramValues, "Data payload cannot be null when mapping configurations exist");
        }

        for (int i = 0; i < paramsIndexOfValue.length; i++) {
            
            int targetTypeIndex = paramsIndexOfValue[i];
            
            // Runtime Guard: Protects against a dynamic runtime database payload mismatch
            if (targetTypeIndex < 0 || targetTypeIndex >= paramValues.length) {
                throw new BasicException("paramsIndexOfValue (index: " + targetTypeIndex + 
                    ") is out of bounds for the provided paramValues array (Length: " + paramValues.length + ")");
            }
            
            
            // Extract the metadata serializer definition
            Datas dataType = paramsTypeOfData[targetTypeIndex];
            
            // Safely write the sequence value from the dynamic index
            dataType.setValue(dataWrite, i + 1, paramValues[targetTypeIndex]);
        }
    }
    
}