//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
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
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.forms;

import java.awt.BorderLayout;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import com.openbravo.pos.config.JPanelConfiguration;
import com.openbravo.pos.instance.AppMessage;
import com.openbravo.pos.util.OSValidator;

/**
 * @author adrianromero
 */
public class JRootFrame extends javax.swing.JFrame implements AppMessage {

    private static final Logger LOGGER = Logger.getLogger(JRootFrame.class.getName());
    private static final long serialVersionUID = 1L;

    private final JSplashScreen splashScreen = new JSplashScreen();
    private final JRootApp m_rootapp;
    private final AppProperties m_props;

    public JRootFrame(AppProperties props) {
        initComponents();
        m_props = props;
        m_rootapp = new JRootApp(m_props);
    }

    public void initFrame(boolean kioskMode) {

        setTitle(AppLocal.APP_NAME + " - " + AppLocal.APP_VERSION);
        String image = "/com/openbravo/images/app_logo_48x48.png";
        try {
            this.setIconImage(ImageIO.read(JRootFrame.class.getResourceAsStream(image)));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception load icon: " + image, e);
        }

        //SHOW SPLASH
        getContentPane().add(splashScreen, BorderLayout.CENTER);

        //LOAD APP PANEL
        if (m_rootapp.initApp()) {
            getContentPane().remove(splashScreen);

            getContentPane().add(m_rootapp, BorderLayout.CENTER);
            sendInitEnvent();

        } else {
            //LOAD CONFIG PANEL
            int opionRes = JOptionPane.showConfirmDialog(this,
                    AppLocal.getIntString("message.databasechange"),
                    "Connection", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (opionRes == JOptionPane.YES_OPTION) {
                //JFrmConfig jFrmConfig = new JFrmConfig(m_props);
                //jFrmConfig.setVisible(true);

                JPanelConfiguration config = new JPanelConfiguration(m_props);
                config.setCloseListener(new JPanelConfiguration.CloseEventListener() {
                    @Override
                    public void windowClosed(JPanelConfiguration.CloseEvent e) {
                        dispose();         //This frame
                        System.exit(0);    //Exit JVM
                    }
                });

                getContentPane().remove(splashScreen);
                getContentPane().add(config, BorderLayout.CENTER);
            } else {
                dispose();
                System.exit(0);
            }
        }

        if (kioskMode) {
            modeKiosk();
        } else {
            modeWindow();
        }
    }

    private void modeWindow() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void modeKiosk() {

        setUndecorated(true);
        setResizable(false);

        // LINUX/UNIX
        if (new OSValidator().isUnix()) {
            GraphicsDevice device = GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getDefaultScreenDevice();

            if (device.isFullScreenSupported()) {
                setResizable(true);

                addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent arg0) {
                        setAlwaysOnTop(true);
                    }

                    @Override
                    public void focusLost(FocusEvent arg0) {
                        setAlwaysOnTop(false);
                    }
                });
                device.setFullScreenWindow(this);
            } else {
                setVisible(true);
            }
        } else {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(0, 0, d.width, d.height);
            setVisible(true);
        }
    }

    private void sendInitEnvent() {
        /**
         * String scriptId = "application.started"; try { ScriptEngine
         * scriptEngine =
         * ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
         *
         * String script = ; scriptEngine.put("device", m_props.getHost());
         * scriptEngine.eval(script); } catch (BeanFactoryException |
         * ScriptException e) { LOGGER.log(Level.WARNING, "Exception on
         * executing scriptId: " + scriptId, e); }
         */
    }

    /**
     * @throws RemoteException
     */
    @Override
    public void restoreWindow() throws RemoteException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (getExtendedState() == JFrame.ICONIFIED) {
                    setExtendedState(JFrame.NORMAL);
                }
                requestFocus();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }

            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        m_rootapp.tryToClose();

    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

        System.exit(0);

    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
