package com.openbravo.pos.spi.core;

import com.openbravo.pos.spi.localization.LocalizationProvider;
import com.openbravo.pos.spi.provider.ConfigProperty;
import java.util.List;

/**
 *
 * @author dev
 */
public class TesteMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        // 1. O core pergunta quais IDs de localização estão instalados na pasta /lib
        var plugins = PluginManager.getInstance().getAllPlugins();
        
        
        //PluginManager.getInstance().

        for (var plugin : plugins) {
            String pluginId = plugin.id();
            // 2. O core solicita o esquema passando apenas as Strings. COMPILATION DEPENDENCY = ZERO!
            List<ConfigProperty> screenProperties = PluginManager.getInstance().getPluginSchemaMetadata(pluginId);

            // Desenha o formulário na tela automaticamente usando os dados obtidos...
            System.out.println("Found Plugin type: "+ plugin.serviceContract() + "; with Id:" + pluginId);
            for(var conf: screenProperties){
                System.out.println("\t Property: "+conf.key() + " ;description: "+conf.description());
            }
        }

    }

}
