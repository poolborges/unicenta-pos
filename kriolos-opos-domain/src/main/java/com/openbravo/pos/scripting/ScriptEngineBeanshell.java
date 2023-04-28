//    KrOS POS
//    Copyright (c) 2019-2023 KriolOS
//    
//
//     
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

import bsh.EvalError;
import bsh.Interpreter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero Created on 5 de marzo de 2007, 19:57
 *
 */
class ScriptEngineBeanshell implements ScriptEngine {

    private static final Logger LOGGER = Logger.getLogger("com.openbravo.pos.scripting.ScriptEngineBeanshell");

    private final Interpreter interpreter;

    /**
     * Creates a new instance of ScriptEngineBeanshell
     */
    public ScriptEngineBeanshell() {
        interpreter = new Interpreter();
    }

    @Override
    public void put(String key, Object value) {

        try {
            interpreter.set(key, value);
        } catch (EvalError e) {
            LOGGER.log(Level.SEVERE, "Exception on put", e);
        }
    }

    @Override
    public Object get(String key) {

        try {
            return interpreter.get(key);
        } catch (EvalError e) {
            LOGGER.log(Level.SEVERE, "Exception", e);
            return null;
        }
    }

    @Override
    public Object eval(String src) throws ScriptException {

        try {
            return interpreter.eval(src);
        } catch (EvalError e) {
            LOGGER.log(Level.SEVERE, "Exception on eval", e);
            throw new ScriptException(e);
        }
    }
}
