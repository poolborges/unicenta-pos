/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.kriolos.opos.sample.jsr233;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author poolb
 */
public class GroovyScriptTest {

    public void executeScript() {
        try {
            javax.script.ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("groovy");
            Objects.requireNonNull(engine, "Require ScriptEngine for Groovy (.groovy extension)");
            
            ScriptContext context = engine.getContext();
            context.setAttribute("label", new Integer(4), ScriptContext.ENGINE_SCOPE);
            engine.eval("println \"${2 + label}\"", context);
            
            Object res1 = engine.eval("def velocity = 3", context);
            Objects.requireNonNull(res1, "res1 must not null");
            Object res2 = engine.eval("velocity + 2", context);
            
            assert ((Integer) res2 < 5) :
                    "Velocidade da particula não pode ser maior que a velocidade da luz";
            
            System.out.println("Valor em res2: "+ (Integer)res2);
            
        } catch (ScriptException ex) {
            Logger.getLogger(MyScriptEngineMng.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
