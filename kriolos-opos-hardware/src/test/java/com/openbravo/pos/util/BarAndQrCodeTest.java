/*
 * Copyright (C) 2023 Paulo Borges
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

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author poolb
 */
public class BarAndQrCodeTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        //f.setFont(new Font("SimSun",Font.PLAIN, 12));
        f.setTitle("BarCode and QrCOde ");
        //f.setTitle("Hello world! - \u7535\u8111\u4F60\u597D\uFF01");
        f.setBounds(100, 50, 800, 650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] codes = new String[]{"QRCODE",
            "CODE128", "CODE93", "CODE39",
            "EAN13", "EAN8",
            "UPCE", "UPCA",
            "CODABAR", "POSTNET"};

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);

        //JTextField enterText = new JTextField("Paulo Borges");
        String[] items = {
            "https://www.example.com",
            "Point of Sales",
            "978-0141026626",
            "0799439112766",
            "0 50332 12701",
            "8005 012",
            "0123413" //Camel cigarettes (UPC E), Check digit should be 3.
        };
        JComboBox<String> enterText = new JComboBox<>(items);
        enterText.setEditable(true);

        com.openbravo.data.gui.JImageEditor imgPanel = new com.openbravo.data.gui.JImageEditor();
        imgPanel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        imgPanel.setPreferredSize(new java.awt.Dimension(200, 200));

        com.openbravo.data.gui.JImageEditor imgPanel2 = new com.openbravo.data.gui.JImageEditor();
        imgPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        imgPanel2.setPreferredSize(new java.awt.Dimension(200, 200));

        com.openbravo.data.gui.JImageEditor imgPanel3 = new com.openbravo.data.gui.JImageEditor();
        imgPanel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        imgPanel3.setPreferredSize(new java.awt.Dimension(200, 200));

        //default value
        String codeTypeInitial = codes[0];
        imgPanel.setImage((BufferedImage) BarcodeImage.getCode(enterText.getItemAt(0), codeTypeInitial));
        imgPanel2.setImage(com.openbravo.pos.util.GraphicCode.getCode(enterText.getItemAt(0), codeTypeInitial));
        try {
            imgPanel3.setImage(com.openbravo.pos.util.GraphicCode.generateQRCodeImage(enterText.getItemAt(0)));
        } catch (Exception ex) {
            System.getLogger(BarAndQrCodeTest.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        JComboBox<String> _fontCombo = new JComboBox<>(codes);         // JComboBox of fonts
        _fontCombo.setSelectedItem("128"); // Select initial font
        _fontCombo.addActionListener((ActionEvent e) -> {
            String codeType = (String) _fontCombo.getSelectedItem();
            imgPanel.setImage(null);
            imgPanel2.setImage(null);
            imgPanel3.setImage(null);
            imgPanel.setImage((BufferedImage) BarcodeImage.getCode(enterText.getSelectedItem().toString(), codeType));
            imgPanel2.setImage(com.openbravo.pos.util.GraphicCode.getCode(enterText.getSelectedItem().toString(), codeType));
            try {
                imgPanel3.setImage(com.openbravo.pos.util.GraphicCode.generateQRCodeImage(enterText.getSelectedItem().toString()));
            } catch (Exception ex) {
                System.getLogger(BarAndQrCodeTest.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });

        panel.add(enterText);
        panel.add(_fontCombo);
        panel.add(new Label("Barcode4J"));
        panel.add(imgPanel);
        panel.add(new Label("Google ZXing"));
        panel.add(imgPanel2);
        panel.add(new Label("Google ZXing ALT"));
        panel.add(imgPanel3);
        panel.add(new Label("Status: " + new Date()));

        f.add(panel);
        f.setSize(800, 480);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

}
