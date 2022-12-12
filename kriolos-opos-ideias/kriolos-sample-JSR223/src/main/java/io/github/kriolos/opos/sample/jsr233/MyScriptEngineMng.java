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
package io.github.kriolos.opos.sample.jsr233;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author pauloborges
 */
public class MyScriptEngineMng {

    public void numerateScriptEngines() {

        ScriptEngineManager mgr = new ScriptEngineManager();

        List<ScriptEngineFactory> factories = mgr.getEngineFactories();

        factories.stream().map(factory -> {
            System.out.printf("ScriptEngineFactory Info: %s%n\r\n", factory.getClass());
            return factory;
        }).forEachOrdered(factory -> {
            String engName = factory.getEngineName();
            String engVersion = factory.getEngineVersion();
            String langName = factory.getLanguageName();
            String langVersion = factory.getLanguageVersion();

            System.out.printf("\tScript Engine: %s (%s)%n", engName, engVersion);

            List<String> engNames = factory.getNames();
            engNames.forEach(name -> {
                System.out.printf("\tEngine Alias: %s%n", name);
            });

            List<String> engExts = factory.getExtensions();
            engExts.forEach(name -> {
                System.out.printf("\tEngine Ext: %s%n", name);
            });

            System.out.printf("\tLanguage: %s (%s)%n", langName, langVersion);
        });
    }



    public static void main(String[] args) {

        MyScriptEngineMng psmng = new MyScriptEngineMng();

        System.out.println("Enumerate ScriptEngines ========================");
        psmng.numerateScriptEngines();
        
        System.out.println("Execute GroovyScript ========================");
        new GroovyScriptTest().executeScript();
        
        System.out.println("Execute KotlinScript ========================");
        new KotlinScriptTest().executeScript();

    }
}
