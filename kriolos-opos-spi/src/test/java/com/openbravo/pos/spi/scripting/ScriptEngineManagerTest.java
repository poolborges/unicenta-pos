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
package com.openbravo.pos.spi.scripting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ScriptEngineManager}.
 *
 * @author KriolOS
 */
class ScriptEngineManagerTest {

    @Test
    @DisplayName("resolveProvider returns empty for non-existent engine")
    void testResolveProviderUnknown() {
        Optional<ScriptEngineProvider> provider = ScriptEngineManager.resolveProvider("non-existent-engine-xyz");
        assertTrue(provider.isEmpty());
    }

    @Test
    @DisplayName("getEngineDefinition returns empty for non-existent engine")
    void testGetEngineDefinitionUnknown() {
        Optional<ScriptEngineDefinition> def = ScriptEngineManager.getEngineDefinition("non-existent-engine-xyz");
        assertTrue(def.isEmpty());
    }

    @Test
    @DisplayName("execute throws ScriptEngineException for unregistered engine")
    void testExecuteUnregisteredEngineThrows() {
        assertThrows(ScriptEngineException.class, () ->
                ScriptEngineManager.execute("unregistered-engine", "1 + 1", Collections.emptyMap())
        );
    }

    @Test
    @DisplayName("renderInline throws ScriptEngineException for unregistered engine")
    void testRenderInlineUnregisteredEngineThrows() {
        assertThrows(ScriptEngineException.class, () ->
                ScriptEngineManager.renderInline("unregistered-engine", "template", Collections.emptyMap())
        );
    }
}
