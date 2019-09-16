/*
 * Copyright (C) 2016-2017 uniCenta <info at unicenta.com>
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.javafx.application.PlatformImpl;

/**
 * SwingFXWebView
 */
public class FXWeb extends JPanel
{
    private Stage     stage;
    private WebView   browser;
    private JFXPanel  jfxPanel;
    private JButton   swingButton;
    private WebEngine webEngine;
    private Object    geo;

    public FXWeb()
    {
        this.initComponents();
    }

    public static void main(final String... args)
    {
        // Run this later:
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final JFrame frame = new JFrame();
                frame.getContentPane().add(new FXWeb());
                frame.setMinimumSize(new Dimension(640, 480));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    private void initComponents()
    {
        this.jfxPanel = new JFXPanel();
        this.createScene();
        this.setLayout(new BorderLayout());
        this.add(this.jfxPanel, BorderLayout.CENTER);
        this.swingButton = new JButton();
        this.swingButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(final ActionEvent e)
            {
                Platform.runLater(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        FXWeb.this.webEngine.reload();
                    }
                });
            }
        });
        this.swingButton.setText("Reload");
        this.add(this.swingButton, BorderLayout.SOUTH);
    }

    /**
     * createScene Note: Key is that Scene needs to be created and run on
     * "FX user thread" NOT on the AWT-EventQueue Thread
     */
    private void createScene()
    {
        PlatformImpl.startup(() -> {
            FXWeb.this.stage = new Stage();
            FXWeb.this.stage.setTitle("Hello Java FX");
            FXWeb.this.stage.setResizable(true);
            final Group root = new Group();
            final Scene scene = new Scene(root, 80, 20);
            FXWeb.this.stage.setScene(scene);
            FXWeb.this.browser = new WebView();
            FXWeb.this.webEngine = FXWeb.this.browser.getEngine();
            FXWeb.this.webEngine.load("http://unicenta.org/splasher.html");
            final ObservableList<Node> children = root.getChildren();
            children.add(FXWeb.this.browser);
            FXWeb.this.jfxPanel.setScene(scene);
        });
    }
}
