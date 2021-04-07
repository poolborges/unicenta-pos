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

import com.openbravo.format.Formats;
import com.openbravo.pos.instance.InstanceManager;
import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.lang.reflect.InvocationTargetException;
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

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Set the look and feel.
                String lafClass = config.getProperty("swing.defaultlaf");
                try {
                    Object laf = Class.forName(lafClass).getDeclaredConstructor().newInstance();
                    if (laf instanceof LookAndFeel) {
                        UIManager.setLookAndFeel((LookAndFeel) laf);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING, "Cannot set Look and Feel: " + lafClass, e);
                }

                //Set I18n or Language 
                String slang = config.getProperty("user.language");
                String scountry = config.getProperty("user.country");
                String svariant = config.getProperty("user.variant");
                if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
                    Locale.setDefault(new Locale(slang, scountry, svariant));
                }

                //Set Format/Pattern for: Number, Date, Currency 
                Formats.setIntegerPattern(config.getProperty("format.integer"));
                Formats.setDoublePattern(config.getProperty("format.double"));
                Formats.setCurrencyPattern(config.getProperty("format.currency"));
                Formats.setPercentPattern(config.getProperty("format.percent"));
                Formats.setDatePattern(config.getProperty("format.date"));
                Formats.setTimePattern(config.getProperty("format.time"));
                Formats.setDateTimePattern(config.getProperty("format.datetime"));

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
