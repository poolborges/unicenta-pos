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
 * Checked exception thrown when a scripting or template engine fails to execute or render.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class ScriptEngineException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public ScriptEngineException(Throwable cause) {
        super(cause);
    }

    public ScriptEngineException(String message) {
        super(message);
    }

    public ScriptEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
