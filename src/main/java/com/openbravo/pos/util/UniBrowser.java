/*
 * Copyright (C) 2016 uniCenta <info at unicenta.com>
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
package com.openbravo.pos.util;
 
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
 
import static javafx.concurrent.Worker.State.FAILED;
  
public class UniBrowser extends JFrame {
 
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();


    private final JButton btnGo = new JButton("Go");
    private final JTextField txtURL = new JTextField();
    private final JProgressBar progressBar = new JProgressBar();
 
    public UniBrowser() {
        super();
        initComponents();
    }

    
    private void initComponents() {
        createScene();
 
        ActionListener al = (ActionEvent e) -> {
            loadURL(txtURL.getText());
        };
 
        btnGo.addActionListener(al);
        txtURL.addActionListener(al);
  
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
  
        JPanel topBar = new JPanel(new BorderLayout(5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        topBar.add(txtURL, BorderLayout.CENTER);
        topBar.add(btnGo, BorderLayout.EAST);
 
        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(lblStatus, BorderLayout.CENTER);
        statusBar.add(progressBar, BorderLayout.EAST);
 
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(jfxPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);
        
        getContentPane().add(panel);
        
        setPreferredSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

    }
 
    private void createScene() {
 
        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();
            
            engine.titleProperty().addListener((ObservableValue<? extends String> observable, String oldValue, final String newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    UniBrowser.this.setTitle(newValue);
                });
            });
            
            engine.setOnStatusChanged((final WebEvent<String> event) -> {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText(event.getData());
                });
            });
            
            engine.locationProperty().addListener((ObservableValue<? extends String> ov, String oldValue, final String newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    txtURL.setText(newValue);
                });
            });
            
            engine.getLoadWorker().workDoneProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(newValue.intValue());
                });
            });
            
            engine.getLoadWorker()
                    .exceptionProperty()
                    .addListener((ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) -> {
                        if (engine.getLoadWorker().getState() == FAILED) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(
                                        panel,
                                        (value != null) ?
                                                engine.getLocation() + "\n" + value.getMessage() :
                                                engine.getLocation() + "\nUnexpected error.",
                                        "Loading error...",
                                        JOptionPane.ERROR_MESSAGE);
                            });
                        }
            });
            
            jfxPanel.setScene(new Scene(view));
        });
    }
 
    public void loadURL(final String url) {
        Platform.runLater(() -> {
            String tmp = toURL(url);
            
            if (tmp == null) {
                tmp = toURL("http://" + url);
            }
            
            engine.load(tmp);
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
                return null;
        }
    }

   

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UniBrowser browser = new UniBrowser();
            browser.setVisible(true);
            browser.loadURL("http://unicenta.org/splasher.html");
        });
    }
}