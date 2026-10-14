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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global Service Registry that aggregates all discovered {@link ScriptEngineProvider} implementations
 * at runtime via {@link ServiceLoader}.
 *
 * <p>Mirrors the pattern of {@code POSThemeManager} and {@code PeripheralManager}: a utility-style
 * final class with no instances, routing all calls through the ServiceLoader-discovered providers.</p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class ScriptEngineManager {

    private static final Logger LOGGER = Logger.getLogger(ScriptEngineManager.class.getName());
    private static final ServiceLoader<ScriptEngineProvider> PROVIDER_LOADER =
            ServiceLoader.load(ScriptEngineProvider.class);

    private ScriptEngineManager() {
        throw new UnsupportedOperationException("ScriptEngineManager cannot be instantiated.");
    }

    /**
     * Reloads all SPI providers from the environment / classpath.
     */
    public static synchronized void reload() {
        PROVIDER_LOADER.reload();
    }

    /**
     * Returns all discovered {@link ScriptEngineProvider} implementations.
     *
     * @return unmodifiable collection of all loaded providers.
     */
    public static Collection<ScriptEngineProvider> getAllProviders() {
        Collection<ScriptEngineProvider> providers = new ArrayList<>();
        for (ScriptEngineProvider provider : PROVIDER_LOADER) {
            providers.add(provider);
        }
        return Collections.unmodifiableCollection(providers);
    }

    /**
     * Aggregates and returns all engine definitions exposed by all loaded providers.
     *
     * @return unmodifiable collection of all available engine definitions.
     */
    public static Collection<ScriptEngineDefinition> getAllAvailableEngines() {
        Collection<ScriptEngineDefinition> engines = new ArrayList<>();
        for (ScriptEngineProvider provider : PROVIDER_LOADER) {
            engines.addAll(provider.getAvailableEngines());
        }
        return Collections.unmodifiableCollection(engines);
    }

    /**
     * Searches across all registered providers for an engine matching the given ID.
     *
     * @param engineId the unique engine identifier.
     * @return an {@link Optional} containing the definition, or empty if not found.
     */
    public static Optional<ScriptEngineDefinition> getEngineDefinition(String engineId) {
        return getAllAvailableEngines().stream()
                .filter(def -> def.id().equalsIgnoreCase(engineId))
                .findFirst();
    }

    /**
     * Finds the provider that manages the given engine ID.
     *
     * @param engineId the unique engine identifier.
     * @return an {@link Optional} containing the provider, or empty if none found.
     */
    public static Optional<ScriptEngineProvider> resolveProvider(String engineId) {
        for (ScriptEngineProvider provider : PROVIDER_LOADER) {
            if (provider.hasEngine(engineId)) {
                return Optional.of(provider);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds the provider whose extensions include the given file extension.
     *
     * @param fileExtension the file extension to match (e.g., ".bsh", ".vm").
     * @return an {@link Optional} containing the first matching provider, or empty if none found.
     */
    public static Optional<ScriptEngineProvider> resolveProviderByExtension(String fileExtension) {
        for (ScriptEngineProvider provider : PROVIDER_LOADER) {
            boolean matches = provider.getAvailableEngines().stream()
                    .anyMatch(def -> provider.getSupportedExtensions(def.id()).contains(fileExtension));
            if (matches) {
                return Optional.of(provider);
            }
        }
        return Optional.empty();
    }

    /**
     * Convenience method: executes a script via the provider that manages {@code engineId}.
     *
     * @param engineId the target engine identifier.
     * @param script   the script source to execute.
     * @param bindings named variables available to the script.
     * @return the script result, or {@code null}.
     * @throws ScriptEngineException if no provider is found or execution fails.
     */
    public static Object execute(String engineId, String script, Map<String, Object> bindings)
            throws ScriptEngineException {
        ScriptEngineProvider provider = resolveProvider(engineId)
                .orElseThrow(() -> new ScriptEngineException(
                        "No ScriptEngineProvider found for engine ID: " + engineId));
        LOGGER.log(Level.FINE, "Executing script via engine ''{0}''", engineId);
        return provider.execute(engineId, script, bindings);
    }

    /**
     * Convenience method: renders a named template via the provider that manages {@code engineId}.
     *
     * @param engineId     the target engine identifier.
     * @param templateName the name or path of the template resource.
     * @param model        named variables available to the template.
     * @return the rendered string.
     * @throws ScriptEngineException if no provider is found or rendering fails.
     */
    public static String render(String engineId, String templateName, Map<String, Object> model)
            throws ScriptEngineException {
        ScriptEngineProvider provider = resolveProvider(engineId)
                .orElseThrow(() -> new ScriptEngineException(
                        "No ScriptEngineProvider found for engine ID: " + engineId));
        LOGGER.log(Level.FINE, "Rendering template ''{0}'' via engine ''{1}''", new Object[]{templateName, engineId});
        return provider.render(engineId, templateName, model);
    }

    /**
     * Convenience method: renders an inline template string via the provider that manages {@code engineId}.
     *
     * @param engineId       the target engine identifier.
     * @param templateSource the raw template source string.
     * @param model          named variables available to the template.
     * @return the rendered string.
     * @throws ScriptEngineException if no provider is found or rendering fails.
     */
    public static String renderInline(String engineId, String templateSource, Map<String, Object> model)
            throws ScriptEngineException {
        ScriptEngineProvider provider = resolveProvider(engineId)
                .orElseThrow(() -> new ScriptEngineException(
                        "No ScriptEngineProvider found for engine ID: " + engineId));
        LOGGER.log(Level.FINE, "Rendering inline template via engine ''{0}''", engineId);
        return provider.renderInline(engineId, templateSource, model);
    }
}
