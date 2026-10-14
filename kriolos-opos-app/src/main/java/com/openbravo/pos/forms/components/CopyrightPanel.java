package com.openbravo.pos.forms.components;

import com.openbravo.app.AppLocal;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Copyright display panel component refactored to use a clean BorderLayout.
 *
 * @author poolborges
 */
public class CopyrightPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private static final String OFICIAL_S = "https://github.com/kriolos-obiz/kriolos-pos";

    private javax.swing.JLabel copyRightLabel;

    /**
     * Creates new form CopyrightPanel
     */
    public CopyrightPanel() {
        initComponents();
        setName("kriolos:copyright:panel");
        copyRightLabel.setName("kriolos:copyright:lbl-content");

        copyRightLabel.setText(String.format("""
            <html>
            <center>
            <h1>KriolOS POS</h1>
            <b>Open Source Point Of Sale</b></br></br>
            <p>More info on <a href="%s">official site</a></p>
            <p>%s</p>
            </center>
            <br>
            <br>
            <h2>License: GPL v3</h2>
            <br>This program is free software: you can redistribute it and/or modify 
            <br>it under the terms of the GNU General Public License as published by 
            <br>the Free Software Foundation, either version 3 of the License, 
            <br>or (at your option) any later version.
            <br>This program is distributed in the hope that it will be useful, 
            <br>but WITHOUT ANY WARRANTY; without even the implied warranty 
            <br>of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
            <br>See the GNU General Public License for more details.
            <br>You should have received a copy of the GNU General Public License 
            <br>along with this program. If not, see http://www.gnu.org/licenses/
            <br>
            </html>
            """, OFICIAL_S, OFICIAL_S)
        );
    }

    /**
     * This method is called from within the constructor to initialize the form
     * using standard layouts.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        copyRightLabel = new JLabel();
        copyRightLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        copyRightLabel.setVerticalAlignment(SwingConstants.TOP);
        copyRightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Wrap inside a JScrollPane to ensure proper scrolling if content exceeds panel boundaries
        JScrollPane scrollPane = new JScrollPane(copyRightLabel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        copyRightLabel.getAccessibleContext().setAccessibleName("Copyright and License Information");
    }
}
