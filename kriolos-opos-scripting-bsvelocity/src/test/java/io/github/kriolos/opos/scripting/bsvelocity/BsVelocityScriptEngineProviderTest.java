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
package io.github.kriolos.opos.scripting.bsvelocity;

import com.openbravo.pos.spi.scripting.ScriptEngineDefinition;
import com.openbravo.pos.spi.scripting.ScriptEngineException;
import com.openbravo.pos.spi.scripting.ScriptEngineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BsVelocityScriptEngineProvider}.
 *
 * @author KriolOS
 */
class BsVelocityScriptEngineProviderTest {

    private BsVelocityScriptEngineProvider provider;

    @BeforeEach
    void setUp() {
        provider = new BsVelocityScriptEngineProvider();
    }

    @Test
    @DisplayName("hasEngine correctly identifies beanshell and velocity case-insensitively")
    void testHasEngine() {
        assertTrue(provider.hasEngine("beanshell"));
        assertTrue(provider.hasEngine("BeanShell"));
        assertTrue(provider.hasEngine("BEANSHELL"));
        assertTrue(provider.hasEngine("velocity"));
        assertTrue(provider.hasEngine("Velocity"));

        assertFalse(provider.hasEngine(null));
        assertFalse(provider.hasEngine(""));
        assertFalse(provider.hasEngine("quickjs"));
        assertFalse(provider.hasEngine("qute"));
    }

    @Test
    @DisplayName("getAvailableEngines returns definitions for beanshell and velocity")
    void testGetAvailableEngines() {
        Collection<ScriptEngineDefinition> engines = provider.getAvailableEngines();
        assertNotNull(engines);
        assertEquals(2, engines.size());

        ScriptEngineDefinition bsh = engines.stream()
                .filter(e -> "beanshell".equals(e.id()))
                .findFirst()
                .orElse(null);
        assertNotNull(bsh);
        assertEquals(ScriptEngineType.SCRIPT, bsh.type());

        ScriptEngineDefinition vel = engines.stream()
                .filter(e -> "velocity".equals(e.id()))
                .findFirst()
                .orElse(null);
        assertNotNull(vel);
        assertEquals(ScriptEngineType.TEMPLATE, vel.type());
    }

    @Test
    @DisplayName("getSupportedExtensions returns appropriate file extensions")
    void testGetSupportedExtensions() {
        Set<String> bshExts = provider.getSupportedExtensions("beanshell");
        assertTrue(bshExts.contains(".bsh"));

        Set<String> velExts = provider.getSupportedExtensions("velocity");
        assertTrue(velExts.contains(".vm"));

        Set<String> unknownExts = provider.getSupportedExtensions("unknown");
        assertTrue(unknownExts.isEmpty());
    }

    @Test
    @DisplayName("execute successfully evaluates BeanShell expression")
    void testExecuteBeanShellSuccess() throws ScriptEngineException {
        Object result = provider.execute("beanshell", "2 + 3;", null);
        assertEquals(5, result);
    }

    @Test
    @DisplayName("execute successfully passes variables via bindings map")
    void testExecuteBeanShellWithBindings() throws ScriptEngineException {
        Map<String, Object> bindings = Map.of(
                "unitPrice", 10.0,
                "quantity", 3.0
        );
        Object result = provider.execute("beanshell", "unitPrice * quantity;", bindings);
        assertEquals(30.0, result);
    }

    @Test
    @DisplayName("execute throws ScriptEngineException on syntax error")
    void testExecuteBeanShellSyntaxError() {
        assertThrows(ScriptEngineException.class, () ->
                provider.execute("beanshell", "this is not valid java script! ;;;", null)
        );
    }

    @Test
    @DisplayName("execute throws ScriptEngineException if invoked on a TEMPLATE engine")
    void testExecuteOnVelocityFails() {
        ScriptEngineException ex = assertThrows(ScriptEngineException.class, () ->
                provider.execute("velocity", "1 + 1", null)
        );
        assertTrue(ex.getMessage().contains("TEMPLATE"));
    }

    @Test
    @DisplayName("renderInline renders Apache Velocity template with context variables")
    void testRenderVelocityInline() throws ScriptEngineException {
        String template = "Receipt for $customer: Total is $total EUR";
        Map<String, Object> model = Map.of(
                "customer", "Alice",
                "total", "42.50"
        );

        String output = provider.renderInline("velocity", template, model);
        assertEquals("Receipt for Alice: Total is 42.50 EUR", output);
    }

    @Test
    @DisplayName("renderInline throws ScriptEngineException if invoked on a SCRIPT engine")
    void testRenderInlineOnBeanShellFails() {
        ScriptEngineException ex = assertThrows(ScriptEngineException.class, () ->
                provider.renderInline("beanshell", "Hello $name", Map.of("name", "Bob"))
        );
        assertTrue(ex.getMessage().contains("SCRIPT"));
    }

    @Test
    @DisplayName("Operations throw ScriptEngineException for unknown engine ID")
    void testUnknownEngineThrows() {
        assertThrows(ScriptEngineException.class, () ->
                provider.execute("unknownEngine", "return 1;", null)
        );
        assertThrows(ScriptEngineException.class, () ->
                provider.renderInline("unknownEngine", "hello", null)
        );
    }
}
