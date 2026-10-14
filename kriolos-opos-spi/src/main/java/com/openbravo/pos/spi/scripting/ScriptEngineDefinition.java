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

/**
 * Immutable metadata representing a discovered scripting or template engine.
 *
 * @param id          Unique engine identifier used for lookups (e.g., "beanshell", "velocity").
 * @param name        Human-readable label shown in settings (e.g., "BeanShell 2.x").
 * @param description Short description of the engine's purpose and constraints.
 * @param type        Whether this engine handles {@link ScriptEngineType#SCRIPT scripts}
 *                    or {@link ScriptEngineType#TEMPLATE templates}.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public record ScriptEngineDefinition(String id, String name, String description, ScriptEngineType type) {

    public ScriptEngineDefinition {
        if (id == null || name == null || description == null || type == null) {
            throw new IllegalArgumentException("ScriptEngineDefinition metadata tokens cannot be null.");
        }
    }
}
