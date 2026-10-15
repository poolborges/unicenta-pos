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

import com.openbravo.pos.spi.annotation.PluginContract;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Service Provider Interface (SPI) contract for scripting and template engine discovery.
 *
 * <p>Each provider registers itself via {@code META-INF/services} and exposes one or more
 * engine definitions. Callers interact exclusively through this interface — never with
 * concrete engine classes directly.</p>
 *
 * <p>A single factory may expose both {@link ScriptEngineType#SCRIPT SCRIPT} and
 * {@link ScriptEngineType#TEMPLATE TEMPLATE} engines (as BeanShell+Velocity does),
 * or specialise in just one type.</p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
@PluginContract(category = "SCRIPTING", description = "Script and template execution")
public interface ScriptEngineProvider {

    /**
     * Checks whether this provider can supply an engine with the given ID.
     *
     * @param engineId the unique engine identifier (e.g., "beanshell", "velocity").
     * @return {@code true} if this provider manages an engine with that ID.
     */
    boolean hasEngine(String engineId);

    /**
     * Returns all engine definitions exposed by this provider.
     *
     * @return unmodifiable collection of engine metadata profiles.
     */
    Collection<ScriptEngineDefinition> getAvailableEngines();

    /**
     * Returns the file extensions handled by the given engine (e.g., ".bsh", ".vm").
     *
     * @param engineId the unique engine identifier.
     * @return set of supported extensions, or empty set if the engine is not found.
     */
    Set<String> getSupportedExtensions(String engineId);

    /**
     * Executes a script using the engine identified by {@code engineId}.
     *
     * <p>Only valid for engines of type {@link ScriptEngineType#SCRIPT}.</p>
     *
     * @param engineId the target engine identifier.
     * @param script   the script source code to execute.
     * @param bindings named variables available to the script at runtime.
     * @return the result object returned by the script, or {@code null}.
     * @throws ScriptEngineException if the engine is not found, is not a SCRIPT engine,
     *                               or if execution fails.
     */
    Object execute(String engineId, String script, Map<String, Object> bindings) throws ScriptEngineException;

    /**
     * Renders a named template using the engine identified by {@code engineId}.
     *
     * <p>Only valid for engines of type {@link ScriptEngineType#TEMPLATE}.</p>
     *
     * @param engineId     the target engine identifier.
     * @param templateName the name or classpath path of the template resource.
     * @param model        named variables available to the template.
     * @return the rendered output string.
     * @throws ScriptEngineException if the engine is not found, is not a TEMPLATE engine,
     *                               or if rendering fails.
     */
    String render(String engineId, String templateName, Map<String, Object> model) throws ScriptEngineException;

    /**
     * Renders an inline template string using the engine identified by {@code engineId}.
     *
     * <p>Only valid for engines of type {@link ScriptEngineType#TEMPLATE}.</p>
     *
     * @param engineId       the target engine identifier.
     * @param templateSource the raw template source string.
     * @param model          named variables available to the template.
     * @return the rendered output string.
     * @throws ScriptEngineException if the engine is not found, is not a TEMPLATE engine,
     *                               or if rendering fails.
     */
    String renderInline(String engineId, String templateSource, Map<String, Object> model) throws ScriptEngineException;
}
