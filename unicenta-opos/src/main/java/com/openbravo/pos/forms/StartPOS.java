//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
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
//    along with this program.  If not, see <http://www.gnu.org/licenses/>
package com.openbravo.pos.forms;

import com.openbravo.pos.instance.InstanceManager;
import java.io.File;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.AlreadyBoundException;
import javax.swing.SwingUtilities;

public class StartPOS {

    private static final Logger LOGGER = Logger.getLogger(StartPOS.class.getName());

    public static void main(final String args[]) {

        /* Single Instance - Use Junique
        try {
            InstanceManager.queryInstance().restoreWindow();
        } catch (RemoteException | NotBoundException e) {
            LOGGER.log(Level.WARNING, "Cannot start the application, another instance is running", e);
            System.exit(1);
        }
        */

        File configFile = (args.length > 0 ? new File(args[0]) : null);
        AppConfig config = new AppConfig(configFile);
        config.load();
        AppConfig.applySystemProperties(config);
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                final JRootFrame rootframe = new JRootFrame(config);
                if ("true".equals(config.getProperty("machine.uniqueinstance"))) {
                    // Register the running application
                    try {
                        final InstanceManager m_instmanager = new InstanceManager(rootframe);

                    } catch (RemoteException | AlreadyBoundException e) {
                        LOGGER.log(Level.WARNING, "Cannot create a new instance of application", e);
                    }
                }

                String screenmode = config.getProperty("machine.screenmode");
                if ("fullscreen".equals(screenmode)) {
                    rootframe.initFrame(true);
                } else {
                    rootframe.initFrame(false);
                }
            }
        });
    }
}
