/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.kriolos.opos.sample.jsr233;


import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import org.netbeans.modules.openide.util.GlobalLookup;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author poolb
 */
public class MyScriptEngineLookup {

    public static void main(String[] args) {

        System.out.println("Global Lookup: " + GlobalLookup.current());

        if (GlobalLookup.current() != null) {
            
            System.out.println("Global Lookup: Iterate current");
            
            GlobalLookup.current().lookupAll(ScriptEngineFactory.class).forEach((t) -> {
                System.out.println("ScriptEngine: " + t.getClass());
            });
        } else {

            // EMPTY is a null
            //GlobalLookup.setSystemLookup(Lookup.EMPTY);
            System.out.println("Global Lookup configuration ");

            GlobalLookup.setSystemLookup(Lookups.metaInfServices(MyScriptEngineLookup.class.getClassLoader()));
        }


        //@ServiceProviders(value = [})
        //@ServiceProvider(sevice=)
        //@NamedServiceDefinition
        
        System.out.println("Netbeans Lookup for find : "+ ScriptEngineFactory.class.getName());
        Lookup.getDefault().lookupAll(ScriptEngineFactory.class).forEach((t) -> {
            System.out.println("ScriptEngine: " + t.getClass());
        });
        
        
        System.out.println("ScriptEngineManager for find : "+ ScriptEngineFactory.class.getName());
        ScriptEngineManager sem = new ScriptEngineManager();
        
        sem.getEngineFactories().forEach((t) -> {
            System.out.println("ScriptEngine: " + t.getClass());
        });
    }

    private static void proxyl() {

        // Lookup is a abstract class 
        //new ProxyLookup(Lookup[] lookups);
        //ProxyLookup(new Controller)
        //SimpleLookup(Collection<Object> instances)
        //SimpleProxyLookup(Provider provider)
        //SingletonLookup(Object objectToLookup);
        //MetaInfServicesLookup(ClassLoader loader, String prefix)
        // InstanceContent extends AbstractLookup.Content {
        //public AbstractLookup(Content content)
        //ExcludingLookup(Lookup delegate, Class[] classes)
    }

    //org.netbeans.modules.openide.util.ServiceProviderProcessor
    //org.netbeans.modules.openide.util.NamedServiceProcessor
}
