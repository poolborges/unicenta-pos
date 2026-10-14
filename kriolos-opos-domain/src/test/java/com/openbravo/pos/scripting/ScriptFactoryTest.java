/*
 * Copyright (C) 2026 KriolOS POS
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
package com.openbravo.pos.scripting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ScriptFactory SPI-Backed Adapter Test Suite")
class ScriptFactoryTest {

    @Test
    @DisplayName("Should evaluate BeanShell expressions with input variables")
    void shouldEvaluateBeanShell() throws ScriptException {
        ScriptEngine engine = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        assertNotNull(engine, "BeanShell ScriptEngine must not be null");

        engine.put("x", 10);
        engine.put("y", 25);
        assertEquals(10, engine.get("x"));
        assertEquals(25, engine.get("y"));

        Object result = engine.eval("x + y;");
        assertEquals(35, result);
    }

    @Test
    @DisplayName("Should export variables assigned inside BeanShell script")
    void shouldExportVariablesFromBeanShell() throws ScriptException {
        ScriptEngine engine = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        engine.put("a", 5);
        engine.put("b", 6);

        engine.eval("c = a * b;");
        assertEquals(30, engine.get("c"));
    }

    @Test
    @DisplayName("Should render Velocity inline templates with bindings")
    void shouldRenderVelocityTemplate() throws ScriptException {
        ScriptEngine engine = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
        assertNotNull(engine, "Velocity ScriptEngine must not be null");

        engine.put("customer", "Maria");
        engine.put("amount", "100$00");

        Object result = engine.eval("Receipt: $customer pays $amount");
        assertEquals("Receipt: Maria pays 100$00", result);
    }

    @Test
    @DisplayName("Should resolve engine names case-insensitively")
    void shouldResolveEngineNamesCaseInsensitively() throws ScriptException {
        assertNotNull(ScriptFactory.getScriptEngine("beanshell"));
        assertNotNull(ScriptFactory.getScriptEngine("BEANSHELL"));
        assertNotNull(ScriptFactory.getScriptEngine("BeanShell"));
        assertNotNull(ScriptFactory.getScriptEngine("velocity"));
        assertNotNull(ScriptFactory.getScriptEngine("VELOCITY"));
        assertNotNull(ScriptFactory.getScriptEngine("Velocity"));
    }

    @Test
    @DisplayName("Should throw ScriptException when engine name is null")
    void shouldThrowOnNullEngine() {
        assertThrows(ScriptException.class, () -> ScriptFactory.getScriptEngine(null));
    }

    @Test
    @DisplayName("Should throw ScriptException when engine is not registered")
    void shouldThrowOnUnknownEngine() {
        assertThrows(ScriptException.class, () -> ScriptFactory.getScriptEngine("non_existent_engine_xyz"));
    }

    @Test
    @DisplayName("Should propagate syntax errors inside scripts as ScriptException")
    void shouldThrowOnScriptSyntaxError() throws ScriptException {
        ScriptEngine engine = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        assertThrows(ScriptException.class, () -> engine.eval("this is invalid syntax !!! ;;;"));
    }
}
