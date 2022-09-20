/*
 * Copyright (C) 2020 Paulo Borges
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
package com.openbravo.pos.scripting;

import com.google.auto.service.AutoService;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 *
 * @author pauloborges
 */
@AutoService(ScriptEngine.class)
public class GroovyScriptEngine implements ScriptEngine {

    // call groovy expressions from Java code
    Binding binding = new Binding();

    @Override
    public void put(String key, Object value) {
        binding.setVariable(key, key);

    }

    @Override
    public Object get(String key) {
        return binding.getVariable(key);
    }

    @Override
    public Object eval(String src) throws ScriptException {
        
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate(src);
        return value;
    }

}

