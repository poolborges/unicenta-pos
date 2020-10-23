//    KrOS POS
//    Copyright (c) 2009-2018 uniCenta
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
     *
     */
    public static final String VELOCITY = "velocity";

    /**
     *
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
// JG 16 May use switch
        switch (name) {
            case VELOCITY:
                return new ScriptEngineVelocity();
            case BEANSHELL:
                return new ScriptEngineBeanshell();
    //        } else if (RHINO.equals(name)) {
    //            return new ScriptEngineRhino();
    //        } else if (name.startsWith("generic:")) {
    //            return new ScriptEngineGeneric(name.substring(8));
            default:
                throw new ScriptException("Script engine not found: " + name);
        }
    }    
}
