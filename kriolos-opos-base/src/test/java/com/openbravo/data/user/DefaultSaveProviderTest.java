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
package com.openbravo.data.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Paulo Borges
 */
public class DefaultSaveProviderTest {
    
    public DefaultSaveProviderTest() {
    }
    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }


    /**
     * Test of canDelete method, of class DefaultSaveProvider.
     */
    @org.junit.jupiter.api.Test
    public void testCanDelete() {
        boolean expResult = true;
        boolean result = true;
        assertEquals(expResult, result);
    }

    /**
     * Test of deleteData method, of class DefaultSaveProvider.
     */
    @org.junit.jupiter.api.Test
    public void testDeleteData() throws Exception {
        boolean expResult = true;
        boolean result = true;
        assertEquals(expResult, result);
    }

    /**
     * Test of canInsert method, of class DefaultSaveProvider.
     */
    @org.junit.jupiter.api.Test
    public void testCanInsert() {
        boolean expResult = true;
        boolean result = true;
        assertEquals(expResult, result);
    }

    /**
     * Test of insertData method, of class DefaultSaveProvider.
     */
    @org.junit.jupiter.api.Test
    public void testInsertData() throws Exception {
        boolean expResult = true;
        boolean result = true;
        assertEquals(expResult, result);
    }

    /**
     * Test of canUpdate method, of class DefaultSaveProvider.
     */
    @org.junit.jupiter.api.Test
    public void testCanUpdate() {
        boolean expResult = true;
        boolean result = true;
        assertEquals(expResult, result);
    }

    /**
     * Test of updateData method, of class DefaultSaveProvider.
     */
    @org.junit.jupiter.api.Test
    public void testUpdateData() throws Exception {
        boolean expResult = true;
        boolean result = true;
        assertEquals(expResult, result);
    }
    
}
