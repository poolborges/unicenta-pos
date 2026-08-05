/*
 * Copyright (C) 2026 Paulo Borges
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
package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SerializerWriteBasicExtTest {

    @Mock
    private DataWrite mockDataWrite;

    @Mock
    private Datas mockDataString;

    @Mock
    private Datas mockDataInteger;

    private Datas[] sampleDataTypes;

    @BeforeEach
    void setUp() {
        // Initialize mock objects annotated with @Mock
        MockitoAnnotations.openMocks(this);
        
        // Prepare a controlled mockup metadata array
        sampleDataTypes = new Datas[]{mockDataString, mockDataInteger};
    }

    // =========================================================================
    // 1. CONSTRUCTOR VALIDATION TESTS (Fail-Fast Checks)
    // =========================================================================

    @Test
    void testConstructor_NullArguments_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> 
            new SerializerWriteBasicExt(null, new int[]{0})
        );

        assertThrows(NullPointerException.class, () -> 
            new SerializerWriteBasicExt(sampleDataTypes, null)
        );
    }

    @Test
    void testConstructor_IndexOutOfBounds_ThrowsIllegalArgumentException() {
        // Index 2 is out of bounds for an array of length 2 (valid indices are 0 and 1)
        int[] invalidIndexesHigh = {0, 2};
        assertThrows(IllegalArgumentException.class, () -> 
            new SerializerWriteBasicExt(sampleDataTypes, invalidIndexesHigh)
        );

        // Negative indices must be rejected immediately
        int[] invalidIndexesNegative = {-1, 1};
        assertThrows(IllegalArgumentException.class, () -> 
            new SerializerWriteBasicExt(sampleDataTypes, invalidIndexesNegative)
        );
    }

    // =========================================================================
    // 2. RUNTIME VALIDATION & SUCCESS TESTS
    // =========================================================================

    @Test
    void testWriteValues_SuccessfulMapping() throws BasicException {
        // Mapping definition: map loop index i=0 to configuration slot 1, i=1 to configuration slot 0
        int[] targetIndexes = {1, 0};
        Object[] runtimePayload = {"Hello World", 42};

        SerializerWriteBasicExt serializer = new SerializerWriteBasicExt(sampleDataTypes, targetIndexes);
        serializer.writeValues(mockDataWrite, runtimePayload);

        // Loop i=0 -> targetTypeIndex = 1 -> uses mockDataInteger. Checks that value parameter maps sequentially (i + 1)
        verify(mockDataInteger, times(1)).setValue(mockDataWrite, 1, runtimePayload[1]);

        // Loop i=1 -> targetTypeIndex = 0 -> uses mockDataString. Checks that sequence parameter maps sequentially (i + 1)
        verify(mockDataString, times(1)).setValue(mockDataWrite, 2, runtimePayload[0]);
    }

    @Test
    void testWriteValues_NullPayloadWithActiveConfig_ThrowsNullPointerException() {
        int[] targetIndexes = {0};
        SerializerWriteBasicExt serializer = new SerializerWriteBasicExt(sampleDataTypes, targetIndexes);

        assertThrows(NullPointerException.class, () -> 
            serializer.writeValues(mockDataWrite, null)
        );
    }

    @Test
    void testWriteValues_EmptyConfigWithNullPayload_Succeeds() {
        int[] emptyIndexes = {};
        SerializerWriteBasicExt serializer = new SerializerWriteBasicExt(sampleDataTypes, emptyIndexes);

        // Should pass safely without throwing a NullPointerException because mapping is empty
        assertDoesNotThrow(() -> serializer.writeValues(mockDataWrite, null));
    }

    @Test
    void testWriteValues_PayloadMismatchAtRuntime_ThrowsBasicException() {
        int[] targetIndexes = {1}; // Demands that index 1 exists in the upcoming array
        Object[] briefPayload = {"Only One Element"}; // Length = 1, meaning index 1 is out of bounds

        SerializerWriteBasicExt serializer = new SerializerWriteBasicExt(sampleDataTypes, targetIndexes);

        BasicException exception = assertThrows(BasicException.class, () -> 
            serializer.writeValues(mockDataWrite, briefPayload)
        );

        // Validate that your informative diagnostic text is preserved
        assertTrue(exception.getMessage().contains("is out of bounds for the provided paramValues array"));
    }
}
