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

/**
 *
 * @author adrianromero
 * Created on 5 de marzo de 2007, 19:56
 *
 */
public class ScriptFactory {
    
    /**
     * Valocity ScriptEngine
     */
    public static final String VELOCITY = "velocity";

    /**
     * BeanSheal ScriptEngine
     */
    public static final String BEANSHELL = "beanshell";

    /**
     *
     */
    public static final String RHINO = "rhino";
    
    /** Creates a new instance of ScriptFactory */
    private ScriptFactory() {
    }
    
    /**
     *
     * @param name
     * @return
     * @throws ScriptException
     */
    public static ScriptEngine getScriptEngine(String name) throws ScriptException {
        switch (name) {
            case VELOCITY:
                return new ScriptEngineVelocity();
            case BEANSHELL:
                return new ScriptEngineBeanshell();
            default:
                throw new ScriptException("Script engine not found: " + name);
        }
    }    
}
