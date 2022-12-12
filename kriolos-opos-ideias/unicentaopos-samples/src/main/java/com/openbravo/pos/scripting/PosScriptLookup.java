/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.openbravo.pos.scripting;


import org.netbeans.modules.openide.util.GlobalLookup;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author poolb
 */
public class PosScriptLookup {

    public static void main(String[] args) {

        System.out.println("Global Lookup: " + GlobalLookup.current());

        if (GlobalLookup.current() != null) {
            
            System.out.println("Global Lookup: Iterate current");
            
            GlobalLookup.current().lookupAll(ScriptEngine.class).forEach((t) -> {
                System.out.println("ScriptEngine: " + t.getClass());
            });
        } else {

            // EMPTY is a null
            //GlobalLookup.setSystemLookup(Lookup.EMPTY);
            System.out.println("Global Lookup configuration ");

            GlobalLookup.setSystemLookup(Lookups.metaInfServices(PosScriptLookup.class.getClassLoader()));
        }

        Lookup.getDefault().lookupAll(ScriptEngine.class).forEach((t) -> {
            System.out.println("ScriptEngine: " + t.getClass());
        });

        //@ServiceProviders(value = [})
        //@ServiceProvider(sevice=)
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
