//    KrOS POS
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
//    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.scripting;

import com.openbravo.pos.spi.scripting.ScriptEngineDefinition;
import com.openbravo.pos.spi.scripting.ScriptEngineException;
import com.openbravo.pos.spi.scripting.ScriptEngineManager;
import com.openbravo.pos.spi.scripting.ScriptEngineType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Factory and adapter providing {@link ScriptEngine} instances backed by the modern
 * {@link ScriptEngineManager} SPI.
 *
 * @author adrianromero
 * @author KriolOS POS
 * @since 1.0.0
 */
public class ScriptFactory {

    /**
     * Velocity ScriptEngine identifier.
     */
    public static final String VELOCITY = "velocity";

    /**
     * BeanShell ScriptEngine identifier.
     */
    public static final String BEANSHELL = "beanshell";

    /**
     * Rhino ScriptEngine identifier.
     */
    public static final String RHINO = "rhino";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private ScriptFactory() {
    }

    /**
     * Obtains an active {@link ScriptEngine} bridge for the specified engine name.
     *
     * @param name The engine name or identifier (e.g. "velocity", "beanshell").
     * @return A configured {@link ScriptEngine} delegating to the SPI.
     * @throws ScriptException If the requested engine is null or no SPI provider is registered.
     */
    public static ScriptEngine getScriptEngine(String name) throws ScriptException {
        if (name == null) {
            throw new ScriptException("Script engine name cannot be null.");
        }

        Optional<ScriptEngineDefinition> definition = ScriptEngineManager.getEngineDefinition(name);
        if (definition.isEmpty()) {
            throw new ScriptException("Script engine not found: " + name);
        }

        return new SpiScriptEngineAdapter(definition.get().id(), definition.get().type());
    }

    /**
     * Internal adapter bridging the legacy {@link ScriptEngine} interface to
     * {@link ScriptEngineManager}.
     */
    private static class SpiScriptEngineAdapter implements ScriptEngine {

        private final String engineId;
        private final ScriptEngineType engineType;
        private final Map<String, Object> bindings = new HashMap<>();

        public SpiScriptEngineAdapter(String engineId, ScriptEngineType engineType) {
            this.engineId = engineId;
            this.engineType = engineType;
        }

        @Override
        public void put(String key, Object value) {
            bindings.put(key, value);
        }

        @Override
        public Object get(String key) {
            return bindings.get(key);
        }

        @Override
        public Object eval(String src) throws ScriptException {
            try {
                if (engineType == ScriptEngineType.TEMPLATE) {
                    return ScriptEngineManager.renderInline(engineId, src, bindings);
                } else {
                    return ScriptEngineManager.execute(engineId, src, bindings);
                }
            } catch (ScriptEngineException ex) {
                throw new ScriptException(ex.getMessage(), ex);
            }
        }
    }
}
